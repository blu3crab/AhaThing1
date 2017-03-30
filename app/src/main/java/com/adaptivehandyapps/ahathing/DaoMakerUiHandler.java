package com.adaptivehandyapps.ahathing;
//
// Created by mat on 1/6/2017.
//

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.TimeUtils;
import com.adaptivehandyapps.ahathing.dal.StoryProvider;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

import java.util.ArrayList;
import java.util.List;

public class DaoMakerUiHandler {
    private static final String TAG = "DaoMakerUiHandler";

    private View mRootView;
    private StoryProvider mStoryProvider;

    private TagListAdapter mTagListAdapter = null;
    private ArrayAdapter<String> mPrereqListAdapter = null;
    private Spinner mSpinnerPrereqs;
    private ArrayAdapter<String> mStageListAdapter = null;
    private Spinner mSpinnerStages;

    private DaoTheatre mActiveTheatre;
    private DaoEpic mActiveEpic;
    private DaoStory mActiveStory;
    private DaoStage mActiveStage;

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
    public DaoMakerUiHandler(View v, StoryProvider storyProvider, final String op, final String objType, final String moniker) {
        mRootView = v;
        mStoryProvider = storyProvider;

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
                mActiveTheatre = (DaoTheatre)mStoryProvider.getDaoTheatreRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveTheatre.getTimestamp()) + "(" + mActiveTheatre.getTimestamp().toString() + ")";
                headline = mActiveTheatre.getHeadline();
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                mActiveEpic = (DaoEpic)mStoryProvider.getDaoEpicRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveEpic.getTimestamp()) + "(" + mActiveEpic.getTimestamp().toString() + ")";
                headline = mActiveEpic.getHeadline();
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                mActiveStory = (DaoStory)mStoryProvider.getDaoStoryRepo().get(moniker);
                date = TimeUtils.secsToDate(mActiveStory.getTimestamp()) + "(" + mActiveStory.getTimestamp().toString() + ")";
                headline = mActiveStory.getHeadline();
                // xfer story to view
                fromStory(mActiveStory);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
            }
        }
        else if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_NEW)) {
            if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                mActiveTheatre = new DaoTheatre();
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                mActiveEpic = new DaoEpic();
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                mActiveStory = new DaoStory();
                fromStory(mActiveStory);
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
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

    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleTagList(final String objType) {

        // list handler
        int resId = R.layout.tag_list_item;
        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();

        if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
            LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_tags);
            ll.setVisibility(View.VISIBLE);
            // default list item color to not selected
            int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
            // dereference epic repo dao list
            List<DaoEpic> daoEpicList = (List<DaoEpic>)(List<?>) mStoryProvider.getDaoEpicRepo().getDaoList();
            // for each epic in repo
            for (DaoEpic epic : daoEpicList) {
                // build list of epic names, labels & images
                tagNameList.add(epic.getMoniker());
//                tagLabelList.add(epic.getHeadline());
                tagLabelList.add("headline activity here...");
                int imageResId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
                tagImageResIdList.add(imageResId);
                // if active theatre defined & epic selected
                bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
                if (mActiveTheatre != null && mActiveTheatre.getTagList().contains(epic.getMoniker())) {
                    // highlight list item
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                }
                tagBgColorList.add(bgColor);
            }
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            // build list of story names, labels & images
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
            // build list of audit trail entries
            for (int i = mStoryProvider.getDaoAuditRepo().size()-1; i > -1 ; i--) {
                DaoAudit audit = mStoryProvider.getDaoAuditRepo().get(i);
                tagNameList.add(audit.toFormattedString());
//                tagLabelList.add("");
                int imageResId = DaoDefs.DAOOBJ_TYPE_STORY_IMAGE_RESID;
                tagImageResIdList.add(imageResId);
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
                // theatre - taglist of epics
                if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                    if (mActiveTheatre != null) {
//                        DaoEpic daoEpic = (DaoEpic) mStoryProvider.getDaoEpicRepo().get(position);
                        // if taglist contains epic
                        if (mActiveTheatre.getTagList().contains(value)) {
                            // find epic in taglist & remove
                            int i = mActiveTheatre.getTagList().indexOf(value);
                            mActiveTheatre.getTagList().remove(i);
                            Log.d(TAG,"handleTagList remove item " + value + " at position " + i);
                        }
                        else {
                            // add epic to taglist
                            mActiveTheatre.getTagList().add(value);
                            // set color selected
                            bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                            Log.d(TAG,"handleTagList add item " + value + " at position " + (mActiveTheatre.getTagList().size()-1));
                        }
                    }
                    else {
                        Log.e(TAG, "Oops! active theatre NULL!");
                    }
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER)) {
                }

                v.setBackgroundColor(bgColor);
            }
        });
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean fromStory(DaoStory daoStory) {

        // set story views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_story);
        ll.setVisibility(View.VISIBLE);

        // dereference stage repo dao list
        List<DaoStage> daoStageList = (List<DaoStage>)(List<?>) mStoryProvider.getDaoStageRepo().getDaoList();
        List<String> stageNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoStage stage : daoStageList) {
            // build list of stage names, labels & images
            stageNameList.add(stage.getMoniker());
        }
        mStageListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                stageNameList);

        mSpinnerStages = (Spinner) mRootView.findViewById(R.id.spinner_stages);
        if (mStageListAdapter != null && mSpinnerStages != null) {
            mSpinnerStages.setAdapter(mStageListAdapter);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mStageListAdapter? " + mStageListAdapter + ", R.id.spinner_stages? " + mSpinnerStages);
            return false;
        }
        // dereference prereq repo dao list
//        List<DaoStage> daoStageList = (List<DaoStage>)(List<?>) mStoryProvider.getDaoStageRepo().getDaoList();
//        List<String> tagNameList = new ArrayList<>();
//        // for each stage in repo
//        for (DaoStage stage : daoStageList) {
//            // build list of stage names, labels & images
//            tagNameList.add(stage.getMoniker());
//        }
        List<String> prereqNameList = new ArrayList<>();
        prereqNameList.clear();
        prereqNameList.add("prereq1");
        mPrereqListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                prereqNameList);

        mSpinnerPrereqs = (Spinner) mRootView.findViewById(R.id.spinner_prereqs);
        if (mPrereqListAdapter != null && mSpinnerPrereqs != null) {
            mSpinnerPrereqs.setAdapter(mPrereqListAdapter);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mPrereqListAdapter? " + mPrereqListAdapter + ", R.id.spinner_Prereqs? " + mSpinnerPrereqs);
            return false;
        }


        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean toTheatre(String op, String moniker, String editedMoniker, String headline) {
        // xfer view to theatre object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            mActiveTheatre = (DaoTheatre) mStoryProvider.getDaoTheatreRepo().get(moniker);
            if (mActiveTheatre != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mStoryProvider.removeTheatre(mActiveTheatre, true);
                }
            }
            else {
                // error: never should edit NULL object!  create a placeholder & carry on
                mActiveTheatre = new DaoTheatre();
                Log.e(TAG, "toTheatre: editing NULL object?");
            }
        }
        // update with edited values
        mActiveTheatre.setMoniker(editedMoniker);
        mActiveTheatre.setHeadline(headline);
        // update repo
        mStoryProvider.updateTheatre(mActiveTheatre, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean toEpic(String op, String moniker, String editedMoniker, String headline) {
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            mActiveEpic = (DaoEpic)mStoryProvider.getDaoEpicRepo().get(moniker);
            if (mActiveEpic != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mStoryProvider.removeEpic(mActiveEpic, true);
                }
            }
            else {
                // error: never should edit NULL object!  create a placeholder & carry on
                mActiveEpic = new DaoEpic();
                Log.e(TAG, "toEpic: editing NULL object?");
            }
        }
        // update with edited values
        mActiveEpic.setMoniker(editedMoniker);
        mActiveEpic.setHeadline(headline);
        // update repo
        mStoryProvider.updateEpic(mActiveEpic, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean toStory(String op, String moniker, String editedMoniker, String headline) {
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            mActiveStory = (DaoStory)mStoryProvider.getDaoStoryRepo().get(moniker);
            if (mActiveStory != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mStoryProvider.removeStory(mActiveStory, true);
                }
            }
            else {
                // error: never should edit NULL object!  create a placeholder & carry on
                mActiveStory = new DaoStory();
                Log.e(TAG, "toStory: editing NULL object?");
            }
        }
        // update with edited values
        mActiveStory.setMoniker(editedMoniker);
        mActiveStory.setHeadline(headline);

        mActiveStory.setStage(mSpinnerStages.getSelectedItem().toString());
        // update repo
        mStoryProvider.updateStory(mActiveStory, true);
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
                    toTheatre(op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    toEpic(op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    toStory (op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                }

                // refresh content view
                Log.d(TAG, "buttonCreate.setOnClickListener callback...");
                if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);

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
                }

                // refresh content view
                Log.d(TAG, "buttonDestroy.setOnClickListener callback...");
                if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);
            }
        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyTheatre(String moniker) {
        DaoTheatre daoTheatre = (DaoTheatre) mStoryProvider.getDaoTheatreRepo().get(moniker);
        if (daoTheatre != null) {
            // remove obsolete entry
            mStoryProvider.removeTheatre(daoTheatre, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyTheatre: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyEpic(String moniker) {
        DaoEpic daoEpic = (DaoEpic) mStoryProvider.getDaoEpicRepo().get(moniker);
        if (daoEpic != null) {
            // remove obsolete entry
            mStoryProvider.removeEpic(daoEpic, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyEpic: editing NULL object?");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean destroyStory(String moniker) {
        DaoStory daoStory = (DaoStory) mStoryProvider.getDaoStoryRepo().get(moniker);
        if (daoStory != null) {
            // remove obsolete entry
            mStoryProvider.removeStory(daoStory, true);
        }
        else {
            // error: never should edit NULL object!  do nothing...
            Log.e(TAG, "destroyStory: editing NULL object?");
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
                Log.v(TAG, "buttonCancel.setOnClickListener: ");
                Toast.makeText(mRootView.getContext(), "Canceling thing...", Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);
            }
        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
