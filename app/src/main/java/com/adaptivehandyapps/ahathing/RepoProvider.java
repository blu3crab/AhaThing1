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
package com.adaptivehandyapps.ahathing;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.adaptivehandyapps.ahathing.dal.DalAction;
import com.adaptivehandyapps.ahathing.dal.DalActor;
import com.adaptivehandyapps.ahathing.dal.DalEpic;
import com.adaptivehandyapps.ahathing.dal.DalOutcome;
import com.adaptivehandyapps.ahathing.dal.DalStage;
import com.adaptivehandyapps.ahathing.dal.DalStory;
import com.adaptivehandyapps.ahathing.dal.DalTheatre;
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
public class RepoProvider extends Service {
//    public class RepoProvider{
    private static final String TAG = RepoProvider.class.getSimpleName();

    private Context mContext;
    private OnRepoProviderRefresh mCallback = null; //call back interface

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
    ///////////////////////////////////////////////////////////////////////////
    // playlist service
    PlayListService mPlayListService;
//    boolean mPlayListBound = false;
//
//    /** Defines callbacks for service binding, passed to bindService() */
//    public ServiceConnection mPlayListConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            PlayListService.LocalBinder binder = (PlayListService.LocalBinder) service;
//            setPlayListService(binder.getService());
//            mPlayListBound = true;
//            Log.d(TAG, "onServiceConnected: mPlayListBound " + mPlayListBound + ", mPlayListService " + mPlayListService);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mPlayListBound = false;
//        }
//    };

    public PlayListService getPlayListService() {
        return mPlayListService;
    }

    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
        Log.d(TAG, "setPlayListService " + mPlayListService);
        if (isFirebaseReady()) {
            // establish listeners
            getDalTheatre().setListener();
            getDalEpic().setListener();
            getDalStory().setListener();
            getDalStage().setListener();
            getDalActor().setListener();
            getDalAction().setListener();
            getDalOutcome().setListener();
            Log.d(TAG, "Firebase ready: " + isFirebaseReady() + ", listeners established for " + mUserId);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Binder given to clients
    private final IBinder mBinder = new RepoProvider.LocalBinder();
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        RepoProvider getService() {
            // Return this instance of LocalService so clients can call public methods
            return RepoProvider.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    ///////////////////////////////////////////////////////////////////////////
    // callback interface when model changes should trigger refresh
    public interface OnRepoProviderRefresh {
        void onRepoProviderRefresh(Boolean refresh);
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public RepoProvider() {
        // service context
        setContext(this);

//        // bind to playlist service
//        Intent intent = new Intent(this, PlayListService.class);
//        this.bindService(intent, mPlayListConnection, Context.BIND_AUTO_CREATE);
//        Log.d(TAG, "onCreateView: mPlayListBound " + mPlayListBound + ", mPlayListService " + mPlayListService);
//
        init();
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean init() {
        // instantiate DAL for each object type
        setDalTheatre(new DalTheatre(this));
        setDalEpic(new DalEpic(this));
        setDalStory(new DalStory(this));
        setDalStage(new DalStage(this));
        setDalActor(new DalActor(this));
        setDalAction(new DalAction(this));
        setDalOutcome(new DalOutcome(this));

        // establish firebase reference
        setFirebaseReference();

        if (!isFirebaseReady()) {
            Log.e(TAG, "Firebase NOT ready.");
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // get user id
            mUserId = getUid();
//
//            // establish listeners
//            getDalTheatre().setListener();
//            getDalEpic().setListener();
//            getDalStory().setListener();
//            getDalStage().setListener();
//            getDalActor().setListener();
//            getDalAction().setListener();
//            getDalOutcome().setListener();
        }
        else {
            setFirebaseReady(false);
        }
        Log.d(TAG, "Firebase ready: " + isFirebaseReady() + ", UserId " + mUserId);

        // create Audit repo
        mDaoAuditRepo = new DaoAuditRepo(this);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    public Context getContext() {
        return mContext;
    }
    public void setContext(Context context) {
        mContext = context;
    }

    public OnRepoProviderRefresh getCallback() {
        return mCallback;
    }

    public void setCallback(OnRepoProviderRefresh callback) {
        this.mCallback = callback;
    }

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
            setFirebaseReady(true);
        }
        return isFirebaseReady();
    }
    public Boolean isFirebaseReady() {
        return mIsFirebaseReady;
    }
    public Boolean setFirebaseReady(Boolean ready) { mIsFirebaseReady = ready; return mIsFirebaseReady; }

    public DatabaseReference getFirebaseReference() {
        return mDatabaseReference;
    }

    public Boolean removeFirebaseListener() {
        // TODO: remove listeners
        return true;
    }
///////////////////////////////////////////////////////////////////////////
}
