package cn.com.morningtec.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * 位置估值器
 * Created by Shui on 2018/4/20.
 */

public class PointEvaluator implements TypeEvaluator<Point> {
    private Point mPoint = new Point();

    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        float x = startValue.x + fraction * (endValue.x - startValue.x);
        float y = startValue.y + fraction * (endValue.y - startValue.y);
        mPoint.set((int) x, (int) y);
        return mPoint;
    }
}
