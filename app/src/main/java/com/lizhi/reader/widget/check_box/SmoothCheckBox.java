package com.lizhi.reader.widget.check_box;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.lizhi.reader.R;
import com.lizhi.reader.utils.DensityUtil;

public class SmoothCheckBox extends View implements Checkable {

    private static final int DEF_DRAW_SIZE = 25;
    private static final int DEF_ANIM_DURATION = 300;

    private Paint mPaint, mFloorPaint;
    private Point mCenterPoint;
    private int mWidth, mStrokeWidth;
    private int mCheckedColor, mUnCheckedColor, mFloorColor, mFloorUnCheckedColor;

    private boolean mChecked;
    private OnCheckedChangeListener mListener;

    public SmoothCheckBox(Context context) {
        this(context, null);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SmoothCheckBox);
        mCheckedColor = context.getResources().getColor(R.color.background_card);
        mUnCheckedColor = context.getResources().getColor(R.color.background_menu);
        mFloorColor = context.getResources().getColor(R.color.transparent30);
        mFloorColor = ta.getColor(R.styleable.SmoothCheckBox_color_unchecked_stroke, mFloorColor);
        mCheckedColor = ta.getColor(R.styleable.SmoothCheckBox_color_checked, mCheckedColor);
        mUnCheckedColor = ta.getColor(R.styleable.SmoothCheckBox_color_unchecked, mUnCheckedColor);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.SmoothCheckBox_stroke_width, DensityUtil.dp2px(getContext(), 0));
        ta.recycle();

        mFloorUnCheckedColor = mFloorColor;

        mFloorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFloorPaint.setStyle(Paint.Style.STROKE);
        mFloorPaint.setStrokeWidth(mStrokeWidth);
        mFloorPaint.setColor(mFloorColor);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCheckedColor);

        mCenterPoint = new Point();
        setOnClickListener(v -> {
            toggle();
            if (isChecked()) {
                startCheckedAnimation();
            } else {
                startUnCheckedAnimation();
            }
        });
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        invalidate();
        if (mListener != null) {
            mListener.onCheckedChanged(SmoothCheckBox.this, mChecked);
        }
    }

    @Override
    public void toggle() {
        this.setChecked(!isChecked());
    }

    /**
     * checked with animation
     *
     * @param checked checked
     * @param animate change with animation
     */
    public void setChecked(boolean checked, boolean animate) {
        if (animate) {
            mChecked = checked;
            if (checked) {
                startCheckedAnimation();
            } else {
                startUnCheckedAnimation();
            }
            if (mListener != null) {
                mListener.onCheckedChanged(SmoothCheckBox.this, mChecked);
            }

        } else {
            this.setChecked(checked);
        }
    }

    private int measureSize(int measureSpec) {
        int defSize = DensityUtil.dp2px(getContext(), DEF_DRAW_SIZE);
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);

        int result = 0;
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = Math.min(defSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getMeasuredWidth();
        mCenterPoint.x = mWidth / 2;
        mCenterPoint.y = getMeasuredHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBorder(canvas);
        drawCenter(canvas);
    }

    private void drawCenter(Canvas canvas) {
        mPaint.setColor(mChecked ? mCheckedColor : mUnCheckedColor);
        float radius = (mCenterPoint.x + mStrokeWidth * 2) / 4.0f;
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, radius, mPaint);
    }

    private void drawBorder(Canvas canvas) {
        mFloorPaint.setColor(mFloorColor);
        int radius = mCenterPoint.x - mStrokeWidth;
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, radius, mFloorPaint);
    }

    private void startCheckedAnimation() {
        postInvalidate();
    }

    private void startUnCheckedAnimation() {
        postInvalidate();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked);
    }
}