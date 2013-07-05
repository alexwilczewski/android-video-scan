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

    private int width=0;
    private int height=0;
    private ScanningMediaPlayer player;
    private VideoView surface;
    private SurfaceHolder holder;
    private boolean playerPrepared;

    private View mFullSize;
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

        setContentView(R.layout.main);

        mFullSize = (ScalingView) findViewById(R.id.full_size);

        mContextBar = (ViewGroup) findViewById(R.id.context_bar);

        surface = (VideoView) findViewById(R.id.videoviewsurface);
        holder = surface.getHolder();
        holder.addCallback(this);
        holder.setFixedSize(1, 1);

        Button btn = (Button) this.findViewById(R.id.select_file);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("file/*");
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
            }
        });
    }

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

                    setUpPlayer(filePath);
                }
            }
        }
    }


    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        // no-op
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        // no-op
    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {
//        setUpPlayer();
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

    public void setUpPlayer(String path) {
        Log.i(TAG, "In setUpPlayer");

        playerPrepared = false;
//        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.test1);
//        FileDescriptor fd = afd.getFileDescriptor();

        try {
            player = new ScanningMediaPlayer();

//            player.setDisplay(holder);
            player.setOnPreparedListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnErrorListener(this);
//            player.setDataSource(fd, afd.getStartOffset(), afd.getLength());
//            afd.close();
            player.setDataSource(path);

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
            playerPrepared = true;

            int scrWidth = mFullSize.getMeasuredWidth();
            int scrHeight = mFullSize.getMeasuredHeight();

            Log.i(TAG, "scrWidth: "+scrWidth);
            Log.i(TAG, "scrHeight: "+scrHeight);

            resizeVideo(scrWidth, scrHeight);

            player.setDisplay(holder);

            seekPosition = player.getCurrentPosition();
        }
    }

    public void resizeVideo(int width, int height) {
        if(playerPrepared) {
            int scrHeight = mFullSize.getHeight();
            int ctxBarHeight = mContextBar.getHeight();
            int maxHeight = scrHeight-ctxBarHeight;
            Log.i(TAG, "MyActivity:resizeVideo - maxheight: "+maxHeight);
            if(height > maxHeight) {
                height = maxHeight;
            }

            IntegerPair p = fitPlayerToAspect(player, width, height);
            Log.i(TAG, "MyActivity:resizeVideo - w:"+p.F);
            Log.i(TAG, "MyActivity:resizeVideo - h:"+p.S);
            holder.setFixedSize(p.F, p.S);
        }
    }

    public void directResize(int width, int height) {
        if(playerPrepared) {
            holder.setFixedSize(width, height);
        }
    }

    public void onSeekComplete(MediaPlayer mediaPlayer) { }

    public void scan(float seek) {
        seeking = true;
        seekPosition = (int) (seek*1000 + player.getCurrentPosition());
        player.seekTo(seekPosition);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Log.e(TAG, "Error 1: "+i);
        Log.e(TAG, "Error 2: "+i2);
        return false;    // Error -38 lol
    }
}
