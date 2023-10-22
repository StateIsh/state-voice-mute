package de.maxhenkel.voicechat_mute;

import org.bukkit.configuration.file.FileConfiguration;

public class Configs {
    public static boolean SHOW_BOSS_BAR = true;

    public static void init(FileConfiguration config) {
        Configs.SHOW_BOSS_BAR = config.getBoolean("show_boss_bar", true);
    }
}
