package com.example.dylan.randomnoel;

import android.os.Build;
import android.view.View;

public class Utils {

    private static final int SURFACE_COLOR = 0xFFFAF9F6;
    private static final int GROUP_TINT_PERCENT = 12;

    private Utils() {
        // Nothing to do
    }

    public static boolean isAtLeastUpsideDownCakeVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    }

    public static boolean isAtLeastTiramisuVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    /**
     * Keeps group identity visible without turning each participant card into a saturated color block.
     */
    public static int getTextColor(Integer coulValue, View row, int textColor) {
        if (coulValue != null) {
            row.setBackgroundColor(tintWithSurface(coulValue));
        }
        return textColor;
    }

    private static int tintWithSurface(int color) {
        int red = blendComponent((SURFACE_COLOR >> 16) & 0xFF, (color >> 16) & 0xFF);
        int green = blendComponent((SURFACE_COLOR >> 8) & 0xFF, (color >> 8) & 0xFF);
        int blue = blendComponent(SURFACE_COLOR & 0xFF, color & 0xFF);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    private static int blendComponent(int surfaceComponent, int groupComponent) {
        return (surfaceComponent * (100 - GROUP_TINT_PERCENT)
                + groupComponent * GROUP_TINT_PERCENT) / 100;
    }
}
