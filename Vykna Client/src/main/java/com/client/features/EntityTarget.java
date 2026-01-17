package com.client.features;

import com.client.Client;
import com.client.Configuration;
import com.client.DrawingArea;
import com.client.Entity;
import com.client.NPC;
import com.client.Player;

import java.util.Random;

public class EntityTarget {
	private final short entityIndex;
	public byte getState() { return state; }
	public short getEntityIndex() { return entityIndex; }

	private byte state;
	private final Client client = Client.getInstance();
	private Entity target;

	// --- Boss bar config ---
	private static final int BOSS_HP_THRESHOLD = 100;

	private static final int OPEN_TICKS = 14;         // open animation duration
	private static final int NAME_HOLD_TICKS = 30;    // show name fully
	private static final int NAME_FADE_TICKS = 25;    // fade duration

	// UI placement (tweak as desired)
	private static final int BOSS_X = 6;
	private static final int BOSS_BASE_Y = 22;

	// Fancy bar sizing
	private static final int BOSS_W = 220;
	private static final int BOSS_H = 42;

	// Runtime UI state
	private int lifeTicks = 0;        // increments each draw tick while active
	private int lastHealth = -1;
	private int lastMaxHealth = -1;
	private String cachedName = "Unknown";

	private final Random rng = new Random(0xB055); // deterministic-ish visuals

	public EntityTarget(byte state, short entityIndex, short currentHealth, short maximumHealth) {
		this.state = state;
		this.entityIndex = entityIndex; // ALWAYS assign

		if (state <= 0 || state >= 3) {
			return;
		}

		try {
			if (state == 1) {
				target = client.npcs[entityIndex];
			} else {
				target = client.players[entityIndex];
			}
		} catch (Exception e) {
			System.err.println("Error when setting target index: " + entityIndex);
			e.printStackTrace();
			state = 0;
			target = null;
			return;
		}

		if (target != null) {
			target.currentHealth = currentHealth;
			target.maxHealth = maximumHealth;
			refreshCachedName();
			resetUiStateIfNeeded(currentHealth, maximumHealth);
		} else {
			// If target slot is empty, disable so draw() doesn’t try to render
			state = 0;
		}
	}

	/**
	 * Call this when you receive an update packet for current target health/max health.
	 * If you already reconstruct EntityTarget each update, you can ignore this and keep constructor only.
	 */
	public void update(short currentHealth, short maximumHealth) {
		if (target == null) return;
		target.currentHealth = currentHealth;
		target.maxHealth = maximumHealth;
		refreshCachedName();
	}


	private void resetUiStateIfNeeded(int currentHealth, int maximumHealth) {
		// If we switch targets or max hp changes radically, restart animation
		if (lastMaxHealth != maximumHealth || lastHealth == -1) {
			lifeTicks = 0;
		}
		lastHealth = currentHealth;
		lastMaxHealth = maximumHealth;
	}

	private void refreshCachedName() {
		String name = "Unknown";

		if (state == 1 && target instanceof NPC) {
			NPC npc = (NPC) target;
			if (npc.desc != null && npc.desc.name != null) {
				name = npc.desc.name;
			}
		} else if (state == 2 && target instanceof Player) {
			name = ((Player) target).displayName;
		}

		cachedName = name;
	}

	private boolean isBoss() {
		// Simple heuristic. Later you can refine:
		// - npc.desc.combatLevel high
		// - npc.desc.name contains certain strings
		// - npc index/id list
		return target != null && target.maxHealth >= BOSS_HP_THRESHOLD && state == 1;
	}

	public void draw() {

		if (target == null) System.out.println("EntityTarget: target null");
		if (state <= 0 || state > 2) System.out.println("EntityTarget: bad state=" + state);
		if (target != null && target.maxHealth <= 0) System.out.println("EntityTarget: maxHealth=" + target.maxHealth);
		if (state <= 0 || state > 2 || target == null) {
			return;
		}

		int x = Client.instance.getLocalPlayerX();
		int y = Client.instance.getLocalPlayerY();

		// Nightmare area skip (your original)
		if (x >= 3862 && y >= 9940 && x <= 3883 && y <= 9961) {
			return;
		}

		// offsets (your original XP counter interaction)
		int offset = 4;
		if (Client.counterOn && Configuration.xpPosition == 2) offset = 31;

		if (isBoss()) {
			drawBossBar(offset);
		} else {
			drawNormalBar(offset);
		}

		lifeTicks++; // tick forward while active
	}

	private void drawNormalBar(int offset) {
		String name = cachedName;

		int width = 134;
		int xPos = 6;
		int yPos = 22 + offset;

		DrawingArea.drawBoxOutline(xPos, yPos, width - 3, 34, 0x393022);
		DrawingArea.drawAlphaBox(xPos, yPos, width - 3, 33, 0x60574E, 110);

		Client.latoBold.drawCenteredString(name, xPos + (width / 2) - 2, yPos + 18, 0xFFFFFF, 0x000000);

		int barWidth = 124;

		// Guard divide by zero
		int maxHp = Math.max(1, target.maxHealth);
		int fill = target.currentHealth * barWidth / maxHp;
		if (fill < 0) fill = 0;
		if (fill > barWidth) fill = barWidth;

		DrawingArea.drawAlphaBox(xPos + 3, yPos + 18, width - 9, 13, 0xB30000, 160);
		DrawingArea.drawAlphaBox(xPos + 3, yPos + 18, fill, 13, 0x00A900, 160);

		Client.latoBold.drawCenteredString(
				target.currentHealth + " / " + target.maxHealth,
				xPos + (width / 2) - 2,
				yPos + 33,
				0xFFFFFF,
				0x000000
		);
	}

	private void drawBossBar(int offset) {
		int xPos = BOSS_X;
		int yPos = BOSS_BASE_Y + offset;

		int cx = xPos + (BOSS_W / 2);

		// Outer frame is ALWAYS full width so it never "disappears"
		int frameX = xPos;
		int frameW = BOSS_W;
		int h = BOSS_H;

		DrawingArea.drawBoxOutline(frameX, (yPos), frameW, h, 0x1A1A1A);
		DrawingArea.drawAlphaBox(frameX + 1, yPos + 1, frameW - 2, h - 2, 0x2B2B2B, 160);

		int innerPad = 6;
		int innerX = frameX + innerPad;
		int innerY = yPos + 18;
		int innerW = frameW - innerPad * 2;
		int innerH = 14;

		// Open animation only affects inner panel width
		float t = Math.min(1f, (lifeTicks + 1) / (float) OPEN_TICKS);
		t = 1f - (1f - t) * (1f - t); // ease-out

		int openInnerW = Math.max(1, (int) (innerW * t));

		// Background bar (only up to open width)
		DrawingArea.drawAlphaBox(innerX, innerY, openInnerW, innerH, 0x550000, 180);

		int maxHp = Math.max(1, target.maxHealth);
		float hpRatio = target.currentHealth / (float) maxHp;
		if (hpRatio < 0f) hpRatio = 0f;
		if (hpRatio > 1f) hpRatio = 1f;

		int fillW = (int) (openInnerW * hpRatio);
		DrawingArea.drawAlphaBox(innerX, innerY, fillW, innerH, 0x00A900, 190);

		// Shine strip
		DrawingArea.drawAlphaBox(innerX, innerY, openInnerW, 4, 0xFFFFFF, 25);

		// Sparks only while opening
		if (lifeTicks < OPEN_TICKS) {
			drawOpeningEdgePixels(innerX, innerY - 12, openInnerW, innerH + 24, lifeTicks);
		}

		int nameAlpha = computeNameAlpha();
		if (nameAlpha > 40) {
			Client.latoBold.drawCenteredString(cachedName, cx, yPos + 14, 0xFFFFFF, 0x000000);
		}

		Client.latoBold.drawCenteredString(
				target.currentHealth + " / " + target.maxHealth,
				cx,
				yPos + 38,
				0xFFFFFF,
				0x000000
		);
	}


	private int computeNameAlpha() {
		// 0..255
		if (lifeTicks < OPEN_TICKS) return 255;

		int afterOpen = lifeTicks - OPEN_TICKS;
		if (afterOpen < NAME_HOLD_TICKS) return 255;

		int fadeT = afterOpen - NAME_HOLD_TICKS;
		if (fadeT >= NAME_FADE_TICKS) return 0;

		float k = 1f - (fadeT / (float) NAME_FADE_TICKS);
		return (int) (255 * k);
	}

	private void drawOpeningEdgePixels(int drawX, int yPos, int w, int h, int tick) {
		// We’ll emit a few 1x1 / 2x1 “pixels” near left/right edge.
		// Density increases early, then decreases.
		int bursts = 10 - (tick * 10 / Math.max(1, OPEN_TICKS));
		if (bursts < 2) bursts = 2;

		int leftX = drawX;
		int rightX = drawX + w - 1;
		int top = yPos;
		int bottom = yPos + h - 1;

		for (int i = 0; i < bursts; i++) {
			int yy = top + 2 + rng.nextInt(Math.max(1, h - 6));
			int dx = 2 + rng.nextInt(10);

			// left sparks
			DrawingArea.drawAlphaBox(leftX - dx, yy, 2, 1, 0xFFFFFF, 90);
			// right sparks
			DrawingArea.drawAlphaBox(rightX + dx, yy, 2, 1, 0xFFFFFF, 90);

			// a couple darker pixels for depth
			if ((i & 1) == 0) {
				DrawingArea.drawAlphaBox(leftX - (dx + 2), yy + 1, 1, 1, 0x000000, 60);
				DrawingArea.drawAlphaBox(rightX + (dx + 2), yy - 1, 1, 1, 0x000000, 60);
			}
		}
	}

	public void stop() {
		state = 0;
		target = null;
		lifeTicks = 0;
		lastHealth = -1;
		lastMaxHealth = -1;
		cachedName = "Unknown";
	}
}
