package de.doridian.multibukkit;

import de.doridian.multibukkit.api.PlayerAPI;
import de.doridian.multibukkit.commands.BaseCommand;
import de.doridian.multibukkit.util.Utils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.logging.Level;

public class MultiBukkit extends JavaPlugin {
	public static MultiBukkit instance;

	public boolean enablePermissions;
	public boolean enableGroups;
	
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
		apiURL = config.getString("api.url", "http://localhost/api.php");
		apiUser = config.getString("api.user", "admin");
		apiKey = config.getString("api.key", "CHANGEME");
		apiServerID = config.getString("api.serverid", "1");

		enablePermissions = config.getBoolean("feature.permissions", true);
		enableGroups = config.getBoolean("feature.groups", true);
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
