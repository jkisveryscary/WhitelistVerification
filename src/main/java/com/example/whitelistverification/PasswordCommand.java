package com.example.whitelistverification;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class PasswordCommand implements CommandExecutor, TabCompleter {
    private final WhitelistVerification plugin;

    public PasswordCommand(WhitelistVerification plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this verification sequence.");
            return true;
        }

        if (!plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getPrefix().append(plugin.colorize("&cYou are already fully verified!")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getPrefix().append(plugin.colorize("&cUsage: /password <password>")));
            return true;
        }

        // Fixed match path to look for "password" instead of "verification-password"
        String correctPassword = plugin.getConfig().getString("password", "ServerPassword123");
        if (args[0].equals(correctPassword)) {
            plugin.getUnverifiedPlayers().remove(player.getUniqueId());
            player.setWhitelisted(true);
            player.clearTitle(); 

            // Safely route player to survival world spawn upon validation
            Location mainSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
            player.teleport(mainSpawn);

            player.sendMessage(plugin.getPrefix().append(plugin.colorize("&aVerification successful! Welcome to the server.")));
        } else {
            int attempts = plugin.getUnverifiedPlayers().get(player.getUniqueId()) - 1;
            if (attempts <= 0) {
                player.kick(plugin.colorize("&cIncorrect password limit reached. Connection terminated."));
            } else {
                plugin.getUnverifiedPlayers().put(player.getUniqueId(), attempts);
                player.sendMessage(plugin.getPrefix().append(plugin.colorize("&cIncorrect password! You have " + attempts + " attempts remaining.")));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>(); // Blocks command parameter leakage
    }
}
