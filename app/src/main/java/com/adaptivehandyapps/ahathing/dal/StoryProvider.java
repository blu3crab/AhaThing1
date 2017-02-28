package com.adaptivehandyapps.ahathing.dal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoStoryList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStageList;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.adaptivehandyapps.ahathing.dao.DaoTheatreList;
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

//    private DaoTheatreList mDaoTheatreList;
    private DaoTheatreRepo mDaoTheatreRepo;
    private DaoStoryList mDaoStoryList;
    private DaoStageList mDaoStageList;

    private DaoTheatre mActiveTheatre;
    private DaoStory mActiveStory;
    private DaoStage mActiveStage;

    private StageModelRing mStageModelRing;

    // firebase
    private String mUserId;
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
        // get user id
        mUserId = getUid();
        Log.d(TAG, "Firebase ready: " + isFirebaseReady() + ", UserId " + mUserId);

        // create theatre list
//        mDaoTheatreList = new DaoTheatreList();
        mDaoTheatreRepo = new DaoTheatreRepo();
        // add new theatre
//        addNewTheatre(mDaoTheatreList);
        queryTheatres();
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

//    public DaoTheatreList getDaoTheatreList() { return mDaoTheatreList; }
//    public void setDaoTheatreList(DaoTheatreList daoTheatreList) {
//        this.mDaoTheatreList = daoTheatreList;
//    }
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

    ///////////////////////////////////////////////////////////////////////////
    public Boolean addNewTheatre(DaoTheatreList daoTheatreList) {

        // create new theatre, add to theatre dao list & set active
        DaoTheatre activeTheatre = new DaoTheatre();
        daoTheatreList.dao.add(activeTheatre);
        setActiveTheatre(activeTheatre);
        // default moniker & add to moniker list
        String moniker = DEFAULT_THEATRE_NICKNAME + daoTheatreList.dao.size();
        activeTheatre.setMoniker(moniker);
        daoTheatreList.moniker.add(moniker);
        // add child to db
        mDatabaseReference.child(DaoTheatreList.JSON_CONTAINER).child(mUserId).setValue(activeTheatre);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateTheatreRepo(DaoTheatre daoTheatre) {
        // create or update theatre list & db
        Log.d(TAG, "updateTheatreRepo: daoTheatre " + daoTheatre.toString());
        // add or update repo with object
        getDaoTheatreRepo().set(daoTheatre);

        // no active theatre, set added child active
        mTheatreReady = true;
        if (getActiveTheatre() == null) setActiveTheatre(daoTheatre);

        // update db
        mDatabaseReference.child(DaoTheatreList.JSON_CONTAINER).child(mUserId).setValue(daoTheatre);
        // refresh
        if (mCallback != null) mCallback.onPlayProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean removeTheatreRepo(DaoTheatre daoTheatre) {
        // create or update theatre list & db
        Log.d(TAG, "removeTheatreRepo: daoTheatre " + daoTheatre.toString());
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

        // remove
        mDatabaseReference.child(DaoTheatreList.JSON_CONTAINER).child(mUserId).child(daoTheatre.getMoniker()).removeValue();
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
            mTheatresReference = FirebaseDatabase.getInstance().getReference()
                    .child(DaoTheatreList.JSON_CONTAINER);
            setFirebaseListener();
        }
        return mIsFirebaseReady;
    }
    private Boolean isFirebaseReady() {
        return mIsFirebaseReady;
    }
    private DatabaseReference getFirebaseReference() {
        return mDatabaseReference;
    }
    private Boolean setFirebaseListener() {
        ValueEventListener theatreListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get theatre object and use the values to update the UI
//                DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                if (daoTheatre != null) {
//                    Log.d(TAG, "onDataChange daoTheatre: " + daoTheatre.toString());
//                }
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
                    if (daoTheatre != null) {
                        Log.d(TAG, "onDataChange daoTheatre: " + daoTheatre.toString());
                    }
                }
                // ...
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
                if (daoTheatre != null) {
                    Log.d(TAG, "childEventListener onChildAdded: daoTheatre " + daoTheatre.toString());
                    updateTheatreRepo(daoTheatre);
//                    // if added child not already present
//                    if (getDaoTheatreList().getDao(daoTheatre.getMoniker()) == null) {
//                        // add moniker & dao to lists
//                        getDaoTheatreList().moniker.add(daoTheatre.getMoniker());
//                        getDaoTheatreList().dao.add(daoTheatre);
//                        // no active theatre, set added child active
//                        if (getActiveTheatre() == null) setActiveTheatre(daoTheatre);
//                        Log.d(TAG, "childEventListener onChildAdded added daoTheatre: " + daoTheatre.getMoniker());
//                    }
//                    else {
//                        // added child already present, replace dao
//                        int i = getDaoTheatreList().getIndex(daoTheatre.getMoniker());
//                        getDaoTheatreList().dao.set(i, daoTheatre);
//                        Log.d(TAG, "childEventListener onChildAdded updated daoTheatre(" + i + "): " + daoTheatre.getMoniker());
//                    }
//                    mTheatreReady = true;
//                    if (mCallback != null) mCallback.onPlayProviderRefresh(true);
                }
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildChanged:" + dataSnapshot.getKey());
                DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
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
    private Boolean queryTheatres() {
        ValueEventListener theatreListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get theatre objects
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
                    if (daoTheatre != null) {
                        Log.d(TAG, "queryTheatres onDataChange daoTheatre: " + daoTheatre.toString());
                        updateTheatreRepo(daoTheatre);
//                        // if changed child not already present
//                        if (getDaoTheatreList().getDao(daoTheatre.getMoniker()) == null) {
//                            // add moniker & dao to lists
//                            getDaoTheatreList().moniker.add(daoTheatre.getMoniker());
//                            getDaoTheatreList().dao.add(daoTheatre);
//                            // no active theatre, set added child active
//                            if (getActiveTheatre() == null) setActiveTheatre(daoTheatre);
//                            Log.d(TAG, "queryTheatres onDataChange added daoTheatre: " + daoTheatre.getMoniker());
//                        }
//                        else {
//                            // changed child already present, replace dao
//                            int i = getDaoTheatreList().getIndex(daoTheatre.getMoniker());
//                            getDaoTheatreList().dao.set(i, daoTheatre);
//                            Log.d(TAG, "queryTheatres onDataChange updated daoTheatre(" + i + "): " + daoTheatre.getMoniker());
//                        }
//                        mTheatreReady = true;
                    }
                }
//                if (mCallback != null) mCallback.onPlayProviderRefresh(true);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // get theatre object failed, log a message
                Log.e(TAG, "queryTheatres onCancelled: ", databaseError.toException());
                // ...
            }
        };

        Query mTheatreQuery = mTheatresReference.child(DaoTheatreList.JSON_CONTAINER).child(mUserId)
                .orderByChild("moniker");
        mTheatreQuery.addValueEventListener(theatreListener);

        return true;
    }
///////////////////////////////////////////////////////////////////////////
}
