package de.doridian.multibukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BaseCommand.Name("list")
public class ListCommand extends BaseCommand {
	@Override
	public boolean onCommandConsole(CommandSender commandSender, Command command, String s, String[] strings) {
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		String str = "Connected players: ";
		if(players.length > 0) {
			str += players[0].getName();
			for(int i=1;i<players.length;i++) {
				str += ", " + players[i].getName();
			}
		}
		commandSender.sendMessage(str);
		return true;
	}
	
	@Override
	public boolean onCommandPlayer(Player player, Command commands, String s, String[] strings) {
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		String str = "";
		if(players.length > 0) {
			for(Player ply : players) {
				String name = ply.getName();
				String dispName = ply.getDisplayName();
				if(name.equals(dispName)) {
					str += ", " + ChatColor.GRAY + dispName + ChatColor.WHITE;
				} else {
					str += ", " + ChatColor.GRAY + dispName + ChatColor.WHITE + "[" + name + "]";
				}
			}
			str = str.substring(2).trim();
		}
		str = "Connected players: " + str;
		sendResponse(player, str);
		return true;
	}
}
