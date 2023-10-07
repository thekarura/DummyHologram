# Dummy Hologram

視認範囲の選択が可能なホログラムを提供する 1.19.4 専用の Spigot Plugin 用ライブラリです。

## Getting Started / 始め方

この plugin は他の plugin 開発者向けに作成されており、単体での使用は想定されていません。
別のプロジェクトで作成したホログラム処理のコードを公開する目的で作成されています。

### Prerequisites / 必要なもの

依存関係として、動作環境の Spigot・Paper 1.19.4、及び 依存関係から ProtocolLib 5.0.0 以上が必要です。

### Installing / インストール

このプロジェクトは Maven で管理されています。以下のコマンドを実行をする事で成果物が作成されます。

```
mvn install
```

作成された成果物は spigot または paper の plugin フォルダに成果物をコピーしてください。

## 開発者向け

Installの手順を踏まえて、以下の依存関係を使用するプロジェクトに追加してください。

### Maven

```
<dependency>
    <groupId>net.tkarura.spigot.dummy.hologram</groupId>
    <artifactId>DummyHologram</artifactId>
    <version>1.19.4-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```
dependencies {
    compileOnly group: 'net.tkarura.spigot.dummy.hologram', name: 'DummyHologram', version: '1.19.4-SNAPSHOT'
}
```

次に、plugin.yml に以下の内容を追加してください。

```
depend: [DummyHologram]
```

ホログラムに関する大まかな使用方法は以下の通りです。

````java
IObserveHologram hologram = Holograms.create(player.getLocation(), TextComponent.fromLegacyText("Hello, world!"));
hologram.addObserve(player); // プレイヤーに表示させる

// 5秒後にテキストを変更する
HologramParam param = hologram.getParam();
param.setText(TextComponent.fromLegacyText("changed again!"));
Bukkit.getScheduler().runTaskLater(plugin, hologram::update, 20 * 5);

// 10秒後に削除する
Bukkit.getScheduler().runTaskLater(plugin, hologram::remove, 20 * 10);
````

## Built With / 使用したもの

* [Spigot-API](https://hub.spigotmc.org) - Spigot Plugin API
* [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) - パケット送信に使用
* [Maven](https://maven.apache.org/) - 依存関係の管理

## Authors / 著者

* **the_karura** - *Initial work* - [the_karura](https://github.com/thekarura)

## Acknowledgments / 謝辞

* パケットの送信データの作成にあたり、https://wiki.vg/Protocol に記載されている情報を参考にしました。
* パケットを扱った処理に関して情報が少ない為、何かの参考になると良いなと思います。
