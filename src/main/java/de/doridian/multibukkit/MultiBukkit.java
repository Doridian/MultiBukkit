package de.doridian.multibukkit;

import de.doridian.multibukkit.api.PlayerAPI;
import de.doridian.multibukkit.commands.BaseCommand;
import de.doridian.multibukkit.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
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
	
	private String apiURL;
	private String apiUser;
	private String apiKey;
	public String apiServerID;

	private MultiBukkitListener listener;
	public PlayerAPI playerAPI;

	public MultiBukkit() {
		super();
		instance = this;
	}

	public void loadConfig() {
		Configuration config = getConfiguration();

		config.load();
		apiURL = config.getString("api.url", "http://localhost/api.php");
		apiUser = config.getString("api.user", "admin");
		apiKey = config.getString("api.key", "CHANGEME");
		
		apiServerID = config.getString("api.serverid", "INVALID");
		if(apiServerID.equals("INVALID")) {
			try {
				File pwd = new File(".");
				Pattern pat = Pattern.compile("^(.*[\\/])?server([0-9]+)[\\/]?$", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pat.matcher(pwd.getCanonicalPath());
				if(matcher.matches()) {
					apiServerID = matcher.group(2);
					config.setProperty("api.serverid", apiServerID);
				}
			} catch(Exception e) { }
		}

		enablePermissions = config.getBoolean("feature.permissions", true);
		enableGroups = config.getBoolean("feature.groups", true);
		enableKick = config.getBoolean("feature.kick", true);

		config.save();
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

	protected HashMap<Player, PermissionAttachment> playerAttachments = new HashMap<Player, PermissionAttachment>();
	public PermissionAttachment findOrCreatePermissionAttachmentFor(Player player) {
		if(playerAttachments.containsKey(player)) {
			return playerAttachments.get(player);
		}
		for(PermissionAttachmentInfo info : player.getEffectivePermissions()) {
			try {
				if(info.getAttachment().getPlugin() == this) {
					return info.getAttachment();
				}
			} catch(Exception e) { }
		}
		PermissionAttachment attachment = player.addAttachment(this);
		playerAttachments.put(player, attachment);
		return attachment;
	}
	
	public Object apiCall(String method, Map<String, String> params) {
		try {
			URL url = new URL(apiURL);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			params.put("server_id", apiServerID);

			params.put("_MulticraftAPIMethod", method);
			params.put("_MulticraftAPIUser", apiUser);

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(apiKey.getBytes());
			for(String str : params.values()) {
				md.update(str.getBytes());
			}
			params.put("_MulticraftAPIKey", Utils.getHexString(md.digest()));

			boolean notfirst = false;
			for(Map.Entry<String, String> param : params.entrySet()) {
				if(notfirst) {
					writer.write('&');
				} else {
					notfirst = true;
				}
				writer.write(URLEncoder.encode(param.getKey()));
				writer.write('=');
				writer.write(URLEncoder.encode(param.getValue()));
			}
			writer.close();

			params.remove("_MulticraftAPIKey");
			params.remove("_MulticraftAPIUser");
			params.remove("_MulticraftAPIMethod");

			JSONObject result = (JSONObject)(new JSONParser()).parse(new InputStreamReader(conn.getInputStream()));
			if(!((Boolean)result.get("success"))) {
				JSONArray errors = (JSONArray)result.get("errors");
				StringBuilder exc = new StringBuilder();
				for(Object o : errors) {
					exc.append(o.toString());
					exc.append(", ");
				}
				throw new Exception(exc.toString());
			}
			return result.get("data");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
