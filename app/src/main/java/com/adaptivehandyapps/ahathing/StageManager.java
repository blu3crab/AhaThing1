/*
 * Project: AhaThing1
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
 */
package com.adaptivehandyapps.ahathing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicActorBoard;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;

import java.util.List;
////////////////////////////////////////////////////////////////////////////
// StageManager: manage stage actions and outcomes
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

//    private int mMarkIndex = DaoDefs.INIT_INTEGER_MARKER;
    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    private PlayListService getPlayListService() {
        return mParent.getPlayListService();
    }
    private RepoProvider getRepoProvider() {
        return mParent.getRepoProvider();
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

//    public int getMarkIndex() {
//        return mMarkIndex;
//    }
    public void setMarkIndex(int markIndex) {
        // TODO: if mark, set stage, update repo, start timer
        // TODO: if unmark, set stage, update repo, clear timer
    }
    // TODO: set timer, when countdown unmark, advance turn order

    ///////////////////////////////////////////////////////////////////////////
    public StageManager(Context context) {

        mContext = context;
        mParent = (MainActivity) context;
        if (mParent != null) {
            Log.v(TAG, "StageManager ready with parent " + mParent.toString() + "...");
            // if music not playing & theatre music is enabled, start background music
            SoundCheck.playSoundMusic(mParent);
        }
        else {
            Log.e(TAG, "Oops!  StageManager Parent context (MainActivity) NULL!");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OnAction
    //      validation:
    //          activeEpic
    //          activeStage
    //          activeActor
    //          activeStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)
    //          actorBoardInx
    //          activeStory
    //          activeAction
    //          activeOutcome
    //
    public Boolean onAction(StageViewRing stageViewRing, StageModelRing stageModelRing, String action) {
        Boolean success = false;
        Log.d(TAG, "onAction action " + action);
        // if stage view or model undefined, log error
        if (stageViewRing == null || stageModelRing == null) {
            Log.e(TAG, "onAction finds NULL StageViewRing or stageModelRing.");
            return false;
        }
        // if music not playing & theatre music is enabled, start background music
        SoundCheck.playSoundMusic(mParent);

        // get active epic, stage, actor
        DaoEpic activeEpic = getPlayListService().getActiveEpic();
        DaoStage activeStage = getPlayListService().getActiveStage();
        DaoActor activeActor = getPlayListService().getActiveActor();
        // if active epic, stage, actor undefined, log error
        if (activeEpic == null || activeStage == null || activeActor == null) {
            Log.e(TAG, "onAction finds NULL Epic, Stage, Actor...");
            return false;
        }
        // only RING WORLDS allowed
        if (!activeStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            Log.e(TAG, "onAction finds unknown stage type(" + activeStage.getStageType() + ").");
            return false;
        }
        // confirm actor at touch selection is epic active actor
        int actorBoardInx = activeEpic.getEpicActorList().indexOf(activeActor.getMoniker());
        if ( actorBoardInx == DaoDefs.INIT_INTEGER_MARKER) {
            // log epic starboard
            for (String actor : activeEpic.getEpicActorList()) {
                int actorInx = activeEpic.getEpicActorList().indexOf(actor);
                Log.d(TAG, "ActorBoard-> actor " + actor + "(" + actorInx + ")");
            }
            Log.e(TAG, "onAction finds active actor ("  + activeActor.getMoniker() + ") not defined for Epic.");
            return false;
        }
        // if active actor is allowed
        if (isActorAllowed(activeEpic, activeStage, stageViewRing)) {

            // if story exists associating the active actor (or all actors) with the action
            if (updatePlaylist(action, activeEpic, activeActor)) {
                // update playlist to reflect story, action, outcome
                DaoStory activeStory = getPlayListService().getActiveStory();
                DaoAction activeAction = getPlayListService().getActiveAction();
                DaoOutcome activeOutcome = getPlayListService().getActiveOutcome();
                // if active epic, stage, actor undefined, log error
                if (activeStory == null || activeAction == null || activeOutcome == null) {
                    Log.e(TAG, "onAction finds NULL Story, Action, Outcome...");
                    return false;
                }

                // if prereq satisfied
                if (isPreReqSatisfied(stageViewRing, activeStage, activeStory, activeActor)) {
                    // pause music?
                    SoundCheck.pauseSoundMusic(mParent);

                    // play action sounds
                    SoundCheck.playSoundAction(mParent, action);

                    // deliver outcome
                    onOutcome(stageViewRing, stageModelRing, activeEpic, activeStage, activeActor, activeOutcome.getMoniker());

                    // if post-operation indicated
                    onPostOp(activeEpic, activeStage, activeStory);

                    // resume music?
                    SoundCheck.resumeSoundMusic(mParent);
                    success = true;

                } else {
                    // play uh-uh sound
                    SoundCheck.playSoundFlorish(mParent);
                    Log.d(TAG, "Oops! Prereq not satisfied...");
                }
            } else {
                Log.d(TAG, "Oops! updatePlaylist finds no matching story...");
            }
        }
        else {
            Log.d(TAG, "Oops! isActorAllowed finds daoEpic.getOrder actor " + activeEpic.getActiveActor() + " not selected...");
        }
        return success;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean isActorAllowed(DaoEpic daoEpic, DaoStage daoStage, StageViewRing stageViewRing) {
        // if order not random (last entry in order list)
        if (!daoEpic.getOrder().equals(DaoEpic.EPIC_ORDER_LIST.get(DaoEpic.EPIC_ORDER_LIST.size()-1))) {
            String vertActor = DaoDefs.INIT_STRING_MARKER;
            // get ring index of touch to determine actor at vert
            float touchX = getTouchX();
            float touchY = getTouchY();
            float z = 0.0f;
            int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
            // if touch locus found
            if (selectIndex >= 0 && selectIndex < daoStage.getActorList().size()) {
                vertActor = daoStage.getActorList().get(selectIndex);
                // if actor at touch matches active actor or locus is empty
                if (vertActor.equals(daoEpic.getActiveActor()) ||

                        vertActor.equals(DaoDefs.INIT_STRING_MARKER)) return true;
            }
            // no match between epic active actor & actor at touch selection
            Toast.makeText(mContext, "Active actor " + daoEpic.getActiveActor() + " not found at selection (" + vertActor + "...", Toast.LENGTH_SHORT).show();
            return false;
        }
        // random order allows any actor
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean updatePlaylist(String action, DaoEpic activeEpic, DaoActor activeActor) {
        Log.d(TAG, "updatePlaylist action " + action);
        // if story exists associating the active actor (or all actor) with the action
        DaoStory daoStory = isStory(action, activeEpic, activeActor);
        if (daoStory != null) {
            // set active: story, action, outcome  (actor is already active)
            getPlayListService().setActiveStory(daoStory);
            DaoAction daoAction = (DaoAction) getRepoProvider().getDalAction().getDaoRepo().get(daoStory.getAction());
            getPlayListService().setActiveAction(daoAction);
            DaoOutcome daoOutcome = (DaoOutcome) getRepoProvider().getDalOutcome().getDaoRepo().get(daoStory.getOutcome());
            getPlayListService().setActiveOutcome(daoOutcome);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // if active actor is associated with incoming action - return story
    private DaoStory isStory(String action, DaoEpic activeEpic, DaoActor activeActor) {
        // for each story in epic
        for (Integer i = 0; i < activeEpic.getTagList().size(); i++) {
            String storyMoniker = activeEpic.getTagList().get(i);
            DaoStory daoStory = (DaoStory) getRepoProvider().getDalStory().getDaoRepo().get(storyMoniker);
            if (daoStory != null) {
                Log.v(TAG, "test story " + daoStory);
                // if any actor or active actor  &&  action match
                if ((daoStory.getActor().contains(DaoDefs.ANY_ACTOR_WILDCARD) ||
                        daoStory.getActor().equals(activeActor.getMoniker())) &&
                            daoStory.getAction().equals(action)) {
                    Log.v(TAG, "returning story " + daoStory.getMoniker() + " with outcome " + daoStory.getOutcome());
                    return daoStory;
                }
            }
            else {
                Log.e(TAG, "oops! no story found matching " + activeEpic.getTagList().get(i) + "...");
            }
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean isPreReqSatisfied(StageViewRing stageViewRing,
                                      DaoStage activeStage, DaoStory activeStory, DaoActor activeActor) {
            if (activeStory.getPreReq().equals(DaoDefs.INIT_STRING_MARKER) ||
                    activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_NONE) ||
                    stageViewRing == null) {
                Log.d(TAG, "isPreReqSatisfied-> no pre-req defined...");
                return true;
            } else {
                // get ring index of touch to determine actor at vert
                float touchX = getTouchX();
                float touchY = getTouchY();
                float z = 0.0f;
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                if (selectIndex >= 0 && selectIndex < activeStage.getActorList().size()) {
                    String vertActor = activeStage.getActorList().get(selectIndex);
                    // get active actor
                    String actorMoniker = activeActor.getMoniker();
                    Log.d(TAG, "isPreReqSatisfied(" + activeStory.getPreReq() + ")-> vertActor, activeActor= " + vertActor + ", " + activeActor);
                    if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_OWNED)) {
                        // if vert is owned by active player, return true
                        if (vertActor.equals(actorMoniker)) return true;
                    } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_BLOCKED)) {
                        // if vert is blocked by another player, return true
                        if (!vertActor.equals(actorMoniker) && !vertActor.equals(DaoDefs.INIT_STRING_MARKER))
                            return true;
                    } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_EMPTY)) {
                        // if vert is empty, return true
                        if (vertActor.equals(DaoDefs.INIT_STRING_MARKER)) return true;
                    }
                } else {
                    // ArrayIndexOutOfBoundsException
                    Log.e(TAG, "Selected ring index (" + selectIndex + ") out of bounds for stage actorList size " + activeStage.getActorList().size());
                    return false;
                }
            }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Outcomes
    private Boolean onOutcome(StageViewRing stageViewRing, StageModelRing stageModelRing,
                              DaoEpic daoEpic, DaoStage daoStage, DaoActor daoActor,
                              String outcome) {
        Log.d(TAG, "onOutcome action " + outcome);
        // execute outcome
        switch (outcome) {
            case DaoOutcome.OUTCOME_TYPE_TOGGLE:
                // toggle selection
                toggleActorSelection(stageViewRing, stageModelRing, daoStage, daoActor,
                                        getTouchX(), getTouchY(), 0.0f, false);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PLUS:
                // toggle selection plus adjacent
                toggleActorSelection(stageViewRing, stageModelRing, daoStage, daoActor,
                                        getTouchX(), getTouchY(), 0.0f, true);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PATH:
                // toggle selection plus adjacent
                toggleActorPath(stageViewRing, daoStage, daoActor,
                        getVelocityX(), getVelocityY(),
                        getEvent1().getX(), getEvent1().getY(),
                        getEvent2().getX(), getEvent2().getY());
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PROP:
                // toggle prop at selection
                togglePropSelection(stageViewRing, daoStage, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_AREA:
                // toggle area at selection to fill or clear
                toggleAreaSelection(stageViewRing, stageModelRing, daoStage, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_MIRROR:
                // toggle area at selection to fill or clear
                toggleMirrorSelection(stageViewRing, daoStage, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_MARK_ACTOR:
                // toggle area at selection to fill or clear
                markActor(stageViewRing, stageModelRing, daoEpic, daoStage, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_MOVE_ACTOR:
                // toggle area at selection to fill or clear
                moveActor(stageViewRing, stageModelRing, daoStage, getTouchX(), getTouchY(), 0.0f);
                return true;
            case DaoOutcome.OUTCOME_TYPE_MARKMOVE_ACTOR:
                // if mark action not performed
                if (!markActor(stageViewRing, stageModelRing, daoEpic, daoStage,
                                getTouchX(), getTouchY(), 0.0f)) {
                    // try moving
                    moveActor(stageViewRing, stageModelRing, daoStage, getTouchX(), getTouchY(), 0.0f);
                }
                return true;
            case DaoOutcome.OUTCOME_TYPE_RESET_EPIC:
                // clear actors on stage
                clearActors(daoStage);
                return true;
            default:
                Log.e(TAG, "Oops! Unknown outcome? " + outcome);
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean onPostOp(DaoEpic daoEpic, DaoStage daoStage, DaoStory activeStory) {
        // return true if no post-op defined
        if (activeStory.getPostOp().equals(DaoDefs.INIT_STRING_MARKER) ||
                activeStory.getPostOp().equals(DaoStory.STORY_POSTOP_NONE)) {
            Log.d(TAG, "onPostOp-> no post-op defined...");
            return true;
        }
        else if (activeStory.getPostOp().equals(DaoStory.STORY_POSTOP_CURTAIN_CALL)) {
            // curtains should come down
            if (daoEpic.isCurtainClose(daoStage)) {
                // bring down the curtain!  prompt for an encore
                List<DaoEpicActorBoard> starBoardList = daoEpic.getTallyOrder(false);
                String title = starBoardList.get(0).getActorMoniker() + " dominates the universe of Marbles!";
                postCurtainCloseDialog(mContext, title, daoEpic, daoStage);
            }
            // update epic repo, at the very least, tic will be incremented for active actor
            getRepoProvider().getDalEpic().update(daoEpic, true);

        }
        else {
            Log.e(TAG, "Oops!  Unknown postop " + activeStory.getPostOp());
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean postCurtainCloseDialog(Context c, String title, DaoEpic epic, DaoStage stage) {
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
                        int starInx = daoEpic.getEpicActorList().indexOf(getPlayListService().getActiveActor().getMoniker());
                        daoEpic.removeActor(daoStage, starInx);
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
    // mark actor at selection
    private Boolean markActor(StageViewRing stageViewRing, StageModelRing stageModelRing,
                              DaoEpic daoEpic, DaoStage daoStage,
                             float touchX, float touchY, float z) {
        Log.d(TAG, "markActor touch (x,y) " + touchX + ", " + touchY);
        // get ring index
        int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
        // if touch found
        if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
            // if actor present (not nada)
            if (!daoStage.getActorList().get(selectIndex).equals(DaoDefs.INIT_STRING_MARKER)) {
                // if touch of marked actor
                if (selectIndex == daoStage.getMarkIndex()) {
                    // clear mark
                    daoStage.setMarkIndex(DaoDefs.INIT_INTEGER_MARKER);
                    Log.d(TAG, "unmarkActor " + daoStage.getActorList().get(selectIndex) + " at locus " + selectIndex);
                    // advance active actor based on order
                    daoEpic.advanceActiveActor();
                    DaoActor daoActor = (DaoActor) getRepoProvider().getDalActor().getDaoRepo().get(daoEpic.getActiveActor());
                    if (daoActor != null) {
                        getPlayListService().setActiveActor(daoActor);
                        // update epic repo
                        mParent.getRepoProvider().getDalEpic().update(daoEpic, true);
                    }
                }
                else {
                    // mark actor
                    daoStage.setMarkIndex(selectIndex);
                    Log.d(TAG, "markActor " + daoStage.getActorList().get(selectIndex) + " at locus " + selectIndex);
                }
                // update stage repo
                mParent.getRepoProvider().getDalStage().update(daoStage, true);
                // indicate mark or unmark occurred
                return true;
            }
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // select actor at selection
    private Boolean moveActor(StageViewRing stageViewRing, StageModelRing stageModelRing, DaoStage daoStage,
                             float touchX, float touchY, float z) {
        Log.d(TAG, "moveActor touch (x,y) " + touchX + ", " + touchY);
        // get ring index
        int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
        // if touch found & actor not present
        if (selectIndex != DaoDefs.INIT_INTEGER_MARKER &&
                daoStage.getActorList().get(selectIndex).equals(DaoDefs.INIT_STRING_MARKER)) {
            if (daoStage.getMarkIndex() > DaoDefs.INIT_INTEGER_MARKER) {
                Log.d(TAG, "moveActor " + daoStage.getActorList().get(daoStage.getMarkIndex()) + " from locus " + daoStage.getMarkIndex() + " to " + selectIndex);
                // move actor from mark to move selection
                daoStage.getActorList().set(selectIndex, daoStage.getActorList().get(daoStage.getMarkIndex()));
                // clear actor at mark index
                daoStage.getActorList().set(daoStage.getMarkIndex(), DaoDefs.INIT_STRING_MARKER);
            }
            // reset mark index to select
            daoStage.setMarkIndex(selectIndex);
            // update object
            getRepoProvider().getDalStage().update(daoStage, true);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // toggle area at selection - filling area with active actor or clearing the area
    private Boolean toggleAreaSelection(StageViewRing stageViewRing, StageModelRing stageModelRing, DaoStage daoStage,
                                       float touchX, float touchY, float z) {
        Log.d(TAG, "toggleAreaSelection touch (x,y) " + touchX + ", " + touchY);
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
                List<Integer> r1IndexList = stageModelRing.findRing(selectIndex);
                // for each locus in ring 1 list
                for (Integer r1 : r1IndexList) {
                    // if not forbidden
                    if (!daoStage.getPropList().get(r1).equals(DaoActor.ACTOR_MONIKER_FORBIDDEN)) {
                        // set actor at locus
                        daoStage.getActorList().set(r1, actorMoniker);
                        // build ring list
                        List<Integer> r2IndexList = stageModelRing.findRing(r1);
                        for (Integer r2 : r2IndexList) {
                            if (!daoStage.getPropList().get(r2).equals(DaoActor.ACTOR_MONIKER_FORBIDDEN)) {
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
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // toggle active actor for mirror props at selection -
    // filling mirrored cells with active actor or clearing the mirrored cells
    private Boolean toggleMirrorSelection(StageViewRing stageViewRing, DaoStage daoStage,
                                            float touchX, float touchY, float z) {
        Log.d(TAG, "toggleMirrorSelection touch (x,y) " + touchX + ", " + touchY);
        // get ring index
        int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
        // if touch found
        if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
            if (getPlayListService().getActiveActor() != null &&
                    daoStage.getPropList().get(selectIndex).equals(DaoActor.ACTOR_MONIKER_MIRROR)) {
                Log.d(TAG, "toggleMirrorSelection (" + selectIndex + ") for actor " + daoStage.getActorList().get(selectIndex));
                // default moniker to active actor
                String actorMoniker = getPlayListService().getActiveActor().getMoniker();
                // if actor present at selected locus then set moniker to clear the actor list
                String setMoniker = actorMoniker;
                if (daoStage.getActorList().get(selectIndex).equals(actorMoniker)) setMoniker = DaoDefs.INIT_STRING_MARKER;
                // all empty mirrored cells get actor - all actor at mirrored cells cleared
                // for each locus
                for (int i = 0; i < daoStage.getPropList().size(); i++) {
                    // if mirror prop at locus && selected actor present or no actors present
                    if (daoStage.getPropList().get(i).equals(DaoActor.ACTOR_MONIKER_MIRROR) &&
                            (daoStage.getActorList().get(i).equals(actorMoniker) ||
                                    daoStage.getActorList().get(i).equals(DaoDefs.INIT_STRING_MARKER))) {
                        // set actor at locus
                        daoStage.getActorList().set(i, setMoniker);
                    }
                }
                // update object
                getRepoProvider().getDalStage().update(daoStage, true);
            }
            else {
                if (getPlayListService().getActiveActor() != null) Log.e(TAG,"Oops! toggleMirrorSelection No active actor...");
                else Log.e(TAG,"Oops! toggleMirrorSelection selection NOT MIRROR cell...");
            }
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean togglePropSelection(StageViewRing stageViewRing, DaoStage daoStage,
                                        float touchX, float touchY, float z) {
        Log.d(TAG, "togglPropSelection touch (x,y) " + touchX + ", " + touchY);
        // get ring index
        int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
        // if touch found
        if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
                Log.d(TAG, "togglPropSelection (" + selectIndex + ") for prop " + daoStage.getPropList().get(selectIndex));
                DaoActor activeActor = getPlayListService().getActiveActor();
                if (activeActor != null) {
                    if (activeActor.getMoniker().equals(DaoActor.ACTOR_MONIKER_FORBIDDEN)) {
                        daoStage.togglePropList(selectIndex,
                                DaoActor.ACTOR_MONIKER_FORBIDDEN,
                                DaoStage.STAGE_BG_COLOR,
                                DaoStage.STAGE_BG_COLOR);
                    }
                    else {
                        daoStage.togglePropList(selectIndex,
                                DaoActor.ACTOR_MONIKER_MIRROR,
                                activeActor.getForeColor(),
                                DaoStage.STAGE_BG_COLOR);
                    }
                    // update object
                    getRepoProvider().getDalStage().update(daoStage, true);
                }
                else {
                    Log.e(TAG, "Ooops! active actor NULL... ");
                }
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean toggleActorSelection(StageViewRing stageViewRing, StageModelRing stageModelRing,
                                         DaoStage daoStage, DaoActor daoActor,
                                         float touchX, float touchY, float z, Boolean plus) {
        Log.d(TAG, "toggleActorSelection touch (x,y) " + touchX + ", " + touchY);
        // get ring index
        int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
        // if touch found
        if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
            Log.d(TAG, "toggleActorSelection (" + selectIndex + ") for actor " + daoStage.getActorList().get(selectIndex));
            // toggle actor
            daoStage.toggleActorList(daoActor.getMoniker(), selectIndex);
            // if selecting plus ring
            if (plus) {
                if (stageModelRing != null) {
                    List<Integer> ringIndexList = stageModelRing.findRing(selectIndex);
                    // toggle each rect in ring list
                    for (Integer i : ringIndexList) {
                        daoStage.toggleActorList(daoActor.getMoniker(), i);
                    }
                } else {
                    Log.e(TAG, "Oops! for repo " + getRepoProvider().toString() + " NULL getStageModelRing()...");
                }
            }
            // update object
            getRepoProvider().getDalStage().update(daoStage, true);
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean toggleActorPath(StageViewRing stageViewRing, DaoStage daoStage, DaoActor daoActor,
                                    float velocityX, float velocityY,
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

        // get ring index
        Log.d(TAG, "toggleActorPath origin X, Y " + event1X + ", " + event1Y);
        int ringIndex = stageViewRing.getRingIndex(event1X, event1Y, 0.0f);
        // if valid ring index
        if (ringIndex != DaoDefs.INIT_INTEGER_MARKER) {
            // toggle initial position
            daoStage.toggleActorList(daoActor.getMoniker(), ringIndex);

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
                    daoStage.toggleActorList(daoActor.getMoniker(), ringIndex);
                }
            }
            // update object
            getRepoProvider().getDalStage().update(daoStage, true);
        }
        else {
            Log.e(TAG, "Oops!  no active actor...");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean clearActors(DaoStage daoStage) {
        // clear actor list
        daoStage.setActorList(DaoDefs.INIT_STRING_MARKER);
        // update object
        getRepoProvider().getDalStage().update(daoStage, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
