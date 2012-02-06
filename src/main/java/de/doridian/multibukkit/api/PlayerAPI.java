package de.doridian.multibukkit.api;

import de.doridian.multibukkit.MultiBukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerAPI {
	MultiBukkit plugin;
	public PlayerAPI(MultiBukkit plugin) {
		this.plugin = plugin;
	}
	
	private String transformName(Player ply) {
		return ply.getName().toLowerCase();
	}

	private HashMap<String, Integer> playerLevels = new HashMap<String, Integer>();
	public int getLevel(Player player) {
		return getLevel(player, false);
	}

	public int getLevel(Player player, boolean nocache) {
		String name = transformName(player);
		if(nocache || !playerLevels.containsKey(name)) {
			try {
				loadLevel(player);
			} catch(Exception e) {
				playerLevels.put(name, 1);
			}
		}

		return playerLevels.get(name);
	}

	private void loadLevel(Player player) throws Exception {
		String playerID = getPlayerID(player);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", playerID);
		playerLevels.put(transformName(player), Integer.parseInt((String)((JSONObject)((JSONObject)plugin.apiCall("getPlayer", params)).get("Player")).get("level")));
	}
	
	public void setLevel(Player player, int level) throws Exception {
		String playerID = getPlayerID(player);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", playerID);
		params.put("field", "\"level\"");
		params.put("value", "\"" + level + "\"");
		plugin.apiCall("updatePlayer", params);
		playerLevels.put(transformName(player), level);
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
