package com.xk.sanjay.rulberview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by zz
 * date 2015/5/15
 * Description: 参考自: http://blog.csdn.net/dashu8193058/article/details/45846189
 */
public class RulerWheel extends View {
    // 默认刻度模式
    public static final int MOD_TYPE_SCALE = 5;
    // 1/2模式
    public static final int MOD_TYPE_HALF = 2;
    //刻度的上对齐方式
    public static final int ALIGN_MOD_UP = 0;
    //刻度的下对齐方式
    public static final int ALIGN_MOD_DOWN = 1;
    public static final int DEFAULT_ALIGN_MOD = ALIGN_MOD_DOWN;
    private int alignMode = DEFAULT_ALIGN_MOD;
    // 字体大小
    private int mTextSize = 36;
    // 分隔线(大号)
    private int mLineHeighMax;
    private int mLineColorMax;
    // 分隔线(中号)
    private int mLineHeighMid;
    private int mLineColorMid;
    // 分隔线(小号)
    private int mLineHeighMin;
    private int mLineColorMin;
    // 当前值
    private int mCurrValue;
    // 显示最大值
    private int mMaxValue;
    //显示最小值
    private int mMinValue;
    private int DEFAULT_MINI_VALUE = 0;
    private int DEFAULT_MAX_VALUE = 100;
    private int DEFAULT_CUR_VALUE = 0;
    private int DEFAULT_TEXT_SIZE = 30;
    private int DEFAULT_LINE_SIZE = 4;
    private int DEFAULT_LINE_DIVID_SIZE = 0;
    // 分隔模式
    private int mModType = MOD_TYPE_SCALE;
    /**
     * 分隔线之间间隔
     */
    private int mLineDivder;
    // 滚动器
    private WheelHorizontalScroller scroller;
    // 是否执行滚动
    private boolean isScrollingPerformed;
    // 滚动偏移量
    private int scrollingOffset;
    // 中间标线
    private Bitmap midBitmap;
    // 显示刻度值
    private boolean isShowScaleValue;

    //是否支持渐显效果
    private boolean mIsGradinet = false;
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private float mTpDesiredWidth;


    public RulerWheel(Context context) {
        this(context, null);
    }

    public RulerWheel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerWheel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {

        scroller = new WheelHorizontalScroller(context, scrollingListener);
        // 获取自定义属性和默认值
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerWheel);


        // 刻度宽度
        int scaleWidth = mTypedArray.getDimensionPixelSize(R.styleable.RulerWheel_scaleWidth, DEFAULT_LINE_SIZE);
        linePaint.setStrokeWidth(scaleWidth);

        // 刻度颜色
        mLineColorMax = mTypedArray.getColor(R.styleable.RulerWheel_lineColorMax, Color.BLACK);
        mLineColorMid = mTypedArray.getColor(R.styleable.RulerWheel_lineColorMid, Color.BLACK);
        mLineColorMin = mTypedArray.getColor(R.styleable.RulerWheel_lineColorMin, Color.BLACK);

        mIsGradinet = mTypedArray.getBoolean(R.styleable.RulerWheel_showGradient, false);

        mTextSize = mTypedArray.getInteger(R.styleable.RulerWheel_text_Size, DEFAULT_TEXT_SIZE);
        mCurrValue = mTypedArray.getInteger(R.styleable.RulerWheel_def_value, DEFAULT_CUR_VALUE);
        mMaxValue = mTypedArray.getInteger(R.styleable.RulerWheel_max_value, DEFAULT_MAX_VALUE);
        mMinValue = mTypedArray.getInteger(R.styleable.RulerWheel_min_value, DEFAULT_MINI_VALUE);

        if (mCurrValue < mMinValue) {
            mCurrValue = mMinValue;
        }

        // 刻度模式
        mModType = obtainMode(mTypedArray.getInteger(R.styleable.RulerWheel_mode, 0));

        // 刻度对齐模式
        alignMode = mTypedArray.getInteger(R.styleable.RulerWheel_alignMode, DEFAULT_ALIGN_MOD);

        int maskResId;
        if (alignMode == ALIGN_MOD_UP) {
            // mask，我们的箭头
            maskResId = mTypedArray.getResourceId(R.styleable.RulerWheel_mask_bg, R.drawable.ruler_mid_arraw);
        } else {
            maskResId = mTypedArray.getResourceId(R.styleable.RulerWheel_mask_bg, R.drawable.ruler_mid_arraw_down);
        }
        midBitmap = BitmapFactory.decodeResource(getResources(), maskResId);


        // 线条间距
        mLineDivder = obtainLineDivder(mTypedArray.getDimensionPixelSize(R.styleable.RulerWheel_line_divider, DEFAULT_LINE_DIVID_SIZE));

        // 显示刻度值
        isShowScaleValue = mTypedArray.getBoolean(R.styleable.RulerWheel_showScaleValue, true);
        textPaint.setTextSize(mTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        mTpDesiredWidth = Layout.getDesiredWidth("0", textPaint);

        mTypedArray.recycle();
    }

    private int obtainMode(int mode) {
        if (mode == 1) {
            return MOD_TYPE_HALF;
        }
        return MOD_TYPE_SCALE;
    }

    private int obtainLineDivder(int lineDivder) {
        if (0 == lineDivder) {
            if (mModType == MOD_TYPE_HALF) {
                mLineDivder = 80;
            } else {
                mLineDivder = 20;
            }
            return mLineDivder;
        }

        return lineDivder;
    }

    // Scrolling listener
    WheelHorizontalScroller.ScrollingListener scrollingListener = new WheelHorizontalScroller.ScrollingListener() {
        @Override
        public void onStarted() {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }

        @Override
        public void onScroll(int distance) {
            doScroll(distance);
        }

        @Override
        public void onFinished() {
            if (thatExceed()) {
                return;
            }
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd();
                isScrollingPerformed = false;
            }
            scrollingOffset = 0;
            invalidate();
        }

        @Override
        public void onJustify() {
            if (thatExceed()) {
                return;
            }
            if (Math.abs(scrollingOffset) > WheelHorizontalScroller.MIN_DELTA_FOR_SCROLLING) {
                if (scrollingOffset < -mLineDivder / 2) {
                    scroller.scroll(mLineDivder + scrollingOffset, 0);
                } else if (scrollingOffset > mLineDivder / 2) {
                    scroller.scroll(scrollingOffset - mLineDivder, 0);
                } else {
                    scroller.scroll(scrollingOffset, 0);
                }
            }
        }
    };


    private void doScroll(int delta) {
        scrollingOffset += delta;
        int offsetCount = scrollingOffset / mLineDivder;
        if (0 != offsetCount) {
            // 显示在范围内
            int oldValue = Math.min(Math.max(mMinValue, mCurrValue), mMaxValue);
            mCurrValue -= offsetCount;
            scrollingOffset -= offsetCount * mLineDivder;
            if (null != onWheelListener) {
                //回调通知最新的值
                onWheelListener.onChanged(this, oldValue, Math.min(Math.max(mMinValue, mCurrValue), mMaxValue));
            }
        }
        invalidate();
    }

    /**
     * 越界回滚
     *
     * @return
     */
    private boolean thatExceed() {
        //这个是越界后需要回滚的大小值
        int outRange = 0;
        if (mCurrValue < mMinValue) {
            outRange = (mCurrValue - mMinValue) * mLineDivder;
        } else if (mCurrValue > mMaxValue) {
            outRange = (mCurrValue - mMaxValue) * mLineDivder;
        }
        if (0 != outRange) {
            scrollingOffset = 0;
            scroller.scroll(-outRange, 100);
            return true;
        }
        return false;
    }

    public void setValue(int current, int maxValue) {
        if (current < mMinValue) {
            current = mMinValue;
        }

        if (maxValue < 0) {
            maxValue = 100;
        }

        this.mCurrValue = current;
        this.mMaxValue = maxValue;
        invalidate();
    }

    /**
     * 获取当前值
     *
     * @return
     */
    public int getValue() {
        return Math.min(Math.max(mMinValue, mCurrValue), mMaxValue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int heightSize;
        if (midBitmap != null) {
            heightSize = midBitmap.getHeight() + getPaddingTop() + getPaddingBottom();
        } else {
            heightSize = getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0)
            return;
        int rHeight = h - getPaddingTop() - getPaddingBottom();
        mLineHeighMax = rHeight / 2;
        mLineHeighMid = rHeight / 4;
        mLineHeighMin = rHeight / 8;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLineHeighMin == 0) {
            return;
        }
        int rWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int rHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        //画数据
        drawRulerLine(canvas, rWidth, rHeight);
        //画指示器
        drawMiddleUpArrowLine(canvas, rWidth, rHeight);
    }

    /**
     * 画中间的箭头指示的，当然也可以替代别的mask
     *
     * @param canvas
     * @param rWidth
     * @param rHeight
     */
    private void drawMiddleUpArrowLine(Canvas canvas, int rWidth, int rHeight) {
        canvas.drawBitmap(midBitmap, (rWidth - midBitmap.getWidth()) / 2, (rHeight - midBitmap.getHeight()) / 2, markPaint);
    }

    /**
     * @param canvas
     * @param rWidth  显示宽度
     * @param rHeight 显示高度
     */
    private void drawRulerLine(Canvas canvas, int rWidth, int rHeight) {
        // 根据间隔计算当前一半宽度的个数+偏移2个
        final int halfCount = (int) Math.ceil(rWidth / 2f / mLineDivder) + 2;
        final int distanceX = scrollingOffset;
        final int currValue = mCurrValue;

        if (alignMode == ALIGN_MOD_UP) {
            drawUpMode(canvas, halfCount, distanceX, currValue, rWidth, rHeight);
        } else {
            drawDownMode(canvas, halfCount, distanceX, currValue, rWidth, rHeight);
        }

    }

    //标刻的对齐方式是上对齐
    private void drawUpMode(Canvas canvas, int halfCount, int distanceX, int currValue, int rWidth, int rHeight) {

        int ry = (int) (getPaddingTop() + (rHeight - mLineHeighMax) / 2 + textPaint.getTextSize());

        int value;
        float xPosition;
        for (int i = 0; i < halfCount; i++) {
            //  right
            xPosition = rWidth / 2f + i * mLineDivder + distanceX;
            value = currValue + i;
            if (xPosition <= rWidth && value >= mMinValue && value <= mMaxValue) {
                if (value % mModType == 0) {
                    if (mModType == MOD_TYPE_HALF) {
                        linePaint.setColor(mLineColorMax);
                        canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMax, linePaint);
                        if (isShowScaleValue) {
                            canvas.drawText(String.valueOf(value / 2), xPosition, ry - mTpDesiredWidth, textPaint);
                        }
                    } else if (mModType == MOD_TYPE_SCALE) {
                        if (value % (MOD_TYPE_SCALE * 2) == 0) {
                            linePaint.setColor(mLineColorMax);
                            canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMax, linePaint);
                            if (isShowScaleValue) {
                                canvas.drawText(String.valueOf(value), xPosition, ry - mTpDesiredWidth, textPaint);
                            }
                        } else {
                            linePaint.setColor(mLineColorMid);
                            canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMid, linePaint);
                        }
                    }
                } else {
                    linePaint.setColor(mLineColorMin);
                    canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMin, linePaint);
                }
            }

            //  left
            xPosition = rWidth / 2f - i * mLineDivder + distanceX;
            value = currValue - i;
            if (xPosition > getPaddingLeft() && value > mMinValue && value <= mMaxValue) {
                if (value % mModType == 0) {
                    if (mModType == MOD_TYPE_HALF) {
                        linePaint.setColor(mLineColorMax);
                        canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMax, linePaint);
                        if (isShowScaleValue) {
                            canvas.drawText(String.valueOf(value / 2), xPosition, ry - mTpDesiredWidth, textPaint);
                        }
                    } else if (mModType == MOD_TYPE_SCALE) {
                        if (value % (MOD_TYPE_SCALE * 2) == 0) {
                            linePaint.setColor(mLineColorMax);
                            canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMax, linePaint);
                            if (isShowScaleValue) {
                                canvas.drawText(String.valueOf(value), xPosition, ry - mTpDesiredWidth, textPaint);
                            }
                        } else {
                            linePaint.setColor(mLineColorMid);
                            canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMid, linePaint);
                        }
                    }
                } else {
                    linePaint.setColor(mLineColorMin);
                    canvas.drawLine(xPosition, ry, xPosition, ry + mLineHeighMin, linePaint);
                }
            }
        }
    }

    //标刻的对齐方式是下对齐
    private void drawDownMode(Canvas canvas, int halfCount, int distanceX, int currValue, int rWidth, int rHeight) {
        int value;
        float xPosition;
        //线y坐标
        int ry = (int) (rHeight - mTpDesiredWidth - textPaint.getTextSize()) - getPaddingBottom();
        for (int i = 0; i < halfCount; i++) {

            //画显示在屏幕上的右半部分数据---right part
            xPosition = rWidth / 2f + i * mLineDivder + distanceX;
            value = currValue + i;
            if (xPosition <= rWidth && value >= mMinValue && value <= mMaxValue) {
                if (value % mModType == 0) {
                    if (mModType == MOD_TYPE_HALF) {
                        linePaint.setColor(mLineColorMax);
                        canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMax, linePaint);
                        if (isShowScaleValue) {
                            canvas.drawText(String.valueOf(value / 2), xPosition, rHeight - mTpDesiredWidth, textPaint);
                        }
                    } else if (mModType == MOD_TYPE_SCALE) {
                        if (value % (MOD_TYPE_SCALE * 2) == 0) {
                            linePaint.setColor(mLineColorMax);

                            canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMax, linePaint);
                            if (isShowScaleValue) {
                                canvas.drawText(String.valueOf(value), xPosition, rHeight - mTpDesiredWidth, textPaint);
                            }
                        } else {
                            linePaint.setColor(mLineColorMid);
                            canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMid, linePaint);
                        }
                    }
                } else {
                    linePaint.setColor(mLineColorMin);
                    canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMin, linePaint);
                }
            }

            //画显示在屏幕上的左半部分数据---left part
            xPosition = rWidth / 2f - i * mLineDivder + distanceX;
            value = currValue - i;
            if (xPosition > getPaddingLeft() && value >= mMinValue && value <= mMaxValue) {
                if (value % mModType == 0) {
                    if (mModType == MOD_TYPE_HALF) {
                        linePaint.setColor(mLineColorMax);
                        canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMax, linePaint);
                        if (isShowScaleValue) {
                            canvas.drawText(String.valueOf(value / 2), xPosition, rHeight - mTpDesiredWidth, textPaint);
                        }
                    } else if (mModType == MOD_TYPE_SCALE) {
                        if (value % (MOD_TYPE_SCALE * 2) == 0) {
                            linePaint.setColor(mLineColorMax);
                            canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMax, linePaint);
                            if (isShowScaleValue) {
                                canvas.drawText(String.valueOf(value), xPosition, rHeight - mTpDesiredWidth, textPaint);
                            }
                        } else {
                            linePaint.setColor(mLineColorMid);
                            canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMid, linePaint);
                        }
                    }
                } else {
                    linePaint.setColor(mLineColorMin);
                    canvas.drawLine(xPosition, ry, xPosition, ry - mLineHeighMin, linePaint);
                }
            }
        }
    }

    private float mDownFocusX;
    private float mDownFocusY;
    private boolean isDisallowIntercept;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownFocusX = event.getX();
                mDownFocusY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDisallowIntercept && Math.abs(event.getY() - mDownFocusY) < Math.abs(event.getX() - mDownFocusX)) {
                    isDisallowIntercept = true;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                isDisallowIntercept = false;
                break;
        }
        return scroller.onTouchEvent(event);
    }

    //---------------------
    //region 回调接口通知部分

    private OnWheelScrollListener onWheelListener;

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    public void setScrollingListener(OnWheelScrollListener listener) {
        onWheelListener = listener;
    }

    /**
     * Removes wheel changing listener
     */
    public void removeScrollingListener() {
        onWheelListener = null;
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyScrollingListeners(int oldValue, int newValue) {
        onWheelListener.onChanged(this, oldValue, newValue);
    }

    private void notifyScrollingListenersAboutStart() {
        if (null != onWheelListener) {
            onWheelListener.onScrollingStarted(this);
        }
    }

    private void notifyScrollingListenersAboutEnd() {
        if (null != onWheelListener) {
            onWheelListener.onScrollingFinished(this);
        }
    }


    public interface OnWheelScrollListener {
        /**
         * Callback method to be invoked when current item changed
         *
         * @param wheel    the wheel view whose state has changed
         * @param oldValue the old value of current item
         * @param newValue the new value of current item
         */
        void onChanged(RulerWheel wheel, int oldValue, int newValue);

        /**
         * Callback method to be invoked when scrolling started.
         *
         * @param wheel the wheel view whose state has changed.
         */
        void onScrollingStarted(RulerWheel wheel);

        /**
         * Callback method to be invoked when scrolling ended.
         *
         * @param wheel the wheel view whose state has changed.
         */
        void onScrollingFinished(RulerWheel wheel);
    }
//endregion

}