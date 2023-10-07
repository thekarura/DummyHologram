package net.tkarura.spigot.dummyhologram.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.tkarura.spigot.dummyhologram.entity.HologramEntity;
import net.tkarura.spigot.dummyhologram.entity.IObserveHologram;
import org.bukkit.Location;

/**
 * ホログラムを作成するためのユーティリティクラスです。
 */
public class Holograms {

    /**
     * 指定した位置に新しいホログラムを作成します。
     * 作成されたホログラムは空のテキストとして作成されます。
     *
     * @param location ホログラムを作成する場所。
     * @return 作成されたホログラム。
     */
    public static IObserveHologram create(Location location) {
        return new HologramEntity(location);
    }

    /**
     * 指定した位置にカスタムテキストコンポーネントを持つ新しいホログラムを作成します。
     *
     * @param location   ホログラムを作成する場所。
     * @param components ホログラムに表示するテキストコンポーネント。
     * @return 作成されたホログラム。
     */
    public static IObserveHologram create(Location location, BaseComponent[] components) {
        IObserveHologram hologram = create(location);
        hologram.getParam().setText(components);
        return hologram;
    }
}
