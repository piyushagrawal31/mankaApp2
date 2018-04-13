package com.pstech.ramayanmanka108;

/**
 * Created by pagrawal on 17-01-2018.
 */

public interface PlayerAdapter {

    void loadMedia(int resourceId);

    void release();

    boolean isPlaying();

    public int getCurrentPosition();

    void play();

    void reset();

    void pause();

    void initializeProgressCallback();

    void seekTo(int position);
}
