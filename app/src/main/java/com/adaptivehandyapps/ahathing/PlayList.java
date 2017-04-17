package com.adaptivehandyapps.ahathing;
//
// Created by mat on 4/13/2017.
//

import android.content.Context;

import com.adaptivehandyapps.ahathing.dal.RepoProvider;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

///////////////////////////////////////////////////////////////////////////
public class PlayList {
    private static final String TAG = PlayList.class.getSimpleName();

    private Context mContext;
    private RepoProvider mRepoProvider;

    private Boolean mReady = false;

    private DaoTheatre mActiveTheatre = null;
    private DaoEpic mActiveEpic = null;
    private DaoStory mActiveStory = null;
    private DaoStage mActiveStage = null;
    private DaoActor mActiveActor = null;
    private DaoAction mActiveAction = null;
    private DaoOutcome mActiveOutcome = null;

    ///////////////////////////////////////////////////////////////////////////
    public PlayList(Context context) {
        setContext(context);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    private Context getContext() {
        return mContext;
    }
    private void setContext(Context context) {
        mContext = context;
    }

    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        this.mRepoProvider = repoProvider;
    }

    public Boolean isReady() { return mReady;}
    public Boolean isReady(Boolean ready) { mReady = ready; return mReady;}

    public DaoTheatre getActiveTheatre() { return mActiveTheatre; }
    public void setActiveTheatre(DaoTheatre activeDao) {
        isReady(false);
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // set object ready & extract moniker
            isReady(true);
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY, moniker);
        mActiveTheatre = activeDao;
    }

    public DaoEpic getActiveEpic() { return mActiveEpic; }
    public void setActiveEpic(DaoEpic activeDao) {
        isReady(false);
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // set object ready & extract moniker
            isReady(true);
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_EPIC_KEY, moniker);
        mActiveEpic = activeDao;
    }

    public DaoStory getActiveStory() { return mActiveStory; }
    public void setActiveStory(DaoStory activeDao) {
        isReady(false);
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // set object ready & extract moniker
            isReady(true);
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STORY_KEY, moniker);
        mActiveStory = activeDao;
    }

    public DaoStage getActiveStage() { return mActiveStage; }
    public void setActiveStage(DaoStage activeDao) {
        isReady(false);
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // set object ready & extract moniker
            isReady(true);
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STAGE_KEY, moniker);
        mActiveStage = activeDao;
    }

    public DaoActor getActiveActor() { return mActiveActor; }
    public void setActiveActor(DaoActor activeDao) {
        isReady(false);
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // set object ready & extract moniker
            isReady(true);
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTOR_KEY, moniker);
        mActiveActor = activeDao;
    }

    public DaoAction getActiveAction() { return mActiveAction; }
    public void setActiveAction(DaoAction activeDao) {
        isReady(false);
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // set object ready & extract moniker
            isReady(true);
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTION_KEY, moniker);
        mActiveAction = activeDao;
    }

    public DaoOutcome getActiveOutcome() { return mActiveOutcome; }
    public void setActiveOutcome(DaoOutcome activeDao) {
        isReady(false);
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // set object ready & extract moniker
            isReady(true);
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_OUTCOME_KEY, moniker);
        mActiveOutcome = activeDao;
    }
    ///////////////////////////////////////////////////////////////////////////
//    private Boolean setActiveHierarchy() {
//        DaoTheatre activeTheatre = null;
//        DaoEpic activeEpic = null;
//        DaoStory activeStory = null;
//        DaoStage activeStage = null;
//        DaoActor activeActor = null;
//        DaoAction activeAction = null;
//        DaoOutcome activeOutcome = null;
//
//        activeTheatre = getRepoProvider().getDalTheatre().getActiveTheatre();
//        if (activeTheatre != null) {
//            activeEpic = getRepoProvider().getDalEpic().getActiveTheatre();
//            // if active epic defined but not contained in theatre tag list
//            if (activeEpic != null &&
//                    !activeTheatre.getTagList().contains(activeEpic.getMoniker())) {
//                // if epic defined
//                if (activeTheatre.getTagList().size() > 0) {
//                    // assign 1st in list
//                    activeEpic = (DaoEpic)getRepoProvider().getDalEpic().getDaoRepo().get(activeTheatre.getTagList().get(0));
//                }
//            }
//        }
//        // set active objects based on above scan (null = none active)
//        getRepoProvider().getDalTheatre().setActiveDao(activeTheatre);
//        getRepoProvider().getDalEpic().setActiveDao(activeEpic);
//        getRepoProvider().getDalStory().setActiveDao(activeStory);
//        getRepoProvider().getDalStage().setActiveDao(activeStage);
//        getRepoProvider().getDalActor().setActiveDao(activeActor);
//        getRepoProvider().getDalAction().setActiveDao(activeAction);
//        getRepoProvider().getDalOutcome().setActiveDao(activeOutcome);
//        return true;
//    }

}
