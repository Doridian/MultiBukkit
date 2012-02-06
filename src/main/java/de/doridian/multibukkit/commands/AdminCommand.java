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
			boolean reloadPermissions = (arg2.equalsIgnoreCase("all") || arg2.startsWith("perm"));
			boolean reloadConfig = (arg2.equalsIgnoreCase("all") || arg2.startsWith("conf"));

			if(reloadConfig) {
				plugin.reloadConfig();
				commandSender.sendMessage("Reloading config...");
			}

			if(reloadPermissions) {
				plugin.playerAPI.rebuildCaches();
				commandSender.sendMessage("Reloading permissions...");
			}
		}
		return true;
	}
}
