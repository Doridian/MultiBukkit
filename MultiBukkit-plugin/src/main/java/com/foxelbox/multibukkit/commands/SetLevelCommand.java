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
package com.foxelbox.multibukkit.commands;

import com.foxelbox.multibukkit.util.Role;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BaseCommand.Name("setlevel")
public class SetLevelCommand extends BaseCommand {
	@Override
	public boolean onCommandAll(final CommandSender commandSender, Command command, String s, String[] strings) throws Exception {
		final Player other = getPlayerSingle(strings[0]);
		final String to = strings[1];
		new Thread() {
			@Override
			public void run() {
				try {
					plugin.playerAPI.setLevel(other, to);
					sendResponse(commandSender, "Level of player " + other.getName() + " set to " + Role.formatRoleLevel(other));
				} catch(Exception e) {
					sendResponse(commandSender, "Error: " + e.getMessage(), ChatColor.DARK_RED);
					e.printStackTrace();
				}
			}
		}.start();
		return true;
	}
}
