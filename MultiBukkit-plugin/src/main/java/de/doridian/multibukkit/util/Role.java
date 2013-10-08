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
