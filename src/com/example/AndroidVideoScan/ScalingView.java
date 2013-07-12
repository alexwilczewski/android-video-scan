package com.example.AndroidVideoScan;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

public class ScalingView extends View {

    protected static final String TAG = "AndroidVideoScan";
    private MyActivity mContext;
    private boolean mOnMeasured;

    public ScalingView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public ScalingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public void init(Context context) {
        mContext = (MyActivity) context;
        mOnMeasured = false;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(!isInEditMode()) {
            mOnMeasured = true;

            Log.i(TAG, "ScalingView:onMeasure");

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
