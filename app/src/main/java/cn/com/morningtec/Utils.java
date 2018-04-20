package cn.com.morningtec;

import android.content.Context;

/**
 * Created by Shui on 2018/4/20.
 */

public class Utils {
    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
