package de.maxhenkel.voicechat_mute;

import com.github.puregero.multilib.MultiLib;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.SoundPacketEvent;
import de.maxhenkel.voicechat.api.packets.SoundPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.UUID;

public final class VoicechatMute extends JavaPlugin {

    public static final String PLUGIN_ID = "voicechat_mute";
    public static final Logger LOGGER = LogManager.getLogger(PLUGIN_ID);

    @Nullable
    private MuteVoicechatPlugin voicechatPlugin;

    @Override
    public void onEnable() {
        MultiLib.getDataStorage();

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

        PluginCommand command = getCommand(VoiceChatmuteCommands.VOICECHAT_COMMAND);
        if (command != null) {
            command.setExecutor(new VoiceChatmuteCommands());
            command.setTabCompleter(new VoiceChatmuteCommands());
        }

        MultiLib.getDataStorage().getInt("voicechat_mute:mute_all", 0).thenAccept((data) -> MuteVoicechatPlugin.GLOBAL_MUTE = data == 1);
    }

    @Override
    public void onDisable() {
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
            LOGGER.info("Successfully unregistered voice chat mute plugin");
        }
    }
}
