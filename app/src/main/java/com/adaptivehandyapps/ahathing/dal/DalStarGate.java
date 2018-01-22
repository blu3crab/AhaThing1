/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker SEP 2017
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

import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.RepoProvider;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoBase;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoStarGate;
import com.adaptivehandyapps.ahathing.dao.DaoStarGateRepo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

///////////////////////////////////////////////////////////////////////////
// StarGate Data Access Layer
public class DalStarGate {
    private static final String TAG = DalStarGate.class.getSimpleName();

    private RepoProvider mRepoProvider;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFirebaseReference;
    private Boolean mIsFirebaseReady = false;

    private Class daoClass = DaoBase.class;
    private String signature = DaoDefs.INIT_STRING_MARKER;
    private int actorUpdateResId = R.string.actor_updateStarGate;
    private int actorRemoveResId = R.string.actor_removeStarGate;

    private DaoStarGateRepo mDaoRepo;

    ///////////////////////////////////////////////////////////////////////////
    public DalStarGate(RepoProvider repoProvider) {
        // retain parent repo provider
        mRepoProvider = repoProvider;

        // set class
        setDaoClass(DaoStarGate.class);
        // set firebase child signature
        setSignature(DaoStarGateRepo.JSON_CONTAINER);
        // set audit actors
        setActorUpdateResId(R.string.actor_updateStarGate);
        setActorRemoveResId(R.string.actor_removeStarGate);
        // create StarGate repo
        setDaoRepo(new DaoStarGateRepo());

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

    public DaoStarGateRepo getDaoRepo() { return mDaoRepo; }
    private void setDaoRepo(DaoStarGateRepo daoStarGateRepo) {
        this.mDaoRepo = daoStarGateRepo;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean update(DaoStarGate dao, Boolean updateDatabase) {
        Log.d(TAG, "update(updateDatabase = " + updateDatabase + "): dao " + dao.toString());
        // add or update repo with object
        getDaoRepo().set(dao);
        // post audit trail
        mRepoProvider.getDaoAuditRepo().postAudit(getActorUpdateResId(), R.string.action_set, dao.getMoniker());

        if (updateDatabase) {
            // post audit trail
            mRepoProvider.getDaoAuditRepo().postAudit(getActorUpdateResId(), R.string.action_child_setValue, dao.getMoniker());
            // update timestamp
            dao.setTimestamp((System.currentTimeMillis()));
            // update db
            mFirebaseReference.child(dao.getMoniker()).setValue(dao);
        }

        // TODO: do not refresh navmenu & stage
        if (mRepoProvider.getCallback() != null) mRepoProvider.getCallback().onRepoProviderRefresh(false);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean remove(DaoStarGate dao, Boolean updateDatabase) {
        Log.d(TAG, "remove(updateDatabase = " + updateDatabase + "): dao " + dao.toString());
        // remove object from repo
        getDaoRepo().remove(dao.getMoniker());
        Log.d(TAG, "remove removed dao: " + dao.getMoniker());
        // post audit trail
        mRepoProvider.getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_remove, dao.getMoniker());
        mRepoProvider.getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_remove, dao.getMoniker());

        if (updateDatabase) {
            // post audit trail
            mRepoProvider.getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_child_removeValue, dao.getMoniker());
            mRepoProvider.getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_child_removeValue, dao.getMoniker());
            // remove object from remote db
            mFirebaseReference.child(dao.getMoniker()).removeValue();
        }

        // refresh
        if (mRepoProvider.getCallback() != null) mRepoProvider.getCallback().onRepoProviderRefresh(true);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean setListener() {
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange:" + dataSnapshot.getKey());
//                // get StarGate object and use the values to update the UI
//                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    if (dataSnapshot.getKey() != null && dataSnapshot.getKey().equals(DaoStarGateRepo.JSON_CONTAINER)) {
//                        DaoStarGate daoStarGate = snapshot.getValue(DaoStarGate.class);
//                        if (daoStarGate != null) {
//                            // if no recent local activity
//                            DaoAudit daoAudit = MainActivity.getRepoProviderInstance().getDaoAuditRepo().get(daoStarGate.getMoniker());
//                            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
//                                Log.d(TAG, "onDataChange daoStarGate (remote trigger): " + daoStarGate.toString());
//                                // update repo but not db
//                                MainActivity.getRepoProviderInstance().update(daoStarGate, false);
//                            }
//                            else {
//                                Log.d(TAG, "onDataChange: daoStarGate (ignore local|multiple trigger): " + daoStarGate.toString());
//                            }
//                        }
//                        else {
//                            Log.e(TAG, "onDataChange: NULL daoStarGate?");
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
//                // get StarGate object failed, log a message
////                Log.e(TAG, "StarGateListener:onCancelled", databaseError.toException());
//                Log.e(TAG, "StarGateListener:onCancelled: " + databaseError.getMessage());
//                MainActivity.getRepoProviderInstance().getDaoAuditRepo().postAudit(R.string.actor_onDataChange, R.string.action_cancelled, databaseError.getMessage());
//            }
//        };
//        // StarGate level value event listener: dataSnapshot.getKey() "StarGates"
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
                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildAdded, R.string.action_listen, dataSnapshot.getKey());
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
                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildChanged, R.string.action_listen, dataSnapshot.getKey());
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
                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildRemoved, R.string.action_listen, dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "childEventListener onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "childEventListener onCancelled: " + databaseError.getMessage());
                Toast.makeText(mRepoProvider.getContext(), "childEventListener onCancelled: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
            }
        };
        // StarGate level child event listener: dataSnapshot.getKey() = "StarGateXX"
        getFirebaseReference().addChildEventListener(childEventListener);
        // database level child event listener: dataSnapshot.getKey() = "StarGates"
        // missing remote RemoveChild notifications!
//        mDatabaseReference.addChildEventListener(childEventListener);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotUpdate(DataSnapshot dataSnapshot) {
        DaoStarGate daoStarGate = dataSnapshot.getValue(DaoStarGate.class);
        if (daoStarGate != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStarGate.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildAdded daoStarGate (remote trigger): " + daoStarGate.toString());
                // update repo but not db
                update(daoStarGate, false);
            }
            else {
                Log.d(TAG, "onChildAdded: daoStarGate (ignore local|multiple trigger): " + daoStarGate.toString());
            }
        }
        else {
            Log.e(TAG, "onChildAdded: NULL daoStarGate?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotRemove(DataSnapshot dataSnapshot) {
        DaoStarGate daoStarGate = dataSnapshot.getValue(DaoStarGate.class);
        if (daoStarGate != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoStarGate.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildRemoved daoStarGate (remote trigger): " + daoStarGate.toString());
                // remove from repo leaving db unchanged
                remove(daoStarGate, false);
            }
            else {
                Log.d(TAG, "onChildRemoved: daoStarGate (ignore local|multiple trigger): " + daoStarGate.toString());
            }
        }
        else {
            Log.e(TAG, "onChildRemoved: NULL daoStarGate?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean queryThings() {
        ValueEventListener thingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get StarGate objects
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (dataSnapshot.getKey() != null && dataSnapshot.getKey().equals(DaoStarGateRepo.JSON_CONTAINER)) {
                        DaoStarGate daoStarGate = snapshot.getValue(DaoStarGate.class);
                        if (daoStarGate != null) {
                            Log.d(TAG, "queryThings onDataChange daoStarGate: " + daoStarGate.toString());
                            update(daoStarGate, false);
                        }
                    }
                    else {
                        Log.e(TAG, "queryThings onDataChange unknown key: " + snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // get StarGate object failed, log a message
                Log.e(TAG, "queryThings onCancelled: ", databaseError.toException());
                // ...
            }
        };

        Query mStarGateQuery = getFirebaseReference().child(DaoStarGateRepo.JSON_CONTAINER).orderByChild("moniker");
        mStarGateQuery.addValueEventListener(thingsListener);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
}
