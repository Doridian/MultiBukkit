package de.doridian.multibukkit.api;

import de.doridian.multibukkit.MultiBukkit;
import de.doridian.multibukkit.util.Role;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class PlayerAPI {
	final MultiBukkit plugin;
	private final HashMap<String, Integer> playerLevels = new HashMap<String, Integer>();
	private final HashMap<String, Integer> playerIDs = new HashMap<String, Integer>();

	private final HashMap<Integer, HashMap<String, Boolean>> permissionsForLevels = new HashMap<Integer, HashMap<String, Boolean>>();

	public PlayerAPI(MultiBukkit plugin) {
		this.plugin = plugin;
		loadConfig();
	}

	public void loadConfig() {
		File permsFile = new File(plugin.getDataFolder(), "permissions.yml");
		if(!permsFile.exists()) {
			try {
				PrintStream stream = new PrintStream(new FileOutputStream(permsFile));
				stream.println("permissions:");
				stream.println("    '1':");
				stream.println("        'permissions.build': true");
				stream.println("    '50':");
				stream.println("        'multibukkit.level.set': true");
				stream.println("        'multibukkit.level.get': true");
				stream.println("        'multibukkit.admin': true");
				stream.close();
			}
			catch(Exception e) {
				plugin.log(Level.WARNING, "Could not create default permissions.yml");
			}
		}

		try {
			YamlConfiguration config = new YamlConfiguration();
			config.load(permsFile);
			ConfigurationSection mainSection = config.getConfigurationSection("permissions");
			for(String key : mainSection.getKeys(false)) {
				ConfigurationSection subSection = mainSection.getConfigurationSection(key);
				HashMap<String, Boolean> perms = new HashMap<String, Boolean>();
				for(Map.Entry<String, Object> setPerms : subSection.getValues(true).entrySet()) {
					Object value = setPerms.getValue();
					if(value instanceof Boolean) {
						perms.put(setPerms.getKey(), (Boolean)value);
					}
				}

				permissionsForLevels.put(Integer.parseInt(key), perms);
			}
		} catch(Exception e) {
			plugin.log(Level.WARNING, "Could not read permissions.yml");
			e.printStackTrace();
		}
	}
	
	private String transformName(Player ply) {
		return ply.getName().toLowerCase();
	}

	public void rebuildCaches() {
		new Thread() {
			@Override
			public void run() {
				synchronized(playerIDs) {
					playerIDs.clear();
				}
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
				int level = Integer.parseInt((String) ((JSONObject) ((JSONObject) plugin.apiCall("getPlayer", params)).get("Player")).get("level"));
				refreshLevel(player, level, nocache);
			} catch(Exception e) {
				refreshLevel(player, 1, nocache);
			}
		}

		synchronized(playerLevels) {
			return playerLevels.get(name);
		}
	}
	
	public void setLevel(Player player, String to) throws Exception {
		try {
			int level = Integer.parseInt(to);
			setLevel(player, level);
			return;
		} catch(Exception e) { }

		try {
			Role role = Role.getByName(to);
			setLevel(player, role);
			return;
		} catch(Exception e) { }

		throw new Exception("Invalid level or role specified!");
	}
	
	public void setLevel(Player player, Role role) throws Exception {
		setLevel(player, role.getLevel());
	}
	
	public void setLevel(Player player, int level) throws Exception {
		String playerID = getPlayerID(player);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", playerID);
		params.put("field", "\"level\"");
		params.put("value", "\"" + level + "\"");
		plugin.apiCall("updatePlayer", params);
		refreshLevel(player, level, false);
	}

	public String getPlayerID(Player player) throws Exception {
		String name = transformName(player);

		synchronized(playerIDs) {
			if(playerIDs.containsKey(name)) {
				return playerIDs.get(name).toString();
			}
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("field", "\"name\"");
		params.put("value", "\"" + name + "\"");
		JSONObject ret = (JSONObject)plugin.apiCall("findPlayers", params);
		ret = (JSONObject)ret.get("Players");
		for(Map.Entry<Object, Object> ent : (Set<Map.Entry<Object, Object>>)ret.entrySet()) {
			if(ent.getValue().toString().equalsIgnoreCase(name)) {
				String id =  ent.getKey().toString();
				synchronized(playerIDs) {
					playerIDs.put(name, Integer.parseInt(id));
				}
				return id;
			}
		}

		throw new Exception("Player not found");
	}
	
	private void refreshLevel(Player player, int newlevel, boolean nocache) {
		String name = transformName(player);
		synchronized(playerLevels) {
			if(!nocache && playerLevels.containsKey(name)) {
				if(newlevel == playerLevels.get(name)) return;
			}
			playerLevels.put(name, newlevel);
		}

		if(plugin.enableKick) {
			if(newlevel < 1) {
				player.kickPlayer("[MB] You are not authorized to be on this server");
				return;
			}
		}

		if(!plugin.enableGroups && !plugin.enablePermissions) return;
		
		PermissionAttachment attach = plugin.findOrCreatePermissionAttachmentFor(player);
		if(plugin.enablePermissions) {
			for(String str : attach.getPermissions().keySet()) {
				attach.unsetPermission(str);
			}

			for(int i = 0; i <= newlevel; i++) {
				if(!permissionsForLevels.containsKey(i)) continue;
				HashMap<String, Boolean> perms = permissionsForLevels.get(i);
				for(Map.Entry<String, Boolean> perm : perms.entrySet()) {
					attach.setPermission(perm.getKey(), perm.getValue());
				}
			}
		}

		if(plugin.enableGroups) {
			for(String str : attach.getPermissions().keySet()) {
				if(str.startsWith("multibukkit.level.") || str.startsWith("multibukkit.role.")) {
					attach.unsetPermission(str);
				}
			}
			attach.setPermission("multibukkit.level." + newlevel, true);

			Role newrole = Role.getByLevel(newlevel);
			if(newrole != null) {
				attach.setPermission("multibukkit.role." + newrole.name().toLowerCase(), true);
			}
		}
	}
}
