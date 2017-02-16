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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

///////////////////////////////////////////////////////////////////////////
//
// Theatre[1..N]
// Story[1..N]
//      nickname
//      creation date
//      Stage[1..N]
//          dimensions
//              Vert[1..N]
//                  location
//                  connections to other Verts to form paths
//          Prop[1..N]
//              attributes at Verts
//                  undiscovered(hidden)/discovered(visible)
//      Actor[1..N]
//          nickname
//          attributes
//          Avatar[1..N]
//              type
//              capabilities
//      Action[1..N]
//          Avatar navigation on stage
//          conflict creation
//          conflict resolution
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

    //
    // @DaoDefs.ObjType int type = DaoDefs.getObjType(some object);
    @Retention(RetentionPolicy.SOURCE)
    // dao object types
    @IntDef({DAOOBJ_TYPE_UNKNOWN,
            DAOOBJ_TYPE_THEATRE,
            DAOOBJ_TYPE_STORY,
            DAOOBJ_TYPE_STAGE,
            DAOOBJ_TYPE_ACTOR,
            DAOOBJ_TYPE_ACTION,
            DAOOBJ_TYPE_RESERVE
    })
    public @interface DaoObjType {}

    public static final int DAOOBJ_TYPE_UNKNOWN = -1;
    public static final int DAOOBJ_TYPE_THEATRE = 0;
    public static final int DAOOBJ_TYPE_STORY = 1;
    public static final int DAOOBJ_TYPE_STAGE = 2;
    public static final int DAOOBJ_TYPE_ACTOR = 3;
    public static final int DAOOBJ_TYPE_ACTION = 4;
    public static final int DAOOBJ_TYPE_RESERVE = 5;

    public static final String DAOOBJ_TYPE_UNKNOWN_TITLE = "Unknown";
    public static final String DAOOBJ_TYPE_THEATRE_TITLE = "Theatre";
    public static final String DAOOBJ_TYPE_STORY_TITLE = "Story";
    public static final String DAOOBJ_TYPE_STAGE_TITLE = "Stage";
    public static final String DAOOBJ_TYPE_ACTOR_TITLE = "Actor";
    public static final String DAOOBJ_TYPE_ACTION_TITLE = "Action";
    public static final String DAOOBJ_TYPE_RESERVE_TITLE = "Reserve";


    ///////////////////////////////////////////////////////////////////////////
    // constructor stub
    public DaoDefs() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // get object type
    public static @DaoObjType
    Integer getDaoObjType(Integer type) {
        @DaoObjType Integer daoPipeObjType = DAOOBJ_TYPE_UNKNOWN;
        switch (type){
            case DAOOBJ_TYPE_UNKNOWN:
                daoPipeObjType = DAOOBJ_TYPE_UNKNOWN;
                break;
            case DAOOBJ_TYPE_THEATRE:
                daoPipeObjType = DAOOBJ_TYPE_THEATRE;
                break;
            case DAOOBJ_TYPE_STORY:
                daoPipeObjType = DAOOBJ_TYPE_STORY;
                break;
            case DAOOBJ_TYPE_STAGE:
                daoPipeObjType = DAOOBJ_TYPE_STAGE;
                break;
            case DAOOBJ_TYPE_ACTOR:
                daoPipeObjType = DAOOBJ_TYPE_ACTOR;
                break;
            case DAOOBJ_TYPE_ACTION:
                daoPipeObjType = DAOOBJ_TYPE_ACTION;
                break;
            case DAOOBJ_TYPE_RESERVE:
                daoPipeObjType = DAOOBJ_TYPE_RESERVE;
                break;
        }
        return daoPipeObjType;
    }
    ///////////////////////////////////////////////////////////////////////////
}
