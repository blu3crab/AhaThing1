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

import com.adaptivehandyapps.ahathing.PlayList;
import com.adaptivehandyapps.ahathing.PrefsUtils;
import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoBase;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoOutcomeRepo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

///////////////////////////////////////////////////////////////////////////
// Outcome Data Access Layer
public class DalOutcome {
    private static final String TAG = DalOutcome.class.getSimpleName();

    private Context mContext;
    private RepoProvider mRepoProvider;
    private PlayList mPlayList;
    private RepoProvider.OnRepoProviderRefresh mCallback = null; //call back interface

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFirebaseReference;
    private Boolean mIsFirebaseReady = false;

    private Class daoClass = DaoBase.class;
    private String signature = DaoDefs.INIT_STRING_MARKER;
    private String prefsKey = DaoDefs.INIT_STRING_MARKER;
    private int actorUpdateResId = R.string.actor_updateOutcome;
    private int actorRemoveResId = R.string.actor_removeOutcome;

    private DaoOutcomeRepo mDaoRepo;

//    private Boolean mReady = false;
//    private DaoOutcome mActiveDao;
//

    ///////////////////////////////////////////////////////////////////////////
    public DalOutcome(Context context, RepoProvider repoProvider, RepoProvider.OnRepoProviderRefresh callback) {
        mContext = context;
        mRepoProvider = repoProvider;
        mPlayList = mRepoProvider.getPlayList();
        mCallback = callback;

        // set class
        setDaoClass(DaoOutcome.class);
        // set firebase child signature
        setSignature(DaoOutcomeRepo.JSON_CONTAINER);
        // set prefs key
        setPrefsKey(PrefsUtils.ACTIVE_OUTCOME_KEY);
        // set audit actors
        setActorUpdateResId(R.string.actor_updateOutcome);
        setActorRemoveResId(R.string.actor_removeOutcome);
        // create Outcome repo
        mDaoRepo = new DaoOutcomeRepo();

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

    public DaoOutcomeRepo getDaoRepo() { return mDaoRepo; }
    private void setDaoRepo(DaoOutcomeRepo daoOutcomeRepo) {
        this.mDaoRepo = daoOutcomeRepo;
    }

//    public Boolean isReady() { return mReady;}
//
//    public DaoOutcome getActiveDao() { return mActiveDao; }
//    public void setActiveDao(DaoOutcome activeDao) {
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
    public Boolean update(DaoOutcome dao, Boolean updateDatabase) {
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
        // if no active object & this object matches prefs or no prefs
        String prefsActiveDao = PrefsUtils.getPrefs(mContext, getPrefsKey());
        if (mPlayList.getActiveOutcome() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            mPlayList.setActiveOutcome(dao);
        }

        // refresh
        if (mCallback != null) mCallback.onRepoProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean remove(DaoOutcome dao, Boolean updateDatabase) {
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

        // if removing active object
        if (mPlayList.getActiveOutcome().getMoniker().equals(dao.getMoniker())) {
            DaoOutcome daoReplacement = null;
            // if an object is defined, set as replacement
            if (getDaoRepo().size() > 0) daoReplacement = (DaoOutcome) getDaoRepo().get(0);
            // set or clear active object
            mPlayList.setActiveOutcome(daoReplacement);
        }
        // refresh
        if (mCallback != null) mCallback.onRepoProviderRefresh(true);

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
                Toast.makeText(mContext, "childEventListener onCancelled: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
                mRepoProvider.getDaoAuditRepo().postAudit(R.string.actor_onChildListener, R.string.action_cancelled, databaseError.getMessage());
            }
        };
        // Outcome level child event listener: dataSnapshot.getKey() = "OutcomeXX"
        getFirebaseReference().addChildEventListener(childEventListener);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotUpdate(DataSnapshot dataSnapshot) {
        DaoOutcome daoOutcome = dataSnapshot.getValue(DaoOutcome.class);
        if (daoOutcome != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoOutcome.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildAdded daoOutcome (remote trigger): " + daoOutcome.toString());
                // update repo but not db
                update(daoOutcome, false);
            }
            else {
                Log.d(TAG, "onChildAdded: daoOutcome (ignore local|multiple trigger): " + daoOutcome.toString());
            }
        }
        else {
            Log.e(TAG, "onChildAdded: NULL daoOutcome?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotRemove(DataSnapshot dataSnapshot) {
        DaoOutcome daoOutcome = dataSnapshot.getValue(DaoOutcome.class);
        if (daoOutcome != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoOutcome.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildRemoved daoOutcome (remote trigger): " + daoOutcome.toString());
                // remove from repo leaving db unchanged
                remove(daoOutcome, false);
            }
            else {
                Log.d(TAG, "onChildRemoved: daoOutcome (ignore local|multiple trigger): " + daoOutcome.toString());
            }
        }
        else {
            Log.e(TAG, "onChildRemoved: NULL daoOutcome?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
