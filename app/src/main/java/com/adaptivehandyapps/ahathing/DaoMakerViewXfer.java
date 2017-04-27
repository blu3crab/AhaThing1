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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;
import java.util.List;

public class DaoMakerViewXfer {
    private static final String TAG = DaoMakerViewXfer.class.getSimpleName();

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

    private Boolean mIsForeColor;
    private Integer mForeColor = DaoDefs.INIT_INTEGER_MARKER;
    private Integer mBackColor = DaoDefs.INIT_INTEGER_MARKER;
    private Integer mSelectedColorRGB = DaoDefs.INIT_INTEGER_MARKER;

    private Button mButtonForeColor;
    private Button mButtonBackColor;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public DaoMakerViewXfer(ContentFragment parent, View rootView) {

        Log.d(TAG, "DaoMakerUiHandler...");
        mParent = parent;
        mRootView = rootView;
    }
    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    private Boolean isForeColor() { return mIsForeColor; }
    private Boolean isForeColor(Boolean isForeColor) { mIsForeColor = isForeColor; return mIsForeColor; }

    public Integer getForeColor() {
        return mForeColor;
    }
    public void setForeColor(Integer foreColor) {
        // default color to 0
        this.mForeColor = 0;
        // if incoming color defined, assign color
        if (foreColor != DaoDefs.INIT_INTEGER_MARKER) this.mForeColor = foreColor;
        // if button defined, assign button color
        if (mButtonForeColor != null) mButtonForeColor.setBackgroundColor(getForeColor());
        Log.d(TAG, "fore color rgb " + getForeColor());

    }

    public Integer getBackColor() {
        return mBackColor;
    }
    public void setBackColor(Integer backColor) {
        // default color to 0
        this.mBackColor = 0;
        // if incoming color defined, assign color
        if (backColor != DaoDefs.INIT_INTEGER_MARKER) this.mBackColor = backColor;
        // if button defined, assign button color
        if (mButtonBackColor != null) mButtonBackColor.setBackgroundColor(getBackColor());
        Log.d(TAG, "Back color rgb " + getBackColor());
    }
    ///////////////////////////////////////////////////////////////////////////
    // helpers
    private void launchColorPicker(final Boolean isForeColor, int rgb) {
        // clear selected rgb
        mSelectedColorRGB = DaoDefs.INIT_INTEGER_MARKER;
        // extract component r,g,b
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        Log.d(TAG, "r,g,b,rgb " + red + ", " + green  + ", " + blue  + ", " + rgb);
        final ColorPicker cp = new ColorPicker(mParent.getActivity(), red, green, blue);
                /* Show color picker dialog */
        cp.show();

                /* On Click listener for the dialog, when the user select the color */
        Button okColor = (Button)cp.findViewById(R.id.okColorButton);

        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        /* You can get single channel (value 0-255) */
                int selectedColorR = cp.getRed();
                int selectedColorG = cp.getGreen();
                int selectedColorB = cp.getBlue();

                        /* Or the android RGB Color (see the android Color class reference) */
                int selectedColorRGB = cp.getColor();
                Log.d(TAG, "r,g,b,rgb " + selectedColorR + ", " + selectedColorG  + ", " + selectedColorB  + ", " + selectedColorRGB);

                int red = (selectedColorRGB >> 16) & 0xFF;
                int green = (selectedColorRGB >> 8) & 0xFF;
                int blue = selectedColorRGB & 0xFF;
                Log.d(TAG, "r,g,b,rgb " + red + ", " + green  + ", " + blue  + ", " + selectedColorRGB);

                cp.dismiss();

                mSelectedColorRGB = selectedColorRGB;
                // if fore color
                if (isForeColor) {
                    setForeColor(selectedColorRGB);
                }
                else {
                    setBackColor(selectedColorRGB);
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromStory(DaoStory daoStory) {

        // set story views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_story);
        ll.setVisibility(View.VISIBLE);

        // Stage spinner
        // dereference repo dao list
        List<DaoStage> daoStageList = (List<DaoStage>)(List<?>) mParent.getRepoProvider().getDalStage().getDaoRepo().getDaoList();
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
        List<DaoActor> daoActorList = (List<DaoActor>)(List<?>) mParent.getRepoProvider().getDalActor().getDaoRepo().getDaoList();
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
        List<DaoAction> daoActionList = (List<DaoAction>)(List<?>) mParent.getRepoProvider().getDalAction().getDaoRepo().getDaoList();
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
        List<DaoOutcome> daoOutcomeList = (List<DaoOutcome>)(List<?>) mParent.getRepoProvider().getDalOutcome().getDaoRepo().getDaoList();
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
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_actor);
        ll.setVisibility(View.VISIBLE);

        // establish fore color button visibility & click listener
//        final Button buttonForeColor = (Button) mRootView.findViewById(R.id.button_daomaker_forecolor);
        mButtonForeColor = (Button) mRootView.findViewById(R.id.button_daomaker_forecolor);
        mButtonForeColor.setVisibility(View.VISIBLE);
        setForeColor(daoActor.getForeColor());
//        mButtonForeColor.setBackgroundColor(getForeColor());
//        Log.d(TAG, "fore color rgb " + getForeColor());

        mButtonForeColor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonForeColor.setOnClickListener: ");
                Toast.makeText(mRootView.getContext(), "buttonForeColor...", Toast.LENGTH_SHORT).show();
                // launch color picker
                isForeColor(true);
                launchColorPicker(isForeColor(), getForeColor());
            }
        });
        // establish back color button visibility & click listener
//        final Button buttonBackColor = (Button) mRootView.findViewById(R.id.button_daomaker_backcolor);
        mButtonBackColor = (Button) mRootView.findViewById(R.id.button_daomaker_backcolor);
        mButtonBackColor.setVisibility(View.VISIBLE);
        setBackColor(daoActor.getBackColor());
//        mButtonBackColor.setBackgroundColor(getBackColor());
//        Log.d(TAG, "Back color rgb " + getBackColor());

        mButtonBackColor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonBackColor.setOnClickListener: ");
                Toast.makeText(mRootView.getContext(), "buttonBackColor...", Toast.LENGTH_SHORT).show();
                // launch color picker
                isForeColor(false);
                launchColorPicker(isForeColor(), getBackColor());
            }
        });

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
            activeTheatre = (DaoTheatre) mParent.getRepoProvider().getDalTheatre().getDaoRepo().get(moniker);
            if (activeTheatre != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalTheatre().remove(activeTheatre, true);
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
        mParent.getPlayListService().setActiveTheatre(activeTheatre);
        // update repo
        mParent.getRepoProvider().getDalTheatre().update(activeTheatre, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toEpic(String op, String moniker, String editedMoniker, String headline, List<String> tagList) {
        // active object
        DaoEpic activeEpic = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeEpic = (DaoEpic) mParent.getRepoProvider().getDalEpic().getDaoRepo().get(moniker);
            if (activeEpic != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalEpic().remove(activeEpic, true);
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
        mParent.getPlayListService().setActiveEpic(activeEpic);
        // update repo
        mParent.getRepoProvider().getDalEpic().update(activeEpic, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toStory(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoStory activeStory = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeStory = (DaoStory) mParent.getRepoProvider().getDalStory().getDaoRepo().get(moniker);
            if (activeStory != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalStory().remove(activeStory, true);
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
        mParent.getPlayListService().setActiveStory(activeStory);
        // update repo
        mParent.getRepoProvider().getDalStory().update(activeStory, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toStage(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoStage activeStage = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeStage = (DaoStage) mParent.getRepoProvider().getDalStage().getDaoRepo().get(moniker);
            if (activeStage != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalStage().remove(activeStage, true);
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
        if (mParent.getRepoProvider().getStageModelRing() == null) {
            // TODO: single stage model - build stage model per stage
            mParent.getRepoProvider().setStageModelRing(new StageModelRing(mParent.getPlayListService()));
            Integer ringMax = 4;
            mParent.getRepoProvider().getStageModelRing().buildModel(activeStage, ringMax);
            Log.d(TAG, "NEW StageModelRing for repo " + mParent.getRepoProvider().toString() + " at " + mParent.getRepoProvider().getStageModelRing().toString());
        }
        mParent.getPlayListService().setActiveStage(activeStage);        // update repo
        mParent.getRepoProvider().getDalStage().update(activeStage, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toActor(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoActor activeActor = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeActor = (DaoActor) mParent.getRepoProvider().getDalActor().getDaoRepo().get(moniker);
            if (activeActor != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalActor().remove(activeActor, true);
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
        if (getForeColor() != activeActor.getForeColor()) activeActor.setForeColor(getForeColor());
        if (getBackColor() != activeActor.getBackColor()) activeActor.setBackColor(getBackColor());
        mParent.getPlayListService().setActiveActor(activeActor);
        // update repo
        mParent.getRepoProvider().getDalActor().update(activeActor, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toAction(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoAction activeAction = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeAction = (DaoAction) mParent.getRepoProvider().getDalAction().getDaoRepo().get(moniker);
            if (activeAction != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalAction().remove(activeAction, true);
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
        mParent.getPlayListService().setActiveAction(activeAction);
        // update repo
        mParent.getRepoProvider().getDalAction().update(activeAction, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toOutcome(String op, String moniker, String editedMoniker, String headline) {
        // active object
        DaoOutcome activeOutcome = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeOutcome = (DaoOutcome) mParent.getRepoProvider().getDalOutcome().getDaoRepo().get(moniker);
            if (activeOutcome != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalOutcome().remove(activeOutcome, true);
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
        mParent.getPlayListService().setActiveOutcome(activeOutcome);
        // update repo
        mParent.getRepoProvider().getDalOutcome().update(activeOutcome, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
