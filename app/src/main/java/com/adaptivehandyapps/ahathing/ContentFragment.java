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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adaptivehandyapps.ahathing.dao.DaoDefs;

/**
 * Created by matuc on 12/22/2016.
 */

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

    /** Defines callbacks for service binding, passed to bindService() */
    public ServiceConnection mPlayListConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayListService.LocalBinder binder = (PlayListService.LocalBinder) service;
            mPlayListService = binder.getService();
            mPlayListBound = true;
            Log.d(TAG, "onServiceConnected: mPlayListBound " + mPlayListBound + ", mPlayListService " + mPlayListService);
            // if services bound refresh the view
            if (mPlayListBound && mRepoProviderBound) refresh();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPlayListBound = false;
        }
    };

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

    /** Defines callbacks for service binding, passed to bindService() */
    public ServiceConnection mRepoProviderConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RepoProvider.LocalBinder binder = (RepoProvider.LocalBinder) service;
            mRepoProvider = binder.getService();
            mRepoProviderBound = true;
            Log.d(TAG, "onServiceConnected: mRepoProviderBound " + mRepoProviderBound + ", mRepoProviderService " + mRepoProvider);
            // if services bound refresh the view
            if (mPlayListBound && mRepoProviderBound) refresh();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mRepoProviderBound = false;
        }
    };

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

        // bind to playlist service
        Intent intent = new Intent(getActivity(), PlayListService.class);
        getActivity().bindService(intent, mPlayListConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreateView: mPlayListBound " + mPlayListBound + ", mPlayListService " + mPlayListService);

        // bind to repo provider service
        Intent intentRepoProvider = new Intent(getActivity(), RepoProvider.class);
        getActivity().bindService(intentRepoProvider, mRepoProviderConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreateView: mRepoProviderBound " + mRepoProviderBound + ", mRepoProvider " + mRepoProvider);

        Log.v(TAG, "onCreate...");
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
            }
            else {
                mContentId = R.layout.content_daomaker;
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
        Log.v(TAG, "onCreate: Op = " + mContentOp + ", ObjType = " + mContentObjType + ", Moniker = " + mContentMoniker);
        mRootView = mInflater.inflate(mContentId, mContainer, false);
        return mRootView;
    }
    private View refresh() {
//        mRootView = mInflater.inflate(mContentId, mContainer, false);

        if (mContentId == R.layout.content_daomaker) {
            // create new handler & callback
            mDaoMakerUiHandler = new DaoMakerUiHandler(this, mRootView, mContentOp, mContentObjType, mContentMoniker);
            mDaoMakerUiHandler.setOnContentHandlerResultCallback(getOnContentHandlerResultCallback());
        }
        return mRootView;
    }

    ///////////////////////////////////////////////////////////////////////////
    private DaoMakerUiHandler.OnContentHandlerResult getOnContentHandlerResultCallback() {
        // instantiate callback
        DaoMakerUiHandler.OnContentHandlerResult callback = new DaoMakerUiHandler.OnContentHandlerResult() {

            @Override
            public void onContentHandlerResult(String op, String objType, String moniker) {
                Log.v(TAG, "onContentHandlerResult: Op = " + mContentOp + ", ObjType = " + mContentObjType + ", Moniker = " + mContentMoniker);
//                // update the main content by replacing fragments
//                Fragment fragment = new ContentFragment();
//
//                ContentFragment cf = (ContentFragment)fragment;
//                cf.setRepoProvider(mRepoProvider);

                // TODO: consolidate Play launch
                // update the main content with stage
                if (getPlayListService().getActiveStory() != null) {
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                    mContentMoniker = getPlayListService().getActiveStory().getMoniker();
                }
                else {
                    // TODO: determine next step based on just completed operation
                }

                if (getPlayListService() != null && getPlayListService().getActiveStory() != null) {
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                    mContentMoniker = getPlayListService().getActiveStory().getMoniker();
                }

                replaceFragment(getActivity(), mContentOp, mContentObjType, mContentMoniker);

//                Bundle args = new Bundle();
//                args.putString(ContentFragment.ARG_CONTENT_KEY_OP, mContentOp);
//                args.putString(ContentFragment.ARG_CONTENT_KEY_OBJTYPE, mContentObjType);
//                args.putString(ContentFragment.ARG_CONTENT_KEY_MONIKER, mContentMoniker);
//                fragment.setArguments(args);
//
//                FragmentManager fragmentManager = getFragmentManager();
////                fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
//                fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
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

