package de.maxhenkel.voicechat_mute.events;

import de.maxhenkel.voicechat_mute.VoicechatMute;
import org.bukkit.Bukkit;

public class Events {
    public static void init() {
        VoicechatMute plugin = VoicechatMute.getInstance();
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), plugin);
    }
}
