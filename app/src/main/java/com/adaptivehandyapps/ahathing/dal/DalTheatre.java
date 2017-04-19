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
 */package com.adaptivehandyapps.ahathing.dal;
//
// Created by mat on 4/6/2017.
//

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.MainActivity;
import com.adaptivehandyapps.ahathing.PlayList;
import com.adaptivehandyapps.ahathing.PrefsUtils;
import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoBase;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.adaptivehandyapps.ahathing.dao.DaoTheatreRepo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

///////////////////////////////////////////////////////////////////////////
// Theatre Data Access Layer
public class DalTheatre {
    private static final String TAG = DalTheatre.class.getSimpleName();

//    private Context mContext;
//    private RepoProvider mRepoProvider;
//    private PlayList mPlayList;
//    private RepoProvider.OnRepoProviderRefresh mCallback = null; //call back interface

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFirebaseReference;
    private Boolean mIsFirebaseReady = false;

    private Class daoClass = DaoBase.class;
    private String signature = DaoDefs.INIT_STRING_MARKER;
    private String prefsKey = DaoDefs.INIT_STRING_MARKER;
    private int actorUpdateResId = R.string.actor_updateTheatre;
    private int actorRemoveResId = R.string.actor_removeTheatre;

    private DaoTheatreRepo mDaoRepo;

//    private Boolean mReady = false;
//    private DaoTheatre mActiveDao;
//

    ///////////////////////////////////////////////////////////////////////////
    public DalTheatre() {
//    public DalTheatre(Context context, RepoProvider repoProvider, RepoProvider.OnRepoProviderRefresh callback) {
//        mContext = context;
//        mRepoProvider = MainActivity.getRepoProviderInstance();
//        mPlayList = MainActivity.getPlayListInstance();
//        mCallback = callback;

        // set class
        setDaoClass(DaoTheatre.class);
        // set firebase child signature
        setSignature(DaoTheatreRepo.JSON_CONTAINER);
        // set prefs key
        setPrefsKey(PrefsUtils.ACTIVE_THEATRE_KEY);
        // set audit actors
        setActorUpdateResId(R.string.actor_updateTheatre);
        setActorRemoveResId(R.string.actor_removeTheatre);
        // create theatre repo
        mDaoRepo = new DaoTheatreRepo();

        // set firebase reference
        setFirebaseReference();

    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    public Boolean isFirebaseReady() {
        return mIsFirebaseReady;
    }
    public DatabaseReference getFirebaseReference() {
        return mFirebaseReference;
    }
    private Boolean setFirebaseReference() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (mDatabaseReference != null) {
            mIsFirebaseReady = true;
            mFirebaseReference = FirebaseDatabase.getInstance().getReference().child(getSignature());
        }
        return mIsFirebaseReady;
    }

    public Class getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(Class daoClass) {
        this.daoClass = daoClass;
    }

    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPrefsKey() {
        return prefsKey;
    }
    public void setPrefsKey(String prefsKey) {
        this.prefsKey = prefsKey;
    }

    public int getActorUpdateResId() {
        return actorUpdateResId;
    }
    public void setActorUpdateResId(int actorUpdateResId) {
        this.actorUpdateResId = actorUpdateResId;
    }

    public int getActorRemoveResId() {
        return actorRemoveResId;
    }
    public void setActorRemoveResId(int actorRemoveResId) {
        this.actorRemoveResId = actorRemoveResId;
    }

    public DaoTheatreRepo getDaoRepo() { return mDaoRepo; }
    private void setDaoRepo(DaoTheatreRepo daoTheatreRepo) {
        this.mDaoRepo = daoTheatreRepo;
    }

//    public Boolean isReady() { return mReady;}
//
//    public DaoTheatre getActiveTheatre() { return mActiveDao; }
//    public void setActiveDao(DaoTheatre activeDao) {
//        mReady = false;
//        // if setting active object
//        if (activeDao != null) {
//            // set object ready & set prefs
//            mReady = true;
//            PrefsUtils.setPrefs(mContext, getPrefsKey(), activeDao.getMoniker());
//        }
//        else {
//            // clear active object
//            PrefsUtils.setPrefs(mContext, getPrefsKey(), DaoDefs.INIT_STRING_MARKER);
//        }
//        this.mActiveDao = activeDao;
//    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean update(DaoTheatre dao, Boolean updateDatabase) {
        Log.d(TAG, "update(updateDatabase = " + updateDatabase + "): dao " + dao.toString());
        // add or update repo with object
        getDaoRepo().set(dao);
        // post audit trail
        MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(getActorUpdateResId(), R.string.action_set, dao.getMoniker());

        if (updateDatabase) {
            // post audit trail
            MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(getActorUpdateResId(), R.string.action_child_setValue, dao.getMoniker());
            // update timestamp
            dao.setTimestamp((System.currentTimeMillis()));
            // update db
            mFirebaseReference.child(dao.getMoniker()).setValue(dao);
        }
        // update playlist to maintain coherence
        MainActivity.getPlayListInstance().updateActiveTheatre(dao);
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), getPrefsKey());
//        if (MainActivity.getPlayListInstance().getActiveTheatre() == null &&
//                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
//            // set active to updated object
//            MainActivity.getPlayListInstance().setActiveTheatre(dao);
//        }

        // refresh
        if (MainActivity.getRepoProviderInstance().getCallback() != null) {
            MainActivity.getRepoProviderInstance().getCallback().onRepoProviderRefresh(true);
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean remove(DaoTheatre dao, Boolean updateDatabase) {
        Log.d(TAG, "remove(updateDatabase = " + updateDatabase + "): dao " + dao.toString());
        // remove object from repo
        getDaoRepo().remove(dao.getMoniker());
        Log.d(TAG, "remove removed dao: " + dao.getMoniker());
        // post audit trail
        MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_remove, dao.getMoniker());

        if (updateDatabase) {
            // post audit trail
            MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_child_removeValue, dao.getMoniker());
            // remove object from remote db
            mFirebaseReference.child(dao.getMoniker()).removeValue();
        }

        // if removing active object
        MainActivity.getPlayListInstance().removeActiveTheatre(dao);
//        if (MainActivity.getPlayListInstance().getActiveTheatre().getMoniker().equals(dao.getMoniker())) {
//            DaoTheatre daoReplacement = null;
//            // if an object is defined, set as replacement
//            if (getDaoRepo().size() > 0) daoReplacement = (DaoTheatre) getDaoRepo().get(0);
//            // set or clear active object
//            MainActivity.getPlayListInstance().setActiveTheatre(daoReplacement);
//        }
        // refresh
//        if (mCallback != null) mCallback.onRepoProviderRefresh(true);
        if (MainActivity.getRepoProviderInstance().getCallback() != null) {
            MainActivity.getRepoProviderInstance().getCallback().onRepoProviderRefresh(true);
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean setListener() {
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
//                            DaoAudit daoAudit = MainActivity.getRepoProviderInstance().getDaoAuditRepo().get(daoTheatre.getMoniker());
//                            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                                Log.d(TAG, "onDataChange daoTheatre (remote trigger): " + daoTheatre.toString());
//                                // update repo but not db
//                                MainActivity.getRepoProviderInstance().update(daoTheatre, false);
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
//                MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_listen, dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // get theatre object failed, log a message
////                Log.e(TAG, "theatreListener:onCancelled", databaseError.toException());
//                Log.e(TAG, "theatreListener:onCancelled: " + databaseError.getMessage());
//                MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // theatre level value event listener: dataSnapshot.getKey() "theatres"
//        MainActivity.getRepoProviderInstance().getFirebaseReference().addValueEventListener(valueEventListener);
////        mEpicsReference.addValueEventListener(valueEventListener);
////        mDatabaseReference.addValueEventListener(valueEventListener);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey() != null) {
                    snapshotUpdate(dataSnapshot);
                } else {
                    Log.e(TAG, "childEventListener onChildAdded unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildChanged key = " + dataSnapshot.getKey());
                String snapshotKey = dataSnapshot.getKey();
                if (dataSnapshot.getKey() != null && getDaoRepo().contains(dataSnapshot.getKey())) {
                    snapshotUpdate(dataSnapshot);
                } else {
                    Log.e(TAG, "childEventListener onChildChanged unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "childEventListener onChildRemoved:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey() != null && getDaoRepo().contains(dataSnapshot.getKey())) {
                    snapshotRemove(dataSnapshot);
                } else {
                    Log.e(TAG, "childEventListener onChildRemoved unknown key: " + dataSnapshot.getKey());
                }
                // post audit trail
                MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "childEventListener onCancelled: " + databaseError.getMessage());
                Toast.makeText(MainActivity.getRepoProviderInstance().getContext(), "childEventListener onCancelled: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
                MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
            }
        };
        // theatre level child event listener: dataSnapshot.getKey() = "theatreXX"
        getFirebaseReference().addChildEventListener(childEventListener);
        // database level child event listener: dataSnapshot.getKey() = "theatres"
        // missing remote RemoveChild notifications!
//        mDatabaseReference.addChildEventListener(childEventListener);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotUpdate(DataSnapshot dataSnapshot) {
        DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
        if (daoTheatre != null) {
            // if no recent local activity
            DaoAudit daoAudit = MainActivity.getRepoProviderInstance().getDaoAuditRepo().get(daoTheatre.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildAdded daoTheatre (remote trigger): " + daoTheatre.toString());
                // update repo but not db
                update(daoTheatre, false);
            }
            else {
                Log.d(TAG, "onChildAdded: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
            }
        }
        else {
            Log.e(TAG, "onChildAdded: NULL daoTheatre?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotRemove(DataSnapshot dataSnapshot) {
        DaoTheatre daoTheatre = dataSnapshot.getValue(DaoTheatre.class);
        if (daoTheatre != null) {
            // if no recent local activity
            DaoAudit daoAudit = MainActivity.getRepoProviderInstance().getDaoAuditRepo().get(daoTheatre.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildRemoved daoTheatre (remote trigger): " + daoTheatre.toString());
                // remove from repo leaving db unchanged
                remove(daoTheatre, false);
            }
            else {
                Log.d(TAG, "onChildRemoved: daoTheatre (ignore local|multiple trigger): " + daoTheatre.toString());
            }
        }
        else {
            Log.e(TAG, "onChildRemoved: NULL daoTheatre?");
        }

        return true;
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
                            update(daoTheatre, false);
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

        Query mTheatreQuery = getFirebaseReference().child(DaoTheatreRepo.JSON_CONTAINER).orderByChild("moniker");
        mTheatreQuery.addValueEventListener(thingsListener);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
}
