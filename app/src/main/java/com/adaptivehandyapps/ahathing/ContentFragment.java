/*
 * Project: AhaThing1
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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adaptivehandyapps.ahathing.dao.DaoDefs;

///////////////////////////////////////////////////////////////////////////
// ContentFragment: manage the flow of fragments based on op, objtype, moniker
// - stage view presents ongoing epic
// - daoMaker creates, updates, destroys data object
public class ContentFragment extends Fragment {

    private static final String TAG = "ContentFragment";

    public static final String ARG_CONTENT_KEY_ID = "content_id";
    public static final String ARG_CONTENT_KEY_OP = "content_op";
    public static final String ARG_CONTENT_KEY_OBJTYPE = "content_objtype";
    public static final String ARG_CONTENT_KEY_MONIKER = "content_moniker";

    public static final String ARG_CONTENT_VALUE_OP_NADA = DaoDefs.INIT_STRING_MARKER;
    public static final String ARG_CONTENT_VALUE_OP_NEW = "new";
    public static final String ARG_CONTENT_VALUE_OP_EDIT = "edit";
    public static final String ARG_CONTENT_VALUE_OP_PLAY = "play";
    public static final String ARG_CONTENT_VALUE_OP_SHOWLIST = "showlist";
    public static final String ARG_CONTENT_VALUE_OP_STARGATE = "stargate";
    public static final String ARG_CONTENT_VALUE_OP_MARQUEE = "marquee";

    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private View mRootView;

    private int mContentId = DaoDefs.INIT_INTEGER_MARKER;
    private String mContentOp = DaoDefs.INIT_STRING_MARKER;
    private String mContentObjType = DaoDefs.INIT_STRING_MARKER;
    private String mContentMoniker = DaoDefs.INIT_STRING_MARKER;

    private DaoMakerUiHandler mDaoMakerUiHandler;

    ///////////////////////////////////////////////////////////////////////////
    // playlist service
    PlayListService mPlayListService;
    boolean mPlayListBound = false;
    public PlayListService getPlayListService() {
        return mPlayListService;
    }
    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
    }

    ///////////////////////////////////////////////////////////////////////////
    // repo provider service
    RepoProvider mRepoProvider;
    boolean mRepoProviderBound = false;
    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public ContentFragment() {}
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // de-reference parent activity
        MainActivity mParent = (MainActivity) getActivity();

        // attempt to access the PlayListService
        mPlayListService = mParent.getPlayListService();
        if (mPlayListService != null) {
            Log.d(TAG, "onCreateView: mPlayListService " + mPlayListService.toString() + " bound...");
            mPlayListBound = true;
        }
        else {
            Log.d(TAG, "onCreateView: mPlayListService " + " NOT bound...");
            mPlayListBound = false;
        }

        mRepoProvider = mParent.getRepoProvider();
        if (mRepoProvider != null) {
            Log.d(TAG, "onCreateView: mRepoProvider " + mRepoProvider.toString() + " bound...");
            mRepoProviderBound = true;
        }
        else {
            Log.d(TAG, "onCreateView: mRepoProvider " + " NOT bound...");
            mRepoProviderBound = false;
        }

        Log.v(TAG, "onCreateView...");
        Log.d(TAG, PrefsUtils.toString(mParent));
        mInflater = inflater;
        mContainer = container;

        // default layout should never be used.
        mContentId = R.layout.content_undefined;
        mContentOp = DaoDefs.INIT_STRING_MARKER;
        mContentObjType = DaoDefs.INIT_STRING_MARKER;
        mContentMoniker = DaoDefs.INIT_STRING_MARKER;

        if (getArguments().containsKey(ARG_CONTENT_KEY_OP)) {
            // set the content op
            mContentOp = getArguments().getString(ARG_CONTENT_KEY_OP);
            if (mContentOp.equals(ARG_CONTENT_VALUE_OP_PLAY)) {
                mContentId = R.layout.content_stage;
                mParent.setStageViewActive(true);
            }
            else if (mContentOp.equals(ARG_CONTENT_VALUE_OP_STARGATE)) {
                mContentId = R.layout.content_stargate;
                mParent.setStageViewActive(false);
            }
            else if (mContentOp.equals(ARG_CONTENT_VALUE_OP_MARQUEE)) {
                mContentId = R.layout.content_marquee;
                mParent.setStageViewActive(false);
            }
            else if (mContentOp.equals(ARG_CONTENT_VALUE_OP_NEW) ||
                     mContentOp.equals(ARG_CONTENT_VALUE_OP_EDIT) ||
                     mContentOp.equals(ARG_CONTENT_VALUE_OP_SHOWLIST)) {
                mContentId = R.layout.content_daomaker;
                mParent.setStageViewActive(false);
            }
            else {
                mContentId = R.layout.content_splash;
                mParent.setStageViewActive(false);
            }
        }
        if (getArguments().containsKey(ARG_CONTENT_KEY_OBJTYPE)) {
            // set the content moniker
            mContentObjType = getArguments().getString(ARG_CONTENT_KEY_OBJTYPE);
        }
        if (getArguments().containsKey(ARG_CONTENT_KEY_MONIKER)) {
            // set the content moniker
            mContentMoniker = getArguments().getString(ARG_CONTENT_KEY_MONIKER);
        }
        Log.v(TAG, "onCreateView: Op = " + mContentOp + ", ObjType = " + mContentObjType + ", Moniker = " + mContentMoniker);
        mRootView = mInflater.inflate(mContentId, mContainer, false);

//        mRootView = refresh();
        // if services bound refresh the view
        if (mPlayListBound && mRepoProviderBound) refresh();
        Log.v(TAG, "onCreateView: Refresh if " + mPlayListBound + " && " + mRepoProviderBound);

        return mRootView;
    }
    private View refresh() {
        // inflate to refresh
//        mRootView = mInflater.inflate(mContentId, mContainer, false);
//        Log.v(TAG, "refresh inflating " + mContentId + ", Moniker = " + mContentMoniker);
        if (mPlayListBound && mRepoProviderBound) {
            if (mContentId == R.layout.content_daomaker) {
                // create new handler & callback
                mDaoMakerUiHandler = new DaoMakerUiHandler(this, mRootView, mContentOp, mContentObjType, mContentMoniker);
                mDaoMakerUiHandler.setOnContentHandlerResultCallback(getOnContentHandlerResultCallback());
            }
        }
        Log.v(TAG, "refresh invalidating " + mContentId + ", Moniker = " + mContentMoniker);
        mRootView.invalidate();
        return mRootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

        ///////////////////////////////////////////////////////////////////////////
    private DaoMakerUiHandler.OnContentHandlerResult getOnContentHandlerResultCallback() {
        // instantiate callback
        DaoMakerUiHandler.OnContentHandlerResult callback = new DaoMakerUiHandler.OnContentHandlerResult() {

            @Override
            public void onContentHandlerResult(String op, String objType, String moniker) {
                Log.v(TAG, "onContentHandlerResult: Op = " + mContentOp + ", ObjType = " + mContentObjType + ", Moniker = " + mContentMoniker);
                // if story available, override DaoMaker settings
                if (getPlayListService() != null && getPlayListService().getActiveStory() != null) {
                    Log.d(TAG, "OnContentHandlerResult: Story ready!");
                    // DaoMakerUiHandler completes - launch PLAY story
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                    mContentMoniker = getPlayListService().getActiveStory().getMoniker();
                }
                else {
                    Log.d(TAG, "OnContentHandlerResult: Story NOT ready!");
                    // launch stargate
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_STARGATE;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
                }
                // replace fragment with PLAY story
                replaceFragment(getActivity(), mContentOp, mContentObjType, mContentMoniker);
            }
        };
        return callback;
    }
    ///////////////////////////////////////////////////////////////////////////
    // replace fragment
    public static Boolean replaceFragment(Activity activity, String op, String objType, String moniker) {

        Fragment fragment = new ContentFragment();

        ContentFragment cf = (ContentFragment)fragment;
//        cf.setRepoProvider(repoProvider);

        Bundle args = new Bundle();
        args.putString(ContentFragment.ARG_CONTENT_KEY_OP, op);
        args.putString(ContentFragment.ARG_CONTENT_KEY_OBJTYPE, objType);
        args.putString(ContentFragment.ARG_CONTENT_KEY_MONIKER, moniker);
        fragment.setArguments(args);

        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////

}

