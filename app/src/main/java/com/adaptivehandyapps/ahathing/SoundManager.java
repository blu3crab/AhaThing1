/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker MAY 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package com.adaptivehandyapps.ahathing;
//
// Created by mat on 5/24/2017.
//

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicStarBoard;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;

import java.util.List;

public class SoundManager {
    private static final String TAG = SoundManager.class.getSimpleName();

    public static final float SOUND_VOLUME_FULL = 1.0f;
    public static final float SOUND_VOLUME_HALF = 0.5f;
    public static final float SOUND_VOLUME_QTR = 0.10f;

    public static final int SOUND_START_TIC_LONG = 1000;
    public static final int SOUND_START_TIC_MEDIUM = 500;
    public static final int SOUND_START_TIC_SHORT = 100;
    public static final int SOUND_START_TIC_NADA = 0;

    private Context mContext;
    private MainActivity mParent;

    ///////////////////////////////////////////////////////////////////////////
    private MediaPlayer mpUhuh;
    private MediaPlayer mpMusic;
    private MediaPlayer mpTap;
    private MediaPlayer mpPress;
    private MediaPlayer mpFling;

    int mCurrentPosition = 0;

    private Boolean mpUhuhReady;
    private Boolean mpTapReady;
    private Boolean mpPressReady;
    private Boolean mpFlingReady;
    private Boolean mpMusicReady;

    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    public MediaPlayer getMpUhuh() {
        return mpUhuh;
    }
    public void setMpUhuh(MediaPlayer mpUhuh) {
        this.mpUhuh = mpUhuh;
    }

    public MediaPlayer getMpMusic() {
        return mpMusic;
    }
    public void setMpMusic(MediaPlayer mpMusic) {
        this.mpMusic = mpMusic;
    }

    public MediaPlayer getMpTap() {
        return mpTap;
    }
    public void setMpTap(MediaPlayer mpTap) {
        this.mpTap = mpTap;
    }

    public MediaPlayer getMpPress() {
        return mpPress;
    }
    public void setMpPress(MediaPlayer mpPress) {
        this.mpPress = mpPress;
    }

    public MediaPlayer getMpFling() {
        return mpFling;
    }
    public void setMpFling(MediaPlayer mpFling) {
        this.mpFling = mpFling;
    }


    private Boolean isMpUhuhReady() {return mpUhuhReady;}
    private void setMpUhuhReady(Boolean ready) {
        mpUhuhReady = ready;
    }

    public Boolean getMpMusicReady() {
        return mpMusicReady;
    }
    public void setMpMusicReady(Boolean mpMusicReady) {
        this.mpMusicReady = mpMusicReady;
    }

    public Boolean getMpTapReady() {
        return mpTapReady;
    }
    public void setMpTapReady(Boolean mpTapReady) {
        this.mpTapReady = mpTapReady;
    }

    public Boolean getMpPressReady() {
        return mpPressReady;
    }
    public void setMpPressReady(Boolean mpPressReady) {
        this.mpPressReady = mpPressReady;
    }

    public Boolean getMpFlingReady() {
        return mpFlingReady;
    }
    public void setMpFlingReady(Boolean mpFlingReady) {
        this.mpFlingReady = mpFlingReady;
    }

    ///////////////////////////////////////////////////////////////////////////
    public SoundManager(Context context) {

        mContext = context;
        mParent = (MainActivity) context;
        if (mParent != null) {
            Log.v(TAG, "SoundManager ready with parent " + mParent.toString() + "...");
        }
        else {
            Log.e(TAG, "Oops!  SoundManager Parent context (MainActivity) NULL!");
        }

        mpUhuh = MediaPlayer.create(mContext, R.raw.uhuh);
        mpUhuh.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mpUhuh.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setMpUhuhReady(true);
            }
        });

        mpMusic = MediaPlayer.create(mContext, R.raw.dintro);
        mpMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mpMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setMpMusicReady(true);
            }
        });

        setMpTap (MediaPlayer.create(mContext, R.raw.tap));
        getMpTap().setAudioStreamType(AudioManager.STREAM_MUSIC);
        getMpTap().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setMpTapReady(true);
            }
        });

        setMpPress (MediaPlayer.create(mContext, R.raw.press));
        getMpPress().setAudioStreamType(AudioManager.STREAM_MUSIC);
        getMpPress().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setMpPressReady(true);
            }
        });

        setMpFling (MediaPlayer.create(mContext, R.raw.fling));
        getMpFling().setAudioStreamType(AudioManager.STREAM_MUSIC);
        getMpFling().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setMpFlingReady(true);
            }
        });

    }

    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean startSound(final MediaPlayer mp, float leftVolume, float rightVolume, int fromTic, int byTic) {
        Log.d(TAG, "startSound...");
        try {
            if (mp != null) {
                // set volume
                if (leftVolume > 0 || rightVolume > 0) mp.setVolume(leftVolume, rightVolume);
                // start playing
                mp.start();
                Log.d(TAG, "startSound start unconditional...");
                // if timed duration, start counter
                if (fromTic > 0) {
                    CountDownTimer countdown = new CountDownTimer(fromTic, byTic) {
                        public void onTick(long millisUntilFinished) {
//                            mp.start();
                            Log.d(TAG, "startSound onTick...");
                        }

                        public void onFinish() {
//                            mp.seekTo(0);
                            mp.stop();
                            Log.d(TAG, "startSound onFinish stop...");
                            mp.prepareAsync();
                            Log.d(TAG, "startSound onFinish prepareAsync...");
//                            mpTap.release();
                        }
                    };
                    countdown.start();
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Oops!  media player not prepared? " + e.getMessage());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean pauseSound(final MediaPlayer mp, float leftVolume, float rightVolume, int fromTic, int byTic) {
        Log.d(TAG, "startSound...");
        try {
            if (mp != null) {
                // set volume
                if (leftVolume > 0 || rightVolume > 0) mp.setVolume(leftVolume, rightVolume);
                // play stopped, pause will reset the current position so a resume will perform correctly
                mCurrentPosition = 0;
                // if playing, pause sounds
                if (mp.isPlaying()) {
                    mp.pause();
                    mCurrentPosition = mp.getCurrentPosition();
                    // if timed duration, start counter
                    if (fromTic > 0) {
                        CountDownTimer countdown = new CountDownTimer(fromTic, byTic) {
                            public void onTick(long millisUntilFinished) {
                                // pause
                                mp.pause();
                            }

                            public void onFinish() {
                                // resume
                                mp.seekTo(mCurrentPosition);
                                mp.start();
                            }
                        };
                        countdown.start();
                    }
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Oops!  media player not prepared? " + e.getMessage());
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
   public Boolean resumeSound(final MediaPlayer mp, float leftVolume, float rightVolume, int fromTic, int byTic) {
        Log.d(TAG, "startSound...");
        try {
            if (mp != null) {
                // set volume
                if (leftVolume > 0 || rightVolume > 0) mp.setVolume(leftVolume, rightVolume);
                // if not playing, resume sounds
                if (!mp.isPlaying()) {
                    mp.seekTo(mCurrentPosition);
                    mp.start();
                    // if timed duration, start counter
                    if (fromTic > 0) {
                        CountDownTimer countdown = new CountDownTimer(fromTic, byTic) {
                            public void onTick(long millisUntilFinished) {
                                mp.start();
                            }

                            public void onFinish() {
                                mp.stop();
                                Log.d(TAG, "resumeSound onFinish stop...");
                                mp.prepareAsync();
                                Log.d(TAG, "resumeSound onFinish prepareAsync...");
//                            mpTap.release();
                            }
                        };
                        countdown.start();
                    }
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Oops!  media player not prepared? " + e.getMessage());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean stopSound(final MediaPlayer mp) {
        Log.d(TAG, "stopSound...");
        try {
            if (mp != null) {
                // stop playing
                mp.stop();
//              mpTap.release();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Oops!  media player not prepared? " + e.getMessage());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
