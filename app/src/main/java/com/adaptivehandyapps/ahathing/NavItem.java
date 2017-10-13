/*
 * Project: AhaThing1
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

import android.util.Log;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;

///////////////////////////////////////////////////////////////////////////
// NavItem: determine op, obj type, moniker of nav menu selection
public class NavItem {
    private static final String TAG = "NavItem";

    private static final Integer DUP_SKIP_LIMIT = 1024;

    private String mContentOp = DaoDefs.INIT_STRING_MARKER;
    private String mContentObjType = DaoDefs.INIT_STRING_MARKER;
    private String mContentMoniker = DaoDefs.INIT_STRING_MARKER;

    private PlayListService mPlayListService;
    private RepoProvider mRepoProvider;

    ///////////////////////////////////////////////////////////////////////////
    public NavItem () {}
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
    public PlayListService getPlayListService() {
        return mPlayListService;
    }
    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
        Log.d(TAG, "setPlayListService " + mPlayListService);
    }

    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
        Log.d(TAG, "setRepoProvider " + mRepoProvider);
    }
    ///////////////////////////////////////////////////////////////////////////
    // parse nav item to determine op, object type & moniker
    public Boolean parse(String itemname, String[] itemSplit) {
        // trigger parent to update the nav menu
        Boolean triggerUpdate = false;

        mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NADA;
        // check split for top level object: active theatre, story, stage, etc...
        if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER)) {
            // launch STARGATE
            mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_STARGATE;
            mContentObjType = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
            mContentMoniker = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_MARQUEE_MONIKER)) {
            if (getPlayListService().getActiveEpic() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_MARQUEE;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_MARQUEE_MONIKER;
                mContentMoniker = getPlayListService().getActiveEpic().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + mRepoProvider.getDalEpic().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER)) {
            if (getPlayListService().getActiveTheatre() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = getPlayListService().getActiveTheatre().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + mRepoProvider.getDalTheatre().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
            if (getPlayListService().getActiveEpic() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = getPlayListService().getActiveEpic().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + mRepoProvider.getDalEpic().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
            if (getPlayListService().getActiveStory() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = getPlayListService().getActiveStory().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + mRepoProvider.getDalStory().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
            if (getPlayListService().getActiveStage() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = getPlayListService().getActiveStage().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + mRepoProvider.getDalStage().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
            if (getPlayListService().getActiveActor() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = getPlayListService().getActiveActor().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + mRepoProvider.getDalActor().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
            if (getPlayListService().getActiveAction() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = getPlayListService().getActiveAction().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + mRepoProvider.getDalAction().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
            if (getPlayListService().getActiveOutcome() != null) {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = getPlayListService().getActiveOutcome().getMoniker();
            }
            else {
                // launch dao maker
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_NEW;
                mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + mRepoProvider.getDalOutcome().getDaoRepo().size();
            }
        }
        else if (itemSplit[0].equals(DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER)) {
            // ensure playlist is coherent - any undefined objects?
            Boolean removeIfUndefined = true;
            Boolean forceToActiveStage = true;
            mPlayListService.repairAll(removeIfUndefined, forceToActiveStage);

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
                    Integer next = mRepoProvider.getDalTheatre().getDaoRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    // skip dups
                    while (mRepoProvider.getDalTheatre().getDaoRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER)) {
                    Integer next = mRepoProvider.getDalEpic().getDaoRepo().size();
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    // skip dups
                    while (mRepoProvider.getDalEpic().getDaoRepo().contains(mContentMoniker) && next < DUP_SKIP_LIMIT) {
                        ++next;
                        mContentMoniker = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER + next;
                    }
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STORY_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER + mRepoProvider.getDalStory().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER + mRepoProvider.getDalStage().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER + mRepoProvider.getDalActor().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER + mRepoProvider.getDalAction().getDaoRepo().size();
                }
                else if (mContentObjType.equals(DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER)) {
                    mContentMoniker = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER + mRepoProvider.getDalStage().getDaoRepo().size();
                }
                else {
                    Log.e(TAG, "Oops!  Unknown (new) object type " + mContentObjType);
                }
            }
            else {
                // existing object - set active based on find object type
                mContentOp = ContentFragment.ARG_CONTENT_VALUE_OP_EDIT;
                if (mRepoProvider.getDalTheatre().getDaoRepo().get(itemname) != null) {
                    // theatre - set active
                    getPlayListService().setActiveTheatre((DaoTheatre) mRepoProvider.getDalTheatre().getDaoRepo().get(itemname));
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                    mContentMoniker = getPlayListService().getActiveTheatre().getMoniker();
                }
                else if (mRepoProvider.getDalEpic().getDaoRepo().get(itemname) != null) {
                    // Epic - set active
                    getPlayListService().setActiveEpic((DaoEpic) mRepoProvider.getDalEpic().getDaoRepo().get(itemname));
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                    mContentMoniker = getPlayListService().getActiveEpic().getMoniker();
                }
                else if (mRepoProvider.getDalStory().getDaoRepo().get(itemname) != null) {
                    // story - set active
                    getPlayListService().setActiveStory((DaoStory) mRepoProvider.getDalStory().getDaoRepo().get(itemname));
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                    mContentMoniker = getPlayListService().getActiveStory().getMoniker();
                }
                else if (mRepoProvider.getDalStage().getDaoRepo().get(itemname) != null) {
                    // stage - set active
                    getPlayListService().setActiveStage((DaoStage) mRepoProvider.getDalStage().getDaoRepo().get(itemname));
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                    mContentMoniker = getPlayListService().getActiveStage().getMoniker();
                }
                else if (mRepoProvider.getDalActor().getDaoRepo().get(itemname) != null) {
                    // Actor - set active
                    // TODO: setActiveActor on nav submenu select?
                    getPlayListService().setActiveActor((DaoActor) mRepoProvider.getDalActor().getDaoRepo().get(itemname));
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                    mContentMoniker = getPlayListService().getActiveActor().getMoniker();
                }
                else if (mRepoProvider.getDalAction().getDaoRepo().get(itemname) != null) {
                    // Action - set active
                    getPlayListService().setActiveAction((DaoAction) mRepoProvider.getDalAction().getDaoRepo().get(itemname));
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                    mContentMoniker = getPlayListService().getActiveAction().getMoniker();
                }
                else if (mRepoProvider.getDalOutcome().getDaoRepo().get(itemname) != null) {
                    // Outcome - set active
                    getPlayListService().setActiveOutcome((DaoOutcome) mRepoProvider.getDalOutcome().getDaoRepo().get(itemname));
                    // launch dao maker
                    mContentObjType = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                    mContentMoniker = getPlayListService().getActiveOutcome().getMoniker();
                }
                else {
                    Log.e(TAG, "Oops!  Unknown (sub) object type " + mContentObjType);
                }
                // update nav menu
                triggerUpdate = true;
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
