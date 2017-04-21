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
//
// Created by mat on 4/6/2017.
//

import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.MainActivity;
import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.RepoProvider;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoBase;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicRepo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

///////////////////////////////////////////////////////////////////////////
// Epic Data Access Layer
public class DalEpic {
    private static final String TAG = DalEpic.class.getSimpleName();

    private RepoProvider mRepoProvider;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFirebaseReference;
    private Boolean mIsFirebaseReady = false;

    private Class daoClass = DaoBase.class;
    private String signature = DaoDefs.INIT_STRING_MARKER;
    private int actorUpdateResId = R.string.actor_updateEpic;
    private int actorRemoveResId = R.string.actor_removeEpic;

    private DaoEpicRepo mDaoRepo;

    ///////////////////////////////////////////////////////////////////////////
    public DalEpic(RepoProvider repoProvider) {
        // retain parent repo provider
        mRepoProvider = repoProvider;

        // set class
        setDaoClass(DaoEpic.class);
        // set firebase child signature
        setSignature(DaoEpicRepo.JSON_CONTAINER);
        // set audit actors
        setActorUpdateResId(R.string.actor_updateEpic);
        setActorRemoveResId(R.string.actor_removeEpic);

        // create Epic repo
        setDaoRepo(new DaoEpicRepo());

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

    public DaoEpicRepo getDaoRepo() { return mDaoRepo; }
    private void setDaoRepo(DaoEpicRepo daoEpicRepo) {
        this.mDaoRepo = daoEpicRepo;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean update(DaoEpic dao, Boolean updateDatabase) {
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

        // update playlist to maintain coherence
//        MainActivity.getPlayListInstance().updateActiveEpic(dao);
        if (mRepoProvider.getPlayListService() != null) {
            mRepoProvider.getPlayListService().updateActiveEpic(dao);
            Log.d(TAG, mRepoProvider.getPlayListService().hierarchyToString());
        }
        else {
            Log.e(TAG, "oops! update finds PlayListService NULL.");
        }

        // refresh
        if (mRepoProvider.getCallback() != null) mRepoProvider.getCallback().onRepoProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean remove(DaoEpic dao, Boolean updateDatabase) {
        Log.d(TAG, "remove(updateDatabase = " + updateDatabase + "): dao " + dao.toString());
        // remove object from repo
        getDaoRepo().remove(dao.getMoniker());
        Log.d(TAG, "remove removed dao: " + dao.getMoniker());
        // post audit trail
        mRepoProvider.getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_remove, dao.getMoniker());

        if (updateDatabase) {
            // post audit trail
            mRepoProvider.getDaoAuditRepo().postAudit(getActorRemoveResId(), R.string.action_child_removeValue, dao.getMoniker());
            // remove object from remote db
            mFirebaseReference.child(dao.getMoniker()).removeValue();
        }

        // update playlist if removing active object
//        MainActivity.getPlayListInstance().removeActiveEpic(dao);
        if (mRepoProvider.getPlayListService() != null) {
            mRepoProvider.getPlayListService().removeActiveEpic(dao);
            Log.d(TAG, mRepoProvider.getPlayListService().hierarchyToString());
        }
        else {
            Log.e(TAG, "oops! remove finds PlayListService NULL.");
        }

        // refresh
        if (mRepoProvider.getCallback() != null) mRepoProvider.getCallback().onRepoProviderRefresh(true);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean setListener() {

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
        // Epic level child event listener: dataSnapshot.getKey() = "EpicXX"
        getFirebaseReference().addChildEventListener(childEventListener);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotUpdate(DataSnapshot dataSnapshot) {
        DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
        if (daoEpic != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildAdded daoEpic (remote trigger): " + daoEpic.toString());
                // update repo but not db
                update(daoEpic, false);
            }
            else {
                Log.d(TAG, "onChildAdded: daoEpic (ignore local|multiple trigger): " + daoEpic.toString());
            }
        }
        else {
            Log.e(TAG, "onChildAdded: NULL daoEpic?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotRemove(DataSnapshot dataSnapshot) {
        DaoEpic daoEpic = dataSnapshot.getValue(DaoEpic.class);
        if (daoEpic != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoEpic.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildRemoved daoEpic (remote trigger): " + daoEpic.toString());
                // remove from repo leaving db unchanged
                remove(daoEpic, false);
            }
            else {
                Log.d(TAG, "onChildRemoved: daoEpic (ignore local|multiple trigger): " + daoEpic.toString());
            }
        }
        else {
            Log.e(TAG, "onChildRemoved: NULL daoEpic?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
