/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker APR 2017
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
//
// Created by mat on 4/5/2017.
//

import com.adaptivehandyapps.ahathing.dal.RepoProvider;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

///////////////////////////////////////////////////////////////////////////
public class NavItem {
    private static final String TAG = "NavItem";

    private static final Integer DUP_SKIP_LIMIT = 1024;

    private String mContentOp = DaoDefs.INIT_STRING_MARKER;
    private String mContentObjType = DaoDefs.INIT_STRING_MARKER;
    private String mContentMoniker = DaoDefs.INIT_STRING_MARKER;

    ///////////////////////////////////////////////////////////////////////////
    public NavItem () {
    }

    ///////////////////////////////////////////////////////////////////////////
    // parse nav item to determine op, object type & moniker
    public Boolean parse(String itemname, String[] itemSplit) {
        // trigger parent to update the nav menu
        Boolean triggerUpdate = false;

        mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NADA;
        // check split for top level object: active theatre, story, stage, etc...
        if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
            if (MainActivity.getPlayListInstance().getActiveTheatre() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = MainActivity.getPlayListInstance().getActiveTheatre().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            if (MainActivity.getPlayListInstance().getActiveEpic() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = MainActivity.getPlayListInstance().getActiveEpic().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            if (MainActivity.getPlayListInstance().getActiveStory() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = MainActivity.getPlayListInstance().getActiveStory().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
            if (MainActivity.getPlayListInstance().getActiveStage() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = MainActivity.getPlayListInstance().getActiveStage().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
            if (MainActivity.getPlayListInstance().getActiveActor() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = MainActivity.getPlayListInstance().getActiveActor().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
            if (MainActivity.getPlayListInstance().getActiveAction() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = MainActivity.getPlayListInstance().getActiveAction().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
            if (MainActivity.getPlayListInstance().getActiveOutcome() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = MainActivity.getPlayListInstance().getActiveOutcome().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER)) {
            // launch audit
            mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_SHOWLIST;
            mContentObjType = DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER;
            mContentMoniker = "audit trail";
        }
        else {
            // submenu selection - test for existing selection or new object
            // if new - generate next default name
            if (itemname.toLowerCase().contains("new")) {
                // new object - extract object type
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                String split[] = itemname.split(" ");
                mContentObjType = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
                if (split.length > 1) {
                    mContentObjType = split[1];
                }
                // default moniker to list size
                if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
                    Integer next = MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    // skip dups
                    while (MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    Integer next = MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    // skip dups
                    while (MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().size();
                }
            }
            else {
                // existing object - set active based on find object type
                if (MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().get(itemname) != null) {
                    // theatre - set active
                    MainActivity.getPlayListInstance().setActiveTheatre((DaoTheatre) MainActivity.getRepoProviderInstance().getDalTheatre().getDaoRepo().get(itemname));
                }
                else if (MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().get(itemname) != null) {
                    // Epic - set active
                    MainActivity.getPlayListInstance().setActiveEpic((DaoEpic) MainActivity.getRepoProviderInstance().getDalEpic().getDaoRepo().get(itemname));
                }
                else if (MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().get(itemname) != null) {
                    // story - set active
                    MainActivity.getPlayListInstance().setActiveStory((DaoStory) MainActivity.getRepoProviderInstance().getDalStory().getDaoRepo().get(itemname));
                }
                else if (MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().get(itemname) != null) {
                    // stage - set active
                    MainActivity.getPlayListInstance().setActiveStage((DaoStage) MainActivity.getRepoProviderInstance().getDalStage().getDaoRepo().get(itemname));
                }
                else if (MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().get(itemname) != null) {
                    // Actor - set active
                    MainActivity.getPlayListInstance().setActiveActor((DaoActor) MainActivity.getRepoProviderInstance().getDalActor().getDaoRepo().get(itemname));
                }
                else if (MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().get(itemname) != null) {
                    // Action - set active
                    MainActivity.getPlayListInstance().setActiveAction((DaoAction) MainActivity.getRepoProviderInstance().getDalAction().getDaoRepo().get(itemname));
                }
                else if (MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().get(itemname) != null) {
                    // Outcome - set active
                    MainActivity.getPlayListInstance().setActiveOutcome((DaoOutcome) MainActivity.getRepoProviderInstance().getDalOutcome().getDaoRepo().get(itemname));
                }
                // update nav menu
                triggerUpdate = true;
//                setNavMenu();
                // TODO: consolidate Play launch
                if (MainActivity.getPlayListInstance().getActiveStory() != null) {
                    // launch active story
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                    mContentMoniker = MainActivity.getPlayListInstance().getActiveStory().getMoniker();
                }
            }
        }

        return triggerUpdate;
    }
    ///////////////////////////////////////////////////////////////////////////

    public String getOp() {
        return mContentOp;
    }

    public void setOp(String mContentOp) {
        this.mContentOp = mContentOp;
    }

    public String getObjType() {
        return mContentObjType;
    }

    public void setObjType(String mContentObjType) {
        this.mContentObjType = mContentObjType;
    }

    public String getMoniker() {
        return mContentMoniker;
    }

    public void setMoniker(String mContentMoniker) {
        this.mContentMoniker = mContentMoniker;
    }

    ///////////////////////////////////////////////////////////////////////////

}
