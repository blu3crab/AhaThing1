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
// PlayListService: manages local active playlist
public class PlayListService extends Service {
    private static final String TAG = PlayListService.class.getSimpleName();

    private Context mContext;

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
            // onServiceConnected try to load prefs
            loadPrefs();
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

    ///////////////////////////////////////////////////////////////////////////
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
    public Boolean loadPrefs() {
        // set active object to prefs if possible
        if (!isActiveTheatre()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoTheatre dao = (DaoTheatre)getRepoProvider().getDalTheatre().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveTheatre(dao);
                }
            }
        }
        if (!isActiveTheatre()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoTheatre dao = (DaoTheatre)getRepoProvider().getDalTheatre().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveTheatre(dao);
                }
            }
        }
        if (!isActiveEpic()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_EPIC_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoEpic dao = (DaoEpic)getRepoProvider().getDalEpic().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveEpic(dao);
                }
            }
        }
        if (!isActiveStory()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_STORY_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoStory dao = (DaoStory)getRepoProvider().getDalStory().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveStory(dao);
                }
            }
        }
        if (!isActiveStage()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_STAGE_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoStage dao = (DaoStage)getRepoProvider().getDalStage().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveStage(dao);
                }
            }
        }
        if (!isActiveActor()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_ACTOR_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoActor dao = (DaoActor)getRepoProvider().getDalActor().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveActor(dao);
                }
            }
        }
        if (!isActiveAction()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_ACTION_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoAction dao = (DaoAction)getRepoProvider().getDalAction().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveAction(dao);
                }
            }
        }
        if (!isActiveOutcome()) {
            String moniker = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_OUTCOME_KEY);
            if (!moniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                DaoOutcome dao = (DaoOutcome)getRepoProvider().getDalOutcome().getDaoRepo().get(moniker);
                if (dao != null) {
                    setActiveOutcome(dao);
                }
            }
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // theatre
    public Boolean isActiveTheatre() {
        if (mActiveTheatre != null) return true;
        return false;
    }
    public DaoTheatre getActiveTheatre() { return mActiveTheatre; }
    public Boolean setActiveTheatre(DaoTheatre activeDao) {
        Boolean defined = false;
        // if object defined
        if (activeDao != null) {
            defined = true;
            // set prefs only if valid
            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY, activeDao.getMoniker());
            // if no active hierarchy object or active hierarchy object is defined but not included
            if (getActiveEpic() == null ||
                    (getActiveEpic() != null && !activeDao.getTagList().contains(getActiveEpic().getMoniker()))) {
                // if epic defined in theatre, set 1st active
                DaoEpic daoEpic = null;
                if (activeDao.getTagList().size() > 0) {
                    daoEpic = (DaoEpic) getRepoProvider().getDalEpic().getDaoRepo().get(0);
                }
                // set hierarchy
                setActiveEpic(daoEpic);
            }
        }
        // if object not defined
        if (activeDao == null) {
            // clear hierarchy
            setActiveEpic(null);
        }
        // set active object
        mActiveTheatre = activeDao;
        return defined;
    }
    public Boolean updateActiveTheatre(DaoTheatre dao) {
        // if this object matches prefs
        String prefsMoniker = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_THEATRE_KEY);
        if (prefsMoniker.equals(dao.getMoniker())) {
            // set active to updated object
            setActiveTheatre(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveTheatre(DaoTheatre dao) {
        // if dao is active object
        if (getActiveTheatre() != null && dao != null) {
            // active object must match incoming object or NOP
            if (getActiveTheatre().getMoniker().equals(dao.getMoniker())) {
                // clear active object
                setActiveTheatre(null);
                return true;
            }
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
    public Boolean setActiveEpic(DaoEpic activeDao) {
        Boolean defined = false;
        // if object defined
        if (activeDao != null) {
            defined = true;
            // set prefs
            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_EPIC_KEY, activeDao.getMoniker());
            // if no active hierarchy object or active hierarchy object is defined but not included
            if (getActiveStage() == null ||
                    (getActiveStage() != null && !activeDao.getStage().equals(getActiveStage().getMoniker()))) {
                // if stage defined in epic
                DaoStage daoStage = null;
                if (!activeDao.getStage().equals(DaoDefs.INIT_STRING_MARKER)) {
                    daoStage = (DaoStage) getRepoProvider().getDalStage().getDaoRepo().get(activeDao.getStage());
                }
                // set hierarchy
                setActiveStage(daoStage);
            }
            if (getActiveStory() == null ||
                    (getActiveStory() != null && !activeDao.getTagList().contains(getActiveStory().getMoniker()))) {
                // clear hierarchy
                setActiveStory(null);
            }
        }
        // if object not defined or active hierarchy object is defined but not included
        if (activeDao == null) {
            // clear hierarchy
            setActiveStage(null);
            setActiveStory(null);
        }
        // set or clear active object
        mActiveEpic = activeDao;
        return defined;
    }
    public Boolean updateActiveEpic(DaoEpic dao) {
        // if this object matches prefs
        String prefsMoniker = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_EPIC_KEY);
        if (dao != null && prefsMoniker.equals(dao.getMoniker())) {
            // set active to updated object
            setActiveEpic(dao);
            // if repo connected
            if (getRepoProvider() != null) {
                // set active stage
                DaoStage daoStage = (DaoStage) getRepoProvider().getDalStage().getDaoRepo().get(dao.getStage());
                setActiveStage(daoStage);
                // set active actor
                DaoActor daoActor = (DaoActor) getRepoProvider().getDalActor().getDaoRepo().get(dao.getActiveActor());
                setActiveActor(daoActor);
                // if epic stories defined & if no active story or active story not in epic
                if (dao.getTagList().size() > 0 &&
                        (getActiveStory() == null || !dao.getTagList().contains(getActiveStory().getMoniker()))) {
                    // set active story to 1st story in epic
                    DaoStory daoStory = (DaoStory) getRepoProvider().getDalStory().getDaoRepo().get(0);
                    setActiveStory(daoStory);
                }
            }
            return true;
        }
        return false;
    }
    public Boolean removeActiveEpic(DaoEpic dao) {
        // if dao is active object
        if (getActiveEpic() != null && dao != null) {
            // active object must match incoming object or NOP
            if (getActiveEpic().getMoniker().equals(dao.getMoniker())) {
                // set or clear active object
                setActiveEpic(null);
                return true;
            }
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
        // if object defined
        if (activeDao != null) {
            // set prefs & active object
            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STORY_KEY, activeDao.getMoniker());
        }
        else {
            // clearing active theatre, clear hierarchy
            setActiveActor(null);
            setActiveAction(null);
            setActiveOutcome(null);
        }
        // set or clear active object
        mActiveStory = activeDao;
        return;
    }
    public Boolean updateActiveStory(DaoStory dao) {
        // if this object matches prefs
        String prefsMoniker = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_STORY_KEY);
        if (prefsMoniker.equals(dao.getMoniker())) {
            // set active to updated object
            setActiveStory(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveStory(DaoStory dao) {
        // if dao is active object
        if (getActiveStory() != null && dao != null) {
            // active object must match incoming object or NOP
            if (getActiveStory().getMoniker().equals(dao.getMoniker())) {
                // set or clear active object
                setActiveStory(null);
                return true;
            }
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // stage
    public Boolean isActiveStage() {
        if (mActiveStage != null) return true;
        return false;
    }
    public Boolean isActiveStage(DaoStage daoStage) {
        if (mActiveStage != null && daoStage != null) {
            if (mActiveStage.getMoniker().equals(daoStage.getMoniker())) return true;
        }
        return false;
    }
    public DaoStage getActiveStage() { return mActiveStage; }
    public void setActiveStage(DaoStage activeDao) {
        if (mRepoProviderBound && mRepoProvider != null) {
            // if object defined & not current active stage
            if (activeDao != null) {
//                if (activeDao != null && !isActiveStage(activeDao)) {
                // extract moniker
                String moniker = activeDao.getMoniker();
                // set prefs & active object
                PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STAGE_KEY, moniker);
            }
            // set active object
            mActiveStage = activeDao;
        }
        else {
            Log.e(TAG, "Oops! setActiveStage finds mRepoProviderBound " + mRepoProviderBound);
            if (mRepoProvider != null) Log.e(TAG, "Oops! setActiveStage finds mRepoProviderBound " + mRepoProviderBound + " for mRepoProvider " + mRepoProvider.toString());
        }

    }
    public Boolean updateActiveStage(DaoStage dao) {
        // if this object matches prefs
        String prefsMoniker = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_STAGE_KEY);
        if (prefsMoniker.equals(dao.getMoniker())) {
            // setActiveStage creates stage model & triggers refresh - remote updates are displayed
            setActiveStage(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveStage(DaoStage dao) {
        // if dao is active object
        if (getActiveStage() != null && dao != null) {
            // active object must match incoming object or NOP
            if (getActiveStage().getMoniker().equals(dao.getMoniker())) {
                // set or clear active object
                setActiveStage(null);
                return true;
            }
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
        // if object defined
        if (activeDao != null) {
            // set prefs & active object
            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTOR_KEY, activeDao.getMoniker());
        }
        mActiveActor = activeDao;
    }
    public Boolean updateActiveActor(DaoActor dao) {
        // if this object matches prefs
        String prefsMoniker = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_ACTOR_KEY);
        if (prefsMoniker.equals(dao.getMoniker())) {
            // set active to updated object
            setActiveActor(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveActor(DaoActor dao) {
        // if dao is active object
        if (getActiveActor() != null && dao != null) {
            // active object must match incoming object or NOP
            if (getActiveActor().getMoniker().equals(dao.getMoniker())) {
                // set or clear active object
                setActiveActor(null);
                return true;
            }
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
        // if object defined
        if (activeDao != null) {
            // set prefs & active object
            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTION_KEY, activeDao.getMoniker());
        }
        mActiveAction = activeDao;
    }
    public Boolean updateActiveAction(DaoAction dao) {
        // if this object matches prefs
        String prefsMoniker = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_ACTION_KEY);
        if (prefsMoniker.equals(dao.getMoniker())) {
            // set active to updated object
            setActiveAction(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveAction(DaoAction dao) {
        // if dao is active object
        if (getActiveAction() != null && dao != null) {
            // active object must match incoming object or NOP
            if (getActiveAction().getMoniker().equals(dao.getMoniker())) {
                // set or clear active object
                setActiveAction(null);
                return true;
            }
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
        // if object defined
        if (activeDao != null) {
            // set prefs & active object
            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_OUTCOME_KEY, activeDao.getMoniker());
        }
        mActiveOutcome = activeDao;
    }
    public Boolean updateActiveOutcome(DaoOutcome dao) {
        // if this object matches prefs
        String prefsMoniker = PrefsUtils.getPrefs(getContext(), PrefsUtils.ACTIVE_OUTCOME_KEY);
        if (prefsMoniker.equals(dao.getMoniker())) {
            // set active to updated object
            setActiveOutcome(dao);
            return true;
        }
        return false;
    }
    public Boolean removeActiveOutcome(DaoOutcome dao) {
        // if dao is active object
        if (getActiveOutcome() != null && dao != null) {
            // active object must match incoming object or NOP
            if (getActiveOutcome().getMoniker().equals(dao.getMoniker())) {
                // set or clear active object
                setActiveOutcome(null);
                return true;
            }
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
                    // dereference epic
                    DaoEpic daoEpic = (DaoEpic)mRepoProvider.getDalEpic().getDaoRepo().get(monikerEpic);
                    if (daoEpic != null) {
                        // if stage undefined, alert
                        String monikerStage = daoEpic.getStage();
                        // if stage undefined
                        if (mRepoProvider.getDalStage().getDaoRepo().get(monikerStage) == null) {
                            // undefined Stage detected
                            Log.e(TAG, "Undefined Stage " + monikerStage + " in Epic " + monikerEpic + " (remove " + removeIfUndefined + ")");
                            if (removeIfUndefined) {
                                // update epic?
//                                daoEpic.getTagList().remove(monikerStory);
//                                Log.e(TAG, "Undefined Stage " + monikerStage + " in Story " + monikerStory + " (Story removed)...");
//                                mRepoProvider.getDalEpic().update(daoEpic, true);
                            }
                            return true;
                        }
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
                                    mRepoProvider.getDalEpic().update(daoEpic, true);
                                }
                                return true;
                            }
                            else {
                                DaoStory daoStory = (DaoStory) mRepoProvider.getDalStory().getDaoRepo().get(monikerStory);
                                // actor
                                String monikerActor = daoStory.getActor();
                                if (mRepoProvider.getDalActor().getDaoRepo().get(monikerActor) == null) {
                                    // undefined Actor detected
                                    Log.e(TAG, "Undefined Actor " + monikerActor + " in Story " + monikerStory + " (remove " + removeIfUndefined + ")");
                                    if (removeIfUndefined) {
                                        // remove story from epic tag list
                                        daoEpic.getTagList().remove(monikerStory);
                                        Log.e(TAG, "Undefined Actor " + monikerActor + " in Story " + monikerStory + " (Story removed)...");
                                        mRepoProvider.getDalEpic().update(daoEpic, true);
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
                                            mRepoProvider.getDalEpic().update(daoEpic, true);
                                        }
                                        return true;
                                    } else {
                                        // outcome
                                        String monikerOutcome = daoStory.getOutcome();
                                        if (mRepoProvider.getDalOutcome().getDaoRepo().get(monikerOutcome) == null) {
                                            // undefined Outcome detected
                                            Log.e(TAG, "Undefined Outcome " + monikerOutcome + " in Story " + monikerStory + " (remove " + removeIfUndefined + ")");
                                            if (removeIfUndefined) {
                                                // remove story from epic tag list
                                                daoEpic.getTagList().remove(monikerStory);
                                                Log.e(TAG, "Undefined Outcome " + monikerOutcome + " in Story " + monikerStory + " (Story removed)...");
                                                mRepoProvider.getDalEpic().update(daoEpic, true);
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
