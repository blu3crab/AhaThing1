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

public class StageManager {
    private static final String TAG = StageManager.class.getSimpleName();

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
    public StageManager(Context context) {

        mContext = context;
        mParent = (MainActivity) context;
        if (mParent != null) {
            Log.v(TAG, "StageManager ready with parent " + mParent.toString() + "...");
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
            Log.e(TAG, "Oops!  StageManager Parent context (MainActivity) NULL!");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onAction(StageViewRing stageViewRing, String action) {
        Log.d(TAG, "onAction action " + action);
        // if music not playing, start background music
        if (!getSoundManager().getMpMusic().isPlaying() && isSoundMusic()) {
            getSoundManager().startSound(
                    getSoundManager().getMpMusic(),
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
        }

        // if story exists associating the active actor (or all actor) with the action
        if (updatePlaylist(action)) {
            int musicPosition = 0;

            // if prereq satisfied
            if (isPreReqSatisfied(stageViewRing)) {
//            if (isSoundMusic())
//                getSoundManager().pauseSound(
//                        getSoundManager().getMpMusic(),
//                        SoundManager.SOUND_VOLUME_HALF,
//                        SoundManager.SOUND_VOLUME_HALF,
//                        SoundManager.SOUND_START_TIC_NADA,
//                        SoundManager.SOUND_START_TIC_NADA);

                if (isSoundAction()) {
                    // execute outcome
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

                // increment active actors star board tic
                DaoEpic daoEpic = getPlayListService().getActiveEpic();
                DaoStage daoStage = getPlayListService().getActiveStage();
                if (daoEpic != null && daoStage != null && getPlayListService().getActiveActor() != null) {
                    int starInx = daoEpic.getStarList().indexOf(getPlayListService().getActiveActor().getMoniker());
                    if (starInx > -1) {
                        // increment tic for actor
                        int tic = daoEpic.getStarBoardList().get(starInx).getTic();
                        daoEpic.getStarBoardList().get(starInx).setTic(++tic);
                        Log.d(TAG, "Tic " + tic + " for actor " + daoEpic.getStarBoardList().get(starInx).getStarMoniker());
                        // update epic tally based on stage ring locations occupied
                        daoEpic.updateEpicTally(daoStage);
                        // update epic repo
                        getRepoProvider().getDalEpic().update(daoEpic, true);
                    }
                    // deliver outcome
                    DaoOutcome daoOutcome = getPlayListService().getActiveOutcome();
                    if (daoOutcome != null) {
                        onOutcome(stageViewRing, daoOutcome.getMoniker());
                    }
                    else {
                        Log.e(TAG,"Oops! no active outcome...");
                    }

                    // if post-operation indicated
                    onPostOp();
                }
                else {
                    if (daoEpic == null ) Log.e(TAG, "Oops! Active epic NULL! ");
                    else if (daoStage == null) Log.e(TAG, "Oops! Active stage NULL! ");
                    else Log.e(TAG, "Oops! Active actor NULL! ");
                }
            }
            else if (isSoundFlourish()){
                // play uh-uh sound
                getSoundManager().startSound(
                        getSoundManager().getMpUhuh(),
                        SoundManager.SOUND_VOLUME_FULL,
                        SoundManager.SOUND_VOLUME_FULL,
                        SoundManager.SOUND_START_TIC_NADA,
                        SoundManager.SOUND_START_TIC_NADA);
            }
//            if (isSoundMusic())
//            // resume background music sound
//            getSoundManager().resumeSound(
//                    getSoundManager().getMpMusic(),
//                    SoundManager.SOUND_VOLUME_HALF,
//                    SoundManager.SOUND_VOLUME_HALF,
//                    SoundManager.SOUND_START_TIC_NADA,
//                    SoundManager.SOUND_START_TIC_NADA);

            return true;
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean isPreReqSatisfied(StageViewRing stageViewRing) {
        DaoStory activeStory = getPlayListService().getActiveStory();
        if (stageViewRing != null && activeStory != null && getPlayListService().getActiveActor() != null) {
            if (activeStory.getPreReq().equals(DaoDefs.INIT_STRING_MARKER) ||
                    activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_NONE) ||
                    stageViewRing == null) {
                Log.d(TAG, "isPreReqSatisfied-> no pre-req defined...");
                return true;
            } else {
                DaoStage daoStage = getPlayListService().getActiveStage();
                if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                    // get ring index of touch to determine actor at vert
                    float touchX = getTouchX();
                    float touchY = getTouchY();
                    float z = 0.0f;
                    int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                    if (selectIndex < daoStage.getActorList().size()) {
                        String vertActor = daoStage.getActorList().get(selectIndex);
                        // get active actor
                        String activeActor = getPlayListService().getActiveActor().getMoniker();
                        Log.d(TAG, "isPreReqSatisfied(" + activeStory.getPreReq() + ")-> vertActor, activeActor= " + vertActor + ", " + activeActor);
                        if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_OWNED)) {
                            // if vert is owned by active player, return true
                            if (vertActor.equals(activeActor)) return true;
                        } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_BLOCKED)) {
                            // if vert is blocked by another player, return true
                            if (!vertActor.equals(activeActor) && !vertActor.equals(DaoDefs.INIT_STRING_MARKER))
                                return true;
                        } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_EMPTY)) {
                            // if vert is empty, return true
                            if (vertActor.equals(DaoDefs.INIT_STRING_MARKER)) return true;
                        }
                    } else {
                        // ArrayIndexOutOfBoundsException
                        Log.e(TAG, "Selected ring index (" + selectIndex + ") out of bounds for stage actorList size " + daoStage.getActorList().size());
                        return false;
                    }
                } else {
                    if (activeStory == null) Log.e(TAG, "Oops!  no active story...");
                    else if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
                    else if (getPlayListService().getActiveActor() == null) Log.e(TAG, "Oops!  no active actor...");
                    else Log.e(TAG, "Oops! Unknown stage type " + daoStage.getStageType());
                }
            }
        }
        else {
            if (stageViewRing == null) Log.e(TAG, "Oops!  stageViewRing NULL...");
            else Log.e(TAG, "Oops! No active actor...");
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean onPostOp() {
        DaoStory activeStory = getPlayListService().getActiveStory();
        if (activeStory != null) {
            // return true if no post-op defined
            if (activeStory.getPostOp().equals(DaoDefs.INIT_STRING_MARKER) ||
                    activeStory.getPreReq().equals(DaoStory.STORY_POSTOP_NONE)) {
                Log.d(TAG, "onPostOp-> no post-op defined...");
                return true;
            } else {
                DaoStage daoStage = getPlayListService().getActiveStage();
                if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                    if (activeStory.getPostOp().equals(DaoStory.STORY_POSTOP_CURTAIN_CALL)) {
                        DaoEpic daoEpic = getPlayListService().getActiveEpic();
                        if (daoEpic != null) {
                            // curtains should come down
                            if (daoEpic.isCurtainClose()) {
                                // bring down the curtain!  prompt for an encore
                                List<DaoEpicStarBoard> starBoardList = daoEpic.getTallyOrder(false);
                                String title = starBoardList.get(0).getStarMoniker() + " dominates the universe of Marbles!";
                                postCurtainCloseDialog(mContext, title, daoEpic, daoStage);
                            }
                        }
                        else {
                            Log.e(TAG, "Oops!  no active epic...");
                        }

//                    for (String star : daoEpic.getStarList()) {
//                        int starInx = daoEpic.getStarList().indexOf(star);
//                        Log.d(TAG, " star " + star + "(" + starInx + ") has tally " + daoEpic.getStarBoardList().get(starInx).getTally());
//                    }
                    }
                    else {
                        Log.e(TAG, "Oops!  Unknown postop " + activeStory.getPostOp());
                    }
                }
                else {
                    if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
                    else Log.e(TAG, "Oops! Unknown stage type " + daoStage.getStageType());
                }
            }
        }
        else {
            Log.e(TAG, "Oops!  no active story...");
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean postCurtainCloseDialog(Context c, String title, DaoEpic epic, DaoStage stage) {
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
    public Boolean updatePlaylist(String action) {
        Log.d(TAG, "updatePlaylist action " + action);
            // if story exists associating the active actor (or all actor) with the action
            DaoStory daoStory = isStory(action);
            if (daoStory != null) {
                // set active: story, action, outcome  (actor is already active)
                getPlayListService().setActiveStory(daoStory);
                DaoAction daoAction = (DaoAction) getRepoProvider().getDalAction().getDaoRepo().get(daoStory.getAction());
                getPlayListService().setActiveAction(daoAction);
                DaoOutcome daoOutcome = (DaoOutcome) getRepoProvider().getDalOutcome().getDaoRepo().get(daoStory.getOutcome());
                getPlayListService().setActiveOutcome(daoOutcome);
                return true;
            }
//        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // if active actor is associated with incoming action - return story
    public DaoStory isStory(String action) {
        DaoEpic activeEpic = getPlayListService().getActiveEpic();
        if (activeEpic != null) {
            // for each story in epic
            for (Integer i = 0; i < activeEpic.getTagList().size(); i++) {
                String storyMoniker = activeEpic.getTagList().get(i);
                DaoStory daoStory = (DaoStory) getRepoProvider().getDalStory().getDaoRepo().get(storyMoniker);
                if (daoStory != null && getPlayListService().getActiveActor() != null) {
                    Log.v(TAG, "test story " + daoStory);
                    // if any actor or active actor  &&  action match
                    if ((daoStory.getActor().contains("*") ||
                            daoStory.getActor().equals(getPlayListService().getActiveActor().getMoniker())) &&
                            daoStory.getAction().equals(action)) {
                        Log.v(TAG, "returning outcome " + daoStory.getOutcome());
                        return daoStory;
                    }
                }
                else {
                    if (daoStory == null) Log.e(TAG, "oops! no story found matching " + activeEpic.getTagList().get(i) + "...");
                    else Log.e(TAG,"Oops! no active actor");
                }
            }
        }
        else {
            Log.e(TAG, "Oops! no active epic...");
        }
        return null;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Outcomes
    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onOutcome(StageViewRing stageViewRing, String outcome) {
        Log.d(TAG, "onOutcome action " + outcome);
        // execute outcome
        switch (outcome) {
            case DaoOutcome.OUTCOME_TYPE_TOGGLE:
                // toggle selection
                if (stageViewRing == null) return false;
                toggleActorSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f, false);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PLUS:
                if (stageViewRing == null) return false;
                // toggle selection plus adjacent
                toggleActorSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f, true);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PATH:
                if (stageViewRing == null) return false;
                // toggle selection plus adjacent
                toggleActorPath(stageViewRing,
                        getVelocityX(), getVelocityY(),
                        getEvent1().getX(), getEvent1().getY(),
                        getEvent2().getX(), getEvent2().getY());
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PROP:
                // toggle prop at selection
                if (stageViewRing == null) return false;
                togglePropSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_AREA:
                // toggle area at selection to fill or clear
                if (stageViewRing == null) return false;
                toggleAreaSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_MARK_ACTOR:
                // toggle area at selection to fill or clear
                if (stageViewRing == null) return false;
                markActor(stageViewRing, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_MOVE_ACTOR:
                // toggle area at selection to fill or clear
                if (stageViewRing == null) return false;
                moveActor(stageViewRing, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_RESET_EPIC:
                // clear actors on stage
                clearActors();
                return true;
            default:
                Log.e(TAG, "Oops! Unknown outcome? " + outcome);
        }
        // TODO: if outcome positive, update repo
        // update object
//        getRepoProvider().getDalStage().update(daoStage, true);


        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // select actor at selection
    public Boolean markActor(StageViewRing stageViewRing, float touchX, float touchY, float z) {
        Log.d(TAG, "markActor touch (x,y) " + touchX + ", " + touchY);
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            if (stageViewRing != null && getRepoProvider().getStageModelRing() != null) {
                // get ring index
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                // if touch found & actor present
                if (selectIndex != DaoDefs.INIT_INTEGER_MARKER && !daoStage.getActorList().get(selectIndex).equals(DaoDefs.INIT_INTEGER_MARKER)) {
                    // mark actor
                    setMarkIndex(selectIndex);
                    Log.d(TAG, "markActor " + daoStage.getActorList().get(selectIndex) + " at locus " + selectIndex);
                }
            } else {
                Log.e(TAG, "Oops! stageViewRing NULL or stageModelRing NULL...");
            }
        } else {
            if (daoStage == null) Log.e(TAG, "Oops! No active stage...");
            else Log.e(TAG, "markActor UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // select actor at selection
    public Boolean moveActor(StageViewRing stageViewRing, float touchX, float touchY, float z) {
        Log.d(TAG, "moveActor touch (x,y) " + touchX + ", " + touchY);
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            if (stageViewRing != null && getRepoProvider().getStageModelRing() != null) {
                // get ring index
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                // if touch found & actor not present
                if (selectIndex != DaoDefs.INIT_INTEGER_MARKER && daoStage.getActorList().get(selectIndex).equals(DaoDefs.INIT_STRING_MARKER)) {
                    Log.d(TAG, "moveActor " + daoStage.getActorList().get(getMarkIndex()) + " from locus " + + getMarkIndex() + " to " + selectIndex);
                    // move actor from mark to move selection
                    daoStage.getActorList().set(selectIndex, daoStage.getActorList().get(getMarkIndex()));
                    // clear actor at mark index
                    daoStage.getActorList().set(getMarkIndex(), DaoDefs.INIT_STRING_MARKER);
                    // reset mark index to select
                    setMarkIndex(selectIndex);
                    // update object
                    getRepoProvider().getDalStage().update(daoStage, true);
                }
            } else {
                Log.e(TAG, "Oops! stageViewRing NULL or stageModelRing NULL...");
            }
        } else {
            if (daoStage == null) Log.e(TAG, "Oops! No active stage...");
            else Log.e(TAG, "moveActor UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // toggle area at selection - filling area with active actor or clearing the area
    public Boolean toggleAreaSelection(StageViewRing stageViewRing, float touchX, float touchY, float z) {
        Log.d(TAG, "toggleAreaSelection touch (x,y) " + touchX + ", " + touchY);
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            if (stageViewRing != null && getRepoProvider().getStageModelRing() != null) {
                // get ring index
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                // if touch found
                if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
                    if (getPlayListService().getActiveActor() != null) {
                        Log.d(TAG, "toggleAreaSelection (" + selectIndex + ") for actor " + daoStage.getActorList().get(selectIndex));
                        // default moniker to active actor
                        String actorMoniker = getPlayListService().getActiveActor().getMoniker();
                        // if actor present at selected locus then set moniker to clear the actor list
                        if (daoStage.getActorList().get(selectIndex).equals(actorMoniker)) actorMoniker = DaoDefs.INIT_STRING_MARKER;
                        // set actor at locus
                        daoStage.getActorList().set(selectIndex, actorMoniker);
                        // build ring list around selection
                        List<Integer> r1IndexList = getRepoProvider().getStageModelRing().findRing(selectIndex);
                        // for each locus in ring 1 list
                        for (Integer r1 : r1IndexList) {
                            // if not forbidden
                            if (!daoStage.getPropList().get(r1).equals(DaoStage.PROP_TYPE_FORBIDDEN)) {
                                // set actor at locus
                                daoStage.getActorList().set(r1, actorMoniker);
                                // build ring list
                                List<Integer> r2IndexList = getRepoProvider().getStageModelRing().findRing(r1);
                                for (Integer r2 : r2IndexList) {
                                    if (!daoStage.getPropList().get(r2).equals(DaoStage.PROP_TYPE_FORBIDDEN)) {
                                        daoStage.getActorList().set(r2, actorMoniker);
                                    }
                                }
                            }
                        }
                        // update object
                        getRepoProvider().getDalStage().update(daoStage, true);
                    }
                    else {
                        Log.e(TAG,"Oops! No active actor...");
                    }
                }
            }
            else {
                Log.e(TAG,"Oops! stageViewRing NULL or stageModelRing NULL...");
            }
        }
        else {
            if (daoStage == null) Log.e(TAG,"Oops! No active stage...");
            else Log.e(TAG, "toggleAreaSelection UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean togglePropSelection(StageViewRing stageViewRing, float touchX, float touchY, float z) {
        Log.d(TAG, "togglPropSelection touch (x,y) " + touchX + ", " + touchY);
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            if (stageViewRing != null) {
                // get ring index
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                // if touch found
                if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
                        Log.d(TAG, "togglPropSelection (" + selectIndex + ") for prop " + daoStage.getPropList().get(selectIndex));
                        if (!daoStage.togglePropList(DaoStage.PROP_TYPE_FORBIDDEN, selectIndex)) {
                            Log.e(TAG, "Ooops! toggleActorSelection UNKNOWN stage type? " + daoStage.getStageType());
                        }
                        // update object
                        getRepoProvider().getDalStage().update(daoStage, true);
                }
            }
            else {
                Log.e(TAG,"Oops! stageViewRing NULL...");
            }
        }
        else {
            if (daoStage == null) Log.e(TAG,"Oops! No active stage...");
            else Log.e(TAG, "toggleActorSelection UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toggleActorSelection(StageViewRing stageViewRing, float touchX, float touchY, float z, Boolean plus) {
        Log.d(TAG, "toggleActorSelection touch (x,y) " + touchX + ", " + touchY);
//        int selectIndex = DaoDefs.INIT_INTEGER_MARKER;
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            if (stageViewRing != null) {
                // get ring index
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                // if touch found
                if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
                    if (getPlayListService().getActiveActor() != null) {
                        Log.d(TAG, "toggleActorSelection (" + selectIndex + ") for actor " + daoStage.getActorList().get(selectIndex));
                        if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), selectIndex)) {
                            Log.e(TAG, "Ooops! toggleActorSelection UNKNOWN stage type? " + daoStage.getStageType());
                        }
                        // if selecting plus ring
                        if (plus) {
                            if (getRepoProvider().getStageModelRing() != null) {
                                List<Integer> ringIndexList = getRepoProvider().getStageModelRing().findRing(selectIndex);
                                // toggle each rect in ring list
                                for (Integer i : ringIndexList) {
                                    if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), i)) {
                                        Log.e(TAG, "Ooops! toggleActorSelection UNKNOWN stage type? " + daoStage.getStageType());
                                    }
                                }
                            } else {
                                Log.e(TAG, "Oops! for repo " + getRepoProvider().toString() + " NULL getStageModelRing()...");
                            }
                        }
                        // update object
                        getRepoProvider().getDalStage().update(daoStage, true);
                    }
                    else {
                        Log.e(TAG,"Oops! No active actor...");
                    }
                }
            }
            else {
                Log.e(TAG,"Oops! stageViewRing NULL...");
            }
        }
        else {
            if (daoStage == null) Log.e(TAG,"Oops! No active stage...");
            else Log.e(TAG, "toggleActorSelection UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toggleActorPath(StageViewRing stageViewRing, float velocityX, float velocityY,
                                   float event1X, float event1Y, float event2X, float event2Y) {

        // sum velocity ignoring direction (sign)
        float velocity = Math.abs(velocityX) + Math.abs(velocityY);
        Log.d(TAG, "toggleActorPath velocity sum = " + velocity);
        // set # intervals based on velocity
        float intervalCount = velocity / 1000.0f;
        Log.d(TAG, "toggleActorPath intervalCount = " + intervalCount);
        // find angle of fling
        double deltaX = event2X - event1X;
        double deltaY =  -(event2Y - event1Y);
        double thetaRad = Math.atan2(deltaY, deltaX);
        double angle = thetaRad * (180.0f/Math.PI);
        Log.d(TAG, "toggleActorPath thetaRad = " + thetaRad + ", angle = " + angle);

        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && stageViewRing != null) {
            // get ring index
            Log.d(TAG, "toggleActorPath origin X, Y " + event1X + ", " + event1Y);
            int ringIndex = stageViewRing.getRingIndex(event1X, event1Y, 0.0f);
            // toggle initial position
            if (ringIndex != DaoDefs.INIT_INTEGER_MARKER) {
                if (getPlayListService().getActiveActor() != null) {
                    if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                        Log.e(TAG, "Ooops! toggleActorPath UNKNOWN stage type? " + daoStage.getStageType());
                    }
                    // for each interval, generate point along angle
                    for (int i = 1; i < intervalCount; i++) {
                        int prevRingIndex = ringIndex;
                        float x = (float) (event1X + ((StageModelRing.LOCUS_DIST * i) * Math.cos(thetaRad)));
                        float y = (float) (event1Y - ((StageModelRing.LOCUS_DIST * i) * Math.sin(thetaRad)));
                        Log.d(TAG, "toggleActorPath toggle for interval " + i + " at X, Y " + x + ", " + y);
                        // find ring index at interval position
                        ringIndex = stageViewRing.getRingIndex(x, y, 0.0f);
                        // if not previously toggled
                        if (ringIndex != DaoDefs.INIT_INTEGER_MARKER && ringIndex != prevRingIndex) {
                            // toggle at interval position
                            if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                                Log.e(TAG, "Ooops! toggleActorPath UNKNOWN stage type? " + daoStage.getStageType());
                            }
                        }
                    }
                    // update object
                    getRepoProvider().getDalStage().update(daoStage, true);
                }
                else {
                    Log.e(TAG, "Oops!  no active actor...");
                }
            } else {
                Log.e(TAG, "toggleActorPath touch out of bounds...");
            }
        }
        else {
            if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
            else Log.e(TAG, "Oops!  unkonwn stage type..." + daoStage.getStageType());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean clearActors() {
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.setActorList(DaoDefs.INIT_STRING_MARKER)) {
            // update object
            getRepoProvider().getDalStage().update(daoStage, true);
            return false;
        }
        else {
            if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
            else Log.e(TAG, "Oops! UNKNOWN stage type? " + daoStage.getStageType());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
