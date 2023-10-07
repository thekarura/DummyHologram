package net.tkarura.spigot.dummyhologram.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

/**
 * このクラスは、プレイヤーにパケットを送信するためのユーティリティクラスです。
 * {@link ProtocolLibrary#getProtocolManager()} を使用してパケットを送信します。
 *
 * @author the_karura
 */
@UtilityClass
public class Packets {

    /**
     * 指定したプレイヤーにパケットを送信します。
     *
     * @param sender 送信するプレイヤー。
     * @param packet 送信するパケット。
     */
    public static void send(Player sender, PacketContainer... packet) {
        for (PacketContainer p : packet) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(sender, p);
        }
    }
}
