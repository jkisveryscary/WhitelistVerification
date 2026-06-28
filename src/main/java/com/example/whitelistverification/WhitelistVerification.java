package com.example.whitelistverification;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WhitelistVerification extends JavaPlugin {

    private final Map<UUID, Integer> unverifiedPlayers = new HashMap<>();
    private String whitelistWorldName;
    private double teleportX;
    private double teleportY;
    private double teleportZ;
    private float teleportYaw;
    private float teleportPitch;
    private Component prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.whitelistWorldName = getConfig().getString("whitelist-world", "whitelistvoid");
        this.teleportX = getConfig().getDouble("teleport.x", 0.0);
        this.teleportY = getConfig().getDouble("teleport.y", 0.0);
        this.teleportZ = getConfig().getDouble("teleport.z", 0.0);
        this.teleportYaw = (float) getConfig().getDouble("teleport.yaw", 0.0);
        this.teleportPitch = (float) getConfig().getDouble("teleport.pitch", 0.0);
        
        String prefixStr = getConfig().getString("messages.plugin-prefix", "&7[&bVerification&7] ");
        this.prefix = colorize(prefixStr);

        if (Bukkit.getWorld(whitelistWorldName) == null) {
            getLogger().severe("==================================================");
            getLogger().severe("CRITICAL ERROR: The verification world '" + whitelistWorldName + "' was not found!");
            getLogger().severe("Please ensure this world is generated and loaded by your server configuration.");
            getLogger().severe("Disabling WhitelistVerification...");
            getLogger().severe("==================================================");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new VerificationListener(this), this);

        if (getCommand("password") != null) {
            PasswordCommand passwordCommand = new PasswordCommand(this);
            getCommand("password").setExecutor(passwordCommand);
            getCommand("password").setTabCompleter(passwordCommand);
        }

        getLogger().info("WhitelistVerification version " + getDescription().getVersion() + " initialized.");
    }

    @Override
    public void onDisable() {
        unverifiedPlayers.clear();
        getLogger().info("WhitelistVerification disabled cleanly.");
    }

    public Map<UUID, Integer> getUnverifiedPlayers() {
        return unverifiedPlayers;
    }

    public String getWhitelistWorldName() {
        return whitelistWorldName;
    }

    public double getTeleportX() {
        return teleportX;
    }

    public double getTeleportY() {
        return teleportY;
    }

    public double getTeleportZ() {
        return teleportZ;
    }

    public float getTeleportYaw() {
        return teleportYaw;
    }

    public float getTeleportPitch() {
        return teleportPitch;
    }

    public Component getPrefix() {
        return prefix;
    }

    public Component colorize(String message) {
        if (message == null) return Component.empty();
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}