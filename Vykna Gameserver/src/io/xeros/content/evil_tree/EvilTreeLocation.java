package io.xeros.content.evil_tree;


/**
 *
 * Author C.T for koranse
 *
 *
 */

public class EvilTreeLocation {
	
	private static int x;
	
	private static int y;
	
	private String locationName;
	
	private String hint;
		
	public EvilTreeLocation(int x, int y, String locationName, String hint) {
		this.x = x;
		this.y = y;
		this.locationName = locationName;
		this.hint = hint;
	}

	public static int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public static int getY() {
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
	
	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
}
