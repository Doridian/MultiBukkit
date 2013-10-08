package de.doridian.multicraft.api;

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
