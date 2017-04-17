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
    public Boolean parse(String itemname, String[] itemSplit, RepoProvider repoProvider) {
        // trigger parent to update the nav menu
        Boolean triggerUpdate = false;

        mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NADA;
        // check split for top level object: active theatre, story, stage, etc...
        if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
            if (repoProvider.getPlayList().getActiveTheatre() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = repoProvider.getPlayList().getActiveTheatre().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + repoProvider.getDalTheatre().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            if (repoProvider.getPlayList().getActiveEpic() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = repoProvider.getPlayList().getActiveEpic().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + repoProvider.getDalEpic().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            if (repoProvider.getPlayList().getActiveStory() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = repoProvider.getPlayList().getActiveStory().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + repoProvider.getDalStory().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
            if (repoProvider.getPlayList().getActiveStage() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = repoProvider.getPlayList().getActiveStage().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + repoProvider.getDalStage().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
            if (repoProvider.getPlayList().getActiveActor() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = repoProvider.getPlayList().getActiveActor().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + repoProvider.getDalActor().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
            if (repoProvider.getPlayList().getActiveAction() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = repoProvider.getPlayList().getActiveAction().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + repoProvider.getDalAction().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
            if (repoProvider.getPlayList().getActiveOutcome() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = repoProvider.getPlayList().getActiveOutcome().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + repoProvider.getDalOutcome().getDaoRepo().size();
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
                    Integer next = repoProvider.getDalTheatre().getDaoRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    // skip dups
                    while (repoProvider.getDalTheatre().getDaoRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    Integer next = repoProvider.getDalEpic().getDaoRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    // skip dups
                    while (repoProvider.getDalEpic().getDaoRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + repoProvider.getDalStory().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + repoProvider.getDalStage().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + repoProvider.getDalActor().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + repoProvider.getDalAction().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + repoProvider.getDalStage().getDaoRepo().size();
                }
            }
            else {
                // existing object - set active based on find object type
                if (repoProvider.getDalTheatre().getDaoRepo().get(itemname) != null) {
                    // theatre - set active
                    repoProvider.getPlayList().setActiveTheatre((DaoTheatre) repoProvider.getDalTheatre().getDaoRepo().get(itemname));
                }
                else if (repoProvider.getDalEpic().getDaoRepo().get(itemname) != null) {
                    // Epic - set active
                    repoProvider.getPlayList().setActiveEpic((DaoEpic) repoProvider.getDalEpic().getDaoRepo().get(itemname));
                }
                else if (repoProvider.getDalStory().getDaoRepo().get(itemname) != null) {
                    // story - set active
                    repoProvider.getPlayList().setActiveStory((DaoStory) repoProvider.getDalStory().getDaoRepo().get(itemname));
                }
                else if (repoProvider.getDalStage().getDaoRepo().get(itemname) != null) {
                    // stage - set active
                    repoProvider.getPlayList().setActiveStage((DaoStage) repoProvider.getDalStage().getDaoRepo().get(itemname));
                }
                else if (repoProvider.getDalActor().getDaoRepo().get(itemname) != null) {
                    // Actor - set active
                    repoProvider.getPlayList().setActiveActor((DaoActor) repoProvider.getDalActor().getDaoRepo().get(itemname));
                }
                else if (repoProvider.getDalAction().getDaoRepo().get(itemname) != null) {
                    // Action - set active
                    repoProvider.getPlayList().setActiveAction((DaoAction) repoProvider.getDalAction().getDaoRepo().get(itemname));
                }
                else if (repoProvider.getDalOutcome().getDaoRepo().get(itemname) != null) {
                    // Outcome - set active
                    repoProvider.getPlayList().setActiveOutcome((DaoOutcome) repoProvider.getDalOutcome().getDaoRepo().get(itemname));
                }
                // update nav menu
                triggerUpdate = true;
//                setNavMenu();
                // TODO: consolidate Play launch
                if (repoProvider.getPlayList().getActiveStory() != null) {
                    // launch active story
                    mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                    mContentMoniker = repoProvider.getPlayList().getActiveStory().getMoniker();
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
