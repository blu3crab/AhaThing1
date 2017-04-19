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
//    private RepoProvider mRepoProvider;

    private Boolean mReady = false;

    private DaoTheatre mActiveTheatre = null;
    private DaoEpic mActiveEpic = null;
    private DaoStory mActiveStory = null;
    private DaoStage mActiveStage = null;
    private DaoActor mActiveActor = null;
    private DaoAction mActiveAction = null;
    private DaoOutcome mActiveOutcome = null;

    ///////////////////////////////////////////////////////////////////////////
    public PlayList() {
//    public PlayList(Context context) {
//
//        Context context = getContext();
//        setContext(context);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    private Context getContext() {
        return mContext;
    }
    public void setContext(Context context) {
        mContext = context;
    }

//    public RepoProvider getRepoProvider() {
//        return mRepoProvider;
//    }
//    public void setRepoProvider(RepoProvider repoProvider) {
//        this.mRepoProvider = repoProvider;
//    }
//
    public Boolean isReady() { return mReady;}
    public Boolean isReady(Boolean ready) { mReady = ready; return mReady;}

    ///////////////////////////////////////////////////////////////////////////
    // theatre
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
    public Boolean updateActiveTheatre(DaoTheatre dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), PrefsUtils.ACTIVE_THEATRE_KEY);
        if (MainActivity.getPlayListInstance().getActiveTheatre() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            MainActivity.getPlayListInstance().setActiveTheatre(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveTheatre(DaoTheatre dao) {
        // if dao is active object
        if (MainActivity.getPlayListInstance().getActiveTheatre().getMoniker().equals(dao.getMoniker())) {
            DaoTheatre daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().size() > 0) {
                daoReplacement = (DaoTheatre) MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().get(0);
            }
            // set or clear active object
            MainActivity.getPlayListInstance().setActiveTheatre(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // epic
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
    public Boolean updateActiveEpic(DaoEpic dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), PrefsUtils.ACTIVE_EPIC_KEY);
        if (MainActivity.getPlayListInstance().getActiveEpic() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            MainActivity.getPlayListInstance().setActiveEpic(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveEpic(DaoEpic dao) {
        // if dao is active object
        if (MainActivity.getPlayListInstance().getActiveEpic().getMoniker().equals(dao.getMoniker())) {
            DaoEpic daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().size() > 0) {
                daoReplacement = (DaoEpic) MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().get(0);
            }
            // set or clear active object
            MainActivity.getPlayListInstance().setActiveEpic(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // story
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
    public Boolean updateActiveStory(DaoStory dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), PrefsUtils.ACTIVE_STORY_KEY);
        if (MainActivity.getPlayListInstance().getActiveStory() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            MainActivity.getPlayListInstance().setActiveStory(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveStory(DaoStory dao) {
        // if dao is active object
        if (MainActivity.getPlayListInstance().getActiveStory().getMoniker().equals(dao.getMoniker())) {
            DaoStory daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().size() > 0) {
                daoReplacement = (DaoStory) MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().get(0);
            }
            // set or clear active object
            MainActivity.getPlayListInstance().setActiveStory(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // stage
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
    public Boolean updateActiveStage(DaoStage dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), PrefsUtils.ACTIVE_STAGE_KEY);
        if (MainActivity.getPlayListInstance().getActiveStage() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            MainActivity.getPlayListInstance().setActiveStage(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveStage(DaoStage dao) {
        // if dao is active object
        if (MainActivity.getPlayListInstance().getActiveStage().getMoniker().equals(dao.getMoniker())) {
            DaoStage daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().size() > 0) {
                daoReplacement = (DaoStage) MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().get(0);
            }
            // set or clear active object
            MainActivity.getPlayListInstance().setActiveStage(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // actor
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
    public Boolean updateActiveActor(DaoActor dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), PrefsUtils.ACTIVE_ACTOR_KEY);
        if (MainActivity.getPlayListInstance().getActiveActor() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            MainActivity.getPlayListInstance().setActiveActor(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveActor(DaoActor dao) {
        // if dao is active object
        if (MainActivity.getPlayListInstance().getActiveActor().getMoniker().equals(dao.getMoniker())) {
            DaoActor daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().size() > 0) {
                daoReplacement = (DaoActor) MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().get(0);
            }
            // set or clear active object
            MainActivity.getPlayListInstance().setActiveActor(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // action
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
    public Boolean updateActiveAction(DaoAction dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), PrefsUtils.ACTIVE_ACTION_KEY);
        if (MainActivity.getPlayListInstance().getActiveAction() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            MainActivity.getPlayListInstance().setActiveAction(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveAction(DaoAction dao) {
        // if dao is active object
        if (MainActivity.getPlayListInstance().getActiveAction().getMoniker().equals(dao.getMoniker())) {
            DaoAction daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().size() > 0) {
                daoReplacement = (DaoAction) MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().get(0);
            }
            // set or clear active object
            MainActivity.getPlayListInstance().setActiveAction(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // outcome
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
    public Boolean updateActiveOutcome(DaoOutcome dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), PrefsUtils.ACTIVE_OUTCOME_KEY);
        if (MainActivity.getPlayListInstance().getActiveOutcome() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            MainActivity.getPlayListInstance().setActiveOutcome(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveOutcome(DaoOutcome dao) {
        // if dao is active object
        if (MainActivity.getPlayListInstance().getActiveOutcome().getMoniker().equals(dao.getMoniker())) {
            DaoOutcome daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().size() > 0) {
                daoReplacement = (DaoOutcome) MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().get(0);
            }
            // set or clear active object
            MainActivity.getPlayListInstance().setActiveOutcome(daoReplacement);
            return true;
        }
        return false;
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
