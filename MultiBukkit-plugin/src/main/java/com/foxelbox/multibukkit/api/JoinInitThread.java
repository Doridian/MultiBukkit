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
package com.foxelbox.multibukkit.api;

import com.foxelbox.multibukkit.MultiBukkit;
import org.bukkit.entity.Player;

public class JoinInitThread extends Thread {
	final MultiBukkit plugin;
	final Player player;

	public JoinInitThread(MultiBukkit plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}

	@Override
	public void run() {
		plugin.playerAPI.getLevel(player, true);
	}
}
