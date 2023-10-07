package net.tkarura.spigot.dummyhologram.entity;

import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;

/**
 * ホログラムのパラメータを表すクラスです。
 * 殆は {@link TextDisplay} のパラメータと同じです。
 */
@Data
public class HologramParam {
    private Vector3f translation = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private int brightnessOverride = -1;
    private float viewRange = 1.0f;
    private float shadowRadius = 0.0f;
    private float shadowStrength = 0.0f;
    private float width = 0.0f;
    private float height = 0.0f;
    private boolean enableGlowColorOverride = false;
    private Color glowColorOverride = Color.WHITE;
    private BaseComponent[] text = TextComponent.fromLegacyText("");
    private int lineWidth = 300;
    private Color backgroundColor = Color.WHITE;
    private byte opacity = -1;
    private boolean shadow = false;
    private boolean seeThrough = false;
    private boolean useDefaultBackground = false;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
}
