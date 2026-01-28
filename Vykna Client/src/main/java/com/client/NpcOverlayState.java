package com.client;

public final class NpcOverlayState {
	public int hpPercent = -1;
	public int weaknessId = -1;
	public int statusBitmask = 0;

	public void reset() {
		hpPercent = -1;
		weaknessId = -1;
		statusBitmask = 0;
	}
}
