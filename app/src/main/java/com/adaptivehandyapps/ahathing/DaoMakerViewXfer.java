/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker JAN 2017
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
//
// Created by mat on 1/6/2017.
//

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

import java.util.ArrayList;
import java.util.List;

public class DaoMakerViewXfer {
    private static final String TAG = "DaoMakerUiHandler";

    private ContentFragment mParent;
    private View mRootView;

    private ArrayAdapter<String> mStageListAdapter = null;
    private Spinner mSpinnerStages;
    private ArrayAdapter<String> mActorListAdapter = null;
    private Spinner mSpinnerActors;
    private ArrayAdapter<String> mActionListAdapter = null;
    private Spinner mSpinnerActions;
    private ArrayAdapter<String> mOutcomeListAdapter = null;
    private Spinner mSpinnerOutcomes;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public DaoMakerViewXfer(ContentFragment parent, View rootView) {

        Log.d(TAG, "DaoMakerUiHandler...");
        mParent = parent;
        mRootView = rootView;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromStory(DaoStory daoStory) {

        // set story views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_story);
        ll.setVisibility(View.VISIBLE);

        // Stage spinner
        // dereference repo dao list
        List<DaoStage> daoStageList = (List<DaoStage>)(List<?>) MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().getDaoList();
        List<String> stageNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoStage stage : daoStageList) {
            // build list of names
            stageNameList.add(stage.getMoniker());
        }
        mStageListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                stageNameList);

        mSpinnerStages = (Spinner) mRootView.findViewById(R.id.spinner_stages);
        if (mStageListAdapter != null && mSpinnerStages != null) {
            mSpinnerStages.setAdapter(mStageListAdapter);
            if (stageNameList.contains(daoStory.getStage())) {
                int i = stageNameList.indexOf(daoStory.getStage());
                mSpinnerStages.setSelection(i);
            }
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mStageListAdapter? " + mStageListAdapter + ", R.id.spinner_stages? " + mSpinnerStages);
            return false;
        }
        // Actor spinner
        // dereference repo dao list
        List<DaoActor> daoActorList = (List<DaoActor>)(List<?>) MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().getDaoList();
        List<String> actorNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoActor actor : daoActorList) {
            // build list of names
            actorNameList.add(actor.getMoniker());
        }
        if (actorNameList.isEmpty()) {
            actorNameList.add("actors!  where are the actors?");
        }
        mActorListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                actorNameList);

        mSpinnerActors = (Spinner) mRootView.findViewById(R.id.spinner_actors);
        if (mActorListAdapter != null && mSpinnerActors != null) {
            mSpinnerActors.setAdapter(mActorListAdapter);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mActorListAdapter? " + mActorListAdapter + ", R.id.spinner_Prereqs? " + mSpinnerActors);
            return false;
        }
        // Action spinner
        // dereference repo dao list
        List<DaoAction> daoActionList = (List<DaoAction>)(List<?>) MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().getDaoList();
        List<String> actionNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoAction action : daoActionList) {
            // build list of names
            actionNameList.add(action.getMoniker());
        }
        if (actionNameList.isEmpty()) {
            actionNameList.add("actions!  where are the actions?");
        }
        mActionListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                actionNameList);

        mSpinnerActions = (Spinner) mRootView.findViewById(R.id.spinner_actions);
        if (mActionListAdapter != null && mSpinnerActions != null) {
            mSpinnerActions.setAdapter(mActionListAdapter);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mActionListAdapter? " + mActionListAdapter + ", R.id.spinner_Action? " + mSpinnerActions);
            return false;
        }
        // Outcome spinner
        // dereference repo dao list
        List<DaoOutcome> daoOutcomeList = (List<DaoOutcome>)(List<?>) MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().getDaoList();
        List<String> outcomeNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoOutcome outcome : daoOutcomeList) {
            // build list of names
            outcomeNameList.add(outcome.getMoniker());
        }
        if (outcomeNameList.isEmpty()) {
            outcomeNameList.add("outcomes!  where are the outcomes?");
        }
        mOutcomeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                outcomeNameList);

        mSpinnerOutcomes = (Spinner) mRootView.findViewById(R.id.spinner_outcomes);
        if (mOutcomeListAdapter != null && mSpinnerOutcomes != null) {
            mSpinnerOutcomes.setAdapter(mOutcomeListAdapter);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mOutcomeListAdapter? " + mOutcomeListAdapter + ", R.id.spinner_Outcomes? " + mSpinnerOutcomes);
            return false;
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromStage(DaoStage daoStage) {

        // set Stage views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_stages);
        ll.setVisibility(View.VISIBLE);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromActor(DaoActor daoActor) {

        // set Actor views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_actors);
        ll.setVisibility(View.VISIBLE);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromAction(DaoAction daoAction) {

        // set Action views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_actions);
        ll.setVisibility(View.VISIBLE);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromOutcome(DaoOutcome daoOutcome) {

        // set Outcome views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_outcomes);
        ll.setVisibility(View.VISIBLE);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toTheatre(String op, String moniker, String editedMoniker, String headline, List<String> tagList) {
        // active object
        DaoTheatre activeTheatre = null;
        // xfer view to theatre object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeTheatre = (DaoTheatre) MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().get(moniker);
            if (activeTheatre != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    MainActivity.getRepoProviderInstance().getDalTheatre().remove(activeTheatre, true);
                }
            }
        }
        if (activeTheatre == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeTheatre = new DaoTheatre();
            Log.e(TAG, "toTheatre: editing NULL object?");
        }

        // update with edited values
        activeTheatre.setMoniker(editedMoniker);
        activeTheatre.setHeadline(headline);
        activeTheatre.setTagList(tagList);
//        MainActivity.getPlayListInstance().setActiveTheatre(activeTheatre);
        mParent.getPlayListService().setActiveTheatre(activeTheatre);
        // update repo
        MainActivity.getRepoProviderInstance().getDalTheatre().update(activeTheatre, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toEpic(String op, String moniker, String editedMoniker, String headline, List<String> tagList) {
        // active object
        DaoEpic activeEpic = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeEpic = (DaoEpic) MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().get(moniker);
            if (activeEpic != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    MainActivity.getRepoProviderInstance().getDalEpic().remove(activeEpic, true);
                }
            }
        }
        if (activeEpic == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeEpic = new DaoEpic();
            Log.e(TAG, "toEpic: new object....");
        }
        // update with edited values
        activeEpic.setMoniker(editedMoniker);
        activeEpic.setHeadline(headline);
        activeEpic.setTagList(tagList);
//        MainActivity.getPlayListInstance().setActiveEpic(activeEpic);
        mParent.getPlayListService().setActiveEpic(activeEpic);
        // update repo
        MainActivity.getRepoProviderInstance().getDalEpic().update(activeEpic, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toStory(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoStory activeStory = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeStory = (DaoStory) MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().get(moniker);
            if (activeStory != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    MainActivity.getRepoProviderInstance().getDalStory().remove(activeStory, true);
                }
            }
        }
        if (activeStory == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeStory = new DaoStory();
            Log.e(TAG, "toStory: new object....");
        }
        // update with edited values
        activeStory.setMoniker(editedMoniker);
        activeStory.setHeadline(headline);
        String stagename = mSpinnerStages.getSelectedItem().toString();
        Log.d(TAG, "toStory selected stage " + stagename);
        activeStory.setStage(mSpinnerStages.getSelectedItem().toString());
//        MainActivity.getPlayListInstance().setActiveStory(activeStory);
        mParent.getPlayListService().setActiveStory(activeStory);
        // update repo
        MainActivity.getRepoProviderInstance().getDalStory().update(activeStory, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toStage(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoStage activeStage = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeStage = (DaoStage) MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().get(moniker);
            if (activeStage != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    MainActivity.getRepoProviderInstance().getDalStage().remove(activeStage, true);
                }
            }
        }
        if (activeStage == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeStage = new DaoStage();
            Log.e(TAG, "toStage: new object....");
        }
        // update with edited values
        activeStage.setMoniker(editedMoniker);
        activeStage.setHeadline(headline);
        activeStage.setStageType(DaoStage.STAGE_TYPE_RING);
        if (MainActivity.getRepoProviderInstance().getStageModelRing() == null) {
            // TODO: single stage model - build stage model per stage
            MainActivity.getRepoProviderInstance().setStageModelRing(new StageModelRing(mParent.getPlayListService()));
            Integer ringMax = 4;
            MainActivity.getRepoProviderInstance().getStageModelRing().buildModel(activeStage, ringMax);
            Log.d(TAG, "NEW StageModelRing for repo " + MainActivity.getRepoProviderInstance().toString() + " at " + MainActivity.getRepoProviderInstance().getStageModelRing().toString());
        }
//        MainActivity.getPlayListInstance().setActiveStage(activeStage);
        mParent.getPlayListService().setActiveStage(activeStage);        // update repo
        MainActivity.getRepoProviderInstance().getDalStage().update(activeStage, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toActor(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoActor activeActor = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeActor = (DaoActor) MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().get(moniker);
            if (activeActor != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    MainActivity.getRepoProviderInstance().getDalActor().remove(activeActor, true);
                }
            }
        }
        if (activeActor == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeActor = new DaoActor();
            Log.e(TAG, "toActor: new object....");
        }
        // update with edited values
        activeActor.setMoniker(editedMoniker);
        activeActor.setHeadline(headline);
//        MainActivity.getPlayListInstance().setActiveActor(activeActor);
        mParent.getPlayListService().setActiveActor(activeActor);
        // update repo
        MainActivity.getRepoProviderInstance().getDalActor().update(activeActor, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toAction(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoAction activeAction = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeAction = (DaoAction) MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().get(moniker);
            if (activeAction != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    MainActivity.getRepoProviderInstance().getDalAction().remove(activeAction, true);
                }
            }
        }
        if (activeAction == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeAction = new DaoAction();
            Log.e(TAG, "toAction: new object....");
        }
        // update with edited values
        activeAction.setMoniker(editedMoniker);
        activeAction.setHeadline(headline);
//        MainActivity.getPlayListInstance().setActiveAction(activeAction);
        mParent.getPlayListService().setActiveAction(activeAction);
        // update repo
        MainActivity.getRepoProviderInstance().getDalAction().update(activeAction, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toOutcome(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoOutcome activeOutcome = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeOutcome = (DaoOutcome) MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().get(moniker);
            if (activeOutcome != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    MainActivity.getRepoProviderInstance().getDalOutcome().remove(activeOutcome, true);
                }
            }
            else {
                // error: never should edit NULL object!  create a placeholder & carry on
                activeOutcome = new DaoOutcome();
                Log.e(TAG, "toOutcome: editing NULL object?");
            }
        }
        if (activeOutcome == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeOutcome = new DaoOutcome();
            Log.e(TAG, "toAction: new object....");
        }
        // update with edited values
        activeOutcome.setMoniker(editedMoniker);
        activeOutcome.setHeadline(headline);
//        MainActivity.getPlayListInstance().setActiveOutcome(activeOutcome);
        mParent.getPlayListService().setActiveOutcome(activeOutcome);
        // update repo
        MainActivity.getRepoProviderInstance().getDalOutcome().update(activeOutcome, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
