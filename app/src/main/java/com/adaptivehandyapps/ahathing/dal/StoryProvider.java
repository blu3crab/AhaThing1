package com.adaptivehandyapps.ahathing.dal;

import android.content.Context;
import android.util.Log;

import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActionRepo;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoActorRepo;
import com.adaptivehandyapps.ahathing.dao.DaoAuditRepo;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicRepo;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoOutcomeRepo;
import com.adaptivehandyapps.ahathing.dao.DaoStageRepo;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoStoryRepo;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.adaptivehandyapps.ahathing.dao.DaoTheatreRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//
// Created by mat on 1/20/2017.
//
///////////////////////////////////////////////////////////////////////////
// Play model provider
public class StoryProvider {
    private static final String TAG = "StoryProvider";

    private static final String DEFAULT_THEATRE_NICKNAME = "TheatreThing";
    private static final String DEFAULT_EPIC_NICKNAME = "EpicThing";
    private static final String DEFAULT_STORY_NICKNAME = "StoryThing";
    private static final String DEFAULT_STAGE_NICKNAME = "StageThing";

    private Context mContext;
    private OnStoryProviderRefresh mCallback = null; //call back interface

    private StoryProvider mStoryProvider;

    private Boolean mTheatreReady = false;
    private Boolean mEpicReady = false;
    private Boolean mStoryReady = false;
    private Boolean mStageReady = false;
    private Boolean mActorReady = false;
    private Boolean mActionReady = false;
    private Boolean mOutcomeReady = false;

    private DaoAuditRepo mDaoAuditRepo;
    private DaoTheatreRepo mDaoTheatreRepo;
    private DaoEpicRepo mDaoEpicRepo;
    private DaoStoryRepo mDaoStoryRepo;
    private DaoStageRepo mDaoStageRepo;
    private DaoActorRepo mDaoActorRepo;
    private DaoActionRepo mDaoActionRepo;
    private DaoOutcomeRepo mDaoOutcomeRepo;

    private DaoTheatre mActiveTheatre;
    private DaoEpic mActiveEpic;
    private DaoStory mActiveStory;
    private DaoStage mActiveStage;
    private DaoActor mActiveActor;
    private DaoAction mActiveAction;
    private DaoOutcome mActiveOutcome;

    private StageModelRing mStageModelRing;

    // firebase
    private String mUserId = DaoDefs.INIT_STRING_MARKER;
    private Boolean mIsFirebaseReady = false;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mTheatresReference;
    private DatabaseReference mEpicsReference;
    private DatabaseReference mStorysReference;
    private DatabaseReference mStagesReference;
    private DatabaseReference mActorsReference;
    private DatabaseReference mActionsReference;
    private DatabaseReference mOutcomesReference;

    private ProviderListener mProviderListener;

    ///////////////////////////////////////////////////////////////////////////
    // callback interface when model changes should trigger refresh
    public interface OnStoryProviderRefresh {
        void onPlayProviderRefresh(Boolean refresh);
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public StoryProvider(Context context, OnStoryProviderRefresh callback) {
        // retain context & callback
        mContext = context;
        mCallback = callback;
        mStoryProvider = this;

        // establish firebase reference
        setFirebaseReference();

        if (!isFirebaseReady()) {
            Log.e(TAG, "Firebase NOT ready.");
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // get user id
            mUserId = getUid();
            // establish listeners
            mProviderListener = new ProviderListener(mContext, this);
            mProviderListener.setTheatreListener();
            mProviderListener.setEpicListener();
            mProviderListener.setStoryListener();
            mProviderListener.setStageListener();
            mProviderListener.setActorListener();
            mProviderListener.setActionListener();
            mProviderListener.setOutcomeListener();
        }
        else {
            mIsFirebaseReady = false;
        }
        Log.d(TAG, "Firebase ready: " + isFirebaseReady() + ", UserId " + mUserId);

        // create Audit repo
        mDaoAuditRepo = new DaoAuditRepo(mContext);
        // create theatre repo
        mDaoTheatreRepo = new DaoTheatreRepo();
        // create epic repo
        mDaoEpicRepo = new DaoEpicRepo();
        // create story repo
        mDaoStoryRepo = new DaoStoryRepo();
        // create stage repo
        mDaoStageRepo = new DaoStageRepo();
//        // add new play
//        addNewStage(mDaoStoryRepo, mDaoStageRepo);
        // create actor repo
        mDaoActorRepo = new DaoActorRepo();
        // create Action repo
        mDaoActionRepo = new DaoActionRepo();
        // create stage repo
        mDaoOutcomeRepo = new DaoOutcomeRepo();


        if (mActiveStory != null) {
            Log.d(TAG, mActiveStory.toString());
            if (mActiveStage != null) {
                Log.d(TAG, mActiveStage.toString());
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    public Boolean isTheatreReady() { return mTheatreReady;}
    public Boolean isEpicReady() { return mEpicReady;}
    public Boolean isStoryReady() { return mStoryReady;}
    public Boolean isStageReady() { return mStageReady;}
    public Boolean isActorReady() { return mActorReady;}
    public Boolean isActionReady() { return mActionReady;}
    public Boolean isOutcomeReady() { return mOutcomeReady;}

    public ProviderListener getProviderListener() { return mProviderListener; }
    public DaoAuditRepo getDaoAuditRepo() { return mDaoAuditRepo; }

    public DaoTheatreRepo getDaoTheatreRepo() { return mDaoTheatreRepo; }
    private void setDaoTheatreRepo(DaoTheatreRepo daoTheatreRepo) {
        this.mDaoTheatreRepo = daoTheatreRepo;
    }

    public DaoEpicRepo getDaoEpicRepo() { return mDaoEpicRepo; }
    private void setDaoEpicRepo(DaoEpicRepo daoEpicRepo) {
        this.mDaoEpicRepo = daoEpicRepo;
    }

    public DaoStoryRepo getDaoStoryRepo() { return mDaoStoryRepo; }
    private void setDaoStoryRepo(DaoStoryRepo daoStoryRepo) {
        this.mDaoStoryRepo = daoStoryRepo;
    }

    public DaoStageRepo getDaoStageRepo() { return mDaoStageRepo; }
    private void setDaoStageRepo(DaoStageRepo daoStageRepo) {
        this.mDaoStageRepo = daoStageRepo;
    }

    public DaoActorRepo getDaoActorRepo() { return mDaoActorRepo; }
    private void setDaoActorRepo(DaoActorRepo daoActorRepo) {
        this.mDaoActorRepo = daoActorRepo;
    }

    public DaoActionRepo getDaoActionRepo() { return mDaoActionRepo; }
    private void setDaoActionRepo(DaoActionRepo daoActionRepo) {
        this.mDaoActionRepo = daoActionRepo;
    }

    public DaoOutcomeRepo getDaoOutcomeRepo() { return mDaoOutcomeRepo; }
    private void setDaoOutcomeRepo(DaoOutcomeRepo daoOutcomeRepo) {
        this.mDaoOutcomeRepo = daoOutcomeRepo;
    }

    public DaoTheatre getActiveTheatre() { return mActiveTheatre; }
    public void setActiveTheatre(DaoTheatre activeTheatre) {
        mTheatreReady = false;
        if (activeTheatre != null) mTheatreReady = true;
        this.mActiveTheatre = activeTheatre;
    }
    public DaoEpic getActiveEpic() { return mActiveEpic; }
    public void setActiveEpic(DaoEpic activeEpic) {
        mEpicReady = false;
        if (activeEpic != null) mEpicReady = true;
        this.mActiveEpic = activeEpic;
    }

    public DaoStory getActiveStory() { return mActiveStory; }
    public void setActiveStory(DaoStory activeStory) {
        mStoryReady = false;
        if (activeStory != null) mStoryReady = true;
        this.mActiveStory = activeStory;
    }
    public DaoStage getActiveStage() { return mActiveStage; }
    public void setActiveStage(DaoStage activeStage) {
        mStageReady = false;
        if (activeStage != null) mStageReady = true;
        this.mActiveStage = activeStage;
    }
    public DaoActor getActiveActor() { return mActiveActor; }
    public void setActiveActor(DaoActor activeActor) {
        mActorReady = false;
        if (activeActor != null) mActorReady = true;
        this.mActiveActor = activeActor;
    }
    public DaoAction getActiveAction() { return mActiveAction; }
    public void setActiveAction(DaoAction activeAction) {
        mActionReady = false;
        if (activeAction != null) mActionReady = true;
        this.mActiveAction = activeAction;
    }
    public DaoOutcome getActiveOutcome() { return mActiveOutcome; }
    public void setActiveOutcome(DaoOutcome activeOutcome) {
        mOutcomeReady = false;
        if (activeOutcome != null) mOutcomeReady = true;
        this.mActiveOutcome = activeOutcome;
    }

    public StageModelRing getStageModelRing() {
        return mStageModelRing;
    }
    private void setStageModelRing(StageModelRing stageModelRing) {
        this.mStageModelRing = stageModelRing;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateTheatre(DaoTheatre daoTheatre, Boolean updateDatabase) {
        Log.d(TAG, "updateTheatre(updateDatabase = " + updateDatabase + "): daoTheatre " + daoTheatre.toString());
        // add or update repo with object
        getDaoTheatreRepo().set(daoTheatre);

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_updateTheatre, R.string.action_set, daoTheatre.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_updateTheatre, R.string.action_child_setValue, daoTheatre.getMoniker());
            // update timestamp
            daoTheatre.setTimestamp((System.currentTimeMillis()));
            // update db
            mTheatresReference.child(daoTheatre.getMoniker()).setValue(daoTheatre);
        }
        // set active to updated object
        setActiveTheatre(daoTheatre);

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeTheatre(DaoTheatre daoTheatre, Boolean updateDatabase) {
        Log.d(TAG, "removeTheatre(updateDatabase = " + updateDatabase + "): daoTheatre " + daoTheatre.toString());
        // remove object from repo
        getDaoTheatreRepo().remove(daoTheatre.getMoniker());
        Log.d(TAG, "removeTheatre removed daoTheatre: " + daoTheatre.getMoniker());

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_removeTheatre, R.string.action_remove, daoTheatre.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_removeTheatre, R.string.action_child_removeValue, daoTheatre.getMoniker());
            // remove object from remote db
            mTheatresReference.child(daoTheatre.getMoniker()).removeValue();
        }

        // if removing active object
        if (getActiveTheatre().getMoniker().equals(daoTheatre.getMoniker())) {
            // if an object is defined
            if (getDaoTheatreRepo().get(0) != null) {
                // set active object
                setActiveTheatre((DaoTheatre) getDaoTheatreRepo().get(0));
            }
            else {
                // clear active object
                setActiveTheatre(null);
            }
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateEpic(DaoEpic daoEpic, Boolean updateDatabase) {
        Log.d(TAG, "updateEpic(updateDatabase = " + updateDatabase + "): daoEpic " + daoEpic.toString());
        // add or update repo with object
        getDaoEpicRepo().set(daoEpic);

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_updateEpic, R.string.action_set, daoEpic.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_updateEpic, R.string.action_child_setValue, daoEpic.getMoniker());
            // update timestamp
            daoEpic.setTimestamp((System.currentTimeMillis()));
            // update db
            mEpicsReference.child(daoEpic.getMoniker()).setValue(daoEpic);
        }
        // set active to updated object
        setActiveEpic(daoEpic);

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeEpic(DaoEpic daoEpic, Boolean updateDatabase) {
        Log.d(TAG, "removeEpic(updateDatabase = " + updateDatabase + "): daoEpic " + daoEpic.toString());
        // remove object from repo
        getDaoEpicRepo().remove(daoEpic.getMoniker());
        Log.d(TAG, "removeEpic removed daoEpic: " + daoEpic.getMoniker());

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_removeEpic, R.string.action_remove, daoEpic.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_removeEpic, R.string.action_child_removeValue, daoEpic.getMoniker());
            // remove object from remote db
            mEpicsReference.child(daoEpic.getMoniker()).removeValue();
        }

        // if removing active object
        if (getActiveEpic().getMoniker().equals(daoEpic.getMoniker())) {
            // if an object is defined
            if (getDaoEpicRepo().get(0) != null) {
                // set active object
                setActiveEpic((DaoEpic)getDaoEpicRepo().get(0));
            }
            else {
                // clear active object
                setActiveEpic(null);
            }
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateStory(DaoStory daoStory, Boolean updateDatabase) {
        Log.d(TAG, "updateStory(updateDatabase = " + updateDatabase + "): daoStory " + daoStory.toString());
        // add or update repo with object
        getDaoStoryRepo().set(daoStory);

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_updateStory, R.string.action_set, daoStory.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_updateStory, R.string.action_child_setValue, daoStory.getMoniker());
            // update timestamp
            daoStory.setTimestamp((System.currentTimeMillis()));
            // update db
            mStorysReference.child(daoStory.getMoniker()).setValue(daoStory);
        }
        // set active to updated object
        setActiveStory(daoStory);

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeStory(DaoStory daoStory, Boolean updateDatabase) {
        Log.d(TAG, "removeStory(updateDatabase = " + updateDatabase + "): daoStory " + daoStory.toString());
        // remove object from repo
        getDaoStoryRepo().remove(daoStory.getMoniker());
        Log.d(TAG, "removeStory removed daoStory: " + daoStory.getMoniker());

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_removeStory, R.string.action_remove, daoStory.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_removeStory, R.string.action_child_removeValue, daoStory.getMoniker());
            // remove object from remote db
            mStorysReference.child(daoStory.getMoniker()).removeValue();
        }

        // if removing active object
        if (getActiveStory().getMoniker().equals(daoStory.getMoniker())) {
            // if an object is defined
            if (getDaoStoryRepo().get(0) != null) {
                // set active object
                setActiveStory((DaoStory)getDaoStoryRepo().get(0));
            }
            else {
                // clear active object
                setActiveStory(null);
            }
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateStage(DaoStage daoStage, Boolean updateDatabase) {
        Log.d(TAG, "updateStage(updateDatabase = " + updateDatabase + "): daoStage " + daoStage.toString());
        // add or update repo with object
        getDaoStageRepo().set(daoStage);

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_updateStage, R.string.action_set, daoStage.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_updateStage, R.string.action_child_setValue, daoStage.getMoniker());
            // update timestamp
            daoStage.setTimestamp((System.currentTimeMillis()));
            // update db
            mStagesReference.child(daoStage.getMoniker()).setValue(daoStage);
        }
        // set active to updated object
        setActiveStage(daoStage);

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeStage(DaoStage daoStage, Boolean updateDatabase) {
        Log.d(TAG, "removeStage(updateDatabase = " + updateDatabase + "): daoStage " + daoStage.toString());
        // remove object from repo
        getDaoStageRepo().remove(daoStage.getMoniker());
        Log.d(TAG, "removeStage removed daoStage: " + daoStage.getMoniker());

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_removeStage, R.string.action_remove, daoStage.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_removeStage, R.string.action_child_removeValue, daoStage.getMoniker());
            // remove object from remote db
            mStagesReference.child(daoStage.getMoniker()).removeValue();
        }

        // if removing active object
        if (getActiveStage().getMoniker().equals(daoStage.getMoniker())) {
            // if an object is defined
            if (getDaoStageRepo().get(0) != null) {
                // set active object
                setActiveStage((DaoStage)getDaoStageRepo().get(0));
            }
            else {
                // clear active object
                setActiveStory(null);
            }
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateActor(DaoActor daoActor, Boolean updateDatabase) {
        Log.d(TAG, "updateActor(updateDatabase = " + updateDatabase + "): daoActor " + daoActor.toString());
        // add or update repo with object
        getDaoActorRepo().set(daoActor);

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_updateActor, R.string.action_set, daoActor.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_updateActor, R.string.action_child_setValue, daoActor.getMoniker());
            // update timestamp
            daoActor.setTimestamp((System.currentTimeMillis()));
            // update db
            mActorsReference.child(daoActor.getMoniker()).setValue(daoActor);
        }
        // set active to updated object
        setActiveActor(daoActor);

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeActor(DaoActor daoActor, Boolean updateDatabase) {
        Log.d(TAG, "removeActor(updateDatabase = " + updateDatabase + "): daoActor " + daoActor.toString());
        // remove object from repo
        getDaoActorRepo().remove(daoActor.getMoniker());
        Log.d(TAG, "removeActor removed daoActor: " + daoActor.getMoniker());

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_removeActor, R.string.action_remove, daoActor.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_removeActor, R.string.action_child_removeValue, daoActor.getMoniker());
            // remove object from remote db
            mActorsReference.child(daoActor.getMoniker()).removeValue();
        }

        // if removing active object
        if (getActiveActor().getMoniker().equals(daoActor.getMoniker())) {
            // if an object is defined
            if (getDaoActorRepo().get(0) != null) {
                // set active object
                setActiveActor((DaoActor)getDaoActorRepo().get(0));
            }
            else {
                // clear active object
                setActiveActor(null);
            }
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateAction(DaoAction daoAction, Boolean updateDatabase) {
        Log.d(TAG, "updateAction(updateDatabase = " + updateDatabase + "): daoAction " + daoAction.toString());
        // add or update repo with object
        getDaoActionRepo().set(daoAction);

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_updateAction, R.string.action_set, daoAction.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_updateAction, R.string.action_child_setValue, daoAction.getMoniker());
            // update timestamp
            daoAction.setTimestamp((System.currentTimeMillis()));
            // update db
            mActionsReference.child(daoAction.getMoniker()).setValue(daoAction);
        }
        // set active to updated object
        setActiveAction(daoAction);

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeAction(DaoAction daoAction, Boolean updateDatabase) {
        Log.d(TAG, "removeAction(updateDatabase = " + updateDatabase + "): daoAction " + daoAction.toString());
        // remove object from repo
        getDaoActionRepo().remove(daoAction.getMoniker());
        Log.d(TAG, "removeAction removed daoAction: " + daoAction.getMoniker());

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_removeAction, R.string.action_remove, daoAction.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_removeAction, R.string.action_child_removeValue, daoAction.getMoniker());
            // remove object from remote db
            mActionsReference.child(daoAction.getMoniker()).removeValue();
        }

        // if removing active object
        if (getActiveAction().getMoniker().equals(daoAction.getMoniker())) {
            // if an object is defined
            if (getDaoActionRepo().get(0) != null) {
                // set active object
                setActiveAction((DaoAction)getDaoActionRepo().get(0));
            }
            else {
                // clear active object
                setActiveAction(null);
            }
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateOutcome(DaoOutcome daoOutcome, Boolean updateDatabase) {
        Log.d(TAG, "updateOutcome(updateDatabase = " + updateDatabase + "): daoOutcome " + daoOutcome.toString());
        // add or update repo with object
        getDaoOutcomeRepo().set(daoOutcome);

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_updateOutcome, R.string.action_set, daoOutcome.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_updateOutcome, R.string.action_child_setValue, daoOutcome.getMoniker());
            // update timestamp
            daoOutcome.setTimestamp((System.currentTimeMillis()));
            // update db
            mOutcomesReference.child(daoOutcome.getMoniker()).setValue(daoOutcome);
        }
        // set active to updated object
        setActiveOutcome(daoOutcome);

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeOutcome(DaoOutcome daoOutcome, Boolean updateDatabase) {
        Log.d(TAG, "removeOutcome(updateDatabase = " + updateDatabase + "): daoOutcome " + daoOutcome.toString());
        // remove object from repo
        getDaoOutcomeRepo().remove(daoOutcome.getMoniker());
        Log.d(TAG, "removeOutcome removed daoOutcome: " + daoOutcome.getMoniker());

        // post audit trail
        getDaoAuditRepo().postAudit(R.string.actor_removeOutcome, R.string.action_remove, daoOutcome.getMoniker());

        if (updateDatabase) {
            // post audit trail
            getDaoAuditRepo().postAudit(R.string.actor_removeOutcome, R.string.action_child_removeValue, daoOutcome.getMoniker());
            // remove object from remote db
            mOutcomesReference.child(daoOutcome.getMoniker()).removeValue();
        }

        // if removing active object
        if (getActiveOutcome().getMoniker().equals(daoOutcome.getMoniker())) {
            // if an object is defined
            if (getDaoOutcomeRepo().get(0) != null) {
                // set active object
                setActiveOutcome((DaoOutcome)getDaoOutcomeRepo().get(0));
            }
            else {
                // clear active object
                setActiveOutcome(null);
            }
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    public Boolean addNewStage(DaoStoryRepo daoStoryRepo, DaoStageRepo daoStageRepo) {

//        // create new play & set active
//        DaoStory activeStory = new DaoStory();
//        // moniker must be finalized prior to set (moniker list is not updated on object set
//        activeStory.setMoniker(DEFAULT_STORY_NICKNAME + daoStoryRepo.size());
//        updateStory(activeStory, true);


        // create new stage & set active
        DaoStage activeStage = new DaoStage();
        daoStageRepo.set(activeStage);
        setActiveStage(activeStage);

        // create model
        setStageModelRing(new StageModelRing(this));
        Integer ringMax = 4;
        mStageReady = getStageModelRing().buildModel(ringMax);

        // TEST: create new stage & set active
        activeStage = new DaoStage();
        daoStageRepo.set(activeStage);
        setActiveStage(activeStage);

        // create model
        setStageModelRing(new StageModelRing(this));
        ringMax = 4;
        mStageReady = getStageModelRing().buildModel(ringMax);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // firebase helpers
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private Boolean setFirebaseReference() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (mDatabaseReference != null) {
            mIsFirebaseReady = true;
            mTheatresReference = FirebaseDatabase.getInstance().getReference().child(DaoTheatreRepo.JSON_CONTAINER);
            mEpicsReference = FirebaseDatabase.getInstance().getReference().child(DaoEpicRepo.JSON_CONTAINER);
            mStorysReference = FirebaseDatabase.getInstance().getReference().child(DaoStoryRepo.JSON_CONTAINER);
            mStagesReference = FirebaseDatabase.getInstance().getReference().child(DaoStageRepo.JSON_CONTAINER);
            mActorsReference = FirebaseDatabase.getInstance().getReference().child(DaoActorRepo.JSON_CONTAINER);
            mActionsReference = FirebaseDatabase.getInstance().getReference().child(DaoActionRepo.JSON_CONTAINER);
            mOutcomesReference = FirebaseDatabase.getInstance().getReference().child(DaoOutcomeRepo.JSON_CONTAINER);
        }
        return mIsFirebaseReady;
    }
    public Boolean isFirebaseReady() {
        return mIsFirebaseReady;
    }
    public DatabaseReference getFirebaseReference() {
        return mDatabaseReference;
    }
    public DatabaseReference getTheatresReference() {
        return mTheatresReference;
    }
    public DatabaseReference getEpicsReference() {
        return mEpicsReference;
    }
    public DatabaseReference getStorysReference() {
        return mStorysReference;
    }
    public DatabaseReference getStagesReference() {
        return mStagesReference;
    }
    public DatabaseReference getActorsReference() {
        return mActorsReference;
    }
    public DatabaseReference getActionsReference() {
        return mActionsReference;
    }
    public DatabaseReference getOutcomesReference() {
        return mOutcomesReference;
    }
    public Boolean removeFirebaseListener() {
        // TODO: remove listeners
        return true;
    }
///////////////////////////////////////////////////////////////////////////
}
