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
///////////////////////////////////////////////////////////////////////////
package com.adaptivehandyapps.ahathing.dao;

import android.support.annotation.IntDef;

import com.adaptivehandyapps.ahathing.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

///////////////////////////////////////////////////////////////////////////
//
// Theatre[1..N]
//      Epic[1..N]
//          nickname
//          creation date
//          Stage[1..N]
//              dimensions
//                  Vert[1..N]
//                      location
//                      connections to other Verts to form paths
//              Prop[1..N]
//                  attributes at Verts
//                      undiscovered(hidden)/discovered(visible)
//          Story[1..N]
//              Actor[1..N]
//                  nickname
//                  attributes
//                  Avatar[1..N]
//                      type
//                      capabilities
//              Action[1..N]
//                  nickname
//                  attributes
//                  mapped from incoming event (e.g. touch)
//              Outcome[1..N]
//                  nickname
//                  attributes
//                  results in stage updates
//
///////////////////////////////////////////////////////////////////////////
public class DaoDefs {
    //
    // general object initialization settings
    //
    public static final String INIT_STRING_MARKER = "nada";
    public static final Integer INIT_INTEGER_MARKER = -1;
    public static final Long INIT_LONG_MARKER = -1l;
    public static final Double INIT_DOUBLE_MARKER = -1.0;

    public static final int LOGO_IMAGE_RESID = R.drawable.bluecrab48;
    //
    // @DaoDefs.ObjType int type = DaoDefs.getObjType(some object);
    @Retention(RetentionPolicy.SOURCE)
    // dao object types
    @IntDef({DAOOBJ_TYPE_UNKNOWN,
            DAOOBJ_TYPE_STARGATE,
            DAOOBJ_TYPE_MARQUEE,
            DAOOBJ_TYPE_THEATRE,
            DAOOBJ_TYPE_EPIC,
            DAOOBJ_TYPE_STORY,
            DAOOBJ_TYPE_STAGE,
            DAOOBJ_TYPE_AUDIT,
            DAOOBJ_TYPE_ACTOR,
            DAOOBJ_TYPE_ACTION,
            DAOOBJ_TYPE_OUTCOME,
            DAOOBJ_TYPE_RESERVE
    })
    public @interface DaoObjType {}

    public static final int DAOOBJ_TYPE_UNKNOWN = -1;
    public static final int DAOOBJ_TYPE_STARGATE = 0;
    public static final int DAOOBJ_TYPE_MARQUEE = 1;
    public static final int DAOOBJ_TYPE_THEATRE = 2;
    public static final int DAOOBJ_TYPE_EPIC = 3;
    public static final int DAOOBJ_TYPE_STORY = 4;
    public static final int DAOOBJ_TYPE_STAGE = 5;
    public static final int DAOOBJ_TYPE_ACTOR = 6;
    public static final int DAOOBJ_TYPE_ACTION = 7;
    public static final int DAOOBJ_TYPE_OUTCOME = 8;
    public static final int DAOOBJ_TYPE_AUDIT = 9;
    public static final int DAOOBJ_TYPE_RESERVE = 10;

    public static final String DAOOBJ_TYPE_UNKNOWN_MONIKER = "Unknown";
    public static final String DAOOBJ_TYPE_STARGATE_MONIKER = "StarGate";
    public static final String DAOOBJ_TYPE_MARQUEE_MONIKER = "Marquee";
    public static final String DAOOBJ_TYPE_THEATRE_MONIKER = "Theatre";
    public static final String DAOOBJ_TYPE_EPIC_MONIKER = "Epic";
    public static final String DAOOBJ_TYPE_STORY_MONIKER = "Story";
    public static final String DAOOBJ_TYPE_STAGE_MONIKER = "Stage";
    public static final String DAOOBJ_TYPE_ACTOR_MONIKER = "Actor";
    public static final String DAOOBJ_TYPE_ACTION_MONIKER = "Action";
    public static final String DAOOBJ_TYPE_OUTCOME_MONIKER = "Outcome";
    public static final String DAOOBJ_TYPE_AUDIT_MONIKER = "Audit Trail";
    public static final String DAOOBJ_TYPE_RESERVE_MONIKER = "Reserve";

    public static final int DAOOBJ_TYPE_UNKNOWN_IMAGE_RESID = R.drawable.ic_format_clear_black_48dp;
    public static final int DAOOBJ_TYPE_STARGATE_IMAGE_RESID = R.drawable.ic_flare_black_48dp;
    public static final int DAOOBJ_TYPE_MARQUEE_IMAGE_RESID = R.drawable.ic_person_black_48dp;
    public static final int DAOOBJ_TYPE_THEATRE_IMAGE_RESID = R.drawable.ic_local_movies_black_48dp;
    public static final int DAOOBJ_TYPE_EPIC_IMAGE_RESID = R.drawable.ic_burst_mode_black_48dp;
    public static final int DAOOBJ_TYPE_STORY_IMAGE_RESID = R.drawable.ic_play_circle_filled_black_48dp;
    public static final int DAOOBJ_TYPE_STAGE_IMAGE_RESID = R.drawable.ic_crop_original_black_48dp;
    public static final int DAOOBJ_TYPE_ACTOR_IMAGE_RESID = R.drawable.ic_star_black_48dp;
    public static final int DAOOBJ_TYPE_ACTION_IMAGE_RESID = R.drawable.ic_directions_run_black_48dp;
    public static final int DAOOBJ_TYPE_OUTCOME_IMAGE_RESID = R.drawable.ic_satellite_black_48dp;
    public static final int DAOOBJ_TYPE_AUDIT_IMAGE_RESID = R.drawable.ic_bubble_chart_black_48dp;
    public static final int DAOOBJ_TYPE_RESERVE_IMAGE_RESID = R.drawable.ic_map_black_48dp;


    ///////////////////////////////////////////////////////////////////////////
    // constructor stub
    public DaoDefs() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // get object type
    public static @DaoObjType
    Integer getDaoObjType(Integer type) {
        @DaoObjType Integer daoObjType = DAOOBJ_TYPE_UNKNOWN;
        switch (type){
            case DAOOBJ_TYPE_UNKNOWN:
                daoObjType = DAOOBJ_TYPE_UNKNOWN;
                break;
            case DAOOBJ_TYPE_STARGATE:
                daoObjType = DAOOBJ_TYPE_STARGATE;
                break;
            case DAOOBJ_TYPE_THEATRE:
                daoObjType = DAOOBJ_TYPE_THEATRE;
                break;
            case DAOOBJ_TYPE_EPIC:
                daoObjType = DAOOBJ_TYPE_EPIC;
                break;
            case DAOOBJ_TYPE_STORY:
                daoObjType = DAOOBJ_TYPE_STORY;
                break;
            case DAOOBJ_TYPE_STAGE:
                daoObjType = DAOOBJ_TYPE_STAGE;
                break;
            case DAOOBJ_TYPE_ACTOR:
                daoObjType = DAOOBJ_TYPE_ACTOR;
                break;
            case DAOOBJ_TYPE_ACTION:
                daoObjType = DAOOBJ_TYPE_ACTION;
                break;
            case DAOOBJ_TYPE_RESERVE:
                daoObjType = DAOOBJ_TYPE_RESERVE;
                break;
        }
        return daoObjType;
    }
    ///////////////////////////////////////////////////////////////////////////
}
