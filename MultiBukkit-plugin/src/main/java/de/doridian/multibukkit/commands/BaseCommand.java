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

import de.doridian.multibukkit.MultiBukkit;
import de.doridian.multibukkit.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor {
	protected final MultiBukkit plugin;
	protected BaseCommand() {
		plugin = MultiBukkit.instance;
	}

	@Retention(RetentionPolicy.RUNTIME) protected @interface Name { String value(); }

	@Override
	public final boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		try {
			return onCommandAll(commandSender, command, s, strings);
		} catch(Exception e) {
			sendResponse(commandSender, "Error: " + e.getMessage(), ChatColor.DARK_RED);
			return false;
		}
	}

	public boolean onCommandAll(CommandSender commandSender, Command command, String s, String[] strings) throws Exception {
		if(commandSender instanceof Player) {
			return onCommandPlayer((Player)commandSender, command, s, strings);
		} else {
			return onCommandConsole(commandSender, command, s, strings);
		}
	}
	
	public boolean onCommandConsole(CommandSender commandSender, Command command, String s, String[] strings) throws Exception {
		sendResponse(commandSender, "Sorry, this command can not be used from the console!", ChatColor.DARK_RED);
		return true;
	}

	public boolean onCommandPlayer(Player player, Command command, String s, String[] strings) throws Exception {
		sendResponse(player, "Sorry, this command can not be used by a player!", ChatColor.DARK_RED);
		return true;
	}

	public static void registerCommands() {
		List<Class<? extends BaseCommand>> commands = Utils.getSubClasses(BaseCommand.class);
		for(Class<? extends BaseCommand> command : commands) {
			registerCommand(command);
		}
	}

	private static void registerCommand(Class<? extends BaseCommand> commandClass) {
		try {
			Constructor<? extends BaseCommand> ctor = commandClass.getConstructor();
			BaseCommand command = ctor.newInstance();
			if(!commandClass.isAnnotationPresent(Name.class)) return;
			MultiBukkit.instance.getCommand(commandClass.getAnnotation(Name.class).value()).setExecutor(command);
		} catch(Exception e) { }
	}
	
	protected Player getPlayerSingle(String name) throws Exception {
		List<Player> ret = plugin.getServer().matchPlayer(name);
		if(ret == null || ret.isEmpty()) {
			throw new Exception("Sorry, no player found!");
		} else if(ret.size() > 1) {
			throw new Exception("Sorry, multiple players found!");
		}
		return ret.get(0);
	}

	protected void sendResponse(CommandSender cmd, String msg) {
		sendResponse(cmd, msg, ChatColor.DARK_PURPLE);
	}

	protected void sendResponse(final CommandSender cmd, final String msg, final ChatColor color) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				cmd.sendMessage(color + "[MB] " + ChatColor.WHITE + msg);
			}
		}, 0);
	}
}
