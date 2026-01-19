package com.client;

public final class UiSkin {
	public static final int RS3_BG_0 = 0x12100F;
	public static final int RS3_BG_1 = 0x181613;
	public static final int RS3_BG_2 = 0x1C1916;
	public static final int RS3_BG_3 = 0x231E1A;
	public static final int RS3_BORDER_0 = 0x2B2622;
	public static final int RS3_BORDER_1 = 0x3A332D;
	public static final int RS3_METAL_0 = 0x524637;
	public static final int RS3_METAL_1 = 0x887769;
	public static final int RS3_GOLD = 0x967843;
	public static final int RS3_GOLD_HOVER = 0xEEC043;
	public static final int RS3_RED = 0x620509;

	public static final int PANEL_ALPHA = 230;
	public static final int INSET_ALPHA = 240;
	public static final int TOOLTIP_ALPHA = 235;
	public static final int HIGHLIGHT_ALPHA = 70;

	private UiSkin() {
	}

	public static void drawRs3Panel(int x, int y, int width, int height, int alpha) {
		if (width <= 0 || height <= 0) {
			return;
		}
		DrawingArea.drawAlphaBox(x, y, width, height, RS3_BG_1, clampAlpha(alpha));
		drawBorder(x, y, width, height, RS3_BORDER_0);
		if (width > 2 && height > 2) {
			drawBorder(x + 1, y + 1, width - 2, height - 2, RS3_BORDER_1);
			DrawingArea.drawAlphaBox(x + 1, y + 1, width - 2, 1, RS3_METAL_1, HIGHLIGHT_ALPHA);
		}
	}

	public static void drawRs3Inset(int x, int y, int width, int height, int alpha) {
		if (width <= 0 || height <= 0) {
			return;
		}
		DrawingArea.drawAlphaBox(x, y, width, height, RS3_BG_2, clampAlpha(alpha));
		drawBorder(x, y, width, height, RS3_BORDER_1);
		if (width > 2 && height > 2) {
			DrawingArea.drawAlphaBox(x + 1, y + 1, width - 2, 1, RS3_METAL_0, HIGHLIGHT_ALPHA);
		}
	}

	public static void drawRs3Divider(int x, int y, int width, int alpha) {
		if (width <= 0) {
			return;
		}
		DrawingArea.drawAlphaBox(x, y, width, 1, RS3_BORDER_1, clampAlpha(alpha));
	}

	public static void drawRs3TooltipBg(int x, int y, int width, int height, int alpha) {
		if (width <= 0 || height <= 0) {
			return;
		}
		DrawingArea.drawAlphaBox(x, y, width, height, RS3_BG_0, clampAlpha(alpha));
		drawBorder(x, y, width, height, RS3_BORDER_0);
		if (width > 2 && height > 2) {
			drawBorder(x + 1, y + 1, width - 2, height - 2, RS3_BORDER_1);
			DrawingArea.drawAlphaBox(x + 1, y + 1, width - 2, 1, RS3_METAL_1, HIGHLIGHT_ALPHA);
		}
	}

	private static void drawBorder(int x, int y, int width, int height, int color) {
		if (width <= 0 || height <= 0) {
			return;
		}
		DrawingArea.drawPixels(1, y, x, color, width);
		DrawingArea.drawPixels(1, y + height - 1, x, color, width);
		DrawingArea.drawPixels(height, y, x, color, 1);
		DrawingArea.drawPixels(height, y, x + width - 1, color, 1);
	}

	private static int clampAlpha(int alpha) {
		if (alpha < 0) {
			return 0;
		}
		if (alpha > 256) {
			return 256;
		}
		return alpha;
	}
}
