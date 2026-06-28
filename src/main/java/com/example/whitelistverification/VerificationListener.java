package com.example.whitelistverification;

import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import net.kyori.adventure.title.Title;
import java.time.Duration;

public class VerificationListener implements Listener {

    private final WhitelistVerification plugin;

    public VerificationListener(WhitelistVerification plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerSpawnLocation(AsyncPlayerSpawnLocationEvent event) {
        PlayerProfile profile = event.getConnection().getProfile();
        
        if (profile != null && profile.getId() != null) {
            if (Bukkit.getOfflinePlayer(profile.getId()).isWhitelisted()) {
                return;
            }
        }

        World voidWorld = Bukkit.getWorld(plugin.getWhitelistWorldName());
        if (voidWorld != null) {
            Location loc = new Location(voidWorld, plugin.getTeleportX(), plugin.getTeleportY(), plugin.getTeleportZ(), plugin.getTeleportYaw(), plugin.getTeleportPitch());
            event.setSpawnLocation(loc);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isWhitelisted()) {
            return;
        }

        plugin.getUnverifiedPlayers().put(player.getUniqueId(), 3);
        
        World voidWorld = Bukkit.getWorld(plugin.getWhitelistWorldName());
        if (voidWorld != null) {
            Location loc = new Location(voidWorld, plugin.getTeleportX(), plugin.getTeleportY(), plugin.getTeleportZ(), plugin.getTeleportYaw(), plugin.getTeleportPitch());
            player.teleport(loc);
        }

        // 10-Second 2B2T Centered Title Display 
        Title title = Title.title(
            plugin.colorize("&4&lVERIFICATION REQUIRED"),
            plugin.colorize("&eType &a/password <password> &ein chat to join!"),
            Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(10000), Duration.ofMillis(500))
        );
        player.showTitle(title);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getUnverifiedPlayers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            
            if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                Location lockedLoc = from.clone();
                lockedLoc.setYaw(to.getYaw());
                lockedLoc.setPitch(to.getPitch());
                event.setTo(lockedLoc);

                player.sendActionBar(plugin.colorize("&c&lLOG LOCKED: &eType &a/password <password> &eto unlock."));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player) {
            if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
                event.setTarget(null);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player player) {
            if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFish(PlayerFishEvent event) {
        if (plugin.getUnverifiedPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
            // Server Operator Command Pass-through
            if (player.isOp()) {
                return;
            }

            String message = event.getMessage().trim();
            String[] parts = message.split(" ");
            String command = parts[0];

            if (!command.equalsIgnoreCase("/password")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getPrefix().append(plugin.colorize("&cYou must verify first using /password <password>.")));
            }
        }
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (plugin.getUnverifiedPlayers().containsKey(player.getUniqueId())) {
            // Server Operator Autocomplete Pass-through
            if (player.isOp()) {
                return;
            }
            event.getCommands().clear();
            event.getCommands().add("password");
        }
    }
}
