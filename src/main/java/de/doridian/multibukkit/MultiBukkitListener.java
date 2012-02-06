package de.doridian.multibukkit;

import de.doridian.multibukkit.api.JoinInitThread;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MultiBukkitListener implements Listener {
	final MultiBukkit plugin;
	public MultiBukkitListener(MultiBukkit plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		new JoinInitThread(plugin, event.getPlayer()).start();
	}
}
