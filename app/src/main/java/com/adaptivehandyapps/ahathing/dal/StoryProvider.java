package com.adaptivehandyapps.ahathing.dal;

import android.content.Context;
import android.util.Log;

import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoAuditRepo;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicRepo;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoStoryRepo;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStageList;
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

    private DaoAuditRepo mDaoAuditRepo;
    private DaoTheatreRepo mDaoTheatreRepo;
    private DaoEpicRepo mDaoEpicRepo;
    private DaoStoryRepo mDaoStoryRepo;
    private DaoStageList mDaoStageList;

    private DaoTheatre mActiveTheatre;
    private DaoEpic mActiveEpic;
    private DaoStory mActiveStory;
    private DaoStage mActiveStage;

    private StageModelRing mStageModelRing;

    // firebase
    private String mUserId = DaoDefs.INIT_STRING_MARKER;
    private Boolean mIsFirebaseReady = false;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mTheatresReference;
    private DatabaseReference mEpicsReference;
    private DatabaseReference mStorysReference;

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
        // create play list
        mDaoStoryRepo = new DaoStoryRepo();
        // add new play
        addNewStory(mDaoStoryRepo);

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

    public DaoStageList getDaoStageList() { return mDaoStageList; }
    private void setDaoStageList(DaoStageList daoStageList) {
        this.mDaoStageList = daoStageList;
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

//    ///////////////////////////////////////////////////////////////////////////
//    public DaoAudit postAudit(int actorResId, int actionResId, String outcome) {
//        // post audit trail
//        DaoAudit daoAudit = new DaoAudit();
//        daoAudit.setTimestamp(System.currentTimeMillis());
//        daoAudit.setActor(mContext.getString(actorResId));
//        daoAudit.setAction(mContext.getString(actionResId));
//        daoAudit.setOutcome(outcome);
//        mDaoAuditRepo.set(daoAudit);
//        return daoAudit;
//    }
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


    ///////////////////////////////////////////////////////////////////////////
    public Boolean addNewStory(DaoStoryRepo daoStoryRepo) {

        // create new play & set active
        DaoStory activeStory = new DaoStory();
        // moniker must be finalized prior to set (moniker list is not updated on object set
        activeStory.setMoniker(DEFAULT_STORY_NICKNAME + daoStoryRepo.size());
        daoStoryRepo.set(activeStory);
        setActiveStory(activeStory);

        // create stage list, new stage & set active
        DaoStageList daoStageList = new DaoStageList();
        setDaoStageList(daoStageList);
        DaoStage activeStage = new DaoStage();
        daoStageList.stages.add(activeStage);
        setActiveStage(activeStage);
        mStoryReady = true;

        // create model
        setStageModelRing(new StageModelRing(this));
        Integer ringMax = 4;
        mStageReady = getStageModelRing().buildModel(ringMax);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // firebase
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
//            setTheatreListener();
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
    public Boolean removeFirebaseListener() {
        // TODO: remove listeners
        return true;
    }
//    public Boolean setTheatreListener() {
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange:" + dataSnapshot.getKey());
//                // get theatre object and use the values to update the UI
//                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    if (dataSnapshot.getKey() != null && dataSnapshot.getKey().equals(DaoTheatreRepo.JSON_CONTAINER)) {
//                        DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
//                        if (daoTheatre != null) {
//                            // if no recent local activity
//                            DaoAudit daoAudit = mDaoAuditRepo.get(daoTheatre.getMoniker());
//                            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                                Log.d(TAG, "onDataChange daoTheatre (remote trigger): " + daoTheatre.toString());
//                                // update repo but not db
//                                updateTheatre(daoTheatre, false);
//                            }
//                            else {
//                                Log.d(TAG, "onDataChange: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
//                            }
//                        }
//                        else {
//                            Log.e(TAG, "onDataChange: NULL daoTheatre?");
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "valueEventListener onDataChange unknown key: " + dataSnapshot.getKey());
//                    }
//                }
//                // post audit trail
//                getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // get theatre object failed, log a message
////                Log.e(TAG, "theatreListener:onCancelled", databaseError.toException());
//                Log.e(TAG, "theatreListener:onCancelled: " + databaseError.getMessage());
//                getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // theatre level value event listener: dataSnapshot.getKey() "theatres"
//        mTheatresReference.addValueEventListener(valueEventListener);
////        mEpicsReference.addValueEventListener(valueEventListener);
////        mDatabaseReference.addValueEventListener(valueEventListener);
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
//                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                    if (daoTheatre != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mDaoAuditRepo.get(daoTheatre.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoTheatre (remote trigger): " + daoTheatre.toString());
//                            // update repo but not db
//                            updateTheatre(daoTheatre, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildAdded: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildAdded: NULL daoTheatre?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && getDaoTheatreRepo().contains(dataSnapshot.getKey())) {
//                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                    if (daoTheatre != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mDaoAuditRepo.get(daoTheatre.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoTheatre (remote trigger): " + daoTheatre.toString());
//                            // update repo but not db
//                            updateTheatre(daoTheatre, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildChanged: daoTheatre (ignore|multiple local trigger): " + daoTheatre.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildChanged: NULL daoTheatre?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && getDaoTheatreRepo().contains(dataSnapshot.getKey())) {
//                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                    if (daoTheatre != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mDaoAuditRepo.get(daoTheatre.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoTheatre (remote trigger): " + daoTheatre.toString());
//                            // remove from repo leaving db unchanged
//                            removeTheatre(daoTheatre, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildRemoved: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildRemoved: NULL daoTheatre?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildMoved:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "childEventListener onCancelled: " + databaseError.getMessage());
//                Toast.makeText(mContext, "childEventListener onCancelled: " + databaseError.getMessage(),
//                        Toast.LENGTH_LONG).show();
//                getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//           }
//        };
//        // theatre level child event listener: dataSnapshot.getKey() = "theatreXX"
//        mTheatresReference.addChildEventListener(childEventListener);
//        // database level child event listener: dataSnapshot.getKey() = "theatres"
//        // missing remote RemoveChild notifications!
////        mDatabaseReference.addChildEventListener(childEventListener);
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean queryThings() {
//        ValueEventListener thingsListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // get theatre objects
//                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    if (dataSnapshot.getKey() != null && dataSnapshot.getKey().equals(DaoTheatreRepo.JSON_CONTAINER)) {
//                        DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
//                        if (daoTheatre != null) {
//                            Log.d(TAG, "queryThings onDataChange daoTheatre: " + daoTheatre.toString());
//                            updateTheatre(daoTheatre, false);
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "queryThings onDataChange unknown key: " + snapshot.getKey());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // get theatre object failed, log a message
//                Log.e(TAG, "queryThings onCancelled: ", databaseError.toException());
//                // ...
//            }
//        };
//
//        Query mTheatreQuery = mTheatresReference.child(DaoTheatreRepo.JSON_CONTAINER).orderByChild("moniker");
//        mTheatreQuery.addValueEventListener(thingsListener);
//
//        return true;
//    }
///////////////////////////////////////////////////////////////////////////
}
