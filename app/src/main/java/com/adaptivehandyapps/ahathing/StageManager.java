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

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;

import java.util.List;

public class StageManager {
    private static final String TAG = StageManager.class.getSimpleName();

    private StageViewController mStageViewController;

    ///////////////////////////////////////////////////////////////////////////
    public StageManager(StageViewController stageViewController) {
        mStageViewController = stageViewController;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onAction(String action) {
        Log.d(TAG, "onAction action " + action);
        // if story exists associating the active actor (or all actor) with the action
        if (updateForAction(action)) {
            // execute outcome
            switch (action) {
                case DaoAction.ACTION_TYPE_SINGLE_TAP:
                    toggleSelection(mStageViewController.getTouchX(), mStageViewController.getTouchY(), 0.0f, false);
                    return true;
                case DaoAction.ACTION_TYPE_LONG_PRESS:
                    // toggle selection plus adjacent
                    toggleSelection(mStageViewController.getTouchX(), mStageViewController.getTouchY(), 0.0f, true);
                    return true;
                case DaoAction.ACTION_TYPE_FLING:
                    // toggle path along fling vector
                    plotPath(mStageViewController.getVelocityX(), mStageViewController.getVelocityY(),
                            mStageViewController.getmEvent1().getX(), mStageViewController.getmEvent1().getY(),
                            mStageViewController.getmEvent2().getX(), mStageViewController.getmEvent2().getY());
                    return true;
                case DaoAction.ACTION_TYPE_DOUBLE_TAP:
                    // clear actors on stage
                    DaoStage daoStage = mStageViewController.getPlayListService().getActiveStage();
                    if (!daoStage.setActorList(DaoDefs.INIT_STRING_MARKER)) {
                        Log.e(TAG, "Ooops! onDoubleTap UNKNOWN stage type? " + daoStage.getStageType());
                    }
                    // update object
                    mStageViewController.getRepoProvider().getDalStage().update(daoStage, true);
                    return true;
                default:
                    Log.e(TAG, "Oops! Unknown action? " + action);
                    return false;
            }
        }

        return false;
    }

    public Boolean updateForAction(String action) {
        Log.d(TAG, "updateForAction action " + action);
//        if (action.equals(DaoAction.ACTION_TYPE_SINGLE_TAP) ||
//                action.equals(DaoAction.ACTION_TYPE_LONG_PRESS) ||
//                        action.equals(DaoAction.ACTION_TYPE_DOUBLE_TAP) ||
//                                action.equals(DaoAction.ACTION_TYPE_FLING)) {
            // if story exists associating the active actor (or all actor) with the action
            DaoStory daoStory = isStory(action);
            if (daoStory != null) {
                // set active: story, action, outcome  (actor is already active)
                mStageViewController.getPlayListService().setActiveStory(daoStory);
                DaoAction daoAction = (DaoAction) mStageViewController.getRepoProvider().getDalAction().getDaoRepo().get(daoStory.getAction());
                mStageViewController.getPlayListService().setActiveAction(daoAction);
                DaoOutcome daoOutcome = (DaoOutcome) mStageViewController.getRepoProvider().getDalOutcome().getDaoRepo().get(daoStory.getOutcome());
                mStageViewController.getPlayListService().setActiveOutcome(daoOutcome);
                return true;
            }
//        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // if active actor is associated with incoming action - return story
    public DaoStory isStory(String action) {
        DaoEpic activeEpic = mStageViewController.getPlayListService().getActiveEpic();
        if (activeEpic != null) {
            // for each story in epic
            for (Integer i = 0; i < activeEpic.getTagList().size(); i++) {
                String storyMoniker = activeEpic.getTagList().get(i);
                DaoStory daoStory = (DaoStory) mStageViewController.getRepoProvider().getDalStory().getDaoRepo().get(storyMoniker);
                if (daoStory != null) {
                    Log.v(TAG, "test story " + daoStory);
                    // if any actor or active actor  &&  action match
                    if ((daoStory.getActor().contains("*") ||
                            daoStory.getActor().equals(mStageViewController.getPlayListService().getActiveActor().getMoniker())) &&
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
    public Boolean toggleSelection(float touchX, float touchY, float z, Boolean plus) {
        Log.d(TAG, "toggleSelection touch (x,y) " + touchX + ", " + touchY);
        int selectIndex = DaoDefs.INIT_INTEGER_MARKER;
        DaoStage daoStage = mStageViewController.getPlayListService().getActiveStage();
        if (daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            // get ring index
            selectIndex = mStageViewController.getStageViewRing().getRingIndex(touchX, touchY, z);
            // if touch found
            if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
                Log.d(TAG, "toggleSelection (" + selectIndex + ") for actor " + daoStage.getActorList().get(selectIndex));
//                toggleActorList(daoStage, selectIndex);
                if (!daoStage.toggleActorList(mStageViewController.getPlayListService().getActiveActor().getMoniker(), selectIndex)) {
                    Log.e(TAG, "Ooops! toggleSelection UNKNOWN stage type? " + daoStage.getStageType());
                }
                // if selecting plus ring
                if (plus) {
                    if (mStageViewController.getRepoProvider().getStageModelRing() != null) {
                        List<Integer> ringIndexList = mStageViewController.getRepoProvider().getStageModelRing().findRing(selectIndex);
                        // toggle each rect in ring list
                        for (Integer i : ringIndexList) {
//                            toggleActorList(daoStage, i);
                            if (!daoStage.toggleActorList(mStageViewController.getPlayListService().getActiveActor().getMoniker(), i)) {
                                Log.e(TAG, "Ooops! toggleSelection UNKNOWN stage type? " + daoStage.getStageType());
                            }
                        }
                    }
                    else {
                        Log.e(TAG, "Oops! for repo " + mStageViewController.getRepoProvider().toString() + " NULL getStageModelRing()...");
                    }
                }
                // update object
                mStageViewController.getRepoProvider().getDalStage().update(daoStage, true);
            }
        }
        else {
            Log.e(TAG, "toggleSelection UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean plotPath(float velocityX, float velocityY,
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
        DaoStage daoStage = mStageViewController.getPlayListService().getActiveStage();
        Log.d(TAG, "plotPath origin X, Y " + event1X + ", " + event1Y);
        // get ring index
        int ringIndex = mStageViewController.getStageViewRing().getRingIndex(event1X, event1Y, 0.0f);
        if (ringIndex != DaoDefs.INIT_INTEGER_MARKER) {
//            toggleActorList(daoStage, ringIndex);
            if (!daoStage.toggleActorList(mStageViewController.getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                Log.e(TAG, "Ooops! plotPath UNKNOWN stage type? " + daoStage.getStageType());
            }
            // for each interval, generate point along angle
            for (int i = 1; i < intervalCount; i++) {
                int prevRingIndex = ringIndex;
                float x = (float) (event1X + ((StageModelRing.LOCUS_DIST * i) * Math.cos(thetaRad)));
                float y = (float) (event1Y - ((StageModelRing.LOCUS_DIST * i) * Math.sin(thetaRad)));
                Log.d(TAG, "plotPath toggle for interval " + i + " at X, Y " + x + ", " + y);
                // find ring index at interval position
                ringIndex = mStageViewController.getStageViewRing().getRingIndex(x, y, 0.0f);
                // if not previously toggled
                if (ringIndex != DaoDefs.INIT_INTEGER_MARKER && ringIndex != prevRingIndex) {
                    // toggle at interval position
//                    toggleActorList(daoStage, ringIndex);
                    if (!daoStage.toggleActorList(mStageViewController.getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                        Log.e(TAG, "Ooops! plotPath UNKNOWN stage type? " + daoStage.getStageType());
                    }
                }
            }
            // update object
            mStageViewController.getRepoProvider().getDalStage().update(daoStage, true);

//            invalidate();
        }
        else {
            Log.e(TAG, "plotPath touch out of bounds...");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
