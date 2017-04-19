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
import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoAudit;
import com.adaptivehandyapps.ahathing.dao.DaoBase;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicRepo;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStageRepo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

///////////////////////////////////////////////////////////////////////////
// Stage Data Access Layer
public class DalStage {
    private static final String TAG = DalStage.class.getSimpleName();

//    private RepoProvider mRepoProvider;
//    private PlayList mPlayList;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFirebaseReference;
    private Boolean mIsFirebaseReady = false;

    private Class daoClass = DaoBase.class;
    private String signature = DaoDefs.INIT_STRING_MARKER;
    private String prefsKey = DaoDefs.INIT_STRING_MARKER;
    private int actorUpdateResId = R.string.actor_updateStage;
    private int actorRemoveResId = R.string.actor_removeStage;

    private DaoStageRepo mDaoRepo;

//    private Boolean mReady = false;
//    private DaoStage mActiveDao;
//

    ///////////////////////////////////////////////////////////////////////////
    public DalStage() {
//        mRepoProvider = MainActivity.getRepoProviderInstance();
//        mPlayList = MainActivity.getPlayListInstance();

        // set class
        setDaoClass(DaoStage.class);
        // set firebase child signature
        setSignature(DaoStageRepo.JSON_CONTAINER);
        // set prefs key
        setPrefsKey(PrefsUtils.ACTIVE_STAGE_KEY);
        // set audit actors
        setActorUpdateResId(R.string.actor_updateStage);
        setActorRemoveResId(R.string.actor_removeStage);
        // create Stage repo
        mDaoRepo = new DaoStageRepo();

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

    public DaoStageRepo getDaoRepo() { return mDaoRepo; }
    private void setDaoRepo(DaoStageRepo daoStageRepo) {
        this.mDaoRepo = daoStageRepo;
    }

//    public Boolean isReady() { return mReady;}
//    public void setReady(Boolean ready) {
//        this.mReady = ready;
//    }
//
//    public DaoStage getActiveDao() { return mActiveDao; }
//    public void setActiveDao(DaoStage activeDao) {
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
    public Boolean update(DaoStage dao, Boolean updateDatabase) {
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
        if (MainActivity.getPlayListInstance().updateActiveStage(dao)) {
            if (MainActivity.getRepoProviderInstance().getStageModelRing() == null) {
                // TODO: single stage model - build stage model per stage
                MainActivity.getRepoProviderInstance().setStageModelRing(new StageModelRing());
                Integer ringMax = 4;
                MainActivity.getRepoProviderInstance().getStageModelRing().buildModel(dao, ringMax);
                Log.d(TAG, "NEW StageModelRing for repo " + MainActivity.getRepoProviderInstance().toString() + " at " + MainActivity.getRepoProviderInstance().getStageModelRing().toString());
            }
        }
//        // if no active object & this object matches prefs or no prefs
//        String prefsActiveDao = PrefsUtils.getPrefs(MainActivity.getRepoProviderInstance().getContext(), getPrefsKey());
//        if (MainActivity.getPlayListInstance().getActiveStage() == null &&
//                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
//            if (MainActivity.getRepoProviderInstance().getStageModelRing() == null) {
//                // TODO: single stage model - build stage model per stage
//                MainActivity.getRepoProviderInstance().setStageModelRing(new StageModelRing());
//                Integer ringMax = 4;
//                MainActivity.getRepoProviderInstance().getStageModelRing().buildModel(dao, ringMax);
//                Log.d(TAG, "NEW StageModelRing for repo " + MainActivity.getRepoProviderInstance().toString() + " at " + MainActivity.getRepoProviderInstance().getStageModelRing().toString());
//            }
//            // set updated object active
//            MainActivity.getPlayListInstance().setActiveStage(dao);
//        }

        // refresh
        if (MainActivity.getRepoProviderInstance().getCallback() != null) {
            MainActivity.getRepoProviderInstance().getCallback().onRepoProviderRefresh(true);
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean remove(DaoStage dao, Boolean updateDatabase) {
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
        MainActivity.getPlayListInstance().removeActiveStage(dao);
//        // if removing active object
//        if (MainActivity.getPlayListInstance().getActiveStage().getMoniker().equals(dao.getMoniker())) {
//            DaoStage daoReplacement = null;
//            // if an object is defined, set as replacement
//            if (getDaoRepo().size() > 0) daoReplacement = (DaoStage) getDaoRepo().get(0);
//            // set or clear active object
//            MainActivity.getPlayListInstance().setActiveStage(daoReplacement);
//        }
        // refresh
        if (MainActivity.getRepoProviderInstance().getCallback() != null) {
            MainActivity.getRepoProviderInstance().getCallback().onRepoProviderRefresh(true);
        }

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
        // Stage level child event listener: dataSnapshot.getKey() = "StageXX"
        getFirebaseReference().addChildEventListener(childEventListener);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotUpdate(DataSnapshot dataSnapshot) {
        DaoStage daoStage = dataSnapshot.getValue(DaoStage.class);
        if (daoStage != null) {
            // if no recent local activity
            DaoAudit daoAudit = MainActivity.getRepoProviderInstance().getDaoAuditRepo().get(daoStage.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildAdded daoStage (remote trigger): " + daoStage.toString());
                // update repo but not db
                update(daoStage, false);
            }
            else {
                Log.d(TAG, "onChildAdded: daoStage (ignore local|multiple trigger): " + daoStage.toString());
            }
        }
        else {
            Log.e(TAG, "onChildAdded: NULL daoStage?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotRemove(DataSnapshot dataSnapshot) {
        DaoStage daoStage = dataSnapshot.getValue(DaoStage.class);
        if (daoStage != null) {
            // if no recent local activity
            DaoAudit daoAudit = MainActivity.getRepoProviderInstance().getDaoAuditRepo().get(daoStage.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildRemoved daoStage (remote trigger): " + daoStage.toString());
                // remove from repo leaving db unchanged
                remove(daoStage, false);
            }
            else {
                Log.d(TAG, "onChildRemoved: daoStage (ignore local|multiple trigger): " + daoStage.toString());
            }
        }
        else {
            Log.e(TAG, "onChildRemoved: NULL daoStage?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
