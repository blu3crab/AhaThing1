package com.adaptivehandyapps.ahathing.dal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoStoryList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStageList;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.adaptivehandyapps.ahathing.dao.DaoTheatreRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private OnStoryProviderRefresh mCallback = null; //call back interface

    private StoryProvider mStoryProvider;

    private Boolean mTheatreReady = false;
    private Boolean mStoryReady = false;
    private Boolean mStageReady = false;
    private Boolean mLocalUpdate = false;

//    private DaoTheatreList mDaoTheatreList;
    private DaoTheatreRepo mDaoTheatreRepo;
    private DaoStoryList mDaoStoryList;
    private DaoStageList mDaoStageList;

    private DaoTheatre mActiveTheatre;
    private DaoStory mActiveStory;
    private DaoStage mActiveStage;

    private StageModelRing mStageModelRing;

    // firebase
    private String mUserId = DaoDefs.INIT_STRING_MARKER;
    private Boolean mIsFirebaseReady = false;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mTheatresReference;

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
            setFirebaseListener();
            queryTheatres();
        }
        else {
            mIsFirebaseReady = false;
        }
        Log.d(TAG, "Firebase ready: " + isFirebaseReady() + ", UserId " + mUserId);

        // create theatre list
//        mDaoTheatreList = new DaoTheatreList();
        mDaoTheatreRepo = new DaoTheatreRepo();
        // add new theatre
//        addNewTheatre(mDaoTheatreList);
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
    public Boolean isTheatreReady() { return mTheatreReady;}
    public Boolean isStoryReady() { return mStoryReady;}
    public Boolean isStageReady() { return mStageReady;}

    public Boolean isLocalUpdate() {
        return mLocalUpdate;
    }
    public Boolean setLocalUpdate(Boolean localUpdate) {
        Log.d(TAG, "setLocalUpdate from " + mLocalUpdate + " to " + localUpdate);
        mLocalUpdate = localUpdate;
        return (mLocalUpdate);
    }

    public DaoTheatreRepo getDaoTheatreRepo() { return mDaoTheatreRepo; }
    public void setDaoTheatreRepo(DaoTheatreRepo daoTheatreRepo) {
        this.mDaoTheatreRepo = daoTheatreRepo;
    }

    public DaoStoryList getDaoStoryList() { return mDaoStoryList; }
    public void setDaoStoryList(DaoStoryList daoStoryList) {
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

//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean addNewTheatre(DaoTheatreList daoTheatreList) {
//
//        // create new theatre, add to theatre dao list & set active
//        DaoTheatre activeTheatre = new DaoTheatre();
//        daoTheatreList.dao.add(activeTheatre);
//        setActiveTheatre(activeTheatre);
//        // default moniker & add to moniker list
//        String moniker = DEFAULT_THEATRE_NICKNAME + daoTheatreList.dao.size();
//        activeTheatre.setMoniker(moniker);
//        daoTheatreList.moniker.add(moniker);
//        // add child to db
////        mDatabaseReference.child(DaoTheatreList.JSON_CONTAINER).child(activeTheatre.getMoniker()).setValue(activeTheatre);
//        mTheatresReference.child(activeTheatre.getMoniker()).setValue(activeTheatre);
//
//        return true;
//    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateTheatreRepo(DaoTheatre daoTheatre, Boolean remoteTrigger) {
        // create or update theatre list & db
        Log.d(TAG, "updateTheatreRepo(remoteTrigger = " + remoteTrigger + "): daoTheatre " + daoTheatre.toString());
        // add or update repo with object
        getDaoTheatreRepo().set(daoTheatre);

        if (remoteTrigger) {
            // set localUpdate in progress
            setLocalUpdate(true);
            // update db
            mTheatresReference.child(daoTheatre.getMoniker()).setValue(daoTheatre);
        }
//        // no active theatre, set added child active
//        if (getActiveTheatre() == null) setActiveTheatre(daoTheatre);

        // set active to updated object
        setActiveTheatre(daoTheatre);

        mTheatreReady = true;

        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeTheatreRepo(DaoTheatre daoTheatre, Boolean remoteTrigger) {
        // create or update theatre list & db
        Log.d(TAG, "removeTheatreRepo(remoteTrigger = " + remoteTrigger + "): daoTheatre " + daoTheatre.toString());
        // if removing active object
        if (getActiveTheatre().getMoniker().equals(daoTheatre.getMoniker())) {
            // if an object is defined
            if (getDaoTheatreRepo().get(0) != null) {
                // set active object
                mTheatreReady = true;
                setActiveTheatre(getDaoTheatreRepo().get(0));
            }
            else {
                // clear active object
                mTheatreReady = false;
                setActiveTheatre(null);
            }
        }
        // add or update repo with object
        getDaoTheatreRepo().remove(daoTheatre.getMoniker());
        Log.d(TAG, "removeTheatreRepo removed daoTheatre: " + daoTheatre.getMoniker());

        if (remoteTrigger) {
            // set localUpdate in progress
            setLocalUpdate(true);
            // remove object from remote db
            mTheatresReference.child(daoTheatre.getMoniker()).removeValue();
        }
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

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
//            setFirebaseListener();
        }
        return mIsFirebaseReady;
    }
    private Boolean isFirebaseReady() {
        return mIsFirebaseReady;
    }
    private DatabaseReference getFirebaseReference() {
        return mDatabaseReference;
    }
    private DatabaseReference getTheatresReference() {
        return mTheatresReference;
    }
    public Boolean removeFirebaseListener() {
        // TODO: remove listeners
        return true;
    }
    public Boolean setFirebaseListener() {
        ValueEventListener theatreListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get theatre object and use the values to update the UI
//                DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                if (daoTheatre != null) {
//                    Log.d(TAG, "onDataChange daoTheatre: " + daoTheatre.toString());
//                }
                Boolean refresh = false;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
                    if (!isLocalUpdate()) {
                        if (daoTheatre != null) {
                            Log.d(TAG, "onDataChange daoTheatre: " + daoTheatre.toString());
                            refresh = true;
                            // update db
                            mDatabaseReference.child(DaoTheatreRepo.JSON_CONTAINER).child(daoTheatre.getMoniker()).setValue(daoTheatre);
                        } else {
                            Log.e(TAG, "onChildAdded: NULL daoTheatre?");
                        }
                    }
                    else if (isLocalUpdate()) {
                        setLocalUpdate(false);
                    }
                }
                // if data changed, post refresh
                if (refresh) {
                    // refresh
                    if (mCallback != null) mCallback.onPlayProviderRefresh(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // get theatre object failed, log a message
                Log.e(TAG, "theatreListener:onCancelled", databaseError.toException());
                // ...
            }
        };
        mTheatresReference.addValueEventListener(theatreListener);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                // if no local update in progress
                if (!isLocalUpdate()) {
                    if (daoTheatre != null) {
                        Log.d(TAG, "onChildAdded: daoTheatre " + daoTheatre.toString());
                        updateTheatreRepo(daoTheatre, false);
                    }
                    else {
                        Log.e(TAG, "onChildAdded: NULL daoTheatre?");
                    }
                }
                else if (isLocalUpdate()) {
                    setLocalUpdate(false);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
                String snapshotKey = dataSnapshot.getKey();
                DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                // if no local update in progress
                if (!isLocalUpdate()) {
                    if (daoTheatre != null) {
                        Log.d(TAG, "onChildChanged daoTheatre: " + daoTheatre.toString());
                        updateTheatreRepo(daoTheatre, false);
                    }
                    else {
                        Log.e(TAG, "onChildChanged: NULL daoTheatre?");
                    }
                }
                else if (isLocalUpdate()) {
                    setLocalUpdate(false);
                }
             }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
                String snapshotKey = dataSnapshot.getKey();
                DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                if (!isLocalUpdate()) {
                    if (daoTheatre != null) {
                    Log.d(TAG, "onChildRemoved daoTheatre: " + daoTheatre.toString());
                    removeTheatreRepo(daoTheatre, false);
                    }
                    else {
                        Log.e(TAG, "onChildRemoved: NULL daoTheatre?");
                    }
                }
                else if (isLocalUpdate()) {
                    setLocalUpdate(false);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildMoved:" + dataSnapshot.getKey());

                DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "childEventListener onCancelled: ", databaseError.toException());
                Toast.makeText(mContext, "Failed to load...",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mTheatresReference.addChildEventListener(childEventListener);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean queryTheatres() {
        ValueEventListener theatreListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get theatre objects
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
                    if (daoTheatre != null) {
                        Log.d(TAG, "queryTheatres onDataChange daoTheatre: " + daoTheatre.toString());
                        updateTheatreRepo(daoTheatre, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // get theatre object failed, log a message
                Log.e(TAG, "queryTheatres onCancelled: ", databaseError.toException());
                // ...
            }
        };

        Query mTheatreQuery = mTheatresReference.child(DaoTheatreRepo.JSON_CONTAINER).orderByChild("moniker");
        mTheatreQuery.addValueEventListener(theatreListener);

        return true;
    }
///////////////////////////////////////////////////////////////////////////
}
