package net.tkarura.spigot.dummyhologram.entity;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * このインターフェースは、プレイヤーによって観察でき、
 * プレイヤーが観察者を追加、削除、および取得できるホログラムを表します。
 *
 * @author the_karura
 */
public interface IObserveHologram extends IHologram {

    /**
     * ホログラムに観察者を追加します。
     *
     * @param player 観察者として追加するプレイヤー。
     * @return プレイヤーが観察者として追加された場合はtrue、それ以外の場合はfalse。
     */
    boolean addObserve(OfflinePlayer player);

    /**
     * ホログラムから観察者を削除します。
     *
     * @param player 削除する観察者としてのプレイヤー。
     * @return プレイヤーが観察者として削除された場合はtrue、それ以外の場合はfalse。
     */
    boolean removeObserve(OfflinePlayer player);

    /**
     * プレイヤーが観察者である場合、そのプレイヤーのためにホログラムを取得します。
     *
     * @param player ホログラムを取得するプレイヤー。
     * @return プレイヤーが観察者であり、ホログラムが取得された場合はtrue、それ以外の場合はfalse。
     */
    boolean fetchObserve(Player player);

    /**
     * プレイヤーがホログラムの観察者であるかどうかを確認します。
     *
     * @param player 確認するプレイヤー。
     * @return プレイヤーが観察者である場合はtrue、それ以外の場合はfalse。
     */
    boolean containsObserve(OfflinePlayer player);

    /**
     * ホログラムの観察者のセットを取得します。
     *
     * @return 観察者のセット。
     */
    Set<OfflinePlayer> getObservers();
}
