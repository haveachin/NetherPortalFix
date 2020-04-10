package de.haveachin.netherportalfix;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class NetherPortalFix extends JavaPlugin implements Listener {
    private final double PORTAL_WIDTH = 1.4;

    private Map<Player, Location> overWorldPortalLocation = new HashMap<>();
    private Map<Player, Location> netherPortalLocation = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void fixNetherPortal(final PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
            return;

        final Player player = event.getPlayer();
        final Location from = event.getFrom();

        if (isOverWorld(from)) {
            overWorldPortalLocation.put(player, from);
            final Location to = event.getTo();
            netherPortalLocation.put(player, to);
            return;
        }

        if (!isNether(from))
            return;

        if (isOriginalPortal(player, from)) {
            final Location to = overWorldPortalLocation.get(player);
            if (to != null) event.setTo(to);;
        }

        overWorldPortalLocation.remove(player);
        netherPortalLocation.remove(player);
    }

    private boolean isNether(final Location location) {
        final World world = location.getWorld();
        if (world == null) return false;
        return world.getEnvironment() == World.Environment.NETHER;
    }

    private boolean isOverWorld(final Location location) {
        final World world = location.getWorld();
        if (world == null) return false;
        return world.getEnvironment() == World.Environment.NORMAL;
    }

    private boolean isOriginalPortal(final Player player, final Location from) {
        final Location originalLocation = netherPortalLocation.get(player);
        if (originalLocation == null) return false;
        return originalLocation.distance(from) < PORTAL_WIDTH;
    }
}
