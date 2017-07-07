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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.DevUtils;
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
    private ArrayAdapter<String> mPreReqListAdapter = null;
    private Spinner mSpinnerPreReqs;
    private ArrayAdapter<String> mActorListAdapter = null;
    private Spinner mSpinnerActors;
    private ArrayAdapter<String> mActionListAdapter = null;
    private Spinner mSpinnerActions;
    private ArrayAdapter<String> mOutcomeListAdapter = null;
    private Spinner mSpinnerOutcomes;
    private ArrayAdapter<String> mPostOpListAdapter = null;
    private Spinner mSpinnerPostOps;

    private ArrayAdapter<String> mRingTypeListAdapter = null;
    private Spinner mSpinnerRingType;
    private ArrayAdapter<String> mActionTypeListAdapter = null;
    private Spinner mSpinnerActionType;
    private ArrayAdapter<String> mOutcomeTypeListAdapter = null;
    private Spinner mSpinnerOutcomeType;

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
        // prereq spinner
        List<String> prereqList = new ArrayList<>();
        prereqList.add(DaoStory.STORY_PREREQ_NONE);
        prereqList.add(DaoStory.STORY_PREREQ_VERT_OWNED);
        prereqList.add(DaoStory.STORY_PREREQ_VERT_BLOCKED);
        prereqList.add(DaoStory.STORY_PREREQ_VERT_EMPTY);
        mPreReqListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                prereqList);

        mSpinnerPreReqs = (Spinner) mRootView.findViewById(R.id.spinner_prereqs);
        if (mPreReqListAdapter != null && mSpinnerPreReqs != null) {
            mSpinnerPreReqs.setAdapter(mPreReqListAdapter);
            // TODO: ugh! refactor prereq list!
            // set current selection
            int selectInx = 0; // none
            if (daoStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_OWNED)) selectInx = 1;
            else if (daoStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_BLOCKED)) selectInx = 2;
            else if (daoStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_EMPTY)) selectInx = 3;
            mSpinnerPreReqs.setSelection(selectInx);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mPreReqListAdapter? " + mPreReqListAdapter + ", spinner PreReq? " + mSpinnerPreReqs);
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
            if (actorNameList.contains(daoStory.getActor())) {
                int i = actorNameList.indexOf(daoStory.getActor());
                mSpinnerActors.setSelection(i);
            }
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
            if (actionNameList.contains(daoStory.getAction())) {
                int i = actionNameList.indexOf(daoStory.getAction());
                mSpinnerActions.setSelection(i);
            }
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
            if (outcomeNameList.contains(daoStory.getOutcome())) {
                int i = outcomeNameList.indexOf(daoStory.getOutcome());
                mSpinnerOutcomes.setSelection(i);
            }
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mOutcomeListAdapter? " + mOutcomeListAdapter + ", R.id.spinner_Outcomes? " + mSpinnerOutcomes);
            return false;
        }

        // postop spinner
        List<String> postopList = new ArrayList<>();
        postopList.add(DaoStory.STORY_POSTOP_NONE);
        postopList.add(DaoStory.STORY_POSTOP_CURTAIN_CALL);
        mPostOpListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                postopList);

        mSpinnerPostOps = (Spinner) mRootView.findViewById(R.id.spinner_postops);
        if (mPostOpListAdapter != null && mSpinnerPostOps != null) {
            mSpinnerPostOps.setAdapter(mPostOpListAdapter);
            // set current selection
            int selectInx = 0; // none
            if (daoStory.getPostOp().equals(DaoStory.STORY_POSTOP_CURTAIN_CALL)) selectInx = 1;
            mSpinnerPostOps.setSelection(selectInx);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mPostOpListAdapter? " + mPostOpListAdapter + ", spinner PostOp? " + mSpinnerPostOps);
            return false;
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromStage(DaoStage daoStage) {

        // set Stage views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_stage);
        ll.setVisibility(View.VISIBLE);

        // ring type spinner
        List<String> ringTypeList = new ArrayList<>();
        ringTypeList.add(DaoStage.STAGE_TYPE_RING);
        mRingTypeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                ringTypeList);

        mSpinnerRingType = (Spinner) mRootView.findViewById(R.id.spinner_ringtype);
        if (mRingTypeListAdapter != null && mSpinnerRingType != null) {
            mSpinnerRingType.setAdapter(mRingTypeListAdapter);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mRingTypeListAdapter? " + mRingTypeListAdapter + ", spinner ringtype? " + mSpinnerRingType);
            return false;
        }

        // set ring size, locii size
        EditText etRingSize = (EditText) mRootView.findViewById(R.id.et_ringsize);
        etRingSize.setText(daoStage.getRingSize().toString());

        TextView tvLociiSize = (TextView) mRootView.findViewById(R.id.tv_lociisize);
        Integer size = daoStage.getLocusList().locii.size();
        tvLociiSize.setText(size.toString());

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromActor(DaoActor daoActor) {

        // set Actor views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_actor);
        ll.setVisibility(View.VISIBLE);

        DaoEpic daoEpic = mParent.getPlayListService().getActiveEpic();
        if (daoEpic != null && daoActor != null) {
            // if actor is a star, check star checkbox
            if (daoEpic.isStar(daoActor, DevUtils.getDeviceName())) {
                Log.d(TAG, "toActor: existing STAR " + daoActor.getMoniker() + " on device " + DevUtils.getDeviceName() + "...");
                CheckBox cbStar = (CheckBox) mRootView.findViewById(R.id.cb_star);
                cbStar.setChecked(true);
            }


            // establish fore color button visibility & click listener
            mButtonForeColor = (Button) mRootView.findViewById(R.id.button_daomaker_forecolor);
            mButtonForeColor.setVisibility(View.VISIBLE);
            setForeColor(daoActor.getForeColor());
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
            mButtonBackColor = (Button) mRootView.findViewById(R.id.button_daomaker_backcolor);
            mButtonBackColor.setVisibility(View.VISIBLE);
            setBackColor(daoActor.getBackColor());
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
        }
        else {
            if (daoEpic == null) Log.e(TAG, "Oops!  no active epic...");
            else Log.e(TAG, "Oops! actor NULL...");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromAction(DaoAction daoAction) {

        // set Action views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_action);
        ll.setVisibility(View.VISIBLE);

        // action spinner
        final List<String> actionTypeList = DaoAction.getActionTypeList();
        mActionTypeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                actionTypeList);

        mSpinnerActionType = (Spinner) mRootView.findViewById(R.id.spinner_actiontype);
        if (mActionTypeListAdapter != null && mSpinnerActionType != null) {
            mSpinnerActionType.setAdapter(mActionTypeListAdapter);
            // set current selection
            if (actionTypeList.contains(daoAction.getActionType())) {
                int i = actionTypeList.indexOf(daoAction.getActionType());
                mSpinnerActionType.setSelection(i);
            }
            // establish listener
            mSpinnerActionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                    List<String> actionTypeList = DaoAction.getActionTypeList();
                    String actionType = actionTypeList.get(position);
                    Log.d(TAG,"ActionType " + actionType + " at position " + position);
                    EditText etMoniker = (EditText) mRootView.findViewById(R.id.et_moniker);
                    etMoniker.setText(actionType);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mActionTypeListAdapter? " + mActionTypeListAdapter + ", R.id.spinner_actiontype? " + mSpinnerActionType);
            return false;
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromOutcome(DaoOutcome daoOutcome) {
        // set Outcome views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_outcome);
        ll.setVisibility(View.VISIBLE);

        // outcome spinner
        final List<String> outcomeTypeList = DaoOutcome.getOutcomeTypeList();
        mOutcomeTypeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                outcomeTypeList);

        mSpinnerOutcomeType = (Spinner) mRootView.findViewById(R.id.spinner_outcometype);
        if (mOutcomeTypeListAdapter != null && mSpinnerOutcomeType != null) {
            mSpinnerOutcomeType.setAdapter(mOutcomeTypeListAdapter);
            // set current selection
            if (outcomeTypeList.contains(daoOutcome.getOutcomeType())) {
                int i = outcomeTypeList.indexOf(daoOutcome.getOutcomeType());
                mSpinnerOutcomeType.setSelection(i);
            }
            // establish listener
            mSpinnerOutcomeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String outcomeType = outcomeTypeList.get(position);
                    Log.d(TAG,"OutcomeType " + outcomeType + " at position " + position);
                    EditText etMoniker = (EditText) mRootView.findViewById(R.id.et_moniker);
                    etMoniker.setText(outcomeType);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mOutcomeTypeListAdapter? " + mOutcomeTypeListAdapter + ", R.id.spinner_outcometype? " + mSpinnerOutcomeType);
            return false;
        }

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
            Log.d(TAG, "toStory: new object....");
        }
        // update with edited values
        activeStory.setMoniker(editedMoniker);
        activeStory.setHeadline(headline);
        Log.d(TAG, "toStory selected stage " + mSpinnerStages.getSelectedItem().toString());
        activeStory.setStage(mSpinnerStages.getSelectedItem().toString());
        Log.d(TAG, "toStory selected prereq " + mSpinnerPreReqs.getSelectedItem().toString());
        activeStory.setPreReq(mSpinnerPreReqs.getSelectedItem().toString());
        Log.d(TAG, "toStory selected actor " + mSpinnerActors.getSelectedItem().toString());
        activeStory.setActor(mSpinnerActors.getSelectedItem().toString());
        Log.d(TAG, "toStory selected action " + mSpinnerActions.getSelectedItem().toString());
        activeStory.setAction(mSpinnerActions.getSelectedItem().toString());
        Log.d(TAG, "toStory selected outcome " + mSpinnerOutcomes.getSelectedItem().toString());
        activeStory.setOutcome(mSpinnerOutcomes.getSelectedItem().toString());
        Log.d(TAG, "toStory selected postop " + mSpinnerPostOps.getSelectedItem().toString());
        activeStory.setPostOp(mSpinnerPostOps.getSelectedItem().toString());

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
        // ignore ring type
        EditText etRingSize = (EditText) mRootView.findViewById(R.id.et_ringsize);
        Integer ringSize = Integer.parseInt(etRingSize.getText().toString());;
        activeStage.setRingSize(ringSize);
        // update repo
        mParent.getPlayListService().setActiveStage(activeStage);
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
        // if starring on device
        CheckBox cbStar = (CheckBox) mRootView.findViewById(R.id.cb_star);
        DaoEpic daoEpic = mParent.getPlayListService().getActiveEpic();
        if (daoEpic != null && cbStar != null && cbStar.isChecked()) {
            // add to epic star list
            daoEpic.setStar(activeActor, DevUtils.getDeviceName(), mParent.getPlayListService().getActiveStage());
            Log.d(TAG, "toActor: new STAR " + activeActor.getMoniker() + " on device " + DevUtils.getDeviceName() + "...");
            // update repo
            mParent.getRepoProvider().getDalEpic().update(daoEpic, true);
        }

        // TODO: setActiveActor on create/update?
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
        Log.d(TAG, "toAction selected action type " + mSpinnerActionType.getSelectedItem().toString());
        activeAction.setActionType(mSpinnerActionType.getSelectedItem().toString());
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
        Log.d(TAG, "toOutcome selected action type " + mSpinnerOutcomeType.getSelectedItem().toString());
        activeOutcome.setOutcomeType(mSpinnerOutcomeType.getSelectedItem().toString());
        mParent.getPlayListService().setActiveOutcome(activeOutcome);
        // update repo
        mParent.getRepoProvider().getDalOutcome().update(activeOutcome, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
