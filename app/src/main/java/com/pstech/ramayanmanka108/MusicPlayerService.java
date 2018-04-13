package com.pstech.ramayanmanka108;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.IBinder;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
//import android.support.v7.graphics.

public class MusicPlayerService extends Service {

    private long mNotificationPostTime = 0;
    public static final String CHANNEL_ID = "timber_channel_01";

    public MusicPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Notification buildNotification() {
//        final String albumName = getAlbumName();
        final String albumName = "Manka";
//        final String artistName = getArtistName();
        final String artistName = "Ramayan";
//        final boolean isPlaying = isPlaying();
        final boolean isPlaying = true;
        String text = TextUtils.isEmpty(albumName)
                ? artistName : artistName + " - " + albumName;

        int playButtonResId = isPlaying
                ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;

//        Intent nowPlayingIntent = NavigationUtils.getNowPlayingIntent(this);
        final Intent nowPlayingIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap artwork = null;
//        artwork = ImageLoader.getInstance().loadImageSync(TimberUtils.getAlbumArtUri(getAlbumId()).toString());

        if (artwork == null) {
            artwork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ram_darbar);
        }

        if (mNotificationPostTime == 0) {
            mNotificationPostTime = System.currentTimeMillis();
        }

        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(artwork)
                .setContentIntent(clickIntent)
//                .setContentTitle(getTrackName())
                .setContentTitle("Ramayan Manka")
                .setContentText(text)
                .setWhen(mNotificationPostTime);
//                .addAction(R.drawable.ic_btn_backword,
//                        "",
//                        retrievePlaybackAction(PREVIOUS_ACTION))
//                .addAction(playButtonResId, "",
//                        retrievePlaybackAction(TOGGLEPAUSE_ACTION))
//                .addAction(R.drawable.ic_btn_forword,
//                        "",
//                        retrievePlaybackAction(NEXT_ACTION));

        if (AppUtils.isJellyBeanMR1()) {
            builder.setShowWhen(false);
        }

        if (AppUtils.isLollipop()) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            android.support.v4.media.app.NotificationCompat.MediaStyle style = new android.support.v4.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(mSession.getSessionToken())
                    .setShowActionsInCompactView(0, 1, 2, 3);
            builder.setStyle(style);
        }
//        if (artwork != null && AppUtils.isLollipop()) {
//            builder.setColor(Palette.from(artwork).generate().getVibrantColor(Color.parseColor("#403f4d")));
//        }

        if (AppUtils.isOreo()) {
            builder.setColorized(true);
        }

        Notification n = builder.build();

//        if (mActivateXTrackSelector) {
//            addXTrackSelector(n);
//        }

        return n;
    }

}
