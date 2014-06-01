/**
 * This file is part of Multicraft-api.
 *
 * Multicraft-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Multicraft-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Multicraft-api.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.multicraft.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
			for(Map.Entry<String, String> param : params.entrySet()) {
				writer.write(URLEncoder.encode(param.getKey()));
				writer.write('=');
				writer.write(URLEncoder.encode(param.getValue()));
                writer.write('&');

                md.update(param.getValue().getBytes());
			}

            writer.write("_MulticraftAPIKey=");
            writer.write(Utils.getHexString(md.digest()));

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
