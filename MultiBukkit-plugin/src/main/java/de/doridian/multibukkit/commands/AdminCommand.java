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
package de.doridian.multibukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@BaseCommand.Name("mbadm")
public class AdminCommand extends BaseCommand {
	@Override
	public boolean onCommandAll(CommandSender commandSender, Command command, String s, String[] strings) throws Exception {
		String arg = strings[0];
		if(arg.equalsIgnoreCase("reload")) {
			String arg2 = strings[1];
			boolean reloadAll = arg2.equalsIgnoreCase("all");
			boolean reloadPermissions = (reloadAll || arg2.startsWith("perm"));
			boolean reloadLevels = (reloadAll || arg2.startsWith("lev"));
			boolean reloadConfig = (reloadAll || arg2.startsWith("conf"));

			if(reloadConfig) {
				plugin.reloadConfig();
				sendResponse(commandSender, "Reloading config...");
			}

			if(reloadLevels) {
				plugin.playerAPI.rebuildCaches();
				sendResponse(commandSender, "Reloading levels...");
			}

			if(reloadPermissions) {
				plugin.playerAPI.loadConfig();
				sendResponse(commandSender, "Reloading permissions...");
			}
		}
		return true;
	}
}
