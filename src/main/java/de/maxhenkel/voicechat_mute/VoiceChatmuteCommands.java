package de.maxhenkel.voicechat_mute;

import com.github.puregero.multilib.MultiLib;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class VoiceChatmuteCommands implements CommandExecutor, TabCompleter {
    public static final String VOICECHAT_COMMAND = "voicechatmute";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("mute")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /%s mute <player>".formatted(VOICECHAT_COMMAND));
                return true;
            }

            String playerName = args[1];
            Optional<Player> foundPlayer = (Optional<Player>) MultiLib.getAllOnlinePlayers().stream().filter(player -> player.getName().equalsIgnoreCase(playerName)).findFirst();
            if (foundPlayer.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Player %s not found".formatted(playerName));
                return true;
            }
            Player player = foundPlayer.get();
            MultiLib.setPersistentData(player, "voicechat_mute:mute", "true");
            MultiLib.notify("voicechat_mute:mute", (player.getUniqueId().toString() + ";true"));
            sender.sendMessage("Muted player %s".formatted(player.getName()));
            player.sendMessage(ChatColor.RED + "You have been muted");
            return true;
        }

        if (args[0].equalsIgnoreCase("unmute")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /%s unmute <player>".formatted(VOICECHAT_COMMAND));
                return true;
            }

            String playerName = args[1];
            Optional<Player> foundPlayer = (Optional<Player>) MultiLib.getAllOnlinePlayers().stream().filter(player -> player.getName().equalsIgnoreCase(playerName)).findFirst();
            if (foundPlayer.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Player %s not found".formatted(playerName));
                return true;
            }
            Player player = foundPlayer.get();
            MultiLib.setPersistentData(player, "voicechat_mute:mute", "false");
            MultiLib.notify("voicechat_mute:mute", (player.getUniqueId().toString() + ";false"));
            sender.sendMessage("Unmuted player %s".formatted(player.getName()));
            player.sendMessage(ChatColor.GREEN + "You have been unmuted");
            return true;
        }

        if (args[0].equalsIgnoreCase("muteall")) {
            MultiLib.notify("voicechat_mute:mute_all", new byte[] { 1 });
            MuteVoicechatPlugin.GLOBAL_MUTE = true;
            MultiLib.getDataStorage().set("voicechat_mute:mute_all", 1);
            sender.sendMessage("Muted all players");
            for (Player player : MultiLib.getAllOnlinePlayers()) {
                if (player.equals(sender)) continue;
                if (player.hasPermission(MuteVoicechatPlugin.BYPASS_PERMISSION)) {
                    player.sendMessage(ChatColor.GREEN + "Global mute was activated");
                    continue;
                }
                player.sendMessage(ChatColor.RED + "You have been muted");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("unmuteall")) {
            MultiLib.notify("voicechat_mute:mute_all", new byte[] { 0 });
            MuteVoicechatPlugin.GLOBAL_MUTE = false;
            MultiLib.getDataStorage().set("voicechat_mute:mute_all", 0);
            sender.sendMessage("Unmuted all players");
            for (Player player : MultiLib.getAllOnlinePlayers()) {
                if (player.equals(sender)) continue;
                if (player.hasPermission(MuteVoicechatPlugin.BYPASS_PERMISSION)) {
                    player.sendMessage(ChatColor.GREEN + "Global mute was activated");
                    continue;
                }
                player.sendMessage(ChatColor.GREEN + "You have been unmuted");
            }
            return true;
        }

        return false;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("/%s help - Shows this help message".formatted(VOICECHAT_COMMAND));
        sender.sendMessage("/%s mute <player> - Mutes a player".formatted(VOICECHAT_COMMAND));
        sender.sendMessage("/%s unmute <player> - Unmutes a player".formatted(VOICECHAT_COMMAND));
        sender.sendMessage("/%s muteall - Mutes all players".formatted(VOICECHAT_COMMAND));
        sender.sendMessage("/%s unmuteall - Unmutes all players".formatted(VOICECHAT_COMMAND));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("help", "mute", "unmute", "muteall", "unmuteall");
        } else if (args.length == 2) {
            return MultiLib.getAllOnlinePlayers().stream().map(player -> player.getName()).toList();
        }
        return List.of();
    }
}
