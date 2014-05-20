/**
 * This file is part of multicraft-cli.
 *
 * multicraft-cli is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * multicraft-cli is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with multicraft-cli.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.multicraft.cli;

import de.doridian.multicraft.api.MulticraftAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLI {
	private static final Pattern URL_PATTERN = Pattern.compile("^([^:]+://)([^:]*):([^:]*)@(.*)$");
	private static final Pattern PARAM_PATTERN = Pattern.compile("^([^=]+)=(.*)$");
	public static final int MIN_ARGS = 2;

	public static void main(String[] args) {
		if (args.length < MIN_ARGS) {
			System.err.println("Error: Not enough arguments");
			return;
		}

		final Matcher urlMatcher = URL_PATTERN.matcher(args[0]);
		if (!urlMatcher.matches()) {
			System.err.println("Error: Malformed endpoint URL");
			return;
		}

		final String endpointURL = urlMatcher.group(1) + urlMatcher.group(4);
		final String apiUser = urlMatcher.group(2);
		final String apiKey = urlMatcher.group(3);

		final MulticraftAPI api = new MulticraftAPI(endpointURL, apiUser, apiKey);

		final String method = args[1];

		final Map<String,String> params = new HashMap<>(args.length - MIN_ARGS);
		for (int i = MIN_ARGS; i < args.length; i++) {
			final Matcher paramMatcher = PARAM_PATTERN.matcher(args[i]);
			if (!paramMatcher.matches()) {
				System.err.println("Error: Malformed parameter");
				return;
			}

			final String key = paramMatcher.group(1);
			final String value = paramMatcher.group(2);

			params.put(key, value);
		}

		final Object ret = api.call(method, params);
		System.out.println(ret);
	}
}
