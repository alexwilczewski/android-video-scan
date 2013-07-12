package com.example.AndroidVideoScan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.view.View;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.*;

public class MyActivity extends Activity implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, SurfaceHolder.Callback {

    private static String TAG="AndroidVideoScan";

    private static String FILE = "/storage/emulated/0/DCIM/Camera/test1.mp4";
    private static boolean LOADFILE = true;

    private ScanningMediaPlayer mPlayer;
    private VideoView mSurface;
    private SurfaceHolder holder;

    private boolean playerPrepared;
    private boolean surfaceAvailable;

    private ScalingView mFullSize;
    private ViewGroup mContextBar;

    private boolean seeking;
    private int seekPosition;

    final int ACTIVITY_CHOOSE_FILE = 1;

    class IntegerPair {
        public int F;
        public int S;

        public IntegerPair(int F, int S) {
            this.F = F;
            this.S = S;
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playerPrepared = false;
        surfaceAvailable = false;

        setContentView(R.layout.main);

        mFullSize = (ScalingView) findViewById(R.id.full_size);
        mContextBar = (ViewGroup) findViewById(R.id.context_bar);
        mSurface = (VideoView) findViewById(R.id.videoviewsurface);

        holder = mSurface.getHolder();
        holder.addCallback(this);

        mPlayer = new ScanningMediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnErrorListener(this);

        Button btn = (Button) findViewById(R.id.select_file);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent chooseFile;
//                Intent intent;
//                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//                chooseFile.setType("file/*");
//                intent = Intent.createChooser(chooseFile, "Choose a file");
//                startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);

                beginPlaybackFromPath(FILE);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_CHOOSE_FILE: {
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String filePath = uri.getPath();
                    Log.i(TAG, "PATH: "+filePath);

                    beginPlaybackFromPath(filePath);
                }
            }
        }
    }

    // Surface Implementation
    public void surfaceCreated(SurfaceHolder surfaceholder) {
        Log.i(TAG, "MyActivity:surfaceCreated");

        surfaceAvailable = true;

        if(MyActivity.LOADFILE) {
            beginPlaybackFromPath(FILE);
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        // no-op
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        surfaceAvailable = false;
    }
    // End Surface Implementation

    public void beginPlaybackFromPath(String path) {
        Log.i(TAG, "path: "+path);

        playerPrepared = false;

        mPlayer.reset();

        try {
            mPlayer.setDataSource(path);

            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    // Player Implementations
    public void onPrepared(MediaPlayer mediaplayer) {
        Log.i(TAG, "In On Prepared");

        int width = mPlayer.getVideoWidth();
        int height = mPlayer.getVideoHeight();

        if (width != 0 && height != 0) {
            playerPrepared = true;

            int scrWidth = mFullSize.getMeasuredWidth();
            int scrHeight = mFullSize.getMeasuredHeight();

            resizeVideo(scrWidth, scrHeight);

            mPlayer.setDisplay(holder);

            seekPosition = mPlayer.getCurrentPosition();
        }
    }

    public void onSeekComplete(MediaPlayer mediaPlayer) { }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Log.e(TAG, "Error 1: "+i);
        Log.e(TAG, "Error 2: "+i2);
        return false;
    }
    // End Player Implementations

    public void resizeVideo(int width, int height) {
        if(playerPrepared) {
            int scrHeight = mFullSize.getHeight();
            int ctxBarHeight = mContextBar.getHeight();
            int maxHeight = scrHeight-ctxBarHeight;
            Log.i(TAG, "MyActivity:resizeVideo - maxheight: "+maxHeight);
            if(height > maxHeight) {
                height = maxHeight;
            }

            IntegerPair p = fitPlayerToAspect(mPlayer, width, height);
            Log.i(TAG, "MyActivity:resizeVideo - w:"+p.F);
            Log.i(TAG, "MyActivity:resizeVideo - h:"+p.S);
            directResize(p.F, p.S);
        }
    }

    public IntegerPair fitPlayerToAspect(MediaPlayer mediaPlayer, float fitWidth, float fitHeight) {
        float playerWidth = mediaPlayer.getVideoWidth();
        float playerHeight = mediaPlayer.getVideoHeight();
        Log.i(TAG, "playerWidth: "+playerWidth);
        Log.i(TAG, "playerHeight: "+playerHeight);
        int width = 0;
        int height = 0;

        float widthAspect = playerWidth / fitWidth;
        float heightAspect = playerHeight / fitHeight;

        if(widthAspect > heightAspect) {
            width = (int) fitWidth;
            height = (int) ((playerHeight / playerWidth) * fitWidth);
        } else {
            height = (int) fitHeight;
            width = (int) ((playerWidth / playerHeight) * fitHeight);
        }

        return new IntegerPair(width, height);
    }

    public void directResize(int width, int height) {
        if(playerPrepared) {
            holder.setFixedSize(width, height);
        }
    }

    public void scan(float seek) {
        seeking = true;
        seekPosition = (int) (seek*1000 + mPlayer.getCurrentPosition());
        mPlayer.seekTo(seekPosition);
    }
}
