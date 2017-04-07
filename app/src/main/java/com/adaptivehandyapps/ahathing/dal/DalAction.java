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

import com.adaptivehandyapps.ahathing.PrefsUtils;
import com.adaptivehandyapps.ahathing.R;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActionRepo;
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
// Action Data Access Layer
public class DalAction {
    private static final String TAG = DalAction.class.getSimpleName();

    private Context mContext;
    private RepoProvider mRepoProvider;
    private RepoProvider.OnRepoProviderRefresh mCallback = null; //call back interface

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFirebaseReference;
    private Boolean mIsFirebaseReady = false;

    private Class daoClass = DaoBase.class;
    private String signature = DaoDefs.INIT_STRING_MARKER;
    private String prefsKey = DaoDefs.INIT_STRING_MARKER;
    private int actorUpdateResId = R.string.actor_updateAction;
    private int actorRemoveResId = R.string.actor_removeAction;

    private DaoActionRepo mDaoRepo;

    private Boolean mReady = false;
    private DaoAction mActiveDao;


    ///////////////////////////////////////////////////////////////////////////
    public DalAction(Context context, RepoProvider repoProvider, RepoProvider.OnRepoProviderRefresh callback) {
        mContext = context;
        mRepoProvider = repoProvider;
        mCallback = callback;

        // set class
        setDaoClass(DaoAction.class);
        // set firebase child signature
        setSignature(DaoActionRepo.JSON_CONTAINER);
        // set prefs key
        setPrefsKey(PrefsUtils.ACTIVE_ACTION_KEY);
        // set audit actors
        setActorUpdateResId(R.string.actor_updateAction);
        setActorRemoveResId(R.string.actor_removeAction);
        // create Action repo
        mDaoRepo = new DaoActionRepo();

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

    public DaoActionRepo getDaoRepo() { return mDaoRepo; }
    private void setDaoRepo(DaoActionRepo daoActionRepo) {
        this.mDaoRepo = daoActionRepo;
    }

    public Boolean isReady() { return mReady;}

    public DaoAction getActiveDao() { return mActiveDao; }
    public void setActiveDao(DaoAction activeDao) {
        mReady = false;
        // if setting active object
        if (activeDao != null) {
            // set object ready & set prefs
            mReady = true;
            PrefsUtils.setPrefs(mContext, getPrefsKey(), activeDao.getMoniker());
        }
        else {
            // clear active object
            PrefsUtils.setPrefs(mContext, getPrefsKey(), DaoDefs.INIT_STRING_MARKER);
        }
        this.mActiveDao = activeDao;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean update(DaoAction dao, Boolean updateDatabase) {
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
        if (getActiveDao() == null &&
                (prefsActiveDao.equals(dao.getMoniker()) || prefsActiveDao.equals(DaoDefs.INIT_STRING_MARKER))) {
            // set active to updated object
            setActiveDao(dao);
        }

        // refresh
        if (mCallback != null) mCallback.onRepoProviderRefresh(true);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean remove(DaoAction dao, Boolean updateDatabase) {
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
        if (getActiveDao().getMoniker().equals(dao.getMoniker())) {
            // if an object is defined
            if (getDaoRepo().get(0) != null) {
                // set active object
                setActiveDao((DaoAction) getDaoRepo().get(0));
            }
            else {
                // clear active object
                setActiveDao(null);
            }
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
        // Action level child event listener: dataSnapshot.getKey() = "ActionXX"
        getFirebaseReference().addChildEventListener(childEventListener);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotUpdate(DataSnapshot dataSnapshot) {
        DaoAction daoAction = dataSnapshot.getValue(DaoAction.class);
        if (daoAction != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoAction.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildAdded daoAction (remote trigger): " + daoAction.toString());
                // update repo but not db
                update(daoAction, false);
            }
            else {
                Log.d(TAG, "onChildAdded: daoAction (ignore local|multiple trigger): " + daoAction.toString());
            }
        }
        else {
            Log.e(TAG, "onChildAdded: NULL daoAction?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean snapshotRemove(DataSnapshot dataSnapshot) {
        DaoAction daoAction = dataSnapshot.getValue(DaoAction.class);
        if (daoAction != null) {
            // if no recent local activity
            DaoAudit daoAudit = mRepoProvider.getDaoAuditRepo().get(daoAction.getMoniker());
            if (daoAudit == null || !daoAudit.isRecent(System.currentTimeMillis())) {
                Log.d(TAG, "onChildRemoved daoAction (remote trigger): " + daoAction.toString());
                // remove from repo leaving db unchanged
                remove(daoAction, false);
            }
            else {
                Log.d(TAG, "onChildRemoved: daoAction (ignore local|multiple trigger): " + daoAction.toString());
            }
        }
        else {
            Log.e(TAG, "onChildRemoved: NULL daoAction?");
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
