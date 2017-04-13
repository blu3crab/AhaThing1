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

import android.content.Context;
import android.util.Log;

import com.adaptivehandyapps.ahathing.StageModelRing;
import com.adaptivehandyapps.ahathing.dao.DaoAuditRepo;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//
// Created by mat on 1/20/2017.
//
///////////////////////////////////////////////////////////////////////////
// Repository provider
public class RepoProvider {
    private static final String TAG = "RepoProvider";

    private Context mContext;
    private OnRepoProviderRefresh mCallback = null; //call back interface

    private RepoProvider mRepoProvider;

    // data access layer
    private DalTheatre mDalTheatre;
    private DalEpic mDalEpic;
    private DalStory mDalStory;
    private DalStage mDalStage;
    private DalActor mDalActor;
    private DalAction mDalAction;
    private DalOutcome mDalOutcome;

    private DaoAuditRepo mDaoAuditRepo;

    // firebase
    private String mUserId = DaoDefs.INIT_STRING_MARKER;
    private Boolean mIsFirebaseReady = false;
    private DatabaseReference mDatabaseReference;

    // TODO: refactor to DalStage?
    private StageModelRing mStageModelRing;

    ///////////////////////////////////////////////////////////////////////////
    // callback interface when model changes should trigger refresh
    public interface OnRepoProviderRefresh {
        void onRepoProviderRefresh(Boolean refresh);
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public RepoProvider(Context context, OnRepoProviderRefresh callback) {
        // retain context & callback
        mContext = context;
        mCallback = callback;
        mRepoProvider = this;

        // instantiate DAL object
        mDalTheatre = new DalTheatre(mContext, mRepoProvider, mCallback);
        mDalEpic = new DalEpic(mContext, mRepoProvider, mCallback);
        mDalStory = new DalStory(mContext, mRepoProvider, mCallback);
        mDalStage = new DalStage(mContext, mRepoProvider, mCallback);
        mDalActor = new DalActor(mContext, mRepoProvider, mCallback);
        mDalAction = new DalAction(mContext, mRepoProvider, mCallback);
        mDalOutcome = new DalOutcome(mContext, mRepoProvider, mCallback);

        // establish firebase reference
        setFirebaseReference();

        if (!isFirebaseReady()) {
            Log.e(TAG, "Firebase NOT ready.");
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // get user id
            mUserId = getUid();

            // establish listeners
            mDalTheatre.setListener();
            mDalEpic.setListener();
            mDalStory.setListener();
            mDalStage.setListener();
            mDalActor.setListener();
            mDalAction.setListener();
            mDalOutcome.setListener();
        }
        else {
            mIsFirebaseReady = false;
        }
        Log.d(TAG, "Firebase ready: " + isFirebaseReady() + ", UserId " + mUserId);

        // create Audit repo
        mDaoAuditRepo = new DaoAuditRepo(mContext);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    public DalTheatre getDalTheatre() {
        return mDalTheatre;
    }
    public void setDalTheatre(DalTheatre dalTheatre) {
        this.mDalTheatre = dalTheatre;
    }

    public DalEpic getDalEpic() {
        return mDalEpic;
    }
    public void setDalEpic(DalEpic dalEpic) {
        this.mDalEpic = dalEpic;
    }

    public DalStory getDalStory() {
        return mDalStory;
    }
    public void setDalStory(DalStory dalStory) {
        this.mDalStory = dalStory;
    }

    public DalStage getDalStage() {
        return mDalStage;
    }
    public void setDalStage(DalStage dalStage) {
        this.mDalStage = dalStage;
    }

    public DalActor getDalActor() {
        return mDalActor;
    }
    public void setDalActor(DalActor dalActor) {
        this.mDalActor = dalActor;
    }

    public DalAction getDalAction() {
        return mDalAction;
    }
    public void setDalAction(DalAction dalAction) {
        this.mDalAction = dalAction;
    }

    public DalOutcome getDalOutcome() {
        return mDalOutcome;
    }
    public void setDalOutcome(DalOutcome dalOutcome) {
        this.mDalOutcome = dalOutcome;
    }

    public DaoAuditRepo getDaoAuditRepo() { return mDaoAuditRepo; }

    // TODO: refactor to DalStage?
    public StageModelRing getStageModelRing() {
        return mStageModelRing;
    }
    public void setStageModelRing(StageModelRing stageModelRing) {
        this.mStageModelRing = stageModelRing;
    }

    ///////////////////////////////////////////////////////////////////////////
    // firebase helpers
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private Boolean setFirebaseReference() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (mDatabaseReference != null) {
            mIsFirebaseReady = true;
        }
        return mIsFirebaseReady;
    }
    public Boolean isFirebaseReady() {
        return mIsFirebaseReady;
    }
    public DatabaseReference getFirebaseReference() {
        return mDatabaseReference;
    }

    public Boolean removeFirebaseListener() {
        // TODO: remove listeners
        return true;
    }
///////////////////////////////////////////////////////////////////////////
}
