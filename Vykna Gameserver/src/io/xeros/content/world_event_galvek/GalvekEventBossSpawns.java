package io.xeros.content.world_event_galvek;

import io.xeros.model.entity.player.Boundary;
import io.xeros.util.Misc;


/**
 * @author C.T.
 *
 * Handles the galvek event spawning.
 */

public enum GalvekEventBossSpawns {
		secretlocation(Boundary.GALVEK_AREA, 2980, 3954, "at the Ice Plateau!"),

		;
	private GalvekEventBossSpawns(Boundary boundary, int x, int y, String locationName) {
		this.setX(x);
		this.setY(y);
		this.setLocationName(locationName);
		this.setBoundary(boundary);
	}
	

	private Boundary boundary;
	private int x, y;
	
	private String locationName;

	public static GalvekEventBossSpawns generateLocation() {
		return GalvekEventBossSpawns.values()[Misc.random(0, GalvekEventBossSpawns.values().length - 1)];
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

	private static GalvekEventBossSpawns currentLocation;
	public static GalvekEventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

}