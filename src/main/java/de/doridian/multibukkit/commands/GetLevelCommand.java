package de.doridian.multibukkit.commands;

import de.doridian.multibukkit.util.Role;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BaseCommand.Name("getlevel")
public class GetLevelCommand extends BaseCommand {
	@Override
	public boolean onCommandAll(final CommandSender commandSender, Command command, String s, String[] strings) throws Exception {
		final Player other = getPlayerSingle(strings[0]);
		new Thread() {
			@Override
			public void run() {
				try {
					sendResponse(commandSender, "Player " + other.getName() + " has " + Role.formatRoleLevel(other));
				} catch(Exception e) {
					sendResponse(commandSender, "Error: " + e.getMessage(), ChatColor.DARK_RED);
					e.printStackTrace();
				}
			}
		}.start();
		return true;
	}
}
