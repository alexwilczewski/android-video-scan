package com.example.AndroidVideoScan;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

public class ScalingView extends View {

    protected static final String TAG = "AndroidVideoScan";
    private MyActivity mContext;

    public ScalingView(Context context) {
        super(context);
        if(!isInEditMode()) {
            mContext = (MyActivity) context;
        }
    }

    public ScalingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            mContext = (MyActivity) context;
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(!isInEditMode()) {
            Log.i(TAG, "ScalingView:onMeasure");

            // Attempt to resize video to default. Then contextbar will be "visible" on screen.
            mContext.directResize(1, 1);

            ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Log.v(TAG,
                            String.format("new width=%d; new height=%d", getWidth(), getHeight()));

                    mContext.resizeVideo(getWidth(), getHeight());
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

        }
    }
}
