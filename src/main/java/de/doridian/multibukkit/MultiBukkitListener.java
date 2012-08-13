package de.doridian.multibukkit;

import de.doridian.multibukkit.api.JoinInitThread;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MultiBukkitListener implements Listener {
	final MultiBukkit plugin;
	public MultiBukkitListener(MultiBukkit plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		new JoinInitThread(plugin, event.getPlayer()).start();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.deletePermissionAttachmentFor(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		if(event.isCancelled()) return;
		plugin.deletePermissionAttachmentFor(event.getPlayer());
	}
}
