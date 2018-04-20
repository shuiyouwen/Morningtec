package cn.com.morningtec;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Property;
import android.view.View;
import android.view.animation.Animation;

import cn.com.morningtec.evaluator.HsvEvaluator;
import cn.com.morningtec.evaluator.PointEvaluator;

import static android.animation.ValueAnimator.REVERSE;
import static cn.com.morningtec.Utils.dp2px;

/**
 * Created by Shui on 2018/4/20.
 */

public class LoadingView extends View {
    private int mMaxCirDiameter;//最大圆直径
    private int mMinCirDiameter;//最小圆直径
    int mColorStart1 = Color.parseColor("#FFFF4081");
    int mColorMiddle1 = Color.parseColor("#FF80FF1F");
    int mColorEnd1 = Color.parseColor("#FF4060FF");

    private int mWidth;
    private int mHeight;
    private Paint mPaint1;
    private AnimatorSet mAnimatorSet;
    private int mCirDiameter;
    private Point mPoint1 = new Point();
    private int mColor1;

    private static final Property<LoadingView, Integer> CIR_DIAMETER = new IntProperty<LoadingView>("mCirDiameter") {
        @Override
        public void setValue(LoadingView object, int value) {
            object.setCirDiameter(value);
        }

        @Override
        public Integer get(LoadingView object) {
            return object.getCirDiameter();
        }
    };

    private static final Property<LoadingView, Point> POINT1 = new Property<LoadingView, Point>(Point.class, "mPoint1") {
        @Override
        public void set(LoadingView object, Point value) {
            object.setPoint1(value);
        }

        @Override
        public Point get(LoadingView object) {
            return object.getPoint1();
        }
    };

    private static final Property<LoadingView, Integer> COLOR1 = new IntProperty<LoadingView>("mColor1") {
        @Override
        public void setValue(LoadingView object, int value) {
            object.setColor1(value);
        }

        @Override
        public Integer get(LoadingView object) {
            return object.getColor1();
        }
    };

    public int getCirDiameter() {
        return mCirDiameter;
    }


    public void setCirDiameter(int cirDiameter) {
        this.mCirDiameter = cirDiameter;
        invalidate();
    }

    public int getColor1() {
        return mColor1;
    }

    public void setColor1(int color1) {
        mColor1 = color1;
    }

    public Point getPoint1() {
        return mPoint1;
    }

    public void setPoint1(Point point1) {
        mPoint1 = point1;
        invalidate();
    }

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMaxCirDiameter = dp2px(getContext(), 10);
        mMinCirDiameter = dp2px(getContext(), 2);
        mCirDiameter = mMaxCirDiameter;
        mPoint1.x = mMaxCirDiameter;
        mPoint1.y = mMaxCirDiameter;
        mColor1 = mColorStart1;

        mPaint1 = new Paint();
        mPaint1.setStyle(Paint.Style.FILL);
        mAnimatorSet = new AnimatorSet();
    }

    public void start() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet.start();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getSize(widthMeasureSpec);
        int height = getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        initAnimator();
    }

    private void initAnimator() {
        ObjectAnimator diameterAnimator = ObjectAnimator.ofInt(this, CIR_DIAMETER, mMaxCirDiameter, mMinCirDiameter, mMaxCirDiameter);
        setAnimatorMode(diameterAnimator);

        Point pointStart1 = new Point(mMaxCirDiameter, mMaxCirDiameter);
        Point pointMiddle1 = new Point(mWidth / 2, mHeight / 2);
        Point pointEnd1 = new Point(mWidth - mMaxCirDiameter, mHeight - mMaxCirDiameter);
        ObjectAnimator point1Animator = ObjectAnimator.ofObject(this, POINT1, new PointEvaluator(), pointStart1, pointMiddle1, pointEnd1);
        setAnimatorMode(point1Animator);

        ObjectAnimator colorAnimator = ObjectAnimator.ofObject(this, COLOR1, new HsvEvaluator(), mColorStart1, mColorMiddle1, mColorEnd1);
        setAnimatorMode(colorAnimator);

        mAnimatorSet.setDuration(1000);
        mAnimatorSet.playTogether(diameterAnimator, point1Animator, colorAnimator);
    }

    private void setAnimatorMode(ObjectAnimator animator) {
        animator.setRepeatMode(REVERSE);
        animator.setRepeatCount(Animation.INFINITE);
    }

    private int getSize(int measureSpec) {
        int widthMode = MeasureSpec.getMode(measureSpec);
        int result;
        if (widthMode == MeasureSpec.AT_MOST) {
            result = dp2px(getContext(), 80);
        } else {
            result = MeasureSpec.getSize(measureSpec);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint1.setColor(mColor1);
        canvas.drawCircle(mPoint1.x, mPoint1.y, mCirDiameter, mPaint1);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        super.onDetachedFromWindow();
    }
}
