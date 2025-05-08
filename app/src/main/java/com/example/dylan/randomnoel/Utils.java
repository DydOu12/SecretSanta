package com.example.dylan.randomnoel;

import android.os.Build;

public class Utils {
    public static boolean isAtLeastUpsideDownCakeVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    }
}
