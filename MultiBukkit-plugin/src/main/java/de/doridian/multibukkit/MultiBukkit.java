package de.doridian.multibukkit;

import de.doridian.multibukkit.api.PlayerAPI;
import de.doridian.multibukkit.commands.BaseCommand;
import de.doridian.multicraft.api.MulticraftAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiBukkit extends JavaPlugin {
	public static MultiBukkit instance;

	public boolean enablePermissions;
	public boolean enableGroups;
	public boolean enableKick;

	public MulticraftAPI api;
	private String apiServerID;

	private MultiBukkitListener listener;
	public PlayerAPI playerAPI;

	public MultiBukkit() {
		super();
		instance = this;
	}

	public void loadConfig() {
		getDataFolder().mkdirs();

		try {
			File mainFile = new File(getDataFolder(), "config.yml");

			if(!mainFile.exists()) {
				PrintStream stream = new PrintStream(new FileOutputStream(mainFile));
				stream.println("api:");
				stream.println("    serverid: INVALID");
				stream.println("    user: admin");
				stream.println("    key: CHANGEME");
				stream.println("    url: http://localhost/api.php");
				stream.println("feature:");
				stream.println("    permissions: true");
				stream.println("    groups: true");
				stream.println("    kick: true");
				stream.close();
			}

			YamlConfiguration config = new YamlConfiguration();
			config.load(mainFile);

			apiServerID = config.getString("api.serverid", "INVALID");
			if(apiServerID.equals("INVALID")) {
				try {
					File pwd = new File(".");
					Pattern pat = Pattern.compile("^(.*[\\/])?server([0-9]+)[\\/]?$", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pat.matcher(pwd.getCanonicalPath());
					if(matcher.matches()) {
						apiServerID = matcher.group(2);
						config.set("api.serverid", apiServerID);
					}
				} catch(Exception ignored) { }
			}

			api = new MulticraftAPI(
					config.getString("api.url", "http://localhost/api.php"),
					config.getString("api.user", "admin"),
					config.getString("api.key", "CHANGEME")
			);

			enablePermissions = config.getBoolean("feature.permissions", true);
			enableGroups = config.getBoolean("feature.groups", true);
			enableKick = config.getBoolean("feature.kick", true);

			config.save(mainFile);
		} catch(Exception e) { e.printStackTrace(); }
	}

	@Override
	public void onDisable() {
		log("Plugin disabled!");
	}

	@Override
	public void onEnable() {
		loadConfig();
		listener = new MultiBukkitListener(this);
		playerAPI = new PlayerAPI(this);
		BaseCommand.registerCommands();
		log("Plugin enabled!");
	}

	public void log(String msg) {
		log(Level.INFO, msg);
	}

	public void log(Level level, String msg) {
		getLogger().log(level, msg);
	}

	protected HashMap<Player, PermissionAttachment> playerAttachments = new HashMap<>();
	public PermissionAttachment findOrCreatePermissionAttachmentFor(Player player) {
		return findOrCreatePermissionAttachmentFor(player, true);
	}

	public PermissionAttachment findOrCreatePermissionAttachmentFor(Player player, boolean create) {
		if(playerAttachments.containsKey(player)) {
			return playerAttachments.get(player);
		}

		for(PermissionAttachmentInfo info : player.getEffectivePermissions()) {
			try {
				if(info.getAttachment().getPlugin() == this) {
					playerAttachments.put(player, info.getAttachment());
					return info.getAttachment();
				}
			} catch(Exception ignored) { }
		}

		if(!create) return null;

		PermissionAttachment attachment = player.addAttachment(this);
		playerAttachments.put(player, attachment);
		return attachment;
	}

	public void deletePermissionAttachmentFor(Player player) {
		PermissionAttachment attachment = findOrCreatePermissionAttachmentFor(player, false);
		if(attachment != null) {
			player.removeAttachment(attachment);
		}
		playerAttachments.remove(player);
	}

	public Object apiCall(String method, Map<String, String> params) {
		return api.call(method, apiServerID, params);
	}
}
