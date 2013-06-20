package com.example.AndroidVideoScan;

import android.content.Context;
import android.view.SurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.app.Activity;

public class VideoView extends SurfaceView {
    protected static final String TAG = "AndroidVideoScan";

    public VideoView(Context context) {
        super(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        MyActivity host = (MyActivity) getContext();
        host.test();

        return true;
    }

//    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = ev.getX();
//                mDownY = ev.getY();
//                isOnClick = true;
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                if (isOnClick) {
//                    Log.i(TAG, "onClick ");
//                    //TODO onClick code
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_TRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_TRESHOLD)) {
//                    Log.i(TAG, "movement detected");
//                    addRandomBalls(1, (int)ev.getX(), (int)ev.getY(), 50);
////                    isOnClick = false;
//                }
//                break;
//            default:
//                break;
//        }
//
//        return true;
//    }
}
