package io.xeros.content.world_event_solak;

import io.xeros.model.entity.player.Boundary;
import io.xeros.util.Misc;


/**
 * @author C.T, Koranes
 */

public enum SolakEventBossSpawns {
		secretlocation(Boundary.SOLAK_AREA, 1718, 3882, "in an secret location"),

		;
	private SolakEventBossSpawns(Boundary boundary, int x, int y, String locationName) {
		this.setX(x);
		this.setY(y);
		this.setLocationName(locationName);
		this.setBoundary(boundary);
	}
	

	private Boundary boundary;
	private int x, y;
	
	private String locationName;

	public static SolakEventBossSpawns generateLocation() {
		return SolakEventBossSpawns.values()[Misc.random(0, SolakEventBossSpawns.values().length - 1)];
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

	private static SolakEventBossSpawns currentLocation;
	public static SolakEventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

}