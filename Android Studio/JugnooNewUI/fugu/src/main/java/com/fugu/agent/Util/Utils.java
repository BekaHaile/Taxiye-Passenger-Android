package com.fugu.agent.Util;

import android.content.Context;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class Utils {

    public static float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
