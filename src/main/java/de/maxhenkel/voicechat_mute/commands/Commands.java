package de.maxhenkel.voicechat_mute.commands;

import de.maxhenkel.voicechat_mute.VoicechatMute;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

public class Commands {
    public static void init() {
        MuteCmd.init();
        HideBarCmd.init();
    }
}
