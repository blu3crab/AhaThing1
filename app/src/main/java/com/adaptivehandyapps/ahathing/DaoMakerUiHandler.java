package com.adaptivehandyapps.ahathing;
//
// Created by mat on 1/6/2017.
//

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.TimeUtils;
import com.adaptivehandyapps.ahathing.dal.StoryProvider;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

public class DaoMakerUiHandler {
    private static final String TAG = "DaoMakerUiHandler";

    private View mRootView;
    private StoryProvider mStoryProvider;

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

        TextView tvThingLabel = (TextView) mRootView.findViewById(R.id.tv_thing_label);
        tvThingLabel.setText(op + ": " + moniker);

        EditText etThingName = (EditText) mRootView.findViewById(R.id.et_thing_name);
        etThingName.setText(moniker);
        // init object type specific fields
        if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
        }
        else if (objType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
        }

        // show creation date
        TextView tv_last_update = (TextView) mRootView.findViewById(R.id.tv_last_update);
        String date = TimeUtils.secsToDate(System.currentTimeMillis());
        tv_last_update.setText(date);

        // init create button
        final Button buttonCreate = (Button) mRootView.findViewById(R.id.button_new_create);
        buttonCreate.setVisibility(View.VISIBLE);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonCreate.setOnClickListener: ");
                EditText et = (EditText) mRootView.findViewById(R.id.et_thing_name);
                String thingName = et.getText().toString();

                Toast.makeText(mRootView.getContext(), "Creating thing " + thingName, Toast.LENGTH_SHORT).show();

                if (objType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                    // get theatre object, update name, update repo
                    DaoTheatre daoTheatre = mStoryProvider.getDaoTheatreList().getDao(moniker);
                    daoTheatre.setMoniker(thingName);
                    mStoryProvider.updateTheatreRepo(daoTheatre);
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

        // init destroy button
        final Button buttonDestroy = (Button) mRootView.findViewById(R.id.button_new_destroy);
        buttonCreate.setVisibility(View.VISIBLE);

        buttonDestroy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonDestroy.setOnClickListener: ");
                Toast.makeText(mRootView.getContext(), "Destroying thing...", Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);
            }
        });

    }
//    ///////////////////////////////////////////////////////////////////////////
//    public static String secsToDate(long timeMs) {
//        Date date = new Date(timeMs);
////        String pattern = "dd-MM-yyyy";
////        String pattern = "dd-MM-yyyy HH:mm:ss";
//        String pattern = "ddMMMyy HH:mm:ss z";
////        String pattern = PrefsUtils.getPrefsDateFormat(context);
//
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//        String dateText = sdf.format(date);
////        Log.v(TAG, "secsToDate date:" + dateText);
//        return dateText;
//    }
    ///////////////////////////////////////////////////////////////////////////
}
