package cn.com.morningtec.fontsizeselector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import cn.com.morningtec.evaluator.PointEvaluator;

import static cn.com.morningtec.Utils.dp2px;
import static cn.com.morningtec.Utils.getTextSize;

/**
 * 字体大小选择view
 * Created by Shui on 2018/4/21.
 */

public class FontSizeSelectorView extends View {

    private int mScaleVerticalLen;//刻度的垂直长度
    private int mScaleStrokeWidth;//刻度线条粗度
    private int mTextPadding;//文字距离刻度的距离
    private int mBottomTextSize;//底部文字大小
    private int mRadius;//圆的直径
    private int mPaddingLeftAndRight;//距离左右两边的距离
    private int mSelect = 0;//选中的项
    private float mTouchFactor = 2f;//触摸系数，参数越大触摸区域越大

    private int mTapTimeout;
    private OnFontSelectListener mOnFontSelectListener;
    private int mWidth;
    private int mHeight;
    private Font[] mFonts;
    private Paint mScalePaint;//刻度
    private int mScaleUnitLength;//刻度单位长度
    private int[] mScaleX;//刻度x坐标
    private Point[] mTopTextPoins;
    private Point[] mBottomTextPoins;
    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Point mCircleCenterPoint;//圆心坐标
    private Rect mTrackRect = new Rect();
    private int mTouchSlop;//最小滑动距离
    private boolean mSelectUpdate = false;//选中项是否改变

    private final static Property<FontSizeSelectorView, Point> CIRCLE_CENTER_POINT = new Property<FontSizeSelectorView, Point>(Point.class, "mCircleCenterPoint") {
        @Override
        public Point get(FontSizeSelectorView object) {
            return object.getCircleCenterPoint();
        }

        @Override
        public void set(FontSizeSelectorView object, Point value) {
            object.setCircleCenterPoint(value);
        }
    };

    public FontSizeSelectorView(Context context) {
        super(context);
        init();
    }

    public FontSizeSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontSizeSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Point getCircleCenterPoint() {
        return mCircleCenterPoint;
    }

    public void setCircleCenterPoint(Point circleCenterPoint) {
        mCircleCenterPoint = circleCenterPoint;
        invalidate();
    }

    private void init() {
        setClickable(true);
        mScaleStrokeWidth = dp2px(getContext(), 2);
        mScaleVerticalLen = dp2px(getContext(), 20);
        mTextPadding = dp2px(getContext(), 20);
        mBottomTextSize = dp2px(getContext(), 14);
        mRadius = dp2px(getContext(), 10);
        mPaddingLeftAndRight = dp2px(getContext(), 25);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mTapTimeout = ViewConfiguration.getTapTimeout();

        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStyle(Paint.Style.FILL);
        mScalePaint.setStrokeWidth(mScaleStrokeWidth);
        mScalePaint.setColor(Color.parseColor("#ededed"));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setShadowLayer(dp2px(getContext(), 1), -dp2px(getContext(), 1)
                , dp2px(getContext(), 1), Color.parseColor("#bababa"));
        setLayerType(View.LAYER_TYPE_SOFTWARE, mCirclePaint);
    }

    public void setFonts(final Font... fonts) {
        mFonts = fonts;
        if (mFonts != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mTrackRect.set(mPaddingLeftAndRight, 0, mWidth - mPaddingLeftAndRight, mHeight);
                    mScaleUnitLength = (mWidth - mScaleStrokeWidth - mPaddingLeftAndRight * 2) / (mFonts.length - 1);

                    mScaleX = new int[mFonts.length];
                    mBottomTextPoins = new Point[mFonts.length];
                    mTopTextPoins = new Point[mFonts.length];

                    int scaleX = mScaleStrokeWidth / 2 + mPaddingLeftAndRight;
                    for (int i = 0; i < mFonts.length; i++) {
                        mScaleX[i] = scaleX;

                        //底部text坐标计算
                        mTextPaint.setTextSize(mBottomTextSize);
                        int[] bottomTextSize = getTextSize(mTextPaint, mFonts[i].getBottomText().getText());
                        int bottomY = mHeight / 2 + bottomTextSize[1] + mTextPadding;
                        if (i == 0) {
                            mBottomTextPoins[i] = new Point(scaleX, bottomY);
                        } else if (i == mFonts.length - 1) {
                            mBottomTextPoins[i] = new Point(scaleX - bottomTextSize[0], bottomY);
                        } else {
                            mBottomTextPoins[i] = new Point(scaleX - bottomTextSize[0] / 2, bottomY);
                        }

                        //顶部text坐标计算
                        mTextPaint.setTextSize(dp2px(getContext(), mFonts[i].getSize()));
                        int[] topTextSize = getTextSize(mTextPaint, mFonts[i].getTopText().getText());
                        int topY = mHeight / 2 - bottomTextSize[1] - mTextPadding;
                        if (i == 0) {
                            mTopTextPoins[i] = new Point(scaleX, topY);
                        } else if (i == mFonts.length - 1) {
                            mTopTextPoins[i] = new Point(scaleX - topTextSize[0], topY);
                        } else {
                            mTopTextPoins[i] = new Point(scaleX - topTextSize[0] / 2, topY);
                        }

                        scaleX += mScaleUnitLength;
                    }

                    mCircleCenterPoint = new Point(mScaleX[mSelect], mHeight / 2);
                }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
    }

    boolean mIsClick = false;
    boolean mIsDrag = false;
    int mActivePointerId;
    private int mLastX;
    private int mLastY;
    private int mDx;
    private int mDy;
    private long mDownTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getX();
                mLastY = (int) event.getY();
                mDownTime = System.currentTimeMillis();
                mActivePointerId = event.getPointerId(0);
                mIsClick = true;
                mIsDrag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mIsClick = isClickEvent(event);

                if (!mIsClick && (isThumbTouched(event) || mIsDrag) && isInTrack(event)) {
                    //触摸到圆形区域
                    mIsDrag = true;
                    drag(event);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if ((!mIsDrag && mIsClick) || (mIsDrag && !mIsClick)) {
                    //点击,或者拖拽，自动跳转到相应的节点
                    Point point = getNearNodePoint(event.getX());
                    if (point != null) {
                        circleTransactionAnim(point);
                    }
                }

                if (mIsClick) {
                    performClick();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断是否是点击事件
     *
     * @param event
     */
    private boolean isClickEvent(MotionEvent event) {
        int dx = (int) (event.getX() - mLastX);
        int dy = (int) (event.getY() - mLastY);
        mDx = mLastX + Math.abs(dx);
        mDy = mLastY + Math.abs(dy);

        if (System.currentTimeMillis() - mDownTime >= mTapTimeout) {
            if (mDx >= mTouchSlop || mDy >= mTouchSlop) {
                return false;
            }
        }

        mLastX = (int) event.getX();
        mLastY = (int) event.getY();

        return true;
    }

    /**
     * 圆执行动画
     *
     * @param targetPoint
     */
    private void circleTransactionAnim(Point targetPoint) {
        ObjectAnimator mObjectAnimator = ObjectAnimator.ofObject(this, CIRCLE_CENTER_POINT, new PointEvaluator()
                , mCircleCenterPoint, targetPoint);
        mObjectAnimator.setDuration(200);
        mObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnFontSelectListener != null && (mFonts != null && mFonts.length >= 0)
                        && (mSelect >= 0 && mSelect < mFonts.length) && mSelectUpdate) {
                    mOnFontSelectListener.onFontSelect(mFonts[mSelect]);
                }
            }
        });
        mObjectAnimator.start();
    }

    /**
     * 获取指定坐标附近的节点
     *
     * @return
     */
    private Point getNearNodePoint(float x) {
        Point point = new Point();
        int dx = Integer.MAX_VALUE;
        int select = 0;
        for (int i = 0; i < mScaleX.length; i++) {
            if (Math.abs(x - mScaleX[i]) < dx) {
                point.set(mScaleX[i], mHeight / 2);
                dx = (int) Math.abs(x - mScaleX[i]);
                select = i;
            }
        }

        mSelectUpdate = (mSelect != select);
        mSelect = select;
        return point;
    }

    /**
     * 是否在轨迹上
     *
     * @param event
     * @return
     */
    private boolean isInTrack(MotionEvent event) {
        int pointerIndex = event.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1) {
            return false;
        }
        return mTrackRect.contains((int) event.getX(pointerIndex), (int) event.getY(pointerIndex));
    }

    /**
     * 是否触摸着滑块
     *
     * @param event
     * @return
     */
    private boolean isThumbTouched(MotionEvent event) {
        int pointerIndex = event.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1) {
            return false;
        }
        double num = Math.pow(event.getX(pointerIndex) - mCircleCenterPoint.x, 2) + Math.pow(event.getY(pointerIndex) - mCircleCenterPoint.y, 2);
        return num <= Math.pow(mRadius * mTouchFactor, 2);
    }

    /**
     * 拖拽
     *
     * @param event
     */
    private void drag(MotionEvent event) {
        int pointerIndex = event.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1) {
            return;
        }
        float x = event.getX(pointerIndex);
        mCircleCenterPoint.x = (int) x;
        invalidate();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        ss.select = mSelect;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mSelect = ss.select;
        invalidate();
    }


    static class SavedState extends android.support.v4.view.AbsSavedState {
        int select;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            select = (Integer) source.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeValue(select);
        }

        @Override
        public String toString() {
            return "SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " isIconified=" + select + "}";
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(mPaddingLeftAndRight, mHeight / 2, mWidth - mPaddingLeftAndRight, mHeight / 2, mScalePaint);

        if (mFonts == null || mScaleX == null) {
            return;
        }

        for (int i = 0; i < mFonts.length; i++) {
            //绘制刻度
            canvas.drawLine(mScaleX[i], mHeight / 2 - mScaleVerticalLen / 2, mScaleX[i], mHeight / 2 + mScaleVerticalLen / 2, mScalePaint);

            Font.Text bottomText = mFonts[i].getBottomText();
            Font.Text topText = mFonts[i].getTopText();
            //绘制底部Text
            mTextPaint.setTextSize(dp2px(getContext(), 14));
            mTextPaint.setColor(bottomText.getColor());
            canvas.drawText(bottomText.getText(), mBottomTextPoins[i].x, mBottomTextPoins[i].y, mTextPaint);

            //绘制顶部Text
            mTextPaint.setTextSize(dp2px(getContext(), mFonts[i].getSize()));
            mTextPaint.setColor(topText.getColor());
            canvas.drawText(topText.getText(), mTopTextPoins[i].x, mTopTextPoins[i].y, mTextPaint);
        }

        //绘制圆圈
        canvas.drawCircle(mCircleCenterPoint.x, mCircleCenterPoint.y, mRadius, mCirclePaint);
    }

    public void setOnFontSelectListener(OnFontSelectListener onFontSelectListener) {
        mOnFontSelectListener = onFontSelectListener;
    }

    public interface OnFontSelectListener {
        void onFontSelect(Font font);
    }
}
