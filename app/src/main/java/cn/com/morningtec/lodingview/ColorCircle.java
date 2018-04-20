package cn.com.morningtec.lodingview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.util.IntProperty;
import android.util.Property;
import android.view.animation.Animation;

import cn.com.morningtec.evaluator.HsvEvaluator;
import cn.com.morningtec.evaluator.PointEvaluator;

import static android.animation.ValueAnimator.REVERSE;

/**
 * 封装属性动画
 * Created by Shui on 2018/4/20.
 */
class ColorCircle {

    private final int mStartAndEndDiameter;
    private final int mMiddelDiameter;
    private final Point mStartPoint;
    private final Point mMiddlePoint;
    private final Point mEndPoint;
    private final int mStartColor;
    private final int mMiddelColor;
    private final int mEndColor;
    private final LoadingView mLoadingView;
    private int mCirDiameter;
    private int mColor;
    private Point mPoint = new Point();
    private AnimatorSet mAnimatorSet;

    ColorCircle(LoadingView loadingView, int startAndEndDiameter, int middelDiameter, Point startPoint, Point middlePoint, Point endPoint
            , int startColor, int middelColor, int endColor) {
        mStartAndEndDiameter = startAndEndDiameter;
        mMiddelDiameter = middelDiameter;
        mStartPoint = startPoint;
        mMiddlePoint = middlePoint;
        mEndPoint = endPoint;
        mStartColor = startColor;
        mMiddelColor = middelColor;
        mEndColor = endColor;
        mLoadingView = loadingView;

        mCirDiameter = startAndEndDiameter;
        mPoint = startPoint;
        mColor = mStartColor;

        initAnimator();
    }

    private static final Property<ColorCircle, Integer> CIR_DIAMETER = new IntProperty<ColorCircle>("mCirDiameter") {
        @Override
        public void setValue(ColorCircle object, int value) {
            object.setCirDiameter(value);
        }

        @Override
        public Integer get(ColorCircle object) {
            return object.getCirDiameter();
        }
    };

    private static final Property<ColorCircle, Point> POINT1 = new Property<ColorCircle, Point>(Point.class, "mPoint1") {
        @Override
        public void set(ColorCircle object, Point value) {
            object.setPoint(value);
        }

        @Override
        public Point get(ColorCircle object) {
            return object.getPoint();
        }
    };

    private static final Property<ColorCircle, Integer> COLOR1 = new IntProperty<ColorCircle>("mColor1") {
        @Override
        public void setValue(ColorCircle object, int value) {
            object.setColor(value);
        }

        @Override
        public Integer get(ColorCircle object) {
            return object.getColor();
        }
    };

    public int getCirDiameter() {
        return mCirDiameter;
    }


    public void setCirDiameter(int cirDiameter) {
        this.mCirDiameter = cirDiameter;
        mLoadingView.invalidate();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public Point getPoint() {
        return mPoint;
    }

    public void setPoint(Point point) {
        mPoint = point;
    }

    private void initAnimator() {
        ObjectAnimator diameterAnimator = ObjectAnimator.ofInt(this, CIR_DIAMETER, mStartAndEndDiameter, mMiddelDiameter, mStartAndEndDiameter);
        setAnimatorMode(diameterAnimator);

        ObjectAnimator point1Animator = ObjectAnimator.ofObject(this, POINT1, new PointEvaluator(), mStartPoint, mMiddlePoint, mEndPoint);
        setAnimatorMode(point1Animator);

        ObjectAnimator colorAnimator = ObjectAnimator.ofObject(this, COLOR1, new HsvEvaluator(), mStartColor, mMiddelColor, mEndColor);
        setAnimatorMode(colorAnimator);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(500);
        mAnimatorSet.playTogether(diameterAnimator, point1Animator, colorAnimator);
    }

    private void setAnimatorMode(ObjectAnimator animator) {
        animator.setRepeatMode(REVERSE);
        animator.setRepeatCount(Animation.INFINITE);
    }

    void startAnimator() {
        if (mAnimatorSet != null && !mAnimatorSet.isRunning()) {
            mAnimatorSet.start();
        }
    }

    void startAnimatorDelay(long delay) {
        if (mAnimatorSet != null && !mAnimatorSet.isRunning()) {
            mAnimatorSet.setStartDelay(delay);
            mAnimatorSet.start();
        }
    }

    void cancel() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }
}
