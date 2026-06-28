package com.example.whitelistverification;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.List;

public class PasswordCommand implements CommandExecutor, TabCompleter {

    private final WhitelistVerification plugin;

    public PasswordCommand(WhitelistVerification plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (!plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getPrefix().append(plugin.colorize("&cYou are already verified!")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getPrefix().append(plugin.colorize("&cUsage: /password <password>")));
            return true;
        }

        String inputPassword = args[0];
        String correctPassword = plugin.getConfig().getString("password", "SecretPassword123");

        if (correctPassword.equals(inputPassword)) {
            plugin.getLogger().info("Verification Success: Player " + player.getName() + " passed password matching.");
            plugin.getUnverifiedPlayers().remove(player.getUniqueId());

            player.setWhitelisted(true);

            String successMsg = plugin.getConfig().getString("messages.success", "&aSuccessfully verified! Please reconnect.");
            Component finalMessage = plugin.colorize(successMsg);

            Bukkit.getScheduler().runTask(plugin, () -> player.kick(finalMessage));
        } else {
            int attemptsLeft = plugin.getUnverifiedPlayers().get(player.getUniqueId()) - 1;
            plugin.getLogger().warning("Verification Failure: Player " + player.getName() + " input an invalid password. Remaining: " + attemptsLeft);

            if (attemptsLeft <= 0) {
                plugin.getUnverifiedPlayers().remove(player.getUniqueId());
                String kickMsg = plugin.getConfig().getString("messages.kick-failed", "&cToo many failed attempts!");
                Component finalKickMessage = plugin.colorize(kickMsg);

                Bukkit.getScheduler().runTask(plugin, () -> player.kick(finalKickMessage));
            } else {
                plugin.getUnverifiedPlayers().put(player.getUniqueId(), attemptsLeft);
                
                String wrongPwdMsg = plugin.getConfig().getString("messages.wrong-password", "&cWrong password!");
                String remainingMsg = plugin.getConfig().getString("messages.remaining-attempts", "&cRemaining attempts: %attempts%")
                        .replace("%attempts%", String.valueOf(attemptsLeft));

                player.sendMessage(plugin.getPrefix().append(plugin.colorize(wrongPwdMsg)));
                player.sendMessage(plugin.getPrefix().append(plugin.colorize(remainingMsg)));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}