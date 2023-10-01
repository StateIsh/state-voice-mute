package de.maxhenkel.voicechat_mute;

import com.github.puregero.multilib.MultiLib;
import com.github.puregero.multilib.MultiLibImpl;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.SoundPacketEvent;
import de.maxhenkel.voicechat.api.packets.SoundPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MuteVoicechatPlugin implements VoicechatPlugin {
    public static Permission BYPASS_PERMISSION = new Permission("voicechat_mute.bypass", PermissionDefault.OP);
    public static Permission MUTE_PERMISSION = new Permission("voicechat_mute.mute", PermissionDefault.OP);

    public static boolean GLOBAL_MUTE = false;

    private static VoicechatServerApi api;

    public static VoicechatServerApi getApi() {
        return api;
    }

    /**
     * @return the unique ID for this voice chat plugin
     */
    @Override
    public String getPluginId() {
        return VoicechatMute.PLUGIN_ID;
    }

    /**
     * Called when the voice chat initializes the plugin.
     *
     * @param api the voice chat API
     */
    @Override
    public void initialize(VoicechatApi api) {
        MuteVoicechatPlugin.api = (VoicechatServerApi) api;
    }

    /**
     * Called once by the voice chat to register all events.
     *
     * @param registration the event registration
     */
    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
    }

    /**
     * This method is called whenever a player sends audio to the server via the voice chat.
     *
     * @param event the microphone packet event
     */
    private void onMicrophone(MicrophonePacketEvent event) {
        // The connection might be null if the event is caused by other means
        if (event.getSenderConnection() == null) {
            return;
        }
        // Cast the generic player object of the voice chat API to an actual bukkit player
        // This object should always be a bukkit player object on bukkit based servers
        if (!(event.getSenderConnection().getPlayer().getPlayer() instanceof Player player)) {
            return;
        }

        if (player.hasPermission(BYPASS_PERMISSION)) return;

        if (GLOBAL_MUTE) {
            event.cancel();
            return;
        }

        String muted = MultiLib.getPersistentData(player, "voicechat_mute:muted");
        if (muted != null && muted.equals("true")) {
            event.cancel();
        }
    }
}
