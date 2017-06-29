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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mRepoProviderBound = false;
        }
    };

    public Boolean isRepoProviderBound() {
        return mRepoProviderBound;
    }
    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public PlayListService() {
        setContext(this);
    }
    public Boolean bindRepoProvider() {
        // bind to repo provider service
        Intent intentRepoProvider = new Intent(this, RepoProvider.class);
        this.bindService(intentRepoProvider, mRepoProviderConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreateView: mRepoProviderBound " + mRepoProviderBound + ", mRepoProvider " + mRepoProvider);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    private Context getContext() {
        return mContext;
    }
    public void setContext(Context context) {
        mContext = context;
    }

    public String hierarchyToString() {
        String hierarchyText = DaoDefs.INIT_STRING_MARKER;
        String moniker = DaoDefs.INIT_STRING_MARKER;
        if (isActiveTheatre()) moniker = mActiveTheatre.getMoniker();
        hierarchyText = moniker;
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (isActiveEpic()) moniker = mActiveEpic.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (isActiveStory()) moniker = mActiveStory.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (isActiveStage()) moniker = mActiveStage.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (isActiveActor()) moniker = mActiveActor.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (isActiveAction()) moniker = mActiveAction.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);
        moniker = DaoDefs.INIT_STRING_MARKER;
        if (isActiveOutcome()) moniker = mActiveOutcome.getMoniker();
        hierarchyText = hierarchyText.concat(", " + moniker);

        return hierarchyText;
    }
    ///////////////////////////////////////////////////////////////////////////
    // theatre
    public Boolean isActiveTheatre() {
        if (mActiveTheatre != null) return true;
        return false;
    }
    public DaoTheatre getActiveTheatre() { return mActiveTheatre; }
    public void setActiveTheatre(DaoTheatre activeDao) {
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // extract moniker
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY, moniker);
        mActiveTheatre = activeDao;
    }
    public Boolean updateActiveTheatre(DaoTheatre dao) {
        // if this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_THEATRE_KEY);
        if (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER)) {
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
            if (mRepoProviderBound && mRepoProvider.getDalTheatre().getDaoRepo().size() > 0) {
                daoReplacement = (DaoTheatre) mRepoProvider.getDalTheatre().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveTheatre(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // epic
    public Boolean isActiveEpic() {
        if (mActiveEpic != null) return true;
        return false;
    }
    public DaoEpic getActiveEpic() { return mActiveEpic; }
    public void setActiveEpic(DaoEpic activeDao) {
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // extract moniker
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_EPIC_KEY, moniker);
        mActiveEpic = activeDao;
    }
    public Boolean updateActiveEpic(DaoEpic dao) {
        // if this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_EPIC_KEY);
        if (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER)) {
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
            if (mRepoProviderBound && mRepoProvider.getDalEpic().getDaoRepo().size() > 0) {
                daoReplacement = (DaoEpic) mRepoProvider.getDalEpic().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveEpic(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // story
    public Boolean isActiveStory() {
        if (mActiveStory != null) return true;
        return false;
    }
    public DaoStory getActiveStory() { return mActiveStory; }
    public void setActiveStory(DaoStory activeDao) {
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // extract moniker
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STORY_KEY, moniker);
        mActiveStory = activeDao;
    }
    public Boolean updateActiveStory(DaoStory dao) {
        // if this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_STORY_KEY);
        if (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER)) {
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
            if (mRepoProviderBound && mRepoProvider.getDalStory().getDaoRepo().size() > 0) {
                daoReplacement = (DaoStory) mRepoProvider.getDalStory().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveStory(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // stage
    public Boolean isActiveStage() {
        if (mActiveStage != null) return true;
        return false;
    }
    public DaoStage getActiveStage() { return mActiveStage; }
    public void setActiveStage(DaoStage activeDao) {
        if (mRepoProviderBound && mRepoProvider != null) {
            String moniker = DaoDefs.INIT_STRING_MARKER;
            // if object defined
            if (activeDao != null) {
                // extract moniker
                moniker = activeDao.getMoniker();
            }
            // set prefs & active object
            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STAGE_KEY, moniker);
            mActiveStage = activeDao;

            // create new stage model, view, controller
            mRepoProvider.setStageModelRing(new StageModelRing(mRepoProvider.getPlayListService()));
            mRepoProvider.getStageModelRing().buildModel(activeDao);
            Log.d(TAG, "NEW StageModelRing for repo " + mRepoProvider.toString() + " at " + mRepoProvider.getStageModelRing().toString());
        }
        else {
            Log.e(TAG, "Oops! setActiveStage finds mRepoProviderBound " + mRepoProviderBound);
            if (mRepoProvider != null) Log.e(TAG, "Oops! setActiveStage finds mRepoProviderBound " + mRepoProviderBound + " for mRepoProvider " + mRepoProvider.toString());
        }

    }
    public Boolean updateActiveStage(DaoStage dao) {
        // if this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_STAGE_KEY);
        if (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER)) {
            // set active to updated object
            // setActiveStage creates stage model & triggers refresh - remote updates are displayed
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
            if (mRepoProviderBound && mRepoProvider.getDalStage().getDaoRepo().size() > 0) {
                daoReplacement = (DaoStage) mRepoProvider.getDalStage().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveStage(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // actor
    public Boolean isActiveActor() {
        if (mActiveActor != null) return true;
        return false;
    }
    public DaoActor getActiveActor() { return mActiveActor; }
    public void setActiveActor(DaoActor activeDao) {
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // extract moniker
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTOR_KEY, moniker);
        mActiveActor = activeDao;
    }
    public Boolean updateActiveActor(DaoActor dao) {
        // if this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_ACTOR_KEY);
        if (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER)) {
            // TODO: setActiveActor on update?
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
            if (mRepoProviderBound && mRepoProvider.getDalActor().getDaoRepo().size() > 0) {
                daoReplacement = (DaoActor) mRepoProvider.getDalActor().getDaoRepo().get(0);
            }
            // TODO: setActiveActor to an existing actor on remove?
            // set or clear active object
            setActiveActor(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // action
    public Boolean isActiveAction() {
        if (mActiveAction != null) return true;
        return false;
    }
    public DaoAction getActiveAction() { return mActiveAction; }
    public void setActiveAction(DaoAction activeDao) {
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // extract moniker
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTION_KEY, moniker);
        mActiveAction = activeDao;
    }
    public Boolean updateActiveAction(DaoAction dao) {
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_ACTION_KEY);
        if (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER)) {
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
            if (mRepoProviderBound && mRepoProvider.getDalAction().getDaoRepo().size() > 0) {
                daoReplacement = (DaoAction) mRepoProvider.getDalAction().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveAction(daoReplacement);
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // outcome
    public Boolean isActiveOutcome() {
        if (mActiveOutcome != null) return true;
        return false;
    }
    public DaoOutcome getActiveOutcome() { return mActiveOutcome; }
    public void setActiveOutcome(DaoOutcome activeDao) {
        String moniker = DaoDefs.INIT_STRING_MARKER;
        // if object defined
        if (activeDao != null) {
            // extract moniker
            moniker = activeDao.getMoniker();
        }
        // set prefs & active object
        PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_OUTCOME_KEY, moniker);
        mActiveOutcome = activeDao;
    }
    public Boolean updateActiveOutcome(DaoOutcome dao) {
        // if this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_OUTCOME_KEY);
        if (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER)) {
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
            if (mRepoProviderBound && mRepoProvider.getDalOutcome().getDaoRepo().size() > 0) {
                daoReplacement = (DaoOutcome) mRepoProvider.getDalOutcome().getDaoRepo().get(0);
            }
            // set or clear active object
            setActiveOutcome(daoReplacement);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // repair playlist - repair until not required or limit reach
    // TODO: refactor repairs to RepoProvider?
    public Boolean repairAll(Boolean removeIfUndefined, Boolean forceToActiveStage) {
        Boolean repairFlag = true;
        Integer repairCount = 0;
        Integer repairLimit = 64;
        // if repaired
        while (repairFlag && repairCount < repairLimit) {
            ++repairCount;
            repairFlag = repair(removeIfUndefined, forceToActiveStage);
        }
        if (repairCount > 1) {
            --repairCount;
            Log.e(TAG, "Repaired " + repairCount + " defects, complete (" + !repairFlag + ").");
            return true;
        }
        // no repair required
        return false;
    }

    // repair playlist - if repair required (first repair triggers return TRUE)
    private Boolean repair(Boolean removeIfUndefined, Boolean forceToActiveStage) {
        // if active theatre
        if (mRepoProviderBound && isActiveTheatre()) {
            // for each epic in theatre tag list
            for (String monikerEpic : getActiveTheatre().getTagList()) {
                Log.d(TAG, "testing for Undefined Epic " + monikerEpic + " in Theatre " + getActiveTheatre().getMoniker() + " (remove " + removeIfUndefined + ")");
                // if epic undefined
                if (mRepoProvider.getDalEpic().getDaoRepo().get(monikerEpic) == null) {
                    // undefined epic detected
                    Log.e(TAG, "Undefined Epic " + monikerEpic + " in Theatre " + getActiveTheatre().getMoniker() + " (remove " + removeIfUndefined + ")");
                    if (removeIfUndefined) {
                        getActiveTheatre().getTagList().remove(monikerEpic);
                        Log.e(TAG, "Undefined Epic " + monikerEpic + " in Theatre " + getActiveTheatre().getMoniker() + " removed...");
                        mRepoProvider.getDalTheatre().update(getActiveTheatre(),true);
                    }
                    return true;
                }
                else {
                    DaoEpic daoEpic = (DaoEpic)mRepoProvider.getDalEpic().getDaoRepo().get(monikerEpic);
                    // for each story in epic tag list
                    for (String monikerStory : daoEpic.getTagList()) {
                        // if story undefined
                        Log.d(TAG, "testing for Undefined Story " + monikerStory + " in Epic " + monikerEpic + " (remove " + removeIfUndefined + ")");
                        if (mRepoProvider.getDalStory().getDaoRepo().get(monikerStory) == null) {
                            // undefined Story detected
                            Log.e(TAG, "Undefined Story " + monikerStory + " in Epic " + monikerEpic + " (remove " + removeIfUndefined + ")");
                            if (removeIfUndefined) {
                                // remove story from epic tag list
                                daoEpic.getTagList().remove(monikerStory);
                                Log.e(TAG, "Undefined Story " + monikerStory + " in Epic " + monikerEpic + " removed...");
                                mRepoProvider.getDalEpic().update(daoEpic,true);
                            }
                            return true;
                        }
                        else {
                            DaoStory daoStory = (DaoStory)mRepoProvider.getDalStory().getDaoRepo().get(monikerStory);
                            String monikerStage = daoStory.getStage();
                            // if stage undefined
                            if (mRepoProvider.getDalStage().getDaoRepo().get(monikerStage) == null) {
                                // undefined Stage detected
                                Log.e(TAG, "Undefined Stage " + monikerStage + " in Story " + monikerStory + " (remove " + removeIfUndefined + ")");
                                if (removeIfUndefined) {
                                    // remove story from epic tag list
                                    daoEpic.getTagList().remove(monikerStory);
                                    Log.e(TAG, "Undefined Stage " + monikerStage + " in Story " + monikerStory + " (Story removed)...");
                                    mRepoProvider.getDalEpic().update(daoEpic,true);
                                }
                                return true;
                            }
                            else {
                                // stage defined - allow multiple stages?
                                // if flag TRUE to force story stage to active stage & story stage not active stage
                                if (forceToActiveStage && !monikerStage.equals(getActiveStage().getMoniker())) {
                                    ((DaoStory) mRepoProvider.getDalStory().getDaoRepo().get(monikerStory)).setStage(getActiveStage().getMoniker());
                                    Log.e(TAG, "Multiple Stages " + monikerStage + " in Story " + monikerStory + " (Stage reset to " + getActiveStage().getMoniker() + ") Story updated...");
                                    mRepoProvider.getDalStory().update(daoStory,true);
                                    return true;
                                }
                                else {
                                    // actor
                                    String monikerActor = daoStory.getActor();
                                    if (mRepoProvider.getDalActor().getDaoRepo().get(monikerActor) == null) {
                                        // undefined Actor detected
                                        Log.e(TAG, "Undefined Actor " + monikerActor + " in Story " + monikerStory + " (remove " + removeIfUndefined + ")");
                                        if (removeIfUndefined) {
                                            // remove story from epic tag list
                                            daoEpic.getTagList().remove(monikerStory);
                                            Log.e(TAG, "Undefined Actor " + monikerActor + " in Story " + monikerStory + " (Story removed)...");
                                            mRepoProvider.getDalEpic().update(daoEpic,true);
                                        }
                                        return true;
                                    }
                                    else {
                                        // action
                                        String monikerAction = daoStory.getAction();
                                        if (mRepoProvider.getDalAction().getDaoRepo().get(monikerAction) == null) {
                                            // undefined Actor detected
                                            Log.e(TAG, "Undefined Actor " + monikerAction + " in Story " + monikerStory + " (remove " + removeIfUndefined + ")");
                                            if (removeIfUndefined) {
                                                // remove story from epic tag list
                                                daoEpic.getTagList().remove(monikerStory);
                                                Log.e(TAG, "Undefined Action " + monikerAction + " in Story " + monikerStory + " (Story removed)...");
                                                mRepoProvider.getDalEpic().update(daoEpic,true);
                                            }
                                            return true;
                                        }
                                        else {
                                            // outcome
                                            String monikerOutcome = daoStory.getOutcome();
                                            if (mRepoProvider.getDalOutcome().getDaoRepo().get(monikerOutcome) == null) {
                                                // undefined Outcome detected
                                                Log.e(TAG, "Undefined Outcome " + monikerOutcome + " in Story " + monikerStory + " (remove " + removeIfUndefined + ")");
                                                if (removeIfUndefined) {
                                                    // remove story from epic tag list
                                                    daoEpic.getTagList().remove(monikerStory);
                                                    Log.e(TAG, "Undefined Outcome " + monikerOutcome + " in Story " + monikerStory + " (Story removed)...");
                                                    mRepoProvider.getDalEpic().update(daoEpic,true);
                                                }
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // no repair required
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        ///////////////////////////////////////////////////////////////////////////////////////////
        // Unbind from services
        if (mRepoProviderBound) {
            Log.d(TAG, "onDestroy unbinding RepoProvider service" + mRepoProviderConnection.toString());
            this.unbindService(mRepoProviderConnection);
            mRepoProviderBound = false;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
    }
}
