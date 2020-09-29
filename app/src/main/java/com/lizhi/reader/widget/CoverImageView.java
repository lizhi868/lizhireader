package com.lizhi.reader.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;


public class CoverImageView extends androidx.appcompat.widget.AppCompatImageView {
    float width, height;

    public CoverImageView(Context context) {
        super(context);
    }

    public CoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int measuredHeight = measuredWidth * 7 / 5;
//        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width >= 5 && height > 5) {
            @SuppressLint("DrawAllocation")
            Path path = new Path();
            //四个圆角
            path.moveTo(5, 0);
            path.lineTo(width - 5, 0);
            path.quadTo(width, 0, width, 5);
            path.lineTo(width, height - 5);
            path.quadTo(width, height, width - 5, height);
            path.lineTo(5, height);
            path.quadTo(0, height, 0, height - 5);
            path.lineTo(0, 5);
            path.quadTo(0, 0, 5, 0);

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

    public void setHeight(int height) {
        int width = height * 5 / 7;
        setMinimumWidth(width);
    }
}
