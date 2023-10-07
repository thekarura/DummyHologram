package net.tkarura.spigot.dummyhologram.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import net.md_5.bungee.chat.ComponentSerializer;
import net.tkarura.spigot.dummyhologram.utils.Packets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ホログラムを実装するクラスです。
 * パケット上でのみ存在するダミーのDisplayTextの実体を作成して、クラスが持つ観測者でのみ表示されるようにします。
 * このクラスは、{@link IObserveHologram} インターフェースを実装します。
 *
 * @author the_karura
 */
public class HologramEntity implements IObserveHologram {


    private final static EntityType ENTITY_TYPE = EntityType.TEXT_DISPLAY;
    private static Object fakeEntity;
    private static FieldAccessor ENTITY_FIELDS;
    private static ConstructorAccessor DATA_WATCHER_CONSTRUCTOR;
    private static ConstructorAccessor DISPLAY_TEXT_CONSTRUCTOR;


    private final Set<OfflinePlayer> observers = new HashSet<>();

    @Getter
    private final int entityId;
    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter
    private final Location location;
    @Getter
    private final HologramParam param = new HologramParam();

    /**
     * 指定した位置に新しいホログラムを作成します。
     *
     * @param location ホログラムを作成する場所。
     */
    public HologramEntity(Location location) {
        this.entityId = incrementEntityCount();
        this.location = location;
    }

    private int incrementEntityCount() {
        if (ENTITY_FIELDS == null) {
            ENTITY_FIELDS = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), AtomicInteger.class, true);
        }
        AtomicInteger entityId = (AtomicInteger) ENTITY_FIELDS.get(null);
        return entityId.incrementAndGet();
    }

    @Override
    public boolean addObserve(OfflinePlayer player) {
        if (observers.contains(player)) {
            return false;
        }
        observers.add(player);
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            spawnHologram(onlinePlayer);
        }
        return true;
    }

    @Override
    public boolean removeObserve(OfflinePlayer player) {
        if (!observers.contains(player)) {
            return false;
        }
        observers.remove(player);
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            removeHologram(onlinePlayer);
        }
        return true;
    }

    @Override
    public boolean fetchObserve(Player player) {
        if (observers.contains(player)) {
            spawnHologram(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsObserve(OfflinePlayer player) {
        return observers.contains(player);
    }

    @Override
    public Set<OfflinePlayer> getObservers() {
        return observers;
    }

    private void spawnHologram(Player player) {
        PacketContainer packetContainer = createEntitySpawnPacket();
        PacketContainer metaPacket = createEntityMetadataPacket();
        Packets.send(player, packetContainer, metaPacket);
    }

    private void removeHologram(Player player) {
        PacketContainer packetContainer = createEntityDestroyPacket();
        Packets.send(player, packetContainer);
    }

    @Override
    public void update() {
        getOnlinePlayers().forEach(player -> Packets.send(player, createEntityMetadataPacket()));
    }

    @Override
    public void remove() {
        getOnlinePlayers().forEach(this::removeHologram);
        observers.clear();
    }

    private Set<Player> getOnlinePlayers() {
        return observers.stream()
            .filter(OfflinePlayer::isOnline)
            .map(OfflinePlayer::getPlayer)
            .collect(Collectors.toSet());
    }

    private PacketContainer createEntitySpawnPacket() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packetContainer.getModifier().writeDefaults();
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getUUIDs().write(0, uuid);
        packetContainer.getEntityTypeModifier().write(0, ENTITY_TYPE);
        packetContainer.getDoubles()
            .write(0, location.getX())
            .write(1, location.getY())
            .write(2, location.getZ());
        packetContainer.getBytes()
            .write(0, (byte) Math.round(location.getYaw()))
            .write(1, (byte) Math.round(location.getPitch()))
            .write(2, (byte) 0);
        return packetContainer;
    }

    private PacketContainer createEntityDestroyPacket() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packetContainer.getIntLists().write(0, Collections.singletonList(entityId));
        return packetContainer;
    }

    private PacketContainer createEntityMetadataPacket() {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
        metaPacket.getIntegers().write(0, entityId);
        WrappedDataWatcher watcher = createDataWatcher();
        watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
            WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(new WrappedDataValue(
                dataWatcherObject.getIndex(),
                dataWatcherObject.getSerializer(),
                entry.getRawValue()
            ));
        });
        metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        return metaPacket;
    }

    // ダミーのdisplayTextEntityを作成を行い
    // DataWatcherのデフォルト値を取得する
    private WrappedDataWatcher createDataWatcher() {
        return toDataWatcher(new WrappedDataWatcher(newHandle(fakeDisplayTextEntity())));
    }

    private static Object newHandle(Object entity) {
        if (DATA_WATCHER_CONSTRUCTOR == null) {
            DATA_WATCHER_CONSTRUCTOR = Accessors.getConstructorAccessor(
                MinecraftReflection.getDataWatcherClass(),
                MinecraftReflection.getEntityClass()
            );
        }
        return DATA_WATCHER_CONSTRUCTOR.invoke(entity);
    }

    private static Object fakeDisplayTextEntity() {
        if (fakeEntity != null) {
            return fakeEntity;
        }

        if (DISPLAY_TEXT_CONSTRUCTOR == null) {
            DISPLAY_TEXT_CONSTRUCTOR = Accessors.getConstructorAccessor(
                MinecraftReflection.getMinecraftClass("world.entity.Display$TextDisplay", "TextDisplay"),
                MinecraftReflection.getEntityTypes(),
                MinecraftReflection.getNmsWorldClass()
            );
        }

        Object entityType = BukkitConverters.getEntityTypeConverter().getGeneric(HologramEntity.ENTITY_TYPE);
        Object world = BukkitUnwrapper.getInstance().unwrapItem(Bukkit.getWorlds().get(0));
        return fakeEntity = DISPLAY_TEXT_CONSTRUCTOR.invoke(entityType, world);
    }

    // DataWatcherのデフォルト値を設定する
    // 設定参考元： 1.19.4 Entity metadata - Display - vg wiki
    // https://wiki.vg/index.php?title=Entity_metadata&oldid=18076#Display
    private WrappedDataWatcher toDataWatcher(WrappedDataWatcher watcher) {
        watcher.setObject(10, WrappedDataWatcher.Registry.get(Vector3f.class), param.getTranslation());
        watcher.setObject(11, WrappedDataWatcher.Registry.get(Vector3f.class), param.getScale());
        watcher.setObject(14, WrappedDataWatcher.Registry.get(Byte.class), toBillboardToByte());
        watcher.setObject(15, WrappedDataWatcher.Registry.get(Integer.class), param.getBrightnessOverride());
        watcher.setObject(16, WrappedDataWatcher.Registry.get(Float.class), param.getViewRange());
        watcher.setObject(17, WrappedDataWatcher.Registry.get(Float.class), param.getShadowRadius());
        watcher.setObject(18, WrappedDataWatcher.Registry.get(Float.class), param.getShadowStrength());
        watcher.setObject(19, WrappedDataWatcher.Registry.get(Float.class), param.getWidth());
        watcher.setObject(20, WrappedDataWatcher.Registry.get(Float.class), param.getHeight());
        watcher.setObject(22,
            WrappedDataWatcher.Registry.getChatComponentSerializer(false),
            WrappedChatComponent.fromJson(toJson()).getHandle()
        );
        watcher.setObject(23, WrappedDataWatcher.Registry.get(Integer.class), param.getLineWidth());
        watcher.setObject(25, WrappedDataWatcher.Registry.get(Byte.class), param.getOpacity());
        watcher.setObject(26, WrappedDataWatcher.Registry.get(Byte.class), toBitMask());
        return watcher;
    }

    private String toJson() {
        return ComponentSerializer.toString(param.getText());
    }

    private byte toBillboardToByte() {
        return switch (param.getBillboard()) {
            case FIXED -> (byte) 0;
            case VERTICAL -> (byte) 1;
            case HORIZONTAL -> (byte) 2;
            case CENTER -> (byte) 3;
        };
    }

    private byte toBitMask() {
        byte bitMask = 0;
        if (param.isShadow()) {
            bitMask |= 0b00000001;
        }
        if (param.isSeeThrough()) {
            bitMask |= 0b00000010;
        }
        if (param.isUseDefaultBackground()) {
            bitMask |= 0b00000100;
        }
        switch (param.getAlignment()) {
            case LEFT -> bitMask |= 0b00001000;
            case RIGHT -> bitMask |= 0b00010000;
        }
        return bitMask;
    }
}
