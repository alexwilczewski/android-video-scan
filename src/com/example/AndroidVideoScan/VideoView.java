package com.example.AndroidVideoScan;

import android.content.Context;
import android.view.SurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.util.Log;

public class VideoView extends SurfaceView {
    protected static final String TAG = "AndroidVideoScan";
    private MyActivity mContext;

    private int testW = 100;
    private int testH = 100;

    public VideoView(Context context) {
        super(context);
        if(!isInEditMode()) {
            mContext = (MyActivity) context;
        }
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            mContext = (MyActivity) context;
        }
    }

    private float mDownX;
    private float mDownY;
    private float mLastX;
    private float mLastY;
    private boolean isOnClick;
    private boolean isOnDrag;
    private final float SCROLL_TRESHOLD = 5f;

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                isOnClick = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isOnClick) {
                    onTouch(ev, mDownX, mDownY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDragging(ev, mDownX, mDownY)) {
                    onTouchDrag(ev, mDownX, mDownY, mLastX, mLastY);
                    isOnDrag = true;
                    isOnClick = false;
                }
                break;
            default:
                break;
        }

        mLastX = ev.getX();
        mLastY = ev.getY();

        return true;
    }

    private boolean isDragging(MotionEvent ev, float xDown, float yDown) {
        return isOnDrag ||
                (isOnClick && Math.abs(xDown - ev.getX()) > SCROLL_TRESHOLD || Math.abs(yDown - ev.getY()) > SCROLL_TRESHOLD);
    }

    public void onTouchDrag(MotionEvent ev, float xDown, float yDown, float xLast, float yLast) {
        float scanMax = 5;
        float diffMax = 75;

        float xCurr = ev.getX();

        float xDiff = xCurr - xLast;

        // Limit X dragging difference to only 100 pixel difference
        xDiff = Math.min(xDiff, diffMax);
        xDiff = Math.max(xDiff, -diffMax);

        float scanValue = scanMax / diffMax * xDiff;

        mContext.scan(scanValue);

        Log.i(TAG, "scanValue: "+scanValue);
    }

    public void onTouch(MotionEvent ev, float xDown, float yDown) {
        // No-op
    }
}
