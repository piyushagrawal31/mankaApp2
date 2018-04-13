package com.pstech.ramayanmanka108;

import android.os.Build;

/**
 * Created by pagrawal on 18-01-2018.
 */

public class AppUtils {
    public static final String ADMOB_ID = "ca-app-pub-4403433540523787~7599913060";

    public static String getMilliToMinuteSecRep(int millis) {
        if (millis <= 0)
            return "00:00";

        long seconds = millis / 1000;
        long minutes = seconds/60;
        seconds = seconds % 60;
        return (minutes < 10 ? "0" : "") + minutes + ":"+ (seconds < 10 ? "0" : "") + seconds;
    }

    public static String getSharableText(String shareTxt, String packageName) {
        return shareTxt + packageName;
    }

    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

}
