package io.xeros.content.revenant_event;

import io.xeros.model.entity.player.Boundary;
import io.xeros.util.Misc;


/**
 * @author C.T, RuneRogue
 */

public enum RevenantEventBossSpawns {
		REVENANT_CAVE(Boundary.REV_MALEDICTUS_AREA, 3136, 3843, "Rev Cave Entrance @red@(41)."),

		;
	private RevenantEventBossSpawns(Boundary boundary, int x, int y, String locationName) {
		this.setX(x);
		this.setY(y);
		this.setLocationName(locationName);
		this.setBoundary(boundary);
	}
	

	private Boundary boundary;
	private int x, y;
	
	private String locationName;

	public static RevenantEventBossSpawns generateLocation() {
		return RevenantEventBossSpawns.values()[Misc.random(0, RevenantEventBossSpawns.values().length - 1)];
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

	private static RevenantEventBossSpawns currentLocation;
	public static RevenantEventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

}