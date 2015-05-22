package eva.spejderapp;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Klasse der kan afspille lyd
 * Source: http://stackoverflow.com/questions/18254870/play-a-sound-from-res-raw
 */
public class AudioPlayer {

    private static MediaPlayer mMediaPlayer;

    public static void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static void play(Context c, int rid) {
        stop();

        mMediaPlayer = MediaPlayer.create(c, rid);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        mMediaPlayer.start();
    }

}