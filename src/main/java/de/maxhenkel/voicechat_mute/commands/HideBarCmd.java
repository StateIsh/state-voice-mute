package de.maxhenkel.voicechat_mute.commands;

import com.github.puregero.multilib.MultiLib;
import de.maxhenkel.voicechat_mute.MuteVoicechatPlugin;
import de.maxhenkel.voicechat_mute.VoicechatMute;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HideBarCmd implements CommandExecutor {
    public static final String HIDEMUTE_COMMAND = "hidemute";

    public static void init() {
        VoicechatMute plugin = VoicechatMute.getInstance();
        PluginCommand command = plugin.getCommand(HIDEMUTE_COMMAND);
        if (command == null) return;
        command.setExecutor(new HideBarCmd());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command");
            return true;
        }

        String data = MultiLib.getData(player, "voicechat_mute:hide_bar");

        if (data == null) data = "false";

        if (data.equals("true")) {
            MultiLib.setData(player, "voicechat_mute:hide_bar", "false");
            player.sendMessage(ChatColor.RED + "You have unhidden the voice chat mute bar");

            // Check if global mute is enabled
            if (MuteVoicechatPlugin.GLOBAL_MUTE) {
                player.showBossBar(VoicechatMute.BOSS_BAR);
            }

            return true;
        }

        MultiLib.setData(player, "voicechat_mute:hide_bar", "true");
        player.sendMessage(ChatColor.RED + "You have hidden the voice chat mute bar");

        player.hideBossBar(VoicechatMute.BOSS_BAR);

        return true;
    }
}
