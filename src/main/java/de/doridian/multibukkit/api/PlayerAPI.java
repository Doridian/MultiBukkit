package de.doridian.multibukkit.api;

import de.doridian.multibukkit.MultiBukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerAPI {
	MultiBukkit plugin;
	private final HashMap<String, Integer> playerLevels = new HashMap<String, Integer>();

	public PlayerAPI(MultiBukkit plugin) {
		this.plugin = plugin;
	}
	
	private String transformName(Player ply) {
		return ply.getName().toLowerCase();
	}

	public void rebuildCaches() {
		new Thread() {
			@Override
			public void run() {
				Set<String> players = ((HashMap<String, Integer>)playerLevels.clone()).keySet();
				for(String ply : players) {
					Player player = plugin.getServer().getPlayerExact(ply);
					if(player == null) {
						synchronized(playerLevels) {
							playerLevels.remove(ply);
						}
					} else {
						getLevel(player, true);
					}
				}
			}
		}.start();
	}

	public int getLevel(Player player) {
		return getLevel(player, false);
	}

	public int getLevel(Player player, boolean nocache) {
		String name = transformName(player);
		if(nocache || !playerLevels.containsKey(name)) {
			try {
				String playerID = getPlayerID(player);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("id", playerID);
				synchronized(playerLevels) {
					playerLevels.put(transformName(player), Integer.parseInt((String) ((JSONObject) ((JSONObject) plugin.apiCall("getPlayer", params)).get("Player")).get("level")));
				}
			} catch(Exception e) {
				synchronized(playerLevels) {
					playerLevels.put(name, 1);
				}
			}
		}

		synchronized(playerLevels) {
			return playerLevels.get(name);
		}
	}
	
	public void setLevel(Player player, int level) throws Exception {
		String playerID = getPlayerID(player);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", playerID);
		params.put("field", "\"level\"");
		params.put("value", "\"" + level + "\"");
		plugin.apiCall("updatePlayer", params);
		synchronized(playerLevels) {
			playerLevels.put(transformName(player), level);
		}
	}

	public String getPlayerID(Player player) throws Exception {
		String name = player.getName();

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("field", "\"name\"");
		params.put("value", "\"" + name + "\"");
		JSONObject ret = (JSONObject)plugin.apiCall("findPlayers", params);
		ret = (JSONObject)ret.get("Players");
		for(Map.Entry<Object, Object> ent : (Set<Map.Entry<Object, Object>>)ret.entrySet()) {
			if(ent.getValue().toString().equalsIgnoreCase(name)) {
				return ent.getKey().toString();
			}
		}

		throw new Exception("Player not found");
	}
}
