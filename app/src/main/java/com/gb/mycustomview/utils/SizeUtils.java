package com.gb.mycustomview.utils;

import android.content.Context;

/**
 * @author Gerkey
 * Created on 2021/9/22
 */
public class SizeUtils {

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

