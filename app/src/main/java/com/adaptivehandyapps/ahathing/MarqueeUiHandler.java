/*
 * Project: AhaThing1
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker OCT 2017
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

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.adaptivehandyapps.ahathing.dao.DaoStarGate;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// MarqueeUiHandler: create, edit, destroy selected DAO
//      HAS A
//          MarqueeXfer - xfer to/from DAO and view
//          TagListAdapter - generic taglist adapter
public class MarqueeUiHandler {
    private static final String TAG = MarqueeUiHandler.class.getSimpleName();

    private View mRootView;

    private ContentFragment mParent;
    private MarqueeViewXfer mMarqueeViewXfer;

    private TagListAdapter mTagListAdapter = null;
    private List<String> mTagList;

    private DaoEpic mActiveEpic;
    private List<DaoStarGate> mStarGateList;

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
    public MarqueeUiHandler(ContentFragment parent, View v, final String op, final String objType, final String moniker) {

        mParent = parent;
        Log.d(TAG, "MarqueeUiHandler: mParent " + mParent);

        Log.d(TAG, "MarqueeUiHandler: op " + op + ", objtype" + objType + ", moniker " + moniker);
        mRootView = v;

        // create view xfer to transfer between view & objects
        mMarqueeViewXfer = new MarqueeViewXfer(mParent, mRootView);

        // update title: op + moniker
        TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_marquee_title);
        tvTitle.setText(op + ": " + moniker);
        // init object type specific fields
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_MARQUEE) && objType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            // dereference star list
            mStarGateList = (List<DaoStarGate>) (List<?>) mParent.getRepoProvider().getDalStarGate().getDaoRepo().getDaoList();
            // dereference epic
            mActiveEpic = (DaoEpic) mParent.getRepoProvider().getDalEpic().getDaoRepo().get(moniker);
            if (mStarGateList != null && mActiveEpic != null) {
                // establish button handlers
                handleUpdateButton(op, objType, moniker);
                handleCancelButton(op, objType, moniker);
                // xfer object to view
                mMarqueeViewXfer.fromEpic(mActiveEpic, mStarGateList);
            }
        }
        else {
            Log.e(TAG, "MarqueeUiHandler invalid incoming! op " + op + ", objtype" + objType + ", moniker " + moniker);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleUpdateButton(final String op, final String objType, final String moniker) {
        // establish create button visibility & click listener
        final Button buttonUpdate = (Button) mRootView.findViewById(R.id.button_marquee_update);
        buttonUpdate.setVisibility(View.VISIBLE);

        // button handlers
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonUpdate.setOnClickListener: ");
                mMarqueeViewXfer.toEpic(op, moniker);

                Log.d(TAG, "buttonCreate.setOnClickListener callback invoked...");
                if (mCallback != null) mCallback.onContentHandlerResult(op, objType, moniker);
            }
        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleCancelButton(final String op, final String objType, final String moniker) {
        // establish cancel button visibility & click listener
        final Button buttonCancel = (Button) mRootView.findViewById(R.id.button_marquee_cancel);
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
