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
