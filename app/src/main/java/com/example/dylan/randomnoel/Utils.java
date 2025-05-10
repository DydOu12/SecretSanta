package com.example.dylan.randomnoel;

import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class Utils {

    private Utils() {
        // Nothing to do
    }

    public static boolean isAtLeastUpsideDownCakeVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    }

    public static boolean isAtLeastTiramisuVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    public static int getTextColor(Integer coulValue, View row, int textColor) {
        if(coulValue != null) {
            row.setBackgroundColor(coulValue);

            int brightness = (int) Math.round(
                    Color.red(coulValue) * 0.299 +
                            Color.green(coulValue) * 0.587 +
                            Color.blue(coulValue) * 0.114);

            if(brightness <= 125)
                textColor = Color.WHITE;
        }
        return textColor;
    }
}
