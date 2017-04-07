package com.adaptivehandyapps.ahathing.dal;
//
// Created by mat on 3/17/2017.
//

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.adaptivehandyapps.ahathing.dao.DaoTheatreRepo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProviderListener {
    private static final String TAG = "ProviderListener";

    private Context mContext;
    private RepoProvider mRepoProvider;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public ProviderListener (Context context, RepoProvider repoProvider) {
        mContext = context;
        mRepoProvider = repoProvider;
    }
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
//                            mRepoProvider.update(daoTheatre, false);
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
//        Query mTheatreQuery = mRepoProvider.getFirebaseReference().child(DaoTheatreRepo.JSON_CONTAINER).orderByChild("moniker");
//        mTheatreQuery.addValueEventListener(thingsListener);
//
//        return true;
//    }
//    public Boolean setListener() {
////        ValueEventListener valueEventListener = new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                Log.d(TAG, "onDataChange:" + dataSnapshot.getKey());
////                // get theatre object and use the values to update the UI
////                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
////                    if (dataSnapshot.getKey() != null && dataSnapshot.getKey().equals(DaoTheatreRepo.JSON_CONTAINER)) {
////                        DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
////                        if (daoTheatre != null) {
////                            // if no recent local activity
////                            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
////                            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
////                                Log.d(TAG, "onDataChange daoTheatre (remote trigger): " + daoTheatre.toString());
////                                // update repo but not db
////                                mRepoProvider.update(daoTheatre, false);
////                            }
////                            else {
////                                Log.d(TAG, "onDataChange: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
////                            }
////                        }
////                        else {
////                            Log.e(TAG, "onDataChange: NULL daoTheatre?");
////                        }
////                    }
////                    else {
////                        Log.e(TAG, "valueEventListener onDataChange unknown key: " + dataSnapshot.getKey());
////                    }
////                }
////                // post audit trail
////                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_listen, dataSnapshot.getKey());
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                // get theatre object failed, log a message
//////                Log.e(TAG, "theatreListener:onCancelled", databaseError.toException());
////                Log.e(TAG, "theatreListener:onCancelled: " + databaseError.getMessage());
////                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_cancelled, databaseError.getMessage());
////            }
////        };
////        // theatre level value event listener: dataSnapshot.getKey() "theatres"
////        mRepoProvider.getFirebaseReference().addValueEventListener(valueEventListener);
//////        mEpicsReference.addValueEventListener(valueEventListener);
//////        mDatabaseReference.addValueEventListener(valueEventListener);
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
////                    Object daoObject = dataSnapshot.getValue(Object.class);
////                    if (daoObject.getClass().equals(DaoTheatre.class)) {
////                        Log.d(TAG, "onChildAdded Theatre " + dataSnapshot.getKey());
////                    }
//                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                    if (daoTheatre != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoTheatre (remote trigger): " + daoTheatre.toString());
//                            // update repo but not db
//                            mRepoProvider.update(daoTheatre, false);
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoRepo().contains(dataSnapshot.getKey())) {
//                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                    if (daoTheatre != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoTheatre (remote trigger): " + daoTheatre.toString());
//                            // update repo but not db
//                            mRepoProvider.update(daoTheatre, false);
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoRepo().contains(dataSnapshot.getKey())) {
//                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
//                    if (daoTheatre != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoTheatre (remote trigger): " + daoTheatre.toString());
//                            // remove from repo leaving db unchanged
//                            mRepoProvider.remove(daoTheatre, false);
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // theatre level child event listener: dataSnapshot.getKey() = "theatreXX"
//        mRepoProvider.getFirebaseReference().addChildEventListener(childEventListener);
//        // database level child event listener: dataSnapshot.getKey() = "theatres"
//        // missing remote RemoveChild notifications!
////        mDatabaseReference.addChildEventListener(childEventListener);
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean setEpicListener() {
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
//                    DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
//                    if (daoEpic != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoEpic (remote trigger): " + daoEpic.toString());
//                            // update repo but not db
//                            mRepoProvider.updateEpic(daoEpic, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildAdded: daoEpic (ignore local|multiple trigger): " + daoEpic.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildAdded: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoEpicRepo().contains(dataSnapshot.getKey())) {
//                    DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
//                    if (daoEpic != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoEpic (remote trigger): " + daoEpic.toString());
//                            // update repo but not db
//                            mRepoProvider.updateEpic(daoEpic, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildChanged: daoEpic (ignore|multiple local trigger): " + daoEpic.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildChanged: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoEpicRepo().contains(dataSnapshot.getKey())) {
//                    DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
//                    if (daoEpic != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoEpic (remote trigger): " + daoEpic.toString());
//                            // remove from repo leaving db unchanged
//                            mRepoProvider.removeEpic(daoEpic, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildRemoved: daoEpic (ignore local|multiple trigger): " + daoEpic.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildRemoved: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // Epic level child event listener: dataSnapshot.getKey() = "epicXX"
//        mRepoProvider.getEpicsReference().addChildEventListener(childEventListener);
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean setStoryListener() {
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
//                    DaoStory daoStory = dataSnapshot.getValue(DaoStory.class);
//                    if (daoStory != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStory.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoStory (remote trigger): " + daoStory.toString());
//                            // update repo but not db
//                            mRepoProvider.updateStory(daoStory, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildAdded: daoStory (ignore local|multiple trigger): " + daoStory.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildAdded: NULL daoStory?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoStoryRepo().contains(dataSnapshot.getKey())) {
//                    DaoStory daoStory = dataSnapshot.getValue(DaoStory.class);
//                    if (daoStory != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStory.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoStory (remote trigger): " + daoStory.toString());
//                            // update repo but not db
//                            mRepoProvider.updateStory(daoStory, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildChanged: daoStory (ignore|multiple local trigger): " + daoStory.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildChanged: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoStoryRepo().contains(dataSnapshot.getKey())) {
//                    DaoStory daoStory = dataSnapshot.getValue(DaoStory.class);
//                    if (daoStory != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStory.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoStory (remote trigger): " + daoStory.toString());
//                            // remove from repo leaving db unchanged
//                            mRepoProvider.removeStory(daoStory, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildRemoved: daoStory (ignore local|multiple trigger): " + daoStory.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildRemoved: NULL daoStory?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // Story level child event listener: dataSnapshot.getKey() = "epicXX"
//        mRepoProvider.getStorysReference().addChildEventListener(childEventListener);
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean setStageListener() {
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
//                    DaoStage daoStage = dataSnapshot.getValue(DaoStage.class);
//                    if (daoStage != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStage.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoStage (remote trigger): " + daoStage.toString());
//                            // update repo but not db
//                            mRepoProvider.updateStage(daoStage, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildAdded: daoStage (ignore local|multiple trigger): " + daoStage.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildAdded: NULL daoStage?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoStageRepo().contains(dataSnapshot.getKey())) {
//                    DaoStage daoStage = dataSnapshot.getValue(DaoStage.class);
//                    if (daoStage != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStage.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoStage (remote trigger): " + daoStage.toString());
//                            // update repo but not db
//                            mRepoProvider.updateStage(daoStage, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildChanged: daoStage (ignore|multiple local trigger): " + daoStage.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildChanged: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoStageRepo().contains(dataSnapshot.getKey())) {
//                    DaoStage daoStage = dataSnapshot.getValue(DaoStage.class);
//                    if (daoStage != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStage.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoStage (remote trigger): " + daoStage.toString());
//                            // remove from repo leaving db unchanged
//                            mRepoProvider.removeStage(daoStage, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildRemoved: daoStage (ignore local|multiple trigger): " + daoStage.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildRemoved: NULL daoStory?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // Stage level child event listener: dataSnapshot.getKey() = "epicXX"
//        mRepoProvider.getStagesReference().addChildEventListener(childEventListener);
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean setActorListener() {
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
//                    DaoActor daoActor = dataSnapshot.getValue(DaoActor.class);
//                    if (daoActor != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoActor.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoActor (remote trigger): " + daoActor.toString());
//                            // update repo but not db
//                            mRepoProvider.updateActor(daoActor, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildAdded: daoActor (ignore local|multiple trigger): " + daoActor.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildAdded: NULL daoActor?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoActorRepo().contains(dataSnapshot.getKey())) {
//                    DaoActor daoActor = dataSnapshot.getValue(DaoActor.class);
//                    if (daoActor != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoActor.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoActor (remote trigger): " + daoActor.toString());
//                            // update repo but not db
//                            mRepoProvider.updateActor(daoActor, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildChanged: daoActor (ignore|multiple local trigger): " + daoActor.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildChanged: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoActorRepo().contains(dataSnapshot.getKey())) {
//                    DaoActor daoActor = dataSnapshot.getValue(DaoActor.class);
//                    if (daoActor != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoActor.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoActor (remote trigger): " + daoActor.toString());
//                            // remove from repo leaving db unchanged
//                            mRepoProvider.removeActor(daoActor, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildRemoved: daoActor (ignore local|multiple trigger): " + daoActor.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildRemoved: NULL daoStory?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // Actor level child event listener: dataSnapshot.getKey() = "epicXX"
//        mRepoProvider.getActorsReference().addChildEventListener(childEventListener);
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean setActionListener() {
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
//                    DaoAction daoAction = dataSnapshot.getValue(DaoAction.class);
//                    if (daoAction != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoAction.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoAction (remote trigger): " + daoAction.toString());
//                            // update repo but not db
//                            mRepoProvider.updateAction(daoAction, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildAdded: daoAction (ignore local|multiple trigger): " + daoAction.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildAdded: NULL daoStory?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoActionRepo().contains(dataSnapshot.getKey())) {
//                    DaoAction daoAction = dataSnapshot.getValue(DaoAction.class);
//                    if (daoAction != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoAction.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoAction (remote trigger): " + daoAction.toString());
//                            // update repo but not db
//                            mRepoProvider.updateAction(daoAction, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildChanged: daoAction (ignore|multiple local trigger): " + daoAction.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildChanged: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoActionRepo().contains(dataSnapshot.getKey())) {
//                    DaoAction daoAction = dataSnapshot.getValue(DaoAction.class);
//                    if (daoAction != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoAction.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoAction (remote trigger): " + daoAction.toString());
//                            // remove from repo leaving db unchanged
//                            mRepoProvider.removeAction(daoAction, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildRemoved: daoAction (ignore local|multiple trigger): " + daoAction.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildRemoved: NULL daoAction?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // Action level child event listener: dataSnapshot.getKey() = "epicXX"
//        mRepoProvider.getActionsReference().addChildEventListener(childEventListener);
//        return true;
//    }
//    ///////////////////////////////////////////////////////////////////////////
//    public Boolean setOutcomeListener() {
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null) {
//                    DaoOutcome daoOutcome = dataSnapshot.getValue(DaoOutcome.class);
//                    if (daoOutcome != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoOutcome.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildAdded daoOutcome (remote trigger): " + daoOutcome.toString());
//                            // update repo but not db
//                            mRepoProvider.updateOutcome(daoOutcome, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildAdded: daoOutcome (ignore local|multiple trigger): " + daoOutcome.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildAdded: NULL daoStory?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
//                String snapshotKey = dataSnapshot.getKey();
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoOutcomeRepo().contains(dataSnapshot.getKey())) {
//                    DaoOutcome daoOutcome = dataSnapshot.getValue(DaoOutcome.class);
//                    if (daoOutcome != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoOutcome.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildChanged daoOutcome (remote trigger): " + daoOutcome.toString());
//                            // update repo but not db
//                            mRepoProvider.updateOutcome(daoOutcome, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildChanged: daoOutcome (ignore|multiple local trigger): " + daoOutcome.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildChanged: NULL daoEpic?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey() != null && mRepoProvider.getDaoOutcomeRepo().contains(dataSnapshot.getKey())) {
//                    DaoOutcome daoOutcome = dataSnapshot.getValue(DaoOutcome.class);
//                    if (daoOutcome != null) {
//                        // if no recent local activity
//                        DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoOutcome.getMoniker());
//                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                            Log.d(TAG, "onChildRemoved daoOutcome (remote trigger): " + daoOutcome.toString());
//                            // remove from repo leaving db unchanged
//                            mRepoProvider.removeOutcome(daoOutcome, false);
//                        }
//                        else {
//                            Log.d(TAG, "onChildRemoved: daoOutcome (ignore local|multiple trigger): " + daoOutcome.toString());
//                        }
//                    }
//                    else {
//                        Log.e(TAG, "onChildRemoved: NULL daoOutcome?");
//                    }
//                } else {
//                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
//                }
//                // post audit trail
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
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
//                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // Outcome level child event listener: dataSnapshot.getKey() = "epicXX"
//        mRepoProvider.getOutcomesReference().addChildEventListener(childEventListener);
//        return true;
//    }
//
}
