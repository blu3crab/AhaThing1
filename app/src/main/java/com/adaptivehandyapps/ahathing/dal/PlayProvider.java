package com.adaptivehandyapps.ahathing.dal;

import android.content.Context;
import android.util.Log;

import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoPlay;
import com.adaptivehandyapps.ahathing.dao.DaoPlayList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStageList;

//
// Created by mat on 1/20/2017.
//
///////////////////////////////////////////////////////////////////////////
// Play model provider
public class PlayProvider {
    private static final String TAG = "PlayProvider";

    private static final String DEFAULT_PLAY_NICKNAME = "PlayThing";
    private static final String DEFAULT_STAGE_NICKNAME = "StageThing";

    private Context mContext;
    private OnPlayProviderRefresh mDelegate = null; //call back interface

    private PlayProvider mPlayProvider;

    private Boolean mPlayReady = false;

    private DaoPlayList mDaoPlayList;
    private DaoStageList mDaoStageList;

    private DaoPlay mActivePlay;
    private DaoStage mActiveStage;

    private StageModelRing mStageModelRing;

    ///////////////////////////////////////////////////////////////////////////
    // callback interface when model changes should trigger refresh
    public interface OnPlayProviderRefresh {
        void onPlayProviderRefresh(Boolean refresh);
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public PlayProvider(Context context, OnPlayProviderRefresh callback) {
        // retain context & callback
        mContext = context;
        mDelegate = callback;
        mPlayProvider = this;

        // create play list
        mDaoPlayList = new DaoPlayList();
        // add new play
        addNewPlay(mDaoPlayList);

        if (mActivePlay != null) {
            Log.d(TAG, mActivePlay.toString());
            if (mActiveStage != null) {
                Log.d(TAG, mActiveStage.toString());
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    public Boolean isPlayReady() { return mPlayReady;}

    public DaoPlayList getDaoPlayList() { return mDaoPlayList; }
    public void setDaoPlayList(DaoPlayList daoPlayList) {
        this.mDaoPlayList = daoPlayList;
    }
    public DaoStageList getDaoStageList() { return mDaoStageList; }
    public void setDaoStageList(DaoStageList daoStageList) {
        this.mDaoStageList = daoStageList;
    }

    public DaoPlay getActivePlay() { return mActivePlay; }
    public void setActivePlay(DaoPlay activePlay) {
        this.mActivePlay = activePlay;
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
    public Boolean addNewPlay(DaoPlayList daoPlayList) {

        // create new play & set active
        DaoPlay activePlay = new DaoPlay();
        daoPlayList.plays.add(activePlay);
        setActivePlay(activePlay);
        activePlay.setMoniker(DEFAULT_PLAY_NICKNAME + daoPlayList.plays.size());

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
