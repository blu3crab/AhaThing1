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

import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.adaptivehandyapps.ahathing.dao.DaoDefs;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// NavMenu: build Nav menu & submenus
public class NavMenu {
    private static final String TAG = "NavMenu";

    private PlayListService mPlayListService;
    private RepoProvider mRepoProvider;

    ///////////////////////////////////////////////////////////////////////////
    public NavMenu() {}

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
    // build nav menu
    public Boolean build(NavigationView navigationView, String starName) {

        // append active object to menu title
        String prefix = DaoDefs.INIT_STRING_MARKER;
        int iconId = R.drawable.ic_star_black_48dp;
        Menu menu = navigationView.getMenu();
        menu.clear();
        int objTypeCount = DaoDefs.DAOOBJ_TYPE_RESERVE;
        for (int i = 0; i < objTypeCount; i++) {
            String activeName = DaoDefs.INIT_STRING_MARKER;
            if (i == DaoDefs.DAOOBJ_TYPE_STARGATE) {
                iconId = DaoDefs.DAOOBJ_TYPE_STARGATE_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_STARGATE_MONIKER;
                activeName = starName;
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_THEATRE) {
                iconId = DaoDefs.DAOOBJ_TYPE_THEATRE_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
                if (getPlayListService().getActiveTheatre() != null) {
                    activeName = getPlayListService().getActiveTheatre().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_EPIC) {
                iconId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
                if (getPlayListService().getActiveEpic() != null) {
                    activeName = getPlayListService().getActiveEpic().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_STORY) {
                iconId = DaoDefs.DAOOBJ_TYPE_STORY_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
                if (getPlayListService().getActiveStory() != null) {
                    activeName = getPlayListService().getActiveStory().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_STAGE) {
                iconId = DaoDefs.DAOOBJ_TYPE_STAGE_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
                if (getPlayListService().getActiveStage() != null) {
                    activeName = getPlayListService().getActiveStage().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_ACTOR) {
                iconId = DaoDefs.DAOOBJ_TYPE_ACTOR_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
                activeName = "actor...";
                if (getPlayListService().getActiveActor() != null) {
                    activeName = getPlayListService().getActiveActor().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_ACTION) {
                iconId = DaoDefs.DAOOBJ_TYPE_ACTION_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
                activeName = "action...";
                if (getPlayListService().getActiveAction() != null) {
                    activeName = getPlayListService().getActiveAction().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_OUTCOME) {
                iconId = DaoDefs.DAOOBJ_TYPE_OUTCOME_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
                activeName = "outcome...";
                if (getPlayListService().getActiveOutcome() != null) {
                    activeName = getPlayListService().getActiveOutcome().getMoniker();
                }
            }
            else if (i == DaoDefs.DAOOBJ_TYPE_AUDIT) {
                iconId = DaoDefs.DAOOBJ_TYPE_AUDIT_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_AUDIT_MONIKER;
                activeName = "recent...";
            }
            else {
                iconId = DaoDefs.DAOOBJ_TYPE_UNKNOWN_IMAGE_RESID;
                prefix = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
                activeName = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
            }
            String itemName = prefix.concat(": " + activeName);

            MenuItem menuItem = menu.add(itemName);
            menuItem.setIcon(iconId);
            // set background color
//            SpannableString s = new SpannableString("My red MenuItem");
//            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
//            item.setTitle(s);
            // set text formatting
//            final SpannableString textToShow = new SpannableString("Hello stackOverflow");
//            textToShow.setSpan(new RelativeSizeSpan(1.5f), textToShow.length() - "stackOverflow".length(),textToShow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            textView.setText(textToShow);

//            Log.d(TAG, "setNavMenu  add menu item:" + menuItem.getItemId() + ", itemname: " + menuItem.toString());

        }
        // add theatres
        addSubMenu(DaoDefs.DAOOBJ_TYPE_THEATRE, navigationView);
        // add epics
        addSubMenu(DaoDefs.DAOOBJ_TYPE_EPIC, navigationView);
        // add stories
        addSubMenu(DaoDefs.DAOOBJ_TYPE_STORY, navigationView);
        // add stages
        addSubMenu(DaoDefs.DAOOBJ_TYPE_STAGE,  navigationView);
        // add actors
        addSubMenu(DaoDefs.DAOOBJ_TYPE_ACTOR, navigationView);
        // add actions
        addSubMenu(DaoDefs.DAOOBJ_TYPE_ACTION, navigationView);
        // add outcomes
        addSubMenu(DaoDefs.DAOOBJ_TYPE_OUTCOME, navigationView);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // add nav sub menu
    private SubMenu addSubMenu(@DaoDefs.DaoObjType int objType, NavigationView navigationView) {
        // extract moniker list
        String title = DaoDefs.DAOOBJ_TYPE_UNKNOWN_MONIKER;
        int iconId = DaoDefs.DAOOBJ_TYPE_UNKNOWN_IMAGE_RESID;
        List<String> monikerList = new ArrayList<>();
        if (objType == DaoDefs.DAOOBJ_TYPE_THEATRE) {
            title = DaoDefs.DAOOBJ_TYPE_THEATRE_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_THEATRE_IMAGE_RESID;
            monikerList = mRepoProvider.getDalTheatre().getDaoRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_EPIC) {
            title = DaoDefs.DAOOBJ_TYPE_EPIC_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
            monikerList = mRepoProvider.getDalEpic().getDaoRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_STORY) {
            title = DaoDefs.DAOOBJ_TYPE_STORY_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_STORY_IMAGE_RESID;
            monikerList = mRepoProvider.getDalStory().getDaoRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_STAGE) {
            title = DaoDefs.DAOOBJ_TYPE_STAGE_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_STAGE_IMAGE_RESID;
            monikerList = mRepoProvider.getDalStage().getDaoRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_ACTOR) {
            title = DaoDefs.DAOOBJ_TYPE_ACTOR_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_ACTOR_IMAGE_RESID;
            monikerList = mRepoProvider.getDalActor().getDaoRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_ACTION) {
            title = DaoDefs.DAOOBJ_TYPE_ACTION_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_ACTION_IMAGE_RESID;
            monikerList = mRepoProvider.getDalAction().getDaoRepo().getMonikerList();
        }
        else if (objType == DaoDefs.DAOOBJ_TYPE_OUTCOME) {
            title = DaoDefs.DAOOBJ_TYPE_OUTCOME_MONIKER;
            iconId = DaoDefs.DAOOBJ_TYPE_OUTCOME_IMAGE_RESID;
            monikerList = mRepoProvider.getDalOutcome().getDaoRepo().getMonikerList();
        }
        // add submenu from moniker list plus a "new" item
        Menu menu = navigationView.getMenu();
        SubMenu subMenu = menu.addSubMenu(title);
        subMenu.clear();
        MenuItem subMenuItem;
        for (String moniker : monikerList) {
            subMenuItem = subMenu.add(moniker);
            subMenuItem.setIcon(iconId);
//            Log.d(TAG, "addSubMenu submenu item:" + subMenuItem.getItemId() + ", itemname: " + subMenuItem.toString());
        }
        subMenuItem = subMenu.add("New " + title);
        subMenuItem.setIcon(iconId);
//        Log.d(TAG, "addSubMenu submenu item:" + subMenuItem.getItemId() + ", itemname: " + subMenuItem.toString());
        return subMenu;
    }
    ///////////////////////////////////////////////////////////////////////////

}
