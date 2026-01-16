package io.xeros.content.world_boss_events;

import io.xeros.model.entity.player.Boundary;
import io.xeros.util.Misc;


/**
 * @author C.T, Koranes
 */

public enum EventBossSpawns {
		Wilderness(Boundary.Level_54_WILD, 3315, 3887, "in the wilderness Level 46"),
		
		;
	private EventBossSpawns(Boundary boundary, int x, int y, String locationName) {
		this.setX(x);
		this.setY(y);
		this.setLocationName(locationName);
		this.setBoundary(boundary);
	}
	

	private Boundary boundary;
	private int x, y;
	
	private String locationName;

	public static EventBossSpawns generateLocation() {
		return EventBossSpawns.values()[Misc.random(0, EventBossSpawns.values().length - 1)];
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

	private static EventBossSpawns currentLocation;
	public static EventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

}