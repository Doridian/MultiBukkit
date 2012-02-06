package de.doridian.multibukkit.commands;

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
					commandSender.sendMessage("Player " + other.getName() + " has level " + plugin.playerAPI.getLevel(other));
				} catch(Exception e) {
					commandSender.sendMessage("Error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
		return true;
	}
}
