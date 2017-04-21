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
//
// Created by mat on 4/13/2017.
//

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

///////////////////////////////////////////////////////////////////////////
public class PlayListService extends Service {
    private static final String TAG = PlayListService.class.getSimpleName();

    private Context mContext;

    private Boolean mReady = false;

    private DaoTheatre mActiveTheatre = null;
    private DaoEpic mActiveEpic = null;
    private DaoStory mActiveStory = null;
    private DaoStage mActiveStage = null;
    private DaoActor mActiveActor = null;
    private DaoAction mActiveAction = null;
    private DaoOutcome mActiveOutcome = null;

    ///////////////////////////////////////////////////////////////////////////
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        PlayListService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayListService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public PlayListService() {
        setContext(this);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    private Context getContext() {
        return mContext;
    }
    public void setContext(Context context) {
        mContext = context;
    }

    public Boolean isReady() { return mReady;}
    public Boolean isReady(Boolean ready) { mReady = ready; return mReady;}

    public String hierarchyToString() {
        String hierarchyText = DaoDefs.INIT_STRING_MARKER;
        String moniker = DaoDefs.INIT_STRING_MARKER;
        if (mActiveTheatre != null) moniker = mActiveTheatre.getMoniker();
        hierarchyText = moniker;
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (mActiveEpic != null) moniker = mActiveEpic.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (mActiveStory != null) moniker = mActiveStory.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (mActiveStage != null) moniker = mActiveStage.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (mActiveActor != null) moniker = mActiveActor.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (mActiveAction != null) moniker = mActiveAction.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (mActiveOutcome != null) moniker = mActiveOutcome.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);

        return hierarchyText;
    }
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
        if (getActiveTheatre() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveTheatre(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveTheatre(DaoTheatre dao) {
        // if dao is active object
        if (getActiveTheatre().getMoniker().equals(dao.getMoniker())) {
            DaoTheatre daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().size() > 0) {
                daoReplacement = (DaoTheatre) MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveTheatre(daoReplacement);
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
        if (getActiveEpic() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveEpic(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveEpic(DaoEpic dao) {
        // if dao is active object
        if (getActiveEpic().getMoniker().equals(dao.getMoniker())) {
            DaoEpic daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().size() > 0) {
                daoReplacement = (DaoEpic) MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveEpic(daoReplacement);
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
        if (getActiveStory() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveStory(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveStory(DaoStory dao) {
        // if dao is active object
        if (getActiveStory().getMoniker().equals(dao.getMoniker())) {
            DaoStory daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().size() > 0) {
                daoReplacement = (DaoStory) MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveStory(daoReplacement);
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
        if (getActiveStage() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveStage(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveStage(DaoStage dao) {
        // if dao is active object
        if (getActiveStage().getMoniker().equals(dao.getMoniker())) {
            DaoStage daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().size() > 0) {
                daoReplacement = (DaoStage) MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveStage(daoReplacement);
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
        if (getActiveActor() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveActor(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveActor(DaoActor dao) {
        // if dao is active object
        if (getActiveActor().getMoniker().equals(dao.getMoniker())) {
            DaoActor daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().size() > 0) {
                daoReplacement = (DaoActor) MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveActor(daoReplacement);
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
        if (getActiveAction() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveAction(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveAction(DaoAction dao) {
        // if dao is active object
        if (getActiveAction().getMoniker().equals(dao.getMoniker())) {
            DaoAction daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().size() > 0) {
                daoReplacement = (DaoAction) MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveAction(daoReplacement);
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
        if (getActiveOutcome() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveOutcome(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveOutcome(DaoOutcome dao) {
        // if dao is active object
        if (getActiveOutcome().getMoniker().equals(dao.getMoniker())) {
            DaoOutcome daoReplacement = null;
            // if an object is defined, set as replacement
            if (MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().size() > 0) {
                daoReplacement = (DaoOutcome) MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveOutcome(daoReplacement);
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
