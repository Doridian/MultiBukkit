package de.doridian.multicraft.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class MulticraftAPI {
	private String url;
	private String user;
	private String key;

	public MulticraftAPI(String url, String user, String key) {
		this.url = url;
		this.user = user;
		this.key = key;
	}

	public Object call(String method, String serverID, Map<String, String> params) {
		params = new HashMap<>(params);
		params.put("server_id", serverID);

		return call(method, params);
	}

	public Object call(String method, Map<String, String> params) {
		params = new HashMap<>(params);
		try {
			URL url = new URL(this.url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			params.put("_MulticraftAPIMethod", method);
			params.put("_MulticraftAPIUser", user);

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes());
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
