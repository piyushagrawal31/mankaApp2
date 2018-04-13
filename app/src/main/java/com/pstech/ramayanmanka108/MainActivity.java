package com.pstech.ramayanmanka108;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by pagrawal on 18-01-2018.
 */

public class MainActivity extends BaseActivity implements OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String MM_SECOND_SEPARATOR = ":";
    private static final String TAG = "MankaApp";
    private static final String CURR_POSITION = "CURR_POSITION";

    MediaPlayer mPlayer;
    TextView text_shown;
    Handler seekHandler = new Handler();
    private ActionBarDrawerToggle toggle;
    private int SEEK_TIME = 10000; // in ms
    private static boolean isPlaying = false;
    private static boolean isMute = false;
    int currPosition = 0;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.playpause_btn) ImageButton playPauseBtn;
    @BindView(R.id.mute_unmute_btn) ImageButton muteUnmuteBtn;
    @BindView(R.id.reset_btn) ImageButton resetBtn;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.songCurrentDurationLabel) TextView currDuration;
    @BindView(R.id.songTotalDurationLabel) TextView totalDuration;
    @BindView(R.id.songProgressBar) SeekBar seek_bar;
    @BindView(R.id.images) ImageView currImage;
    @BindView(R.id.adView) AdView mAdView;
    int[] imageIds;
    final int MAX_PAGES = 108;
    int[] secondValues;
    Context context;
    int NOTIFY_ID = 1;
    private CharSequence mTitle;
    SharedPreferences sharedPreferences;
    private int currentPosition;
    View controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainLayout = findViewById(R.id.anchorView);
        controller = findViewById(R.id.playerView);

        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                displayMediaController();
                return true;
            }
        });

        ButterKnife.bind(this);
        context = getApplicationContext();
        setSupportActionBar(toolbar);
        mTitle = getResources().getString(R.string.app_name);
        getSupportActionBar().setTitle(mTitle);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initUI();
        super.initAds(mAdView);

        setScreenAlive();

        imageIds = new int[MAX_PAGES];
        secondValues = new int[MAX_PAGES];

        try {
            for (int idx = 0; idx < MAX_PAGES; idx++) {
                String pagePath = "page" + (idx + 1);
                int imageId = getApplicationContext().getResources().getIdentifier(pagePath, "drawable", getApplicationContext().getPackageName());
                imageIds[idx] = imageId;
            }

            readFileFromRawDirectory(R.raw.manka);
            currImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), imageIds[0]));

            initPlayer();
        }
        catch (Exception e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "Failed to initialize using images");
            FirebaseCrash.report(e);
        }

        seekUpdation();
        currImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                displayMediaController();
                return true;
            }
        });

        displayMediaController();

    }

    private void setScreenAlive() {
        boolean keepScreenAlive = ((MainApplication)getApplication()).isKeepAlwaysOn();
        if (keepScreenAlive)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void displayMediaController() {
        controller.setVisibility(View.VISIBLE);
        new SleepHandler().execute();
    }

    private void readFileFromRawDirectory(int resourceId) {
        InputStream iStream = context.getResources().openRawResource(resourceId);

        BufferedInputStream bin = new BufferedInputStream(iStream);
        String line;
        Scanner sc = new Scanner(iStream);
        int second;
        int counter = 0;
        while (sc.hasNext()) {
            line = sc.next();
            String[] mmSec = line.split(MM_SECOND_SEPARATOR);
            secondValues[counter++] = getSeconds(mmSec);
        }
        sc.close();
    }

    private int getSeconds(String[] mmSec) {
        int result = 0;
        try {
            result = Integer.parseInt(mmSec[0]) * 60 + Integer.parseInt(mmSec[1]);
        } catch (NumberFormatException nfe) {
            result = 0;
        }
        return result;
    }

    private void initUI() {
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void initPlayer() {
        playPauseBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_play));
        playPauseBtn.setOnClickListener(this);
        muteUnmuteBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);

        mPlayer = MediaPlayer.create(this, R.raw.ramayan_manka);
        int duration = mPlayer.getDuration();
        seek_bar.setMax(duration);
        totalDuration.setText(AppUtils.getMilliToMinuteSecRep(duration));

        currPosition = getCurrentPosition();
        mPlayer.seekTo(currPosition);

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userSelectedPosition = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(userSelectedPosition);
            }
        });

    }
    public void startNotification() {
//        final Intent notificationIntent = new Intent(getApplicationContext(),
//                MainActivity.class);
//        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//
//        final Notification notification = new Notification.Builder(
//                getApplicationContext())
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setOngoing(true).setContentTitle("Service Running")
//                .setContentText("Go to Feed Reader app")
//                .setContentIntent(pendingIntent).build();
//
//        startForeground(NOTIFICATION_ID, notification);

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setTicker("ticker")
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText("songTitle");

        // TODO: requires API 16, make it 15
        Notification not = builder.build();

//        startForeground(NOTIFY_ID, not);
    }


    @OnClick(R.id.previous_btn)
    void onPressBackward(View view) {
        int currPosition = mPlayer.getCurrentPosition();
        int position = currPosition - SEEK_TIME;
        mPlayer.seekTo(position);
    }

    @OnClick(R.id.next_btn)
    void onPressForward(View view) {
        int currPosition = mPlayer.getCurrentPosition();
        int position = currPosition + SEEK_TIME;
        mPlayer.seekTo(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScreenAlive();

        if (mPlayer != null && !mPlayer.isPlaying() && currPosition != 0) {
            mPlayer.seekTo(currPosition);
            if (isPlaying) {
                mPlayer.start();
                playPauseBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_pause));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean backgroundPlay = ((MainApplication)getApplication()).isBackgroundPlayOn();
        if (!backgroundPlay) {
            mPlayer.pause();
            playPauseBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_play));
        }
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {
        currPosition = mPlayer.getCurrentPosition();
        seek_bar.setProgress(currPosition);
        currDuration.setText(AppUtils.getMilliToMinuteSecRep(currPosition));
        seekHandler.postDelayed(run, 1000);
        int imageId = getImageId(currPosition);
        persistCurrState(currPosition);
        if (imageId < MAX_PAGES) {
            currImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), imageIds[imageId]));
        }
    }

    private void persistCurrState(int currPosition) {
        sharedPreferences.edit().putInt(CURR_POSITION, currPosition).commit();
    }

    private int getImageId(int currPosition) {
        long seconds = currPosition / 1000;
        return getPageId(seconds);
    }

    private int getPageId(long seconds) {
//        int start = 0;
//        int end = MAX_PAGES-1;
//        int mid = (start+end)/2;
//        while (true) {
//            if (seconds > secondValues[mid]) {
//                start = mid;
//            }
//        }
        for (int idx=0; idx < MAX_PAGES-1; idx++) {
            if (seconds >= secondValues[idx] && seconds <= secondValues[idx+1])
                return idx;
        }
        return MAX_PAGES-1;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playpause_btn:
                if (isPlaying) {
                    playPauseBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_play));
                    mPlayer.pause();
                } else {
                    playPauseBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_pause));
                    mPlayer.start();
                }
                isPlaying = !isPlaying;
                break;
            case R.id.mute_unmute_btn:
                if (isMute) {
                    muteUnmuteBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_lock_silent_mode_off));
                    mPlayer.setVolume(1,1);
                } else {
                    mPlayer.setVolume(0,0);
                    muteUnmuteBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_lock_silent_mode));
                }
                isMute = !isMute;
                break;
            case R.id.reset_btn :
                mPlayer.seekTo(0);
                break;
            case 3:
                // case for viewing next manka.
                int tCurrPosition = mPlayer.getCurrentPosition();
                long seconds = tCurrPosition / 1000;
                int pageId = getPageId(seconds);
                long nextSecond = getDuration(pageId, true);

                break;
        }
    }

    /*
        return seek position for next or previous page
     */
    private long getDuration(int i, boolean increment) {
        if (increment && (i+1) <= MAX_PAGES -1) {
            return secondValues[i+1] * 1000;
        } else if (!increment && (i-1) >= 0) {
            return secondValues[i-1] * 1000;
        }
        return secondValues[i] * 1000;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, AppUtils.getSharableText(AppConstants.SHARE_TXT, appPackageName));
            sendIntent.setType("text/html");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        } else if (id == R.id.nav_rate) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.nav_feedback) {

            String emailId = getResources().getString(R.string.emailId);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", emailId, null));
            String[] addresses = {emailId};
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject: " + getPackageName());
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else if (id == R.id.nav_about) {

        }

        return true;
    }

    public int getCurrentPosition() {
        if (((MainApplication)getApplication()).isResumePlayerOn())
            return sharedPreferences.getInt(CURR_POSITION, 0);
        return 0;
    }

    private class SleepHandler extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... strings) {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            controller.setVisibility(View.INVISIBLE);
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        mPlayer.stop();
//        mPlayer.release();
//    }
}
