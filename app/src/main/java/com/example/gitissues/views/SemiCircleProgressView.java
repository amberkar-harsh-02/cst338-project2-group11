package com.example.gitissues.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SemiCircleProgressView extends View {
    private int progress = 0;
    private Paint bgPaint;
    private Paint progressPaint;
    private RectF arcRect = new RectF();
    private float strokeWidthPx;

    public SemiCircleProgressView(Context context) {
        super(context);
        init();
    }

    public SemiCircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SemiCircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context,attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        strokeWidthPx = 6f * density;

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidthPx);
        bgPaint.setColor(Color.LTGRAY);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidthPx);
        progressPaint.setColor(Color.parseColor("#36B442")); // color option
        progressPaint.setStrokeCap(Paint.Cap.ROUND);


    }

    public void setProgress(int value) {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        progress = value;
        invalidate(); // ask Android to redraw
    }

    public int getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float halfStroke = strokeWidthPx / 2f;

        // Compute the bounding oval for the semicircle
        float left = getPaddingLeft() + halfStroke;
        float right = width - getPaddingRight() - halfStroke;
        float radius = (right - left) / 2f;
        float centerX = width / 2f;

        float bottom = height - getPaddingBottom() - halfStroke;
        float top = bottom - 2 * radius;   // semi-circle above bottom

        arcRect.set(centerX - radius, top, centerX + radius, bottom);

        // Background arc (full 180 degrees)
        canvas.drawArc(arcRect, 180f, 180f, false, bgPaint);

        // Foreground arc (portion of 180 degrees)
        float sweep = 180f * (progress / 100f);
        canvas.drawArc(arcRect, 180f, sweep, false, progressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (width == 0) {
            // default width if "wrap_content"
            width = (int) (100 * getResources().getDisplayMetrics().density);
        }

        int desiredHeight = width / 2; // half the width

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

}