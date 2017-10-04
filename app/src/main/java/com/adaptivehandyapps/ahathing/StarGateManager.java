/*
 * Project: AhaThing1
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker SEP 2017
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.MotionEvent;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicStarBoard;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;

import java.util.List;

////////////////////////////////////////////////////////////////////////////
// StarGateManager: manage StarGate actions and outcomes
public class StarGateManager {
    private static final String TAG = StarGateManager.class.getSimpleName();

    private Context mContext;
    private MainActivity mParent;

    ///////////////////////////////////////////////////////////////////////////
    // touch position
    private float mTouchX = 0.0f;
    private float mTouchY = 0.0f;
    private float mVelocityX = 0.0f;
    private float mVelocityY = 0.0f;
    private MotionEvent mEvent1;
    private MotionEvent mEvent2;

    private int mMarkIndex = DaoDefs.INIT_INTEGER_MARKER;
    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    private PlayListService getPlayListService() {
        return mParent.getPlayListService();
    }
    private RepoProvider getRepoProvider() {
        return mParent.getRepoProvider();
    }
    private SoundManager getSoundManager() {
        return mParent.getSoundManager();
    }

    private Boolean isSoundFlourish() {
        if (getPlayListService() != null && getPlayListService().getActiveTheatre() != null) {
            return getPlayListService().getActiveTheatre().getSoundFlourish();
        }
        return false;
    }
    private Boolean isSoundMusic() {
        if (getPlayListService() != null && getPlayListService().getActiveTheatre() != null) {
            return getPlayListService().getActiveTheatre().getSoundMusic();
        }
        return false;
    }
    private Boolean isSoundAction() {
        if (getPlayListService() != null && getPlayListService().getActiveTheatre() != null) {
            return getPlayListService().getActiveTheatre().getSoundAction();
        }
        return false;
    }

    public float getTouchX() {
        return mTouchX;
    }
    public void setTouchX(float touchX) {
        this.mTouchX = touchX;
    }

    public float getTouchY() {
        return mTouchY;
    }
    public void setTouchY(float touchY) {
        this.mTouchY = touchY;
    }

    public float getVelocityX() {
        return mVelocityX;
    }
    public void setVelocityX(float velocityX) {
        this.mVelocityX = velocityX;
    }

    public float getVelocityY() {
        return mVelocityY;
    }

    public void setVelocityY(float mVelocityY) {
        this.mVelocityY = mVelocityY;
    }

    public MotionEvent getEvent1() {
        return mEvent1;
    }
    public void setEvent1(MotionEvent event1) {
        this.mEvent1 = event1;
    }

    public MotionEvent getEvent2() {
        return mEvent2;
    }
    public void setEvent2(MotionEvent event2) {
        this.mEvent2 = event2;
    }

    public int getMarkIndex() {
        return mMarkIndex;
    }
    public void setMarkIndex(int markIndex) {
        this.mMarkIndex = markIndex;
    }

    ///////////////////////////////////////////////////////////////////////////
    public StarGateManager(Context context) {

        mContext = context;
        mParent = (MainActivity) context;
        if (mParent != null) {
            Log.v(TAG, "StarGateManager ready with parent " + mParent.toString() + "...");
            if (getSoundManager() != null && isSoundMusic()) {
                getSoundManager().startSound(
                        getSoundManager().getMpMusic(),
                        SoundManager.SOUND_VOLUME_QTR,
                        SoundManager.SOUND_VOLUME_QTR,
                        SoundManager.SOUND_START_TIC_NADA,
                        SoundManager.SOUND_START_TIC_NADA);
            }
//            else Log.e(TAG, "Oops!  SoundManager NULL?");
        }
        else {
            Log.e(TAG, "Oops!  StarGateManager Parent context (MainActivity) NULL!");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateModel(StarGateModel starGateModel) {
        Log.d(TAG, "updateModel...");
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onAction(StarGateView starGateView, StarGateModel starGateModel, String action) {
        Log.d(TAG, "onAction action " + action);
        // if music not playing & theatre music is enabled, start background music
        if (!getSoundManager().getMpMusic().isPlaying() && isSoundMusic()) {
            getSoundManager().startSound(
                    getSoundManager().getMpMusic(),
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
        }

        // if an activity is associated with action
        if (onActivity(action)) {
            // if theatre action sounds enabled
            if (isSoundAction()) {
                // play sound associated with action
                switch (action) {
                    case DaoAction.ACTION_TYPE_SINGLE_TAP:
                        getSoundManager().startSound(
                                getSoundManager().getMpTap(),
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_START_TIC_SHORT,
                                SoundManager.SOUND_START_TIC_SHORT);
                        break;
                    case DaoAction.ACTION_TYPE_LONG_PRESS:
                        getSoundManager().startSound(
                                getSoundManager().getMpPress(),
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_START_TIC_MEDIUM,
                                SoundManager.SOUND_START_TIC_MEDIUM);
                        break;
                    case DaoAction.ACTION_TYPE_FLING:
                        getSoundManager().startSound(
                                getSoundManager().getMpFling(),
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
        }
        else if (isSoundFlourish()){
            // no activity associated with action, play uh-uh sound
            getSoundManager().startSound(
                    getSoundManager().getMpUhuh(),
                    SoundManager.SOUND_VOLUME_FULL,
                    SoundManager.SOUND_VOLUME_FULL,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean onActivity(String action) {
        Log.d(TAG, "onActivity action " + action);
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean postCurtainCloseDialog(Context c, String title, DaoEpic epic, DaoStage stage) {
//    public static Boolean postCurtainCloseDialog(Context c, String title, DaoEpic epic, DaoStage stage) {
        final Context context = c;
        final DaoEpic daoEpic = epic;
        final DaoStage daoStage = stage;


        // post alert dialog
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage("Play an encore?")
                .setNegativeButton("Encore!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "curtain closing dialog - negative...");
                        // restart epic with all current stars
                        // TODO: separate clear stage from reset tally/tic
                        daoEpic.resetEpicStageTallyTic(daoStage, true, true);
//                        daoEpic.resetStarBoard();
                        // update repo
                        getRepoProvider().getDalEpic().update(daoEpic, true);
                        getRepoProvider().getDalStage().update(daoStage, true);

                    }
                })
                .setNeutralButton("Go Back.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "curtain closing dialog - neutral...");
                        // leave it be...
                    }
                })
                .setPositiveButton("Sign me out.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "curtain closing dialog - positive...");

                        // remove this star from starboard
                        int starInx = daoEpic.getStarList().indexOf(getPlayListService().getActiveActor().getMoniker());
                        daoEpic.removeStar(daoStage, starInx);
                        getRepoProvider().getDalEpic().update(daoEpic, true);
                        getRepoProvider().getDalStage().update(daoStage, true);

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        ;
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
}
