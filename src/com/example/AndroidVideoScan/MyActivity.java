package com.example.AndroidVideoScan;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.content.res.AssetFileDescriptor;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.*;

public class MyActivity extends Activity implements
        MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {

    private static String TAG="AndroidVideoScan";
    private int width=0;
    private int height=0;
    private MediaPlayer player;
    private VideoView surface;
    private SurfaceHolder holder;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        surface=(VideoView)findViewById(R.id.videoviewsurface);
        holder = surface.getHolder();
        holder.addCallback(this);
    }


    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        // no-op
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        // no-op
    }

    public void test() {
        player.seekTo(0);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.sample_mpeg4);

        try {
            player = new MediaPlayer();

            player.setDisplay(holder);
            player.setOnPreparedListener(this);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.i(TAG, "In On Prepared");

        width = player.getVideoWidth();
        height = player.getVideoHeight();

        if (width!=0 && height!=0) {
            holder.setFixedSize(width, height);
            player.start();
        }
    }
}
