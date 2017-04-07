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
package com.adaptivehandyapps.ahathing.dal;

import android.content.Context;
import android.util.Log;

import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoAuditRepo;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//
// Created by mat on 1/20/2017.
//
///////////////////////////////////////////////////////////////////////////
// Repository provider
public class RepoProvider {
    private static final String TAG = "RepoProvider";

    private Context mContext;
    private OnRepoProviderRefresh mCallback = null; //call back interface

    private RepoProvider mRepoProvider;

    // data access layer
    private DalTheatre mDalTheatre;
    private DalEpic mDalEpic;
    private DalStory mDalStory;
    private DalStage mDalStage;
    private DalActor mDalActor;
    private DalAction mDalAction;
    private DalOutcome mDalOutcome;

    private DaoAuditRepo mDaoAuditRepo;
//    private DaoTheatreRepo mDaoTheatreRepo;
//    private DaoEpicRepo mDaoEpicRepo;
//    private DaoStoryRepo mDaoStoryRepo;
//    private DaoStageRepo mDaoStageRepo;
//    private DaoActorRepo mDaoActorRepo;
//    private DaoActionRepo mDaoActionRepo;
//    private DaoOutcomeRepo mDaoOutcomeRepo;

    // firebase
    private String mUserId = DaoDefs.INIT_STRING_MARKER;
    private Boolean mIsFirebaseReady = false;
    private DatabaseReference mDatabaseReference;
//    private DatabaseReference mTheatresReference;
//    private DatabaseReference mEpicsReference;
//    private DatabaseReference mStorysReference;
//    private DatabaseReference mStagesReference;
//    private DatabaseReference mActorsReference;
//    private DatabaseReference mActionsReference;
//    private DatabaseReference mOutcomesReference;
//
//    private ProviderListener mProviderListener;
//
//     story
//    private Boolean mTheatreReady = false;
//    private Boolean mEpicReady = false;
//    private Boolean mStoryReady = false;
//    private Boolean mStageReady = false;
//    private Boolean mActorReady = false;
//    private Boolean mActionReady = false;
//    private Boolean mOutcomeReady = false;

//    private DaoTheatre mActiveTheatre;
//    private DaoEpic mActiveEpic;
//    private DaoStory mActiveStory;
//    private DaoStage mActiveStage;
//    private DaoActor mActiveActor;
//    private DaoAction mActiveAction;
//    private DaoOutcome mActiveOutcome;

    // TODO: refactor to DalStage?
    private StageModelRing mStageModelRing;

    ///////////////////////////////////////////////////////////////////////////
    // callback interface when model changes should trigger refresh
    public interface OnRepoProviderRefresh {
        void onRepoProviderRefresh(Boolean refresh);
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public RepoProvider(Context context, OnRepoProviderRefresh callback) {
        // retain context & callback
        mContext = context;
        mCallback = callback;
        mRepoProvider = this;

        // instantiate DAL object
        mDalTheatre = new DalTheatre(mContext, mRepoProvider, mCallback);
        mDalEpic = new DalEpic(mContext, mRepoProvider, mCallback);
        mDalStory = new DalStory(mContext, mRepoProvider, mCallback);
        mDalStage = new DalStage(mContext, mRepoProvider, mCallback);
        mDalActor = new DalActor(mContext, mRepoProvider, mCallback);
        mDalAction = new DalAction(mContext, mRepoProvider, mCallback);
        mDalOutcome = new DalOutcome(mContext, mRepoProvider, mCallback);

        // establish firebase reference
        setFirebaseReference();

        if (!isFirebaseReady()) {
            Log.e(TAG, "Firebase NOT ready.");
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // get user id
            mUserId = getUid();

            // establish listeners
            mDalTheatre.setListener();
            mDalEpic.setListener();
            mDalStory.setListener();
            mDalStage.setListener();
            mDalActor.setListener();
            mDalAction.setListener();
            mDalOutcome.setListener();

//            mProviderListener = new ProviderListener(mContext, this);
//            mProviderListener.setListener();
//            mProviderListener.setEpicListener();
//            mProviderListener.setStoryListener();
//            mProviderListener.setStageListener();
//            mProviderListener.setActorListener();
//            mProviderListener.setActionListener();
//            mProviderListener.setOutcomeListener();
        }
        else {
            mIsFirebaseReady = false;
        }
        Log.d(TAG, "Firebase ready: " + isFirebaseReady() + ", UserId " + mUserId);

        // create Audit repo
        mDaoAuditRepo = new DaoAuditRepo(mContext);
//        // create theatre repo
//        mDaoTheatreRepo = new DaoTheatreRepo();
//        // create epic repo
//        mDaoEpicRepo = new DaoEpicRepo();
//        // create story repo
//        mDaoStoryRepo = new DaoStoryRepo();
//        // create stage repo
//        mDaoStageRepo = new DaoStageRepo();
//        // add new play
//        addNewStage(mDaoStoryRepo, mDaoStageRepo);
//        // create actor repo
//        mDaoActorRepo = new DaoActorRepo();
//        // create Action repo
//        mDaoActionRepo = new DaoActionRepo();
//        // create stage repo
//        mDaoOutcomeRepo = new DaoOutcomeRepo();

//      TRACE
//        if (getDalStory().getActiveDao() != null) {
//            Log.d(TAG, getDalStory().getActiveDao().toString());
////            if (mActiveStory != null) {
////                Log.d(TAG, mActiveStory.toString());
//            if (mActiveStage != null) {
//                Log.d(TAG, mActiveStage.toString());
//            }
//        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    public DalTheatre getDalTheatre() {
        return mDalTheatre;
    }
    public void setDalTheatre(DalTheatre dalTheatre) {
        this.mDalTheatre = dalTheatre;
    }

    public DalEpic getDalEpic() {
        return mDalEpic;
    }
    public void setDalEpic(DalEpic dalEpic) {
        this.mDalEpic = dalEpic;
    }

    public DalStory getDalStory() {
        return mDalStory;
    }
    public void setDalStory(DalStory dalStory) {
        this.mDalStory = dalStory;
    }

    public DalStage getDalStage() {
        return mDalStage;
    }
    public void setDalStage(DalStage dalStage) {
        this.mDalStage = dalStage;
    }

    public DalActor getDalActor() {
        return mDalActor;
    }
    public void setDalActor(DalActor dalActor) {
        this.mDalActor = dalActor;
    }

    public DalAction getDalAction() {
        return mDalAction;
    }
    public void setDalAction(DalAction dalAction) {
        this.mDalAction = dalAction;
    }

    public DalOutcome getDalOutcome() {
        return mDalOutcome;
    }
    public void setDalOutcome(DalOutcome dalOutcome) {
        this.mDalOutcome = dalOutcome;
    }

    //    public Boolean isReady() { return mTheatreReady;}
//    public Boolean isEpicReady() { return mEpicReady;}
//    public Boolean isStoryReady() { return mStoryReady;}
//    public Boolean isStageReady() { return mStageReady;}
//    public Boolean isActorReady() { return mActorReady;}
//    public Boolean isActionReady() { return mActionReady;}
//    public Boolean isOutcomeReady() { return mOutcomeReady;}
//
//    public ProviderListener getProviderListener() { return mProviderListener; }
    public DaoAuditRepo getDaoAuditRepo() { return mDaoAuditRepo; }

//    public DaoTheatreRepo getDaoRepo() { return mDaoTheatreRepo; }
//    private void setDaoTheatreRepo(DaoTheatreRepo daoTheatreRepo) {
//        this.mDaoTheatreRepo = daoTheatreRepo;
//    }
//
//    public DaoEpicRepo getDaoEpicRepo() { return mDaoEpicRepo; }
//    private void setDaoEpicRepo(DaoEpicRepo daoEpicRepo) {
//        this.mDaoEpicRepo = daoEpicRepo;
//    }
//
//    public DaoStoryRepo getDaoStoryRepo() { return mDaoStoryRepo; }
//    private void setDaoStoryRepo(DaoStoryRepo daoStoryRepo) {
//        this.mDaoStoryRepo = daoStoryRepo;
//    }
//
//    public DaoStageRepo getDaoStageRepo() { return mDaoStageRepo; }
//    private void setDaoStageRepo(DaoStageRepo daoStageRepo) {
//        this.mDaoStageRepo = daoStageRepo;
//    }
//
//    public DaoActorRepo getDaoActorRepo() { return mDaoActorRepo; }
//    private void setDaoActorRepo(DaoActorRepo daoActorRepo) {
//        this.mDaoActorRepo = daoActorRepo;
//    }
//
//    public DaoActionRepo getDaoActionRepo() { return mDaoActionRepo; }
//    private void setDaoActionRepo(DaoActionRepo daoActionRepo) {
//        this.mDaoActionRepo = daoActionRepo;
//    }
//
//    public DaoOutcomeRepo getDaoOutcomeRepo() { return mDaoOutcomeRepo; }
//    private void setDaoOutcomeRepo(DaoOutcomeRepo daoOutcomeRepo) {
//        this.mDaoOutcomeRepo = daoOutcomeRepo;
//    }

//    public DaoTheatre getActiveDao() { return mActiveTheatre; }
//    public void setActiveDao(DaoTheatre activeTheatre) {
//        mTheatreReady = false;
//        // if setting active object
//        if (activeTheatre != null) {
//            // set object ready & set prefs
//            mTheatreReady = true;
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY, activeTheatre.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY, DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveTheatre = activeTheatre;
//    }
//    public DaoEpic getActiveEpic() { return mActiveEpic; }
//    public void setActiveEpic(DaoEpic activeEpic) {
//        mEpicReady = false;
//        if (activeEpic != null) {
//            mEpicReady = true;
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_EPIC_KEY, activeEpic.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_EPIC_KEY, DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveEpic = activeEpic;
//    }
//
//    public DaoStory getActiveStory() { return mActiveStory; }
//    public void setActiveStory(DaoStory activeStory) {
//        mStoryReady = false;
//        if (activeStory != null) {
//            mStoryReady = true;
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STORY_KEY, activeStory.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STORY_KEY, DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveStory = activeStory;
//    }
//    public DaoStage getActiveStage() { return mActiveStage; }
//    public void setActiveStage(DaoStage activeStage) {
//        mStageReady = false;
//        if (activeStage != null) {
//            mStageReady = true;
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STAGE_KEY, activeStage.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_STAGE_KEY, DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveStage = activeStage;
//    }
//    public DaoActor getActiveActor() { return mActiveActor; }
//    public void setActiveActor(DaoActor activeActor) {
//        mActorReady = false;
//        if (activeActor != null) {
//            mActorReady = true;
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTOR_KEY, activeActor.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTOR_KEY, DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveActor = activeActor;
//    }
//    public DaoAction getActiveAction() { return mActiveAction; }
//    public void setActiveAction(DaoAction activeAction) {
//        mActionReady = false;
//        if (activeAction != null) {
//            mActionReady = true;
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTION_KEY, activeAction.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_ACTION_KEY, DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveAction = activeAction;
//    }
//    public DaoOutcome getActiveOutcome() { return mActiveOutcome; }
//    public void setActiveOutcome(DaoOutcome activeOutcome) {
//        mOutcomeReady = false;
//        if (activeOutcome != null) {
//            mOutcomeReady = true;
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_OUTCOME_KEY, activeOutcome.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, PrefsUtils.ACTIVE_OUTCOME_KEY, DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveOutcome = activeOutcome;
//    }
//
    // TODO: refactor to DalStage?
    public StageModelRing getStageModelRing() {
        return mStageModelRing;
    }
    public void setStageModelRing(StageModelRing stageModelRing) {
        this.mStageModelRing = stageModelRing;
    }

//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean update(DaoTheatre daoTheatre, Boolean updateDatabase) {
//        Log.d(TAG, "update(updateDatabase = " + updateDatabase + "): daoTheatre " + daoTheatre.toString());
//        // add or update repo with object
//        getDaoRepo().set(daoTheatre);
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_updateTheatre, R.string.action_set, daoTheatre.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_updateTheatre, R.string.action_child_setValue, daoTheatre.getMoniker());
//            // update timestamp
//            daoTheatre.setTimestamp((System.currentTimeMillis()));
//            // update db
//            mTheatresReference.child(daoTheatre.getMoniker()).setValue(daoTheatre);
//        }
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveTheatre = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_THEATRE_KEY);
//        if (getActiveDao() == null &&
//                (prefsActiveTheatre.equals(daoTheatre.getMoniker()) || prefsActiveTheatre.equals(DaoDefs.INIT_STRING_MARKER))) {
//            // set active to updated object
//            setActiveDao(daoTheatre);
//        }
//
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean remove(DaoTheatre daoTheatre, Boolean updateDatabase) {
//        Log.d(TAG, "remove(updateDatabase = " + updateDatabase + "): daoTheatre " + daoTheatre.toString());
//        // remove object from repo
//        getDaoRepo().remove(daoTheatre.getMoniker());
//        Log.d(TAG, "remove removed daoTheatre: " + daoTheatre.getMoniker());
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_removeTheatre, R.string.action_remove, daoTheatre.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_removeTheatre, R.string.action_child_removeValue, daoTheatre.getMoniker());
//            // remove object from remote db
//            mTheatresReference.child(daoTheatre.getMoniker()).removeValue();
//        }
//
//        // if removing active object
//        if (getActiveDao().getMoniker().equals(daoTheatre.getMoniker())) {
//            // if an object is defined
//            if (getDaoRepo().get(0) != null) {
//                // set active object
//                setActiveDao((DaoTheatre) getDaoRepo().get(0));
//            }
//            else {
//                // clear active object
//                setActiveDao(null);
//            }
//        }
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean updateEpic(DaoEpic daoEpic, Boolean updateDatabase) {
//        Log.d(TAG, "updateEpic(updateDatabase = " + updateDatabase + "): daoEpic " + daoEpic.toString());
//        // add or update repo with object
//        getDaoEpicRepo().set(daoEpic);
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_updateEpic, R.string.action_set, daoEpic.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_updateEpic, R.string.action_child_setValue, daoEpic.getMoniker());
//            // update timestamp
//            daoEpic.setTimestamp((System.currentTimeMillis()));
//            // update db
//            mEpicsReference.child(daoEpic.getMoniker()).setValue(daoEpic);
//        }
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveEpic = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_EPIC_KEY);
//        if (getActiveEpic() == null &&
//                (prefsActiveEpic.equals(daoEpic.getMoniker()) || prefsActiveEpic.equals(DaoDefs.INIT_STRING_MARKER))) {
//            // set active to updated object
//            setActiveEpic(daoEpic);
//        }
//
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean removeEpic(DaoEpic daoEpic, Boolean updateDatabase) {
//        Log.d(TAG, "removeEpic(updateDatabase = " + updateDatabase + "): daoEpic " + daoEpic.toString());
//        // remove object from repo
//        getDaoEpicRepo().remove(daoEpic.getMoniker());
//        Log.d(TAG, "removeEpic removed daoEpic: " + daoEpic.getMoniker());
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_removeEpic, R.string.action_remove, daoEpic.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_removeEpic, R.string.action_child_removeValue, daoEpic.getMoniker());
//            // remove object from remote db
//            mEpicsReference.child(daoEpic.getMoniker()).removeValue();
//        }
//
//        // if removing active object
//        if (getActiveEpic().getMoniker().equals(daoEpic.getMoniker())) {
//            // if an object is defined
//            if (getDaoEpicRepo().get(0) != null) {
//                // set active object
//                setActiveEpic((DaoEpic)getDaoEpicRepo().get(0));
//            }
//            else {
//                // clear active object
//                setActiveEpic(null);
//            }
//        }
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean updateStory(DaoStory daoStory, Boolean updateDatabase) {
//        Log.d(TAG, "updateStory(updateDatabase = " + updateDatabase + "): daoStory " + daoStory.toString());
//        // add or update repo with object
//        getDaoStoryRepo().set(daoStory);
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_updateStory, R.string.action_set, daoStory.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_updateStory, R.string.action_child_setValue, daoStory.getMoniker());
//            // update timestamp
//            daoStory.setTimestamp((System.currentTimeMillis()));
//            // update db
//            mStorysReference.child(daoStory.getMoniker()).setValue(daoStory);
//        }
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveStory = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_STORY_KEY);
//        if (getActiveStory() == null &&
//                (prefsActiveStory.equals(daoStory.getMoniker()) || prefsActiveStory.equals(DaoDefs.INIT_STRING_MARKER))) {
//            // set active to updated object
//            setActiveStory(daoStory);
//        }
//
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean removeStory(DaoStory daoStory, Boolean updateDatabase) {
//        Log.d(TAG, "removeStory(updateDatabase = " + updateDatabase + "): daoStory " + daoStory.toString());
//        // remove object from repo
//        getDaoStoryRepo().remove(daoStory.getMoniker());
//        Log.d(TAG, "removeStory removed daoStory: " + daoStory.getMoniker());
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_removeStory, R.string.action_remove, daoStory.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_removeStory, R.string.action_child_removeValue, daoStory.getMoniker());
//            // remove object from remote db
//            mStorysReference.child(daoStory.getMoniker()).removeValue();
//        }
//
//        // if removing active object
//        if (getActiveStory().getMoniker().equals(daoStory.getMoniker())) {
//            // if an object is defined
//            if (getDaoStoryRepo().get(0) != null) {
//                // set active object
//                setActiveStory((DaoStory)getDaoStoryRepo().get(0));
//            }
//            else {
//                // clear active object
//                setActiveStory(null);
//            }
//        }
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean updateStage(DaoStage daoStage, Boolean updateDatabase) {
//        Log.d(TAG, "updateStage(updateDatabase = " + updateDatabase + "): daoStage " + daoStage.toString());
//        // add or update repo with object
//        getDaoStageRepo().set(daoStage);
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_updateStage, R.string.action_set, daoStage.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_updateStage, R.string.action_child_setValue, daoStage.getMoniker());
//            // update timestamp
//            daoStage.setTimestamp((System.currentTimeMillis()));
//            // update db
//            mStagesReference.child(daoStage.getMoniker()).setValue(daoStage);
//        }
//        // TODO: single stage model - build stage model per stage
//        setStageModelRing(new StageModelRing(this));
//        Integer ringMax = 4;
//        mStageReady = getStageModelRing().buildModel(daoStage, ringMax);
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveStage = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_STAGE_KEY);
//        if (getActiveStage() == null &&
//                (prefsActiveStage.equals(daoStage.getMoniker()) || prefsActiveStage.equals(DaoDefs.INIT_STRING_MARKER))) {
//                // set active to updated object
//            setActiveStage(daoStage);
//        }
//
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean removeStage(DaoStage daoStage, Boolean updateDatabase) {
//        Log.d(TAG, "removeStage(updateDatabase = " + updateDatabase + "): daoStage " + daoStage.toString());
//        // remove object from repo
//        getDaoStageRepo().remove(daoStage.getMoniker());
//        Log.d(TAG, "removeStage removed daoStage: " + daoStage.getMoniker());
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_removeStage, R.string.action_remove, daoStage.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_removeStage, R.string.action_child_removeValue, daoStage.getMoniker());
//            // remove object from remote db
//            mStagesReference.child(daoStage.getMoniker()).removeValue();
//        }
//
//        // if removing active object
//        if (getActiveStage().getMoniker().equals(daoStage.getMoniker())) {
//            // if an object is defined
//            if (getDaoStageRepo().get(0) != null) {
//                // set active object
//                setActiveStage((DaoStage)getDaoStageRepo().get(0));
//            }
//            else {
//                // clear active object
//                setActiveStage(null);
//            }
//        }
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean updateActor(DaoActor daoActor, Boolean updateDatabase) {
//        Log.d(TAG, "updateActor(updateDatabase = " + updateDatabase + "): daoActor " + daoActor.toString());
//        // add or update repo with object
//        getDaoActorRepo().set(daoActor);
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_updateActor, R.string.action_set, daoActor.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_updateActor, R.string.action_child_setValue, daoActor.getMoniker());
//            // update timestamp
//            daoActor.setTimestamp((System.currentTimeMillis()));
//            // update db
//            mActorsReference.child(daoActor.getMoniker()).setValue(daoActor);
//        }
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveActor = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_ACTOR_KEY);
//        if (getActiveActor() == null &&
//                (prefsActiveActor.equals(daoActor.getMoniker()) || prefsActiveActor.equals(DaoDefs.INIT_STRING_MARKER))) {
//            // set active to updated object
//            setActiveActor(daoActor);
//        }
//
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean removeActor(DaoActor daoActor, Boolean updateDatabase) {
//        Log.d(TAG, "removeActor(updateDatabase = " + updateDatabase + "): daoActor " + daoActor.toString());
//        // remove object from repo
//        getDaoActorRepo().remove(daoActor.getMoniker());
//        Log.d(TAG, "removeActor removed daoActor: " + daoActor.getMoniker());
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_removeActor, R.string.action_remove, daoActor.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_removeActor, R.string.action_child_removeValue, daoActor.getMoniker());
//            // remove object from remote db
//            mActorsReference.child(daoActor.getMoniker()).removeValue();
//        }
//
//        // if removing active object
//        if (getActiveActor().getMoniker().equals(daoActor.getMoniker())) {
//            // if an object is defined
//            if (getDaoActorRepo().get(0) != null) {
//                // set active object
//                setActiveActor((DaoActor)getDaoActorRepo().get(0));
//            }
//            else {
//                // clear active object
//                setActiveActor(null);
//            }
//        }
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean updateAction(DaoAction daoAction, Boolean updateDatabase) {
//        Log.d(TAG, "updateAction(updateDatabase = " + updateDatabase + "): daoAction " + daoAction.toString());
//        // add or update repo with object
//        getDaoActionRepo().set(daoAction);
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_updateAction, R.string.action_set, daoAction.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_updateAction, R.string.action_child_setValue, daoAction.getMoniker());
//            // update timestamp
//            daoAction.setTimestamp((System.currentTimeMillis()));
//            // update db
//            mActionsReference.child(daoAction.getMoniker()).setValue(daoAction);
//        }
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveAction = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_ACTION_KEY);
//        if (getActiveAction() == null &&
//                (prefsActiveAction.equals(daoAction.getMoniker())|| prefsActiveAction.equals(DaoDefs.INIT_STRING_MARKER))) {
//            // set active to updated object
//            setActiveAction(daoAction);
//        }
//
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean removeAction(DaoAction daoAction, Boolean updateDatabase) {
//        Log.d(TAG, "removeAction(updateDatabase = " + updateDatabase + "): daoAction " + daoAction.toString());
//        // remove object from repo
//        getDaoActionRepo().remove(daoAction.getMoniker());
//        Log.d(TAG, "removeAction removed daoAction: " + daoAction.getMoniker());
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_removeAction, R.string.action_remove, daoAction.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_removeAction, R.string.action_child_removeValue, daoAction.getMoniker());
//            // remove object from remote db
//            mActionsReference.child(daoAction.getMoniker()).removeValue();
//        }
//
//        // if removing active object
//        if (getActiveAction().getMoniker().equals(daoAction.getMoniker())) {
//            // if an object is defined
//            if (getDaoActionRepo().get(0) != null) {
//                // set active object
//                setActiveAction((DaoAction)getDaoActionRepo().get(0));
//            }
//            else {
//                // clear active object
//                setActiveAction(null);
//            }
//        }
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean updateOutcome(DaoOutcome daoOutcome, Boolean updateDatabase) {
//        Log.d(TAG, "updateOutcome(updateDatabase = " + updateDatabase + "): daoOutcome " + daoOutcome.toString());
//        // add or update repo with object
//        getDaoOutcomeRepo().set(daoOutcome);
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_updateOutcome, R.string.action_set, daoOutcome.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_updateOutcome, R.string.action_child_setValue, daoOutcome.getMoniker());
//            // update timestamp
//            daoOutcome.setTimestamp((System.currentTimeMillis()));
//            // update db
//            mOutcomesReference.child(daoOutcome.getMoniker()).setValue(daoOutcome);
//        }
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveOutcome = PrefsUtils.getPrefs(mContext, PrefsUtils.ACTIVE_OUTCOME_KEY);
//        if (getActiveOutcome() == null &&
//                (prefsActiveOutcome.equals(daoOutcome.getMoniker()) || prefsActiveOutcome.equals(DaoDefs.INIT_STRING_MARKER))) {
//            // set active to updated object
//            setActiveOutcome(daoOutcome);
//        }
//
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean removeOutcome(DaoOutcome daoOutcome, Boolean updateDatabase) {
//        Log.d(TAG, "removeOutcome(updateDatabase = " + updateDatabase + "): daoOutcome " + daoOutcome.toString());
//        // remove object from repo
//        getDaoOutcomeRepo().remove(daoOutcome.getMoniker());
//        Log.d(TAG, "removeOutcome removed daoOutcome: " + daoOutcome.getMoniker());
//
//        // post audit trail
//        getDaoAuditRepo().postAudit(R.string.actor_removeOutcome, R.string.action_remove, daoOutcome.getMoniker());
//
//        if (updateDatabase) {
//            // post audit trail
//            getDaoAuditRepo().postAudit(R.string.actor_removeOutcome, R.string.action_child_removeValue, daoOutcome.getMoniker());
//            // remove object from remote db
//            mOutcomesReference.child(daoOutcome.getMoniker()).removeValue();
//        }
//
//        // if removing active object
//        if (getActiveOutcome().getMoniker().equals(daoOutcome.getMoniker())) {
//            // if an object is defined
//            if (getDaoOutcomeRepo().get(0) != null) {
//                // set active object
//                setActiveOutcome((DaoOutcome)getDaoOutcomeRepo().get(0));
//            }
//            else {
//                // clear active object
//                setActiveOutcome(null);
//            }
//        }
//        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
//
//        return true;
//    }
//
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // firebase helpers
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private Boolean setFirebaseReference() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (mDatabaseReference != null) {
            mIsFirebaseReady = true;
//            mTheatresReference = FirebaseDatabase.getInstance().getReference().child(DaoTheatreRepo.JSON_CONTAINER);
//            mEpicsReference = FirebaseDatabase.getInstance().getReference().child(DaoEpicRepo.JSON_CONTAINER);
//            mStorysReference = FirebaseDatabase.getInstance().getReference().child(DaoStoryRepo.JSON_CONTAINER);
//            mStagesReference = FirebaseDatabase.getInstance().getReference().child(DaoStageRepo.JSON_CONTAINER);
//            mActorsReference = FirebaseDatabase.getInstance().getReference().child(DaoActorRepo.JSON_CONTAINER);
//            mActionsReference = FirebaseDatabase.getInstance().getReference().child(DaoActionRepo.JSON_CONTAINER);
//            mOutcomesReference = FirebaseDatabase.getInstance().getReference().child(DaoOutcomeRepo.JSON_CONTAINER);
        }
        return mIsFirebaseReady;
    }
    public Boolean isFirebaseReady() {
        return mIsFirebaseReady;
    }
    public DatabaseReference getFirebaseReference() {
        return mDatabaseReference;
    }
//    public DatabaseReference getFirebaseReference() {
//        return mTheatresReference;
//    }
//    public DatabaseReference getEpicsReference() {
//        return mEpicsReference;
//    }
//    public DatabaseReference getStorysReference() {
//        return mStorysReference;
//    }
//    public DatabaseReference getStagesReference() {
//        return mStagesReference;
//    }
//    public DatabaseReference getActorsReference() {
//        return mActorsReference;
//    }
//    public DatabaseReference getActionsReference() {
//        return mActionsReference;
//    }
//    public DatabaseReference getOutcomesReference() {
//        return mOutcomesReference;
//    }
    public Boolean removeFirebaseListener() {
        // TODO: remove listeners
        return true;
    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean addNewStage(DaoStageRepo daoStageRepo) {
//
////        // create new play & set active
////        DaoStory activeStory = new DaoStory();
////        // moniker must be finalized prior to set (moniker list is not updated on object set
////        activeStory.setMoniker(DEFAULT_STORY_NICKNAME + daoStoryRepo.size());
////        updateStory(activeStory, true);
//
//
//        // TEST: create new stage & set active
//        DaoStage activeStage = new DaoStage();
//        // create model
//        setStageModelRing(new StageModelRing(this));
//        Integer ringMax = 4;
//        mStageReady = getStageModelRing().buildModel(activeStage, ringMax);
//        // set stage attributes
//        activeStage.setMoniker(DaoStage.STAGE_TYPE_RING + mRepoProvider.getDaoStageRepo().size());
//        activeStage.setStageType(DaoStage.STAGE_TYPE_RING);
//        // update repo with stage
//        updateStage(activeStage, true);
//
//
//        // TEST: create new stage & set active
//        activeStage = new DaoStage();
//
//        // create model
//        setStageModelRing(new StageModelRing(this));
//        ringMax = 4;
//        mStageReady = getStageModelRing().buildModel(activeStage, ringMax);
//        // set stage attributes
//        activeStage.setMoniker(DaoStage.STAGE_TYPE_RING + mRepoProvider.getDaoStageRepo().size());
//        activeStage.setStageType(DaoStage.STAGE_TYPE_RING);
//        // update repo with stage
//        updateStage(activeStage, true);
//
//        return true;
//    }
///////////////////////////////////////////////////////////////////////////
}
