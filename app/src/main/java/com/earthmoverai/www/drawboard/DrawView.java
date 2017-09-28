package com.earthmoverai.www.drawboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    private int width, height;

    private Paint greenPaint, bitmapPaint;

    private Path path;

    private Bitmap bitmap;

    private Canvas canvas;

    private float mX, mY;
    private boolean correctStart = false;
    private static final float TOUCH_TOLERANCE = 4;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setDither(true);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStyle(Paint.Style.STROKE);
        greenPaint.setStrokeJoin(Paint.Join.ROUND);
        greenPaint.setStrokeCap(Paint.Cap.ROUND);
        greenPaint.setStrokeWidth(20);

        bitmapPaint = new Paint(Paint.DITHER_FLAG);

        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, greenPaint);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
    }

    public void clear() {
        path.reset();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        invalidate();
    }

    private void touchDown(float x, float y) {
        correctStart = true;
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        if (!correctStart) {
            return;
        }

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        if (!correctStart) {
            return;
        } else {
            correctStart = false;
        }
        path.lineTo(mX, mY);
        canvas.drawPath(path, greenPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }
}
