package de.maxhenkel.voicechat_mute.events;

import com.github.puregero.multilib.MultiLib;
import de.maxhenkel.voicechat_mute.MuteVoicechatPlugin;
import de.maxhenkel.voicechat_mute.VoicechatMute;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) {
        // Ensure local player
        if (!MultiLib.isLocalPlayer(e.getPlayer())) return;

        // Check if global mute is enabled
        if (!MuteVoicechatPlugin.GLOBAL_MUTE) return;

        // Check if player has bypass permission
        if (e.getPlayer().hasPermission(MuteVoicechatPlugin.BYPASS_PERMISSION)) return;

        // Check if player has boss bar hidden
        String data = MultiLib.getData(e.getPlayer(), "voicechat_mute:hide_bar");
        if (data != null && data.equals("true")) return;

        e.getPlayer().hideBossBar(VoicechatMute.BOSS_BAR);

        // Show boss bar
        Bukkit.getScheduler().runTaskLater(VoicechatMute.getInstance(), () -> {
            e.getPlayer().showBossBar(VoicechatMute.BOSS_BAR);
        }, 20L);
    }
}
