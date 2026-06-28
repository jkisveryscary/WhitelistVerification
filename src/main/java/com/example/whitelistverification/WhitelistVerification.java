package com.example.whitelistverification;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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

        // Dynamic world loading and creation fallbacks
        World voidWorld = Bukkit.getWorld(whitelistWorldName);
        if (voidWorld == null) {
            getLogger().info("World '" + whitelistWorldName + "' was not found active. Forcing server to initialize it...");
            try {
                WorldCreator creator = new WorldCreator(whitelistWorldName);
                voidWorld = Bukkit.createWorld(creator);
                getLogger().info("Successfully initialized and bound to world: " + whitelistWorldName);
            } catch (Exception e) {
                getLogger().severe("==================================================");
                getLogger().severe("CRITICAL ERROR: Failed to automatically build or load '" + whitelistWorldName + "'!");
                getLogger().severe("Disabling WhitelistVerification...");
                getLogger().severe("==================================================");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        // Programmatic Performance Optimization: Locks the void world to an absolute minimum 2-chunk load footprint
        if (voidWorld != null) {
            voidWorld.setViewDistance(2);
            voidWorld.setSimulationDistance(2);
            getLogger().info("Successfully clamped view-distance and simulation-distance to 2 chunks on '" + whitelistWorldName + "'.");
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
