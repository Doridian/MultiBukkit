package de.doridian.multibukkit.api;

import de.doridian.multibukkit.MultiBukkit;
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
