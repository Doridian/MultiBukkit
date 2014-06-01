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

/**
 * Created with IntelliJ IDEA.
 * User: Besitzer
 * Date: 08.10.13
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
	public static String getHexString(byte[] binaryForm) {
		StringBuilder sb = new StringBuilder();
		for(byte b : binaryForm) {
			String str = Integer.toHexString(0xFF & b);
			if(str.length() == 1) sb.append('0');
			sb.append(str);
		}
		return sb.toString();
	}
}
