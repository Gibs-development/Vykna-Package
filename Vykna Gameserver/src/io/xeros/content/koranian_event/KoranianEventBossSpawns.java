package io.xeros.content.koranian_event;

import io.xeros.model.entity.player.Boundary;
import io.xeros.util.Misc;


/**
 * @author C.T, Koranes
 */

public enum KoranianEventBossSpawns {
		ROGUE_CASTLE(Boundary.KORANIAN_AREA, 3306, 3934, "Rogue's Castle @red@(52)."),

		;
	private KoranianEventBossSpawns(Boundary boundary, int x, int y, String locationName) {
		this.setX(x);
		this.setY(y);
		this.setLocationName(locationName);
		this.setBoundary(boundary);
	}
	

	private Boundary boundary;
	private int x, y;
	
	private String locationName;

	public static KoranianEventBossSpawns generateLocation() {
		return KoranianEventBossSpawns.values()[Misc.random(0, KoranianEventBossSpawns.values().length - 1)];
	}

	public Boundary getBoundary() {
		return boundary;
	}

	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	private static KoranianEventBossSpawns currentLocation;
	public static KoranianEventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

}