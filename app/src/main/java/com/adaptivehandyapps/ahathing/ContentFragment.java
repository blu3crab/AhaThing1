package com.adaptivehandyapps.ahathing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adaptivehandyapps.ahathing.dal.StoryProvider;

/**
 * Created by matuc on 12/22/2016.
 */

public class ContentFragment extends Fragment {

    private static final String TAG = "ContentFragment";

    public static final String ARG_CONTENT_ID = "content_id";

    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    private View mRootView;
    private int mContentId = -1;
    private NewUiHandler mNewUiHandler;

    private StoryProvider mStoryProvider;

    ///////////////////////////////////////////////////////////////////////////
    public ContentFragment() {}
    ///////////////////////////////////////////////////////////////////////////
    public StoryProvider getPlayProvider() { return mStoryProvider;}
    public Boolean setPlayProvider(StoryProvider storyProvider) { mStoryProvider = storyProvider; return true;}
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.v(TAG, "onCreate...");
        mInflater = inflater;
        mContainer = container;

        // default layout should never be used.
        mContentId = R.layout.content_undefined;

        if (getArguments().containsKey(ARG_CONTENT_ID)) {
            // set the content id
            mContentId = getArguments().getInt(ARG_CONTENT_ID);
            Log.v(TAG, "onCreate mContentId = " + ((Integer) mContentId).toString());
        }

        return refresh();
    }
    private View refresh() {
        mRootView = mInflater.inflate(mContentId, mContainer, false);

        if (mContentId == R.layout.content_new) {
            // create new handler & callback
            mNewUiHandler = new NewUiHandler(mRootView);
            mNewUiHandler.setOnContentHandlerResultCallback(getOnContentHandlerResultCallback());
        }
        return mRootView;
    }

    ///////////////////////////////////////////////////////////////////////////
    private NewUiHandler.OnContentHandlerResult getOnContentHandlerResultCallback() {
        // instantiate callback
        NewUiHandler.OnContentHandlerResult callback = new NewUiHandler.OnContentHandlerResult() {

            @Override
            public void onContentHandlerResult(int contentId) {
                Log.d(TAG, "OnContentHandlerResult callback initiated for " + contentId);
                // update the main content by replacing fragments
                Fragment fragment = new ContentFragment();

                ContentFragment cf = (ContentFragment)fragment;
                cf.setPlayProvider(mStoryProvider);

                Bundle args = new Bundle();
//                int content_id = R.layout.content_new;
                args.putInt(ContentFragment.ARG_CONTENT_ID, contentId);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
                fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
                mContentId = contentId;
            }
        };
        return callback;
    }
    ///////////////////////////////////////////////////////////////////////////

}

