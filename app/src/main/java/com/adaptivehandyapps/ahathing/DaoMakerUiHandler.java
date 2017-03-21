package com.adaptivehandyapps.ahathing;
//
// Created by mat on 1/6/2017.
//

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.TimeUtils;
import com.adaptivehandyapps.ahathing.dal.StoryProvider;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.adaptivehandyapps.ahathing.dao.DaoTheatreRepo;

import java.util.ArrayList;
import java.util.List;

public class DaoMakerUiHandler {
    private static final String TAG = "DaoMakerUiHandler";

    private View mRootView;
    private StoryProvider mStoryProvider;

    private TagListAdapter mTagListAdapter = null;


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

        // show creation date
        TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        tvTitle.setText(op + ": " + moniker);

        TextView tvMonikerLabel = (TextView) mRootView.findViewById(R.id.tv_moniker_label);
        tvMonikerLabel.setText("Moniker: ");

        EditText etMoniker = (EditText) mRootView.findViewById(R.id.et_moniker);
        etMoniker.setText(moniker);

        TextView tvHeadlineLabel = (TextView) mRootView.findViewById(R.id.tv_headline_label);
        tvHeadlineLabel.setText("Headline: ");

        EditText etHeadline = (EditText) mRootView.findViewById(R.id.et_headline);

        String date = TimeUtils.secsToDate(System.currentTimeMillis());
        // init object type specific fields
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                DaoTheatre daoTheatre = (DaoTheatre)mStoryProvider.getDaoTheatreRepo().get(moniker);
                etHeadline.setText(daoTheatre.getHeadline());
                date = TimeUtils.secsToDate(daoTheatre.getTimestamp()) + "(" + daoTheatre.getTimestamp().toString() + ")";
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                DaoEpic daoEpic = (DaoEpic)mStoryProvider.getDaoEpicRepo().get(moniker);
                etHeadline.setText(daoEpic.getHeadline());
                date = TimeUtils.secsToDate(daoEpic.getTimestamp()) + "(" + daoEpic.getTimestamp().toString() + ")";
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            }
            else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
            }
        }

        // show creation date
        TextView tv_last_update = (TextView) mRootView.findViewById(R.id.tv_last_update);
//        String date = TimeUtils.secsToDate(System.currentTimeMillis());
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
    private Boolean handleTagList(String objType) {

        // list handler
        int resId = R.layout.tag_list_item;
        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();

        if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
            // build list of epic names, labels & images
            List<DaoEpic> daoEpicList = (List<DaoEpic>)(List<?>) mStoryProvider.getDaoEpicRepo().getDaoList();
            for (DaoEpic epic : daoEpicList) {
                tagNameList.add(epic.getMoniker());
                tagLabelList.add(epic.getHeadline());
                int imageResId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
                tagImageResIdList.add(imageResId);
                int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
                if (mStoryProvider.getActiveTheatre() != null && mStoryProvider.getActiveTheatre().getTagList().contains(epic.getMoniker())) {
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                }
                tagBgColorList.add(bgColor);
            }
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            // build list of story names, labels & images
//            for (DaoStory story : mStoryProvider.getDaoEpicRepo().getDaoList()) {
//                tagNameList.add(story.getMoniker());
//                tagLabelList.add(story.getHeadline());
//                int imageResId = DaoDefs.DAOOBJ_TYPE_STORY_IMAGE_RESID;
//                tagImageResIdList.add(imageResId);
//            }
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            // build list of story names, labels & images
//            for (DaoStory story : mStoryProvider.getDaoStoryList().stories) {
//                tagNameList.add(story.getMoniker());
//                tagLabelList.add("...");
//                int imageResId = DaoDefs.DAOOBJ_TYPE_STORY_IMAGE_RESID;
//                tagImageResIdList.add(imageResId);
//            }
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER)) {
            // build list of audit trail entries
            for (int i = mStoryProvider.getDaoAuditRepo().size()-1; i > -1 ; i--) {
                DaoAudit audit = mStoryProvider.getDaoAuditRepo().get(i);
                tagNameList.add(audit.toFormattedString());
                tagLabelList.add("");
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

        ListView lv = (ListView) mRootView.findViewById(R.id.listview_alert);
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
                if (mStoryProvider.getActiveTheatre() != null) {
                    DaoTheatre daoTheatre = mStoryProvider.getActiveTheatre();
                    DaoEpic daoEpic = (DaoEpic)mStoryProvider.getDaoEpicRepo().get(position);
                    if (daoTheatre.getTagList().contains(daoEpic.getMoniker())) {
                        v.setBackgroundColor(mRootView.getResources().getColor(R.color.brightgreen));
                    }
                }

                v.setBackgroundColor(mRootView.getResources().getColor(R.color.brightgreen));
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
                    makeTheatre(op, moniker, editedMoniker, headline);
//                    // get theatre object, update name, update repo
//                    DaoTheatre daoTheatre = new DaoTheatre();
//                    if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
//                        daoTheatre = mStoryProvider.getDaoTheatreRepo().get(moniker);
//                        if (daoTheatre != null) {
//                            // if moniker has been edited
//                            if (!moniker.equals(thingMoniker)) {
//                                // remove obsolete entry
//                                mStoryProvider.removeTheatre(daoTheatre, true);
//                            }
//                        }
//                        else {
//                            // error: never should edit NULL object!  create a placeholder & carry on
//                            daoTheatre = new DaoTheatre();
//                            Log.e(TAG, "buttonCreate.setOnClickListener: editing NULL object?");
//                        }
//                    }
//                    // update with edited values
//                    daoTheatre.setMoniker(thingMoniker);
//                    daoTheatre.setHeadline(thingHeadline);
//                    // update repo
//                    mStoryProvider.updateTheatre(daoTheatre, true);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    makeEpic(op, moniker, editedMoniker, headline);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
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
    private Boolean makeTheatre(String op, String moniker, String editedMoniker, String headline) {
        // make theatre object, update name, update repo
        DaoTheatre daoTheatre = new DaoTheatre();
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            daoTheatre = (DaoTheatre) mStoryProvider.getDaoTheatreRepo().get(moniker);
            if (daoTheatre != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mStoryProvider.removeTheatre(daoTheatre, true);
                }
            }
            else {
                // error: never should edit NULL object!  create a placeholder & carry on
                daoTheatre = new DaoTheatre();
                Log.e(TAG, "makeTheatre: editing NULL object?");
            }
        }
        // update with edited values
        daoTheatre.setMoniker(editedMoniker);
        daoTheatre.setHeadline(headline);
        // update repo
        mStoryProvider.updateTheatre(daoTheatre, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean makeEpic(String op, String moniker, String editedMoniker, String headline) {
        // make Epic object, update name, update repo
        DaoEpic daoEpic = new DaoEpic();
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            daoEpic = (DaoEpic)mStoryProvider.getDaoEpicRepo().get(moniker);
            if (daoEpic != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mStoryProvider.removeEpic(daoEpic, true);
                }
            }
            else {
                // error: never should edit NULL object!  create a placeholder & carry on
                daoEpic = new DaoEpic();
                Log.e(TAG, "makeEpic: editing NULL object?");
            }
        }
        // update with edited values
        daoEpic.setMoniker(editedMoniker);
        daoEpic.setHeadline(headline);
        // update repo
        mStoryProvider.updateEpic(daoEpic, true);
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
//                    // get theatre object, update name, update repo
//                    if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
//                        DaoTheatre daoTheatre = mStoryProvider.getDaoTheatreRepo().get(moniker);
//                        if (daoTheatre != null) {
//                            // remove obsolete entry
//                            mStoryProvider.removeTheatre(daoTheatre, true);
//                        }
//                        else {
//                            // error: never should edit NULL object!  do nothing...
//                            Log.e(TAG, "buttonDestroy.setOnClickListener: editing NULL object?");
//                        }
//                    }
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    destroyEpic(moniker);
                }
                else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
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
