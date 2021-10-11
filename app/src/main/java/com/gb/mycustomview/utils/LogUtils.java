package com.gb.mycustomview.utils;

import android.util.Log;

/**
 * @author Gerkey
 * Created on 2021/9/23
 */
public class LogUtils {

    public static int currentLev = 4;
    public static final int DEBUG_LEV = 4;
    public static final int INFO_LEV = 3;
    public static final int WARNING_LEV = 2;
    public static final int ERROR_LEV = 1;


    public static void d(Object object, String log) {
        if (currentLev >= DEBUG_LEV) {
            Log.d(object.getClass().getSimpleName(), log);
        }
    }

    public static void i(Object object, String log) {
        if (currentLev > INFO_LEV) {
            Log.i(object.getClass().getSimpleName(), log);
        }
    }

    public static void w(Object object, String log) {
        if (currentLev > WARNING_LEV) {
            Log.w(object.getClass().getSimpleName(), log);
        }
    }

    public static void e(Object object ,String log) {
        if (currentLev > ERROR_LEV) {
            Log.e(object.getClass().getSimpleName(), log);
        }
    }
}

