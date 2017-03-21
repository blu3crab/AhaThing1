package com.adaptivehandyapps.ahathing.dal;
//
// Created by mat on 3/17/2017.
//

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
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
    private StoryProvider mStoryProvider;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public ProviderListener (Context context, StoryProvider storyProvider) {
        mContext = context;
        mStoryProvider = storyProvider;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean queryThings() {
        ValueEventListener thingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get theatre objects
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (dataSnapshot.getKey() != null && dataSnapshot.getKey().equals(DaoTheatreRepo.JSON_CONTAINER)) {
                        DaoTheatre daoTheatre = snapshot.getValue(DaoTheatre.class);
                        if (daoTheatre != null) {
                            Log.d(TAG, "queryThings onDataChange daoTheatre: " + daoTheatre.toString());
                            mStoryProvider.updateTheatre(daoTheatre, false);
                        }
                    }
                    else {
                        Log.e(TAG, "queryThings onDataChange unknown key: " + snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // get theatre object failed, log a message
                Log.e(TAG, "queryThings onCancelled: ", databaseError.toException());
                // ...
            }
        };

        Query mTheatreQuery = mStoryProvider.getTheatresReference().child(DaoTheatreRepo.JSON_CONTAINER).orderByChild("moniker");
        mTheatreQuery.addValueEventListener(thingsListener);

        return true;
    }
    public Boolean setTheatreListener() {
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
//                            DaoAudit daoAudit = mStoryProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
//                            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                                Log.d(TAG, "onDataChange daoTheatre (remote trigger): " + daoTheatre.toString());
//                                // update repo but not db
//                                mStoryProvider.updateTheatre(daoTheatre, false);
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
//                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // get theatre object failed, log a message
////                Log.e(TAG, "theatreListener:onCancelled", databaseError.toException());
//                Log.e(TAG, "theatreListener:onCancelled: " + databaseError.getMessage());
//                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // theatre level value event listener: dataSnapshot.getKey() "theatres"
//        mStoryProvider.getTheatresReference().addValueEventListener(valueEventListener);
////        mEpicsReference.addValueEventListener(valueEventListener);
////        mDatabaseReference.addValueEventListener(valueEventListener);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey() != null) {
                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                    if (daoTheatre != null) {
                        // if no recent local activity
                        DaoAudit daoAudit = mStoryProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                            Log.d(TAG, "onChildAdded daoTheatre (remote trigger): " + daoTheatre.toString());
                            // update repo but not db
                            mStoryProvider.updateTheatre(daoTheatre, false);
                        }
                        else {
                            Log.d(TAG, "onChildAdded: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
                        }
                    }
                    else {
                        Log.e(TAG, "onChildAdded: NULL daoTheatre?");
                    }
                } else {
                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
                String snapshotKey = dataSnapshot.getKey();
                if (dataSnapshot.getKey() != null && mStoryProvider.getDaoTheatreRepo().contains(dataSnapshot.getKey())) {
                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                    if (daoTheatre != null) {
                        // if no recent local activity
                        DaoAudit daoAudit = mStoryProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                            Log.d(TAG, "onChildChanged daoTheatre (remote trigger): " + daoTheatre.toString());
                            // update repo but not db
                            mStoryProvider.updateTheatre(daoTheatre, false);
                        }
                        else {
                            Log.d(TAG, "onChildChanged: daoTheatre (ignore|multiple local trigger): " + daoTheatre.toString());
                        }
                    }
                    else {
                        Log.e(TAG, "onChildChanged: NULL daoTheatre?");
                    }
                } else {
                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey() != null && mStoryProvider.getDaoTheatreRepo().contains(dataSnapshot.getKey())) {
                    DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
                    if (daoTheatre != null) {
                        // if no recent local activity
                        DaoAudit daoAudit = mStoryProvider.getDaoAuditRepo().get(daoTheatre.getMoniker());
                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                            Log.d(TAG, "onChildRemoved daoTheatre (remote trigger): " + daoTheatre.toString());
                            // remove from repo leaving db unchanged
                            mStoryProvider.removeTheatre(daoTheatre, false);
                        }
                        else {
                            Log.d(TAG, "onChildRemoved: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
                        }
                    }
                    else {
                        Log.e(TAG, "onChildRemoved: NULL daoTheatre?");
                    }
                } else {
                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "childEventListener onCancelled: " + databaseError.getMessage());
                Toast.makeText(mContext, "childEventListener onCancelled: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
            }
        };
        // theatre level child event listener: dataSnapshot.getKey() = "theatreXX"
        mStoryProvider.getTheatresReference().addChildEventListener(childEventListener);
        // database level child event listener: dataSnapshot.getKey() = "theatres"
        // missing remote RemoveChild notifications!
//        mDatabaseReference.addChildEventListener(childEventListener);
        return true;
    }
    public Boolean setEpicListener() {

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey() != null) {
                    DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
                    if (daoEpic != null) {
                        // if no recent local activity
                        DaoAudit daoAudit = mStoryProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                            Log.d(TAG, "onChildAdded daoEpic (remote trigger): " + daoEpic.toString());
                            // update repo but not db
                            mStoryProvider.updateEpic(daoEpic, false);
                        }
                        else {
                            Log.d(TAG, "onChildAdded: daoEpic (ignore local|multiple trigger): " + daoEpic.toString());
                        }
                    }
                    else {
                        Log.e(TAG, "onChildAdded: NULL daoEpic?");
                    }
                } else {
                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
                String snapshotKey = dataSnapshot.getKey();
                if (dataSnapshot.getKey() != null && mStoryProvider.getDaoEpicRepo().contains(dataSnapshot.getKey())) {
                    DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
                    if (daoEpic != null) {
                        // if no recent local activity
                        DaoAudit daoAudit = mStoryProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                            Log.d(TAG, "onChildChanged daoEpic (remote trigger): " + daoEpic.toString());
                            // update repo but not db
                            mStoryProvider.updateEpic(daoEpic, false);
                        }
                        else {
                            Log.d(TAG, "onChildChanged: daoEpic (ignore|multiple local trigger): " + daoEpic.toString());
                        }
                    }
                    else {
                        Log.e(TAG, "onChildChanged: NULL daoEpic?");
                    }
                } else {
                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey() != null && mStoryProvider.getDaoEpicRepo().contains(dataSnapshot.getKey())) {
                    DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
                    if (daoEpic != null) {
                        // if no recent local activity
                        DaoAudit daoAudit = mStoryProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
                        if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                            Log.d(TAG, "onChildRemoved daoEpic (remote trigger): " + daoEpic.toString());
                            // remove from repo leaving db unchanged
                            mStoryProvider.removeEpic(daoEpic, false);
                        }
                        else {
                            Log.d(TAG, "onChildRemoved: daoEpic (ignore local|multiple trigger): " + daoEpic.toString());
                        }
                    }
                    else {
                        Log.e(TAG, "onChildRemoved: NULL daoEpic?");
                    }
                } else {
                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "childEventListener onCancelled: " + databaseError.getMessage());
                Toast.makeText(mContext, "childEventListener onCancelled: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
                mStoryProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
            }
        };
        // Epic level child event listener: dataSnapshot.getKey() = "epicXX"
        mStoryProvider.getEpicsReference().addChildEventListener(childEventListener);
        return true;
    }

}
