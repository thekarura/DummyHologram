package net.tkarura.spigot.dummyhologram.entity;

/**
 * このインターフェースは、プレイヤーによって観察できるホログラムを表します。
 *
 * @author the_karura
 */
public interface IHologram {

    /**
     * {@link this#getParam} から設定されたパラメータを元に、ホログラムを更新します。
     */
    void update();

    /**
     * ホログラムを削除します。
     */
    void remove();

    /**
     * ホログラムのパラメータを取得します。
     * 設定されたパラメータは、{@link #update()}が呼び出されるまで反映されません。
     *
     * @return ホログラムのパラメータ。
     */
    HologramParam getParam();
}
