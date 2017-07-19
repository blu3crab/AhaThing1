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

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.TimeUtils;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoMakerUiHandler {
    private static final String TAG = "DaoMakerUiHandler";

    private View mRootView;

    private ContentFragment mParent;
    private DaoMakerViewXfer mDaoMakerViewXfer;

    private TagListAdapter mTagListAdapter = null;
    private List<String> mTagList;

    private DaoTheatre mActiveTheatre;
    private DaoEpic mActiveEpic;
    private DaoStory mActiveStory;
    private DaoStage mActiveStage;
    private DaoActor mActiveActor;
    private DaoAction mActiveAction;
    private DaoOutcome mActiveOutcome;

    ///////////////////////////////////////////////////////////////////////////
    // define an interface for a callback invoked when a result occurs e.g. ok/cancal
    private OnContentHandlerResult mCallback = null; //call back interface

    ///////////////////////////////////////////////////////////////////////////
    // interface for a callback invoked when a result occurs e.g. ok/cancal
    public interface OnContentHandlerResult {
        void onContentHandlerResult(String op, String objType, String moniker);
    }
    ///////////////////////////////////////////////////////////////////////////
    // setter
    public boolean setOnContentHandlerResultCallback(OnContentHandlerResult callback) {
        mCallback = callback;
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public DaoMakerUiHandler(ContentFragment parent, View v, final String op, final String objType, final String moniker) {

        mParent = parent;
        Log.d(TAG, "DaoMakerUiHandler: mParent " + mParent);

        Log.d(TAG, "DaoMakerUiHandler: op " + op + ", objtype" + objType + ", moniker " + moniker);
        mRootView = v;

//        // TODO: rationalize FAB
//        // if story outcome is ResetEpic, launch run FAB to run the story
//        DaoStory daoStory = mParent.getPlayListService().getActiveStory();
//        if (daoStory != null && daoStory.getOutcome().equals(DaoOutcome.OUTCOME_TYPE_RESET_EPIC)) {
//            final DaoEpic daoEpic = mParent.getPlayListService().getActiveEpic();
//            final DaoStage daoStage = mParent.getPlayListService().getActiveStage();
//
//            if (daoEpic != null && daoStage != null) {
//
//                FloatingActionButton fab = (FloatingActionButton) mParent.getActivity().findViewById(R.id.fab_run);
//                fab.setVisibility(View.VISIBLE);
//                fab.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Snackbar.make(view, "Patience, Grasshopper.", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                        Log.d(TAG, "execute fab operation...");
//                        // reset epic
//                        daoEpic.resetEpicStageTallyTic(daoStage, true, true);
//                        mParent.getRepoProvider().getDalEpic().update(daoEpic, true);
//                        mParent.getRepoProvider().getDalStage().update(daoStage, true);
//                        // banish fab
//                        FloatingActionButton fab = (FloatingActionButton) mParent.getActivity().findViewById(R.id.fab_run);
//                        fab.setVisibility(View.GONE);
//                        // refresh content view
//                        Log.e(TAG, "buttonCreate.setOnClickListener unknown object - callback...");
//                        if (mCallback != null)
//                            mCallback.onContentHandlerResult(op, objType, moniker);
//                    }
//                });
//            }
//        }
        // if editing epic, provide reset FAB
        if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER) && op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            final DaoEpic daoEpic = mParent.getPlayListService().getActiveEpic();
            final DaoStage daoStage = mParent.getPlayListService().getActiveStage();
            // if epic & stage are defined
            if (daoEpic != null && daoStage != null) {

                FloatingActionButton fab = (FloatingActionButton) mParent.getActivity().findViewById(R.id.fab_run);
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Patience, Grasshopper.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Log.d(TAG, "execute fab operation...");
                        // reset epic
                        daoEpic.resetEpicStageTallyTic(daoStage, true, true);
                        mParent.getRepoProvider().getDalEpic().update(daoEpic, true);
                        mParent.getRepoProvider().getDalStage().update(daoStage, true);
                        // banish fab
                        FloatingActionButton fab = (FloatingActionButton) mParent.getActivity().findViewById(R.id.fab_run);
                        fab.setVisibility(View.GONE);
                        // refresh content view
                        if (mCallback != null)
                            mCallback.onContentHandlerResult(op, objType, moniker);
                    }
                });
            }
            else  Log.e(TAG, "DaoMakerUiHandler finds undefined epic or stage, no FAB presented...");
        }

        // create view xfer to transfer between view & objects
        mDaoMakerViewXfer = new DaoMakerViewXfer(mParent, mRootView);

        // show title: op + moniker
        TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        tvTitle.setText(op + ": " + moniker);
        // show moniker
        TextView tvMonikerLabel = (TextView) mRootView.findViewById(R.id.tv_moniker_label);
        tvMonikerLabel.setText("Moniker: ");

        EditText etMoniker = (EditText) mRootView.findViewById(R.id.et_moniker);
        etMoniker.setText(moniker);

        // set current date
        String date = TimeUtils.secsToDate(System.currentTimeMillis());
        String headline = "";
        // init object type specific fields
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                mActiveTheatre = (DaoTheatre) mParent.getRepoProvider().getDalTheatre().getDaoRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveTheatre.getTimestamp()) + "(" + mActiveTheatre.getTimestamp().toString() + ")";
                headline = mActiveTheatre.getHeadline();
//                mTagList = new ArrayList<>(mActiveTheatre.getTagList());
                // xfer object to view
                mDaoMakerViewXfer.fromTheatre(mActiveTheatre);

            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                mActiveEpic = (DaoEpic) mParent.getRepoProvider().getDalEpic().getDaoRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveEpic.getTimestamp()) + "(" + mActiveEpic.getTimestamp().toString() + ")";
                headline = mActiveEpic.getHeadline();

                mActiveStage = null;
                mTagList = new ArrayList<>(mActiveEpic.getTagList());
                if (mTagList != null && mTagList.size() > 0) {
                    mActiveStory = (DaoStory) mParent.getRepoProvider().getDalStory().getDaoRepo().get(mTagList.get(0));
                    if (mActiveStory != null) {
                        String stageMoniker = mActiveStory.getStage();
                        mActiveStage = (DaoStage) mParent.getRepoProvider().getDalStage().getDaoRepo().get(stageMoniker);
                    }
                }
                // xfer object to view
                mDaoMakerViewXfer.fromEpic(mActiveEpic, mActiveStage);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                mActiveStory = (DaoStory) mParent.getRepoProvider().getDalStory().getDaoRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveStory.getTimestamp()) + "(" + mActiveStory.getTimestamp().toString() + ")";
                headline = mActiveStory.getHeadline();
                // xfer object to view
                mDaoMakerViewXfer.fromStory(mActiveStory);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                mActiveStage = (DaoStage) mParent.getRepoProvider().getDalStage().getDaoRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveStage.getTimestamp()) + "(" + mActiveStage.getTimestamp().toString() + ")";
                headline = mActiveStage.getHeadline();
                // xfer object to view
                mDaoMakerViewXfer.fromStage(mActiveStage);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                mActiveActor = (DaoActor) mParent.getRepoProvider().getDalActor().getDaoRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveActor.getTimestamp()) + "(" + mActiveActor.getTimestamp().toString() + ")";
                headline = mActiveActor.getHeadline();
                // xfer object to view
                mDaoMakerViewXfer.fromActor(mActiveActor);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                mActiveAction = (DaoAction) mParent.getRepoProvider().getDalAction().getDaoRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveAction.getTimestamp()) + "(" + mActiveAction.getTimestamp().toString() + ")";
                headline = mActiveAction.getHeadline();
                // xfer object to view
                mDaoMakerViewXfer.fromAction(mActiveAction);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                mActiveOutcome = (DaoOutcome) mParent.getRepoProvider().getDalOutcome().getDaoRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveOutcome.getTimestamp()) + "(" + mActiveOutcome.getTimestamp().toString() + ")";
                headline = mActiveOutcome.getHeadline();
                // xfer object to view
                mDaoMakerViewXfer.fromOutcome(mActiveOutcome);
            }
        }
        else if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_NEW)) {
            // if new
            if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                mActiveTheatre = new DaoTheatre();
//                mTagList = new ArrayList<>();
                mDaoMakerViewXfer.fromTheatre(mActiveTheatre);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                mActiveEpic = new DaoEpic();
//                mTagList = new ArrayList<>();
                // xfer object to view
                mDaoMakerViewXfer.fromEpic(mActiveEpic, null);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                // create object & xfer the contents to the view
                mActiveStory = new DaoStory();
                mDaoMakerViewXfer.fromStory(mActiveStory);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                // create object & xfer the contents to the view
                mActiveStage = new DaoStage();
                mDaoMakerViewXfer.fromStage(mActiveStage);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                // create object & xfer the contents to the view
                mActiveActor = new DaoActor();
                mDaoMakerViewXfer.fromActor(mActiveActor);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                // create object & xfer the contents to the view
                mActiveAction = new DaoAction();
                mDaoMakerViewXfer.fromAction(mActiveAction);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                // create object & xfer the contents to the view
                mActiveOutcome = new DaoOutcome();
                mDaoMakerViewXfer.fromOutcome(mActiveOutcome);
            }
        }

        /// show headline
        TextView tvHeadlineLabel = (TextView) mRootView.findViewById(R.id.tv_headline_label);
        tvHeadlineLabel.setText("Headline: ");

        EditText etHeadline = (EditText) mRootView.findViewById(R.id.et_headline);
        etHeadline.setText(headline);

        // show last update date
        TextView tv_last_update = (TextView) mRootView.findViewById(R.id.tv_last_update);
        tv_last_update.setText(date);

        // establish tag list
        handleTagList(objType);

        if (!op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_SHOWLIST)) {
            // establish button handlers
            handleCreateButton(op, objType, moniker);
            handleDestroyButton(op, objType, moniker);
            handleCancelButton(op, objType, moniker);
        }

    }

//    ///////////////////////////////////////////////////////////////////////////
//    private Boolean isOrphanInSubList(List<String> completeList, List<String> subList, Boolean repair) {
//        Integer testIndex = 0;
//        Integer orphanCount = 0;
//        List<Integer> orphanList = new ArrayList<>();
//        for (String sub : subList) {
//            if (!completeList.contains(sub)) {
//                ++orphanCount;
//                orphanList.add(testIndex);
//            }
//            ++testIndex;
//        }
//        if (repair && orphanCount > 0) {
//            for (Integer i = 0; i < orphanCount; ++i) {
//                subList.remove(orphanList.get(i));
//            }
//        }
//        return false;
//    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleTagList(final String objType) {

        // list handler
        int resId = R.layout.tag_list_item;
        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();

        if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
//            LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_tags);
//            ll.setVisibility(View.VISIBLE);
//            // default list item color to not selected
//            int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
//            // dereference epic repo dao list
//            List<DaoEpic> daoEpicList = (List<DaoEpic>)(List<?>) mParent.getRepoProvider().getDalEpic().getDaoRepo().getDaoList();
//            List<String> daoEpicMonikerList = (List<String>)(List<?>) mParent.getRepoProvider().getDalEpic().getDaoRepo().getMonikerList();
//            if (!daoEpicMonikerList.containsAll(mTagList)) {
//                Log.e(TAG, "Oops!  Orphan Epic in Theatre, repairing...");
//                mParent.getPlayListService().repairAll(true, true);
//            }
//            // for each epic in repo
//            for (DaoEpic epic : daoEpicList) {
//                // build list of epic names, labels & images
//                tagNameList.add(epic.getMoniker());
//                if (!epic.getHeadline().equals(DaoDefs.INIT_STRING_MARKER)) {
//                    tagLabelList.add(epic.getHeadline());
//                }
//                else {
//                    tagLabelList.add("epic headline activity here...");
//                }
//                int imageResId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
//                tagImageResIdList.add(imageResId);
//                // epic is in tag list - set selected color
//                bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
//                if (mTagList.contains(epic.getMoniker())) {
//                    // highlight list item
//                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
//                }
//                tagBgColorList.add(bgColor);
//            }
            return true;
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
//            // build list of story names, labels & images
//            LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_tags);
//            ll.setVisibility(View.VISIBLE);
//            // default list item color to not selected
//            int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
//            // dereference story repo dao list
//            List<DaoStory> daoStoryList = (List<DaoStory>)(List<?>) mParent.getRepoProvider().getDalStory().getDaoRepo().getDaoList();
//            List<String> daoStoryMonikerList = (List<String>)(List<?>) mParent.getRepoProvider().getDalStory().getDaoRepo().getMonikerList();
//            if (!daoStoryMonikerList.containsAll(mTagList)) {
//                Log.e(TAG, "Oops!  Orphan Story in Epic...");
//                mParent.getPlayListService().repairAll(true, true);
//            }
//            // for each story in repo
//            for (DaoStory story : daoStoryList) {
//                // build list of story names, labels & images
//                tagNameList.add(story.getMoniker());
//                if (!story.getHeadline().equals(DaoDefs.INIT_STRING_MARKER)) {
//                    tagLabelList.add(story.getHeadline());
//                }
//                else {
//                    tagLabelList.add("epic headline activity here...");
//                }
//                int imageResId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
//                tagImageResIdList.add(imageResId);
//                // story is in tag list - set selected color
//                bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
//                if (mTagList.contains(story.getMoniker())) {
//                    // highlight list item
//                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
//                }
//                tagBgColorList.add(bgColor);
//            }
            return true;
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            // no tag list
            return true;
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
            // no tag list
            return true;
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
            // no tag list
            return true;
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
            // no tag list
            return true;
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
            // no tag list
            return true;
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER)) {
            LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_tags);
            ll.setVisibility(View.VISIBLE);
            int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
            // build list of audit trail entries
            for (int i = mParent.getRepoProvider().getDaoAuditRepo().size()-1; i > -1 ; i--) {
                DaoAudit audit = mParent.getRepoProvider().getDaoAuditRepo().get(i);
                tagNameList.add(audit.toFormattedString());
//                tagLabelList.add("");
                int imageResId = DaoDefs.DAOOBJ_TYPE_AUDIT_IMAGE_RESID;
                tagImageResIdList.add(imageResId);
                tagBgColorList.add(bgColor);
            }
        }

        // instantiate list adapter
        mTagListAdapter =
                new TagListAdapter(mRootView.getContext(),
                        resId,
                        tagNameList,
                        tagLabelList,
                        tagImageResIdList,
                        tagBgColorList);

        ListView lv = (ListView) mRootView.findViewById(R.id.listview_tags);
        if (mTagListAdapter != null && lv != null) {
            lv.setAdapter(mTagListAdapter);
        } else {
            // pipe_list_item_site panel ahs no alert list
            Log.e(TAG, "NULL mTagListAdapter? " + mTagListAdapter + ", R.id.listview? " + lv);
            return false;
        }
        // establish listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                Log.d(TAG,"handleTagList item " + value + " at position " + position);
                int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);

                // if taglist contains selection
                if (mTagList.contains(value)) {
                    // find epic in taglist & remove
                    int i = mTagList.indexOf(value);
                    mTagList.remove(i);
                    Log.d(TAG,"handleTagList remove item " + value + " at position " + i);
                }
                else {
                    // add selection to taglist
                    mTagList.add(value);
                    // set color selected
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                    Log.d(TAG,"handleTagList add item " + value + " at position " + (mTagList.size()-1));
                }

                v.setBackgroundColor(bgColor);
            }
        });
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleCreateButton(final String op, final String objType, final String moniker) {
        // establish create button visibility & click listener
        final Button buttonCreate = (Button) mRootView.findViewById(R.id.button_daomaker_create);
        buttonCreate.setVisibility(View.VISIBLE);
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            buttonCreate.setText("Update");
        }
        // button handlers
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonCreate.setOnClickListener: ");
                EditText etMoniker = (EditText) mRootView.findViewById(R.id.et_moniker);
                String editedMoniker = etMoniker.getText().toString();
                EditText etHeadline = (EditText) mRootView.findViewById(R.id.et_headline);
                String headline = etHeadline.getText().toString();

                Toast.makeText(mRootView.getContext(), "Creating thing " + editedMoniker, Toast.LENGTH_SHORT).show();

                if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                    mDaoMakerViewXfer.toTheatre(op, moniker, editedMoniker, headline, mTagList);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    mDaoMakerViewXfer.toEpic(op, moniker, editedMoniker, headline, mTagList);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    mDaoMakerViewXfer.toStory (op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                    mDaoMakerViewXfer.toStage (op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                    mDaoMakerViewXfer.toActor (op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                    mDaoMakerViewXfer.toAction (op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                    mDaoMakerViewXfer.toOutcome (op, moniker, editedMoniker, headline);
                }
                else {
                    // banish fab
                    FloatingActionButton fab = (FloatingActionButton) mParent.getActivity().findViewById(R.id.fab_run);
                    fab.setVisibility(View.GONE);
                    // refresh content view
                    Log.e(TAG, "buttonCreate.setOnClickListener unknown object - callback...");
                    if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);
                }

            }
        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleDestroyButton(final String op, final String objType, final String moniker) {
        // establish destroy button visibility & click listener
        final Button buttonDestroy = (Button) mRootView.findViewById(R.id.button_daomaker_destroy);
        // if editing existing object, present Destroy option
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            buttonDestroy.setVisibility(View.VISIBLE);
        }

        buttonDestroy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonDestroy.setOnClickListener: ");
                Toast.makeText(mRootView.getContext(), "Destroying thing...", Toast.LENGTH_SHORT).show();
                if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                    destroyTheatre(moniker);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    destroyEpic(moniker);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    destroyStory(moniker);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                    destroyStage(moniker);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                    destroyActor(moniker);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                    destroyAction(moniker);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                    destroyOutcome(moniker);
                }
                else {
                    // refresh content view
                    Log.e(TAG, "buttonDestroy.setOnClickListener unknown object - callback...");
                    if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);
                }
                // ensure playlist is coherent - any undefined objects?
                Boolean removeIfUndefined = true;
                Boolean forceToActiveStage = true;
                mParent.getPlayListService().repairAll(removeIfUndefined, forceToActiveStage);
            }
        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyTheatre(String moniker) {
        DaoTheatre daoTheatre = (DaoTheatre) mParent.getRepoProvider().getDalTheatre().getDaoRepo().get(moniker);
        if (daoTheatre != null) {
            // remove obsolete entry
            mParent.getRepoProvider().getDalTheatre().remove(daoTheatre, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyTheatre: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyEpic(String moniker) {
        DaoEpic daoEpic = (DaoEpic) mParent.getRepoProvider().getDalEpic().getDaoRepo().get(moniker);
        if (daoEpic != null) {
            // remove obsolete entry
            mParent.getRepoProvider().getDalEpic().remove(daoEpic, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyEpic: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyStory(String moniker) {
        DaoStory daoStory = (DaoStory) mParent.getRepoProvider().getDalStory().getDaoRepo().get(moniker);
        if (daoStory != null) {
            // remove obsolete entry
            mParent.getRepoProvider().getDalStory().remove(daoStory, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyStory: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyStage(String moniker) {
        DaoStage daoStage = (DaoStage) mParent.getRepoProvider().getDalStage().getDaoRepo().get(moniker);
        if (daoStage != null) {
            // remove obsolete entry
            mParent.getRepoProvider().getDalStage().remove(daoStage, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyStage: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyActor(String moniker) {
        DaoActor daoActor = (DaoActor) mParent.getRepoProvider().getDalActor().getDaoRepo().get(moniker);
        if (daoActor != null) {
            // remove obsolete entry
            mParent.getRepoProvider().getDalActor().remove(daoActor, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyActor: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyAction(String moniker) {
        DaoAction daoAction = (DaoAction) mParent.getRepoProvider().getDalAction().getDaoRepo().get(moniker);
        if (daoAction != null) {
            // remove obsolete entry
            mParent.getRepoProvider().getDalAction().remove(daoAction, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyAction: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyOutcome(String moniker) {
        DaoOutcome daoOutcome = (DaoOutcome) mParent.getRepoProvider().getDalOutcome().getDaoRepo().get(moniker);
        if (daoOutcome != null) {
            // remove obsolete entry
            mParent.getRepoProvider().getDalOutcome().remove(daoOutcome, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyOutcome: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleCancelButton(final String op, final String objType, final String moniker) {
        // establish cancel button visibility & click listener
        final Button buttonCancel = (Button) mRootView.findViewById(R.id.button_daomaker_cancel);
        buttonCancel.setVisibility(View.VISIBLE);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // banish fab
                FloatingActionButton fab = (FloatingActionButton) mParent.getActivity().findViewById(R.id.fab_run);
                fab.setVisibility(View.GONE);

                Log.v(TAG, "buttonCancel.setOnClickListener: ");
                Toast.makeText(mRootView.getContext(), "Canceling thing...", Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);
            }
        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
