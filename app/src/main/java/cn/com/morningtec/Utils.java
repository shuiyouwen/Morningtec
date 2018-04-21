package cn.com.morningtec;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

/**
 * Created by Shui on 2018/4/20.
 */

public class Utils {
    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /**
     * 获取text文本的长宽
     *
     * @return
     */
    public static int[] getTextSize(Paint paint, String text) {
        int[] ints = new int[2];
        ints[0] = 0;
        ints[1] = 0;
        if (!TextUtils.isEmpty(text) && paint != null) {
            Rect rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
            ints[0] = rect.right - rect.left;
            ints[1] = rect.bottom - rect.top;
        }
        return ints;
    }
}
