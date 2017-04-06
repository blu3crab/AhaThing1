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

import com.adaptivehandyapps.ahathing.dal.StoryProvider;
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

    private StoryProvider mStoryProvider;

    private String mContentOp = DaoDefs.INIT_STRING_MARKER;
    private String mContentObjType = DaoDefs.INIT_STRING_MARKER;
    private String mContentMoniker = DaoDefs.INIT_STRING_MARKER;

    ///////////////////////////////////////////////////////////////////////////
    public NavItem (StoryProvider storyProvider) {
        mStoryProvider = storyProvider;
    }

    ///////////////////////////////////////////////////////////////////////////
    // parse nav item to determine op, object type & moniker
    public Boolean parse(String itemname, String[] itemSplit) {
        // trigger parent to update the nav menu
        Boolean triggerUpdate = false;

        mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NADA;
        // check split for top level object: active theatre, story, stage, etc...
        if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
            if (mStoryProvider.getActiveTheatre() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = mStoryProvider.getActiveTheatre().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + mStoryProvider.getDaoTheatreRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            if (mStoryProvider.getActiveEpic() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = mStoryProvider.getActiveEpic().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + mStoryProvider.getDaoEpicRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            if (mStoryProvider.getActiveStory() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = mStoryProvider.getActiveStory().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + mStoryProvider.getDaoStoryRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
            if (mStoryProvider.getActiveStage() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = mStoryProvider.getActiveStage().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + mStoryProvider.getDaoStageRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
            if (mStoryProvider.getActiveActor() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = mStoryProvider.getActiveActor().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + mStoryProvider.getDaoActorRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
            if (mStoryProvider.getActiveAction() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = mStoryProvider.getActiveAction().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + mStoryProvider.getDaoActionRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
            if (mStoryProvider.getActiveOutcome() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = mStoryProvider.getActiveOutcome().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + mStoryProvider.getDaoOutcomeRepo().size();
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
                    Integer next = mStoryProvider.getDaoTheatreRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    // skip dups
                    while (mStoryProvider.getDaoTheatreRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    Integer next = mStoryProvider.getDaoEpicRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    // skip dups
                    while (mStoryProvider.getDaoEpicRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + mStoryProvider.getDaoStoryRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + mStoryProvider.getDaoStageRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + mStoryProvider.getDaoActorRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + mStoryProvider.getDaoActionRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + mStoryProvider.getDaoOutcomeRepo().size();
                }
            }
            else {
                // existing object - set active based on find object type
                if (mStoryProvider.getDaoTheatreRepo().get(itemname) != null) {
                    // theatre - set active
                    mStoryProvider.setActiveTheatre((DaoTheatre) mStoryProvider.getDaoTheatreRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoEpicRepo().get(itemname) != null) {
                    // Epic - set active
                    mStoryProvider.setActiveEpic((DaoEpic) mStoryProvider.getDaoEpicRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoStoryRepo().get(itemname) != null) {
                    // story - set active
                    mStoryProvider.setActiveStory((DaoStory) mStoryProvider.getDaoStoryRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoStageRepo().get(itemname) != null) {
                    // stage - set active
                    mStoryProvider.setActiveStage((DaoStage)mStoryProvider.getDaoStageRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoActorRepo().get(itemname) != null) {
                    // Actor - set active
                    mStoryProvider.setActiveActor((DaoActor)mStoryProvider.getDaoActorRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoActionRepo().get(itemname) != null) {
                    // Action - set active
                    mStoryProvider.setActiveAction((DaoAction)mStoryProvider.getDaoActionRepo().get(itemname));
                }
                else if (mStoryProvider.getDaoOutcomeRepo().get(itemname) != null) {
                    // Outcome - set active
                    mStoryProvider.setActiveOutcome((DaoOutcome)mStoryProvider.getDaoOutcomeRepo().get(itemname));
                }
                // update nav menu
                triggerUpdate = true;
//                setNavMenu();
                // launch active story
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_PLAY;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = mStoryProvider.getActiveStory().getMoniker();
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
