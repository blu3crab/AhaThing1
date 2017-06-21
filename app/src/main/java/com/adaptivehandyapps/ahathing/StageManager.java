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

import android.util.Log;
import android.view.MotionEvent;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;

import java.util.List;

public class StageManager {
    private static final String TAG = StageManager.class.getSimpleName();

//    private StageViewController mStageViewController;

    ///////////////////////////////////////////////////////////////////////////
    private PlayListService mPlayListService;
    private RepoProvider mRepoProvider;

    // touch position
    private float mTouchX = 0.0f;
    private float mTouchY = 0.0f;
    private float mVelocityX = 0.0f;
    private float mVelocityY = 0.0f;
    private MotionEvent mEvent1;
    private MotionEvent mEvent2;

    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    public PlayListService getPlayListService() {
        return mPlayListService;
    }
    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
    }

    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
        Log.d(TAG, "setRepoProvider " + mRepoProvider);
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

    ///////////////////////////////////////////////////////////////////////////
    public StageManager(PlayListService playListService, RepoProvider repoProvider) {
//    public StageManager(StageViewController stageViewController, PlayListService playListService, RepoProvider repoProvider) {
//        mStageViewController = stageViewController;
        setPlayListService(playListService);
        setRepoProvider(repoProvider);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onAction(StageViewRing stageViewRing, String action) {
        Log.d(TAG, "onAction action " + action);
        // if story exists associating the active actor (or all actor) with the action
        if (updatePlaylist(action)) {
            // execute outcome
            switch (action) {
                case DaoAction.ACTION_TYPE_SINGLE_TAP:
                case DaoAction.ACTION_TYPE_LONG_PRESS:
                case DaoAction.ACTION_TYPE_FLING:
                case DaoAction.ACTION_TYPE_DOUBLE_TAP:
                    // if prereq satisfied
                    if (isPreReqSatisfied(stageViewRing)) {
                        DaoOutcome daoOutcome = getPlayListService().getActiveOutcome();
                        onOutcome(stageViewRing, daoOutcome.getMoniker());

                        // if post-operation indicated
                        onPostOp();
                    }

                    return true;

                default:
                    Log.e(TAG, "Oops! Unknown action? " + action);
                    return false;
            }
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean isPreReqSatisfied(StageViewRing stageViewRing) {
        DaoStory activeStory = getPlayListService().getActiveStory();
        // return true if no stageViewRing or no pre-req defined
        if (stageViewRing == null) {
            Log.d(TAG,"isPreReqSatisfied-> no stageViewRing defined...");
            return true;
        }
        else if (activeStory.getPreReq().equals(DaoDefs.INIT_STRING_MARKER) ||
                activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_NONE) ||
                        stageViewRing == null) {
            Log.d(TAG,"isPreReqSatisfied-> no pre-req defined...");
            return true;
        }
        else {
            DaoStage daoStage = getPlayListService().getActiveStage();
            if (daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                // get ring index of touch to determine actor at vert
                float touchX = getTouchX();
                float touchY = getTouchY();
                float z = 0.0f;
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                String vertActor = daoStage.getActorList().get(selectIndex);
                // get active actor
                String activeActor = getPlayListService().getActiveActor().getMoniker();
                Log.d(TAG,"isPreReqSatisfied(" + activeStory.getPreReq() + ")-> vertActor, activeActor= " + vertActor + ", " + activeActor);
                if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_OWNED)) {
                    // if vert is owned by active player, return true
                    if (vertActor.equals(activeActor)) return true;
                } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_BLOCKED)) {
                    // if vert is blocked by another player, return true
                    if (!vertActor.equals(activeActor) && !vertActor.equals(DaoDefs.INIT_STRING_MARKER)) return true;
                } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_EMPTY)) {
                    // if vert is empty, return true
                    if (vertActor.equals(DaoDefs.INIT_STRING_MARKER)) return true;
                }
            }
            else {
                Log.e(TAG, "Oops! Unknown stage type " + daoStage.getStageType());
            }
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean onPostOp() {
        DaoStory activeStory = getPlayListService().getActiveStory();
        // return true if no post-op defined
        if (activeStory.getPostOp().equals(DaoDefs.INIT_STRING_MARKER) ||
                activeStory.getPreReq().equals(DaoStory.STORY_POSTOP_NONE)) {
            Log.d(TAG,"onPostOp-> no post-op defined...");
            return true;
        }
        else {
            DaoStage daoStage = getPlayListService().getActiveStage();
            if (daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                if (activeStory.getPostOp().equals(DaoStory.STORY_POSTOP_TALLY)) {
                    DaoEpic daoEpic = getPlayListService().getActiveEpic();
                    for (String vertActor : daoStage.getActorList()) {
                        int vertActorInx = daoEpic.getStarList().indexOf(vertActor);
                        if (vertActorInx > -1) {
                            int tally = daoEpic.getTallyList().get(vertActorInx);
                            daoEpic.getTallyList().set(vertActorInx, ++tally);
                        }
                        else {
                            Log.e(TAG, "Oops!  Unknown vert actor " + vertActor);
                        }
                    }
                    for (String star : daoEpic.getStarList()) {
                        int starInx = daoEpic.getStarList().indexOf(star);
                        Log.d(TAG, " star " + star + " has tally " + daoEpic.getTallyList().get(starInx));
                    }
                } else {
                    Log.e(TAG, "Oops!  Unknown postop " + activeStory.getPostOp());
                }
            }
            else {
                Log.e(TAG, "Oops! Unknown stage type " + daoStage.getStageType());
            }
        }
        return false;
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
                if (daoStory != null) {
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
                    Log.e(TAG, "oops! no story found matching " + activeEpic.getTagList().get(i) + "...");
                }
            }
        }
        else {
            Log.e(TAG, "oops! no active epic...");
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
                toggleSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f, false);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PLUS:
                if (stageViewRing == null) return false;
                // toggle selection plus adjacent
                toggleSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f, true);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PATH:
                if (stageViewRing == null) return false;
                // toggle selection plus adjacent
                plotPath(stageViewRing,
                        getVelocityX(), getVelocityY(),
                        getEvent1().getX(), getEvent1().getY(),
                        getEvent2().getX(), getEvent2().getY());
                return true;
            case DaoOutcome.OUTCOME_TYPE_CLEAR_ACTORS:
                // clear actors on stage
                clearActors();
                return true;
            default:
                Log.e(TAG, "Oops! Unknown outcome? " + outcome);
        }

        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toggleSelection(StageViewRing stageViewRing, float touchX, float touchY, float z, Boolean plus) {
        Log.d(TAG, "toggleSelection touch (x,y) " + touchX + ", " + touchY);
//        int selectIndex = DaoDefs.INIT_INTEGER_MARKER;
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            // get ring index
            int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
            // if touch found
            if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
                Log.d(TAG, "toggleSelection (" + selectIndex + ") for actor " + daoStage.getActorList().get(selectIndex));
                if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), selectIndex)) {
                    Log.e(TAG, "Ooops! toggleSelection UNKNOWN stage type? " + daoStage.getStageType());
                }
                // if selecting plus ring
                if (plus) {
                    if (getRepoProvider().getStageModelRing() != null) {
                        List<Integer> ringIndexList = getRepoProvider().getStageModelRing().findRing(selectIndex);
                        // toggle each rect in ring list
                        for (Integer i : ringIndexList) {
                            if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), i)) {
                                Log.e(TAG, "Ooops! toggleSelection UNKNOWN stage type? " + daoStage.getStageType());
                            }
                        }
                    }
                    else {
                        Log.e(TAG, "Oops! for repo " + getRepoProvider().toString() + " NULL getStageModelRing()...");
                    }
                }
                // update object
                getRepoProvider().getDalStage().update(daoStage, true);
            }
        }
        else {
            Log.e(TAG, "toggleSelection UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean plotPath(StageViewRing stageViewRing, float velocityX, float velocityY,
                             float event1X, float event1Y, float event2X, float event2Y) {
        // sum velocity ignoring direction (sign)
        float velocity = Math.abs(velocityX) + Math.abs(velocityY);
        Log.d(TAG, "plotPath velocity sum = " + velocity);
        // set # intervals based on velocity
        float intervalCount = velocity / 1000.0f;
        Log.d(TAG, "plotPath intervalCount = " + intervalCount);
        // find angle of fling
        double deltaX = event2X - event1X;
        double deltaY =  -(event2Y - event1Y);
        double thetaRad = Math.atan2(deltaY, deltaX);
        double angle = thetaRad * (180.0f/Math.PI);
        Log.d(TAG, "plotPath thetaRad = " + thetaRad + ", angle = " + angle);

        // toggle initial position
        DaoStage daoStage = getPlayListService().getActiveStage();
        Log.d(TAG, "plotPath origin X, Y " + event1X + ", " + event1Y);
        // get ring index
        int ringIndex = stageViewRing.getRingIndex(event1X, event1Y, 0.0f);
        if (ringIndex != DaoDefs.INIT_INTEGER_MARKER) {
//            toggleActorList(daoStage, ringIndex);
            if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                Log.e(TAG, "Ooops! plotPath UNKNOWN stage type? " + daoStage.getStageType());
            }
            // for each interval, generate point along angle
            for (int i = 1; i < intervalCount; i++) {
                int prevRingIndex = ringIndex;
                float x = (float) (event1X + ((StageModelRing.LOCUS_DIST * i) * Math.cos(thetaRad)));
                float y = (float) (event1Y - ((StageModelRing.LOCUS_DIST * i) * Math.sin(thetaRad)));
                Log.d(TAG, "plotPath toggle for interval " + i + " at X, Y " + x + ", " + y);
                // find ring index at interval position
                ringIndex = stageViewRing.getRingIndex(x, y, 0.0f);
                // if not previously toggled
                if (ringIndex != DaoDefs.INIT_INTEGER_MARKER && ringIndex != prevRingIndex) {
                    // toggle at interval position
//                    toggleActorList(daoStage, ringIndex);
                    if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                        Log.e(TAG, "Ooops! plotPath UNKNOWN stage type? " + daoStage.getStageType());
                    }
                }
            }
            // update object
            getRepoProvider().getDalStage().update(daoStage, true);

//            invalidate();
        }
        else {
            Log.e(TAG, "plotPath touch out of bounds...");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean clearActors() {
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (!daoStage.setActorList(DaoDefs.INIT_STRING_MARKER)) {
            Log.e(TAG, "Ooops! onDoubleTap UNKNOWN stage type? " + daoStage.getStageType());
            return false;
        }
        // update object
        getRepoProvider().getDalStage().update(daoStage, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}