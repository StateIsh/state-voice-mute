package de.maxhenkel.voicechat_mute;

import com.github.puregero.multilib.MultiLib;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat_mute.commands.Commands;
import de.maxhenkel.voicechat_mute.events.Events;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.UUID;

public final class VoicechatMute extends JavaPlugin {

    public static final String PLUGIN_ID = "voicechat_mute";
    public static final Logger LOGGER = LogManager.getLogger(PLUGIN_ID);

    public static final BossBar BOSS_BAR = BossBar.bossBar(Component.text("GLOBAL MUTED"), 1F, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

    private static VoicechatMute instance;

    @Nullable
    private MuteVoicechatPlugin voicechatPlugin;

    @Override
    public void onEnable() {
        instance = this;

        MultiLib.getDataStorage();

        saveDefaultConfig();

        Configs.init(getConfig());

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            voicechatPlugin = new MuteVoicechatPlugin();
            service.registerPlugin(voicechatPlugin);
            LOGGER.info("Successfully registered voice chat mute plugin");
        } else {
            LOGGER.info("Failed to register voice chat mute plugin");
        }

        MultiLib.on(this, "voicechat_mute:mute_all", (data) -> {
            MuteVoicechatPlugin.GLOBAL_MUTE = data[0] == 1;
            MultiLib.getDataStorage().set("voicechat_mute:mute_all", data[0]);

            // Check if global mute is enabled
            if (!MuteVoicechatPlugin.GLOBAL_MUTE) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.hideBossBar(BOSS_BAR);
                }
                return;
            }

            for (Player p : MultiLib.getLocalOnlinePlayers()) {
                if (p.hasPermission(MuteVoicechatPlugin.BYPASS_PERMISSION)) continue;

                // Check if player has hidden the bar
                String hasMute = MultiLib.getData(p, "voicechat_mute:hide_bar");
                if (hasMute != null && hasMute.equals("true")) return;
                p.showBossBar(VoicechatMute.BOSS_BAR);
            }
        });
        MultiLib.onString(this, "voicechat_mute:mute", (data) -> {
            String[] split = data.split(";");
            UUID playerUUID = UUID.fromString(split[0]);
            String mute = split[1];
            Player player = getServer().getPlayer(playerUUID);
            if (player != null) {
                MultiLib.setPersistentData(player, "voicechat_mute:muted", mute);
            }
        });

        Commands.init();
        Events.init();

        MultiLib.getDataStorage().getInt("voicechat_mute:mute_all", 0).thenAccept((data) -> MuteVoicechatPlugin.GLOBAL_MUTE = data == 1);
    }

    @Override
    public void onDisable() {
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
            LOGGER.info("Successfully unregistered voice chat mute plugin");
        }
    }

    public static VoicechatMute getInstance() {
        return instance;
    }
}
