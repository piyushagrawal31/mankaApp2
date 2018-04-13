package com.pstech.ramayanmanka108;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.media.MediaPlayer.OnPreparedListener;
import android.view.MotionEvent;
import android.widget.TextView;

import java.io.IOException;


/**
 * Created by pagrawal on 12-04-2018.
 */

public class AudioPlayerActivity extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private static final String TAG = "AudioPlayer";

    public static final String AUDIO_FILE_NAME = "audioFileName";

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private String audioFile;
    private Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_player);
//        audioFile = R.raw.manka

//        audioFile = this.getIntent().getStringExtra(AUDIO_FILE_NAME);
        ((TextView)findViewById(R.id.now_playing_text)).setText(audioFile);

//        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(this, R.raw.ramayan_manka);
        mediaPlayer.setOnPreparedListener(this);

        mediaController = new MediaController(this);

        //
        Button searchButton = new Button(getApplicationContext());
        searchButton.setText("Search");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
//        mediaController.setAnchorView(searchButton);
        mediaController.addView(searchButton, params);
//        addView(searchButton, params);
        //


//        try {

//            mediaPlayer.setDataSource(audioFile);
//            mediaPlayer.prepare();
            mediaPlayer.start();
//        } catch (IOException e) {
//            Log.e(TAG, "Could not open file " + audioFile + " for playback.", e);
//        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        mediaController.hide();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mediaController.show();
        return false;
    }

    //--MediaPlayerControl methods----------------------------------------------------
    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        return 0;
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    //--------------------------------------------------------------------------------

    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared");
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.main_audio_view));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

}
