/**
 * This file is part of MultiBukkit-plugin.
 *
 * MultiBukkit-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MultiBukkit-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MultiBukkit-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.multibukkit.util;

import de.doridian.multibukkit.MultiBukkit;
import org.bukkit.entity.Player;

public enum Role {
	OWNER(50),
	ADMINISTRATOR(40),
	MODERATOR(30),
	USER(20),
	GUEST(10),
	DEFAULT(1),
	NO_ACCESS(0);

	int level = 1;
	String name = "";
	Role(int level) {
		this.level = level;
		name = Utils.ucWords(name().replace('_', ' '));
	}

	public static Role getByLevel(int level) {
		for(Role role : Role.values()) {
			if(role.level == level) return role;
		}
		return null;
	}

	public static Role getByName(String name) {
		return valueOf(name.toUpperCase().replace(' ','_'));
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	//Formatting methods
	public static String formatRoleLevel(Player player) {
		return formatRoleLevel(MultiBukkit.instance.playerAPI.getLevel(player));
	}
	
	public static String formatRoleLevel(Role role) {
		return formatRoleLevel(role, role.getLevel());
	}
	
	public static String formatRoleLevel(int level) {
		return formatRoleLevel(getByLevel(level), level);
	}
	
	protected static String formatRoleLevel(Role role, int level) {
		return ((role == null) ? "level " + level : "role \"" + role.getName() + "\" [level " + level + "]");
	}
}
