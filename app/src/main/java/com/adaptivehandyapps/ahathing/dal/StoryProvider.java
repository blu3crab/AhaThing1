package com.adaptivehandyapps.ahathing.dal;

import android.content.Context;
import android.util.Log;

import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoStoryList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStageList;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.adaptivehandyapps.ahathing.dao.DaoTheatreList;

//
// Created by mat on 1/20/2017.
//
///////////////////////////////////////////////////////////////////////////
// Play model provider
public class StoryProvider {
    private static final String TAG = "StoryProvider";

    private static final String DEFAULT_THEATRE_NICKNAME = "TheatreThing";
    private static final String DEFAULT_STORY_NICKNAME = "StoryThing";
    private static final String DEFAULT_STAGE_NICKNAME = "StageThing";

    private Context mContext;
    private OnPlayProviderRefresh mDelegate = null; //call back interface

    private StoryProvider mStoryProvider;

    private Boolean mPlayReady = false;

    private DaoTheatreList mDaoTheatreList;
    private DaoStoryList mDaoStoryList;
    private DaoStageList mDaoStageList;

    private DaoTheatre mActiveTheatre;
    private DaoStory mActiveStory;
    private DaoStage mActiveStage;

    private StageModelRing mStageModelRing;

    ///////////////////////////////////////////////////////////////////////////
    // callback interface when model changes should trigger refresh
    public interface OnPlayProviderRefresh {
        void onPlayProviderRefresh(Boolean refresh);
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public StoryProvider(Context context, OnPlayProviderRefresh callback) {
        // retain context & callback
        mContext = context;
        mDelegate = callback;
        mStoryProvider = this;

        // create theatre list
        mDaoTheatreList = new DaoTheatreList();
        // add new theatre
        addNewTheatre(mDaoTheatreList);
        // create play list
        mDaoStoryList = new DaoStoryList();
        // add new play
        addNewStory(mDaoStoryList);

        if (mActiveStory != null) {
            Log.d(TAG, mActiveStory.toString());
            if (mActiveStage != null) {
                Log.d(TAG, mActiveStage.toString());
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    public Boolean isPlayReady() { return mPlayReady;}

    public DaoStoryList getDaoPlayList() { return mDaoStoryList; }
    public void setDaoPlayList(DaoStoryList daoStoryList) {
        this.mDaoStoryList = daoStoryList;
    }
    public DaoStageList getDaoStageList() { return mDaoStageList; }
    public void setDaoStageList(DaoStageList daoStageList) {
        this.mDaoStageList = daoStageList;
    }

    public DaoTheatre getActiveTheatre() { return mActiveTheatre; }
    public void setActiveTheatre(DaoTheatre activeTheatre) {
        this.mActiveTheatre = activeTheatre;
    }
    public DaoStory getActiveStory() { return mActiveStory; }
    public void setActiveStory(DaoStory activeStory) {
        this.mActiveStory = activeStory;
    }
    public DaoStage getActiveStage() { return mActiveStage; }
    public void setActiveStage(DaoStage activeStage) {
        this.mActiveStage = activeStage;
    }

    public StageModelRing getStageModelRing() {
        return mStageModelRing;
    }
    private void setStageModelRing(StageModelRing stageModelRing) {
        this.mStageModelRing = stageModelRing;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean addNewTheatre(DaoTheatreList daoTheatreList) {

        // create new play & set active
        DaoTheatre activeTheatre = new DaoTheatre();
        daoTheatreList.theatres.add(activeTheatre);
        setActiveTheatre(activeTheatre);
        activeTheatre.setMoniker(DEFAULT_THEATRE_NICKNAME + daoTheatreList.theatres.size());
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean addNewStory(DaoStoryList daoStoryList) {

        // create new play & set active
        DaoStory activeStory = new DaoStory();
        daoStoryList.stories.add(activeStory);
        setActiveStory(activeStory);
        activeStory.setMoniker(DEFAULT_STORY_NICKNAME + daoStoryList.stories.size());

        // create stage list, new stage & set active
        DaoStageList daoStageList = new DaoStageList();
        setDaoStageList(daoStageList);
        DaoStage activeStage = new DaoStage();
        daoStageList.stages.add(activeStage);
        setActiveStage(activeStage);

        // create model
        setStageModelRing(new StageModelRing(this));
        Integer ringMax = 4;
        mPlayReady = getStageModelRing().buildModel(ringMax);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
}
