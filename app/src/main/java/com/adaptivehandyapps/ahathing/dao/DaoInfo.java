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
package com.adaptivehandyapps.ahathing.dao;

import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

///////////////////////////////////////////////////////////////////////////
public class DaoInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public final static Integer AHA_ID_BASE_PLAY = 10;
    public final static Integer AHA_ID_BASE_STAGE = 100;
    public final static Integer AHA_ID_BASE_ACTOR = 1000;
    public final static Integer AHA_ID_BASE_RULE = 10000;
    public final static Integer AHA_ID_BASE_RESERVE = 100000;

    @Retention(RetentionPolicy.SOURCE)
    // DAO object states
    @IntDef({DAOOBJ_STATE_UNKNOWN,
            DAOOBJ_STATE_ACTIVE,
            DAOOBJ_STATE_INACTIVE,
            DAOOBJ_STATE_FLUSH
    })
    public @interface DaoObjState {}

    public static final int DAOOBJ_STATE_UNKNOWN = 0;  // indeterminate (initial state)
    public static final int DAOOBJ_STATE_ACTIVE = 1;   // active
    public static final int DAOOBJ_STATE_INACTIVE = 2; // inactive
    public static final int DAOOBJ_STATE_FLUSH = 3;    // flush (ready to push)

    public static final String DAOOBJ_STATE_UNKNOWN_TEXT = "unknown";
    public static final String DAOOBJ_STATE_ACTIVE_TEXT = "active";
    public static final String DAOOBJ_STATE_INACTIVE_TEXT = "inactive";
    public static final String DAOOBJ_STATE_FLUSH_TEXT = "flush";

    // after DB load, these ahaIds will contains the greatest
    private static Integer mPlayAhaId = DaoInfo.AHA_ID_BASE_PLAY;
    private static Integer mStageAhaId = DaoInfo.AHA_ID_BASE_STAGE;
    private static Integer mActorAhaId = DaoInfo.AHA_ID_BASE_ACTOR;
    private static Integer mRuleAhaId = DaoInfo.AHA_ID_BASE_RULE;
    private static Integer mReserveAhaId = DaoInfo.AHA_ID_BASE_RESERVE;

    /////////////////////////////constructors//////////////////////////////////
    // DAO object info
    @SerializedName("ahaId")		
    private String ahaId;			// unique id
    @SerializedName("lastUpdate")	
    private Integer lastUpdate;		// epoch secs timestamp
	
    @SerializedName("state")		// object state: active, inactive, flush, undefined
    private @DaoInfo.DaoObjState
    Integer state;

    /////////////////////////////constructors//////////////////////////////////
    public DaoInfo() {
        this.ahaId = DaoDefs.INIT_STRING_MARKER;
        this.lastUpdate = DaoDefs.INIT_INTEGER_MARKER;
        this.state = DaoInfo.DAOOBJ_STATE_UNKNOWN;
    }
    public DaoInfo(
            String ahaId,
            Integer lastUpdate,
            @DaoInfo.DaoObjState Integer state
    ) {
        this.ahaId = ahaId;
        this.lastUpdate = lastUpdate;
        this.state = state;
    }

    /////////////////////////////helpers//////////////////////////////////
    public String toString() {
        return ahaId + ", " + lastUpdate + ", " + state;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAhaId() {
        return ahaId;
    }

    public void setAhaId(String ahaId) {
        this.ahaId = ahaId;
    }

    public Integer getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Integer lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public @DaoInfo.DaoObjState
    Integer getState() {
        return state;
    }
    public void setState(@DaoInfo.DaoObjState Integer state) {
        this.state = state;
    }

    public @DaoInfo.DaoObjState
    Integer getState(Integer state) {
        @DaoInfo.DaoObjState Integer daoObjState = DAOOBJ_STATE_UNKNOWN;
        switch (state){
            case DAOOBJ_STATE_ACTIVE:
                daoObjState = DAOOBJ_STATE_ACTIVE;
                break;
            case DAOOBJ_STATE_INACTIVE:
                daoObjState = DAOOBJ_STATE_INACTIVE;
                break;
            case DAOOBJ_STATE_FLUSH:
                daoObjState = DAOOBJ_STATE_FLUSH;
                break;
        }
        return daoObjState;
    }
    public String getStateText(Integer state) {
        String stateText = DAOOBJ_STATE_UNKNOWN_TEXT;
        switch (state) {
            case DAOOBJ_STATE_ACTIVE:
                stateText = DAOOBJ_STATE_ACTIVE_TEXT;
                break;
            case DAOOBJ_STATE_INACTIVE:
                stateText = DAOOBJ_STATE_INACTIVE_TEXT;
                break;
            case DAOOBJ_STATE_FLUSH:
                stateText = DAOOBJ_STATE_FLUSH_TEXT;
                break;
        }
        return stateText;
    }
    ///////////////////////////////////////////////////////////////////////////
    // AhaId - getters/setters
    //   getNextxxxxx allocates next id when inserting
    public static Integer getPlayAhaId() {
        return mPlayAhaId;
    }
    public static Integer getNextPlayAhaId() {
        return ++mPlayAhaId;
    }

    public static Boolean setPlayAhaId(Integer id) {
        mPlayAhaId = id;
        return true;
    }

    public static Integer getStageAhaId() {
        return mStageAhaId;
    }
    public static Integer getNextStageAhaId() {
        return ++mStageAhaId;
    }

    public static Boolean setStageAhaId(Integer id) {
        mStageAhaId = id;
        return true;
    }

    public static Integer getActorAhaId() {
        return mActorAhaId;
    }
    public static Integer getNextActorAhaId() {
        return ++mActorAhaId;
    }

    public static Boolean setActorAhaId(Integer id) {
        mActorAhaId = id;
        return true;
    }

    public static Integer getRuleAhaId() {
        return mRuleAhaId;
    }
    public static Integer getNextRuleAhaId() {
        return ++mRuleAhaId;
    }

    public static Boolean setRuleAhaId(Integer id) {
        mRuleAhaId = id;
        return true;
    }

    public static Integer getReserveAhaId() {
        return mReserveAhaId;
    }
    public static Integer getNextReserveAhaId() {
        return ++mReserveAhaId;
    }

    public static Boolean setReserveAhaId(Integer id) {
        mReserveAhaId = id;
        return true;
    }

}
///////////////////////////////////////////////////////////////////////////