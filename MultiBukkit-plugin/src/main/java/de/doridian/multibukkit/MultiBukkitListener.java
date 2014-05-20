/**
 * This file is part of MultiBukkit-plugin.
 *
 * MultiBukkit-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MultiBukkit-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MultiBukkit-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
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
