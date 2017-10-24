/*
 * Project: AhaThing1
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker OCT 2017
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
 */
package com.adaptivehandyapps.ahathing;

import android.util.Log;

import com.adaptivehandyapps.ahathing.dao.DaoAction;

////////////////////////////////////////////////////////////////////////////
// StageManager: manage stage actions and outcomes
public class SoundCheck {
    private static final String TAG = SoundCheck.class.getSimpleName();
    // enable/disable pause of music
    private static Boolean PAUSE_MUSIC = false;

    ///////////////////////////////////////////////////////////////////////////
    private static Boolean isSoundMusic(MainActivity mParent) {
        if (mParent != null &&
                mParent.getPlayListService() != null && mParent.getPlayListService().getActiveTheatre() != null) {
            return mParent.getPlayListService().getActiveTheatre().getSoundMusic();
        }
        return false;
    }
    private static Boolean isSoundAction(MainActivity mParent) {
        if (mParent != null &&
                mParent.getPlayListService() != null && mParent.getPlayListService().getActiveTheatre() != null) {
            return mParent.getPlayListService().getActiveTheatre().getSoundAction();
        }
        return false;
    }
    private static Boolean isSoundFlourish(MainActivity mParent) {
        if (mParent != null &&
                mParent.getPlayListService() != null && mParent.getPlayListService().getActiveTheatre() != null) {
            return mParent.getPlayListService().getActiveTheatre().getSoundFlourish();
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // sound checkers
    public static Boolean playSoundMusic(MainActivity mParent) {
        // if theatre music is enabled & not already playing, start background music
        if (isSoundMusic(mParent) &&
                mParent.getSoundManager() != null && !mParent.getSoundManager().getMpMusic().isPlaying()) {
            mParent.getSoundManager().startSound(
                    mParent.getSoundManager().getMpMusic(),
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static Boolean pauseSoundMusic(MainActivity mParent) {
        // if theatre music is enabled & is currently playing, pause background music
        if (PAUSE_MUSIC && isSoundMusic(mParent) &&
                mParent.getSoundManager() != null && mParent.getSoundManager().getMpMusic().isPlaying()) {
            mParent.getSoundManager().pauseSound(
                    mParent.getSoundManager().getMpMusic(),
                    SoundManager.SOUND_VOLUME_HALF,
                    SoundManager.SOUND_VOLUME_HALF,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
            return true;
        }
        return false;
}
    ///////////////////////////////////////////////////////////////////////////
    public static Boolean resumeSoundMusic(MainActivity mParent) {
        // if theatre music is enabled & not playing, resume background music
        if (PAUSE_MUSIC && isSoundMusic(mParent) &&
                mParent.getSoundManager() != null && !mParent.getSoundManager().getMpMusic().isPlaying()) {
            // resume background music sound
            mParent.getSoundManager().resumeSound(
                    mParent.getSoundManager().getMpMusic(),
                    SoundManager.SOUND_VOLUME_HALF,
                    SoundManager.SOUND_VOLUME_HALF,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static Boolean playSoundAction(MainActivity mParent, String action) {
        // if theatre action sounds enabled
        if (isSoundAction(mParent) && mParent.getSoundManager() != null) {

            // play sound associated with action
            switch (action) {
                case DaoAction.ACTION_TYPE_SINGLE_TAP:
                    mParent.getSoundManager().startSound(
                            mParent.getSoundManager().getMpTap(),
                            SoundManager.SOUND_VOLUME_FULL,
                            SoundManager.SOUND_VOLUME_FULL,
                            SoundManager.SOUND_START_TIC_SHORT,
                            SoundManager.SOUND_START_TIC_SHORT);
                    break;
                case DaoAction.ACTION_TYPE_LONG_PRESS:
                    mParent.getSoundManager().startSound(
                            mParent.getSoundManager().getMpPress(),
                            SoundManager.SOUND_VOLUME_FULL,
                            SoundManager.SOUND_VOLUME_FULL,
                            SoundManager.SOUND_START_TIC_MEDIUM,
                            SoundManager.SOUND_START_TIC_MEDIUM);
                    break;
                case DaoAction.ACTION_TYPE_FLING:
                    mParent.getSoundManager().startSound(
                            mParent.getSoundManager().getMpFling(),
                            SoundManager.SOUND_VOLUME_FULL,
                            SoundManager.SOUND_VOLUME_FULL,
                            SoundManager.SOUND_START_TIC_LONG,
                            SoundManager.SOUND_START_TIC_LONG);
                    break;
                case DaoAction.ACTION_TYPE_DOUBLE_TAP:
                    break;
                default:
                    Log.e(TAG, "Oops! Unknown action? " + action);
                    return false;
            }
        }

        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static Boolean playSoundFlorish(MainActivity mParent) {
        if (isSoundFlourish(mParent) && mParent.getSoundManager() != null) {
            // play uh-uh sound
            mParent.getSoundManager().startSound(
                    mParent.getSoundManager().getMpUhuh(),
                    SoundManager.SOUND_VOLUME_FULL,
                    SoundManager.SOUND_VOLUME_FULL,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
}
