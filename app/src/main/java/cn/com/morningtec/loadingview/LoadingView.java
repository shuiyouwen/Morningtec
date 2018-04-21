package cn.com.morningtec.loadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static cn.com.morningtec.Utils.dp2px;

/**
 * Created by Shui on 2018/4/20.
 */

public class LoadingView extends View {
    private int mMaxCirDiameter;//最大圆直径
    private int mMinCirDiameter;//最小圆直径
    int mColorStart = Color.parseColor("#FFFF4081");
    int mColorMiddle = Color.parseColor("#FF80FF1F");
    int mColorEnd = Color.parseColor("#FF4060FF");

    private int mWidth;
    private int mHeight;
    private Paint mPaint1;
    private Paint mPaint2;
    private ColorCircle mColorCircle1;
    private ColorCircle mColorCircle2;


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

        mPaint1 = new Paint();
        mPaint1.setStyle(Paint.Style.FILL);

        mPaint2 = new Paint();
        mPaint2.setStyle(Paint.Style.FILL);
    }

    public void start() {
        if (mColorCircle1 != null) {
            mColorCircle1.startAnimator();
        }
        if (mColorCircle2 != null) {
            mColorCircle2.startAnimatorDelay(250);
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
        initColorCircle();
    }

    private void initColorCircle() {
        Point startPoint1 = new Point(mMaxCirDiameter, mMaxCirDiameter);
        Point middlePoint1 = new Point(mWidth / 2, mHeight / 2);
        Point endPoint1 = new Point(mWidth - mMaxCirDiameter, mHeight - mMaxCirDiameter);
        mColorCircle1 = new ColorCircle(this, mMaxCirDiameter, mMinCirDiameter
                , startPoint1, middlePoint1, endPoint1, mColorStart, mColorMiddle, mColorEnd);

        Point startPoint2 = new Point(mMinCirDiameter, mHeight - mMinCirDiameter);
        Point middlePoint2 = new Point(mWidth / 2, mHeight / 2);
        Point endPoint2 = new Point(mWidth - mMinCirDiameter, mMinCirDiameter);
        mColorCircle2 = new ColorCircle(this, mMinCirDiameter, mMaxCirDiameter
                , startPoint2, middlePoint2, endPoint2, mColorEnd, mColorMiddle, mColorStart);
    }

    private int getSize(int measureSpec) {
        int widthMode = MeasureSpec.getMode(measureSpec);
        int result;
        if (widthMode == MeasureSpec.AT_MOST) {
            result = dp2px(getContext(), 50);
        } else {
            result = MeasureSpec.getSize(measureSpec);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint1.setColor(mColorCircle1.getColor());
        canvas.drawCircle(mColorCircle1.getPoint().x, mColorCircle1.getPoint().y
                , mColorCircle1.getCirDiameter(), mPaint1);

        mPaint2.setColor(mColorCircle2.getColor());
        canvas.drawCircle(mColorCircle2.getPoint().x, mColorCircle2.getPoint().y
                , mColorCircle2.getCirDiameter(), mPaint2);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mColorCircle1 != null) {
            mColorCircle1.cancel();
        }
        if (mColorCircle2 != null) {
            mColorCircle2.cancel();
        }
        super.onDetachedFromWindow();
    }
}
