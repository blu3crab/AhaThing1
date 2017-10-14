/*
 * Project: AhaThing1
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker OCT 2017
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicActorBoard;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStarGate;
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// DaoMakerViewXfer: transfer to/from DAO and view
public class MarqueeViewXfer {
    private static final String TAG = MarqueeViewXfer.class.getSimpleName();

    private ContentFragment mParent;
    private View mRootView;
    // epic controls
    private TagListAdapter mStoryListAdapter = null;
    private List<String> mStoryList;

    private TagListAdapter mStarListAdapter = null;
    private List<Boolean> mStarListSelected;
    private int mPrevStarPosition = DaoDefs.INIT_INTEGER_MARKER;
    private View mPrevStarView = null;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public MarqueeViewXfer(ContentFragment parent, View rootView) {

        Log.d(TAG, "DaoMakerUiHandler...");
        mParent = parent;
        mRootView = rootView;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromEpic(DaoEpic daoEpic, List<DaoStarGate> starGateList) {

        // set epic views visible
        LinearLayout llStarList = (LinearLayout) mRootView.findViewById(R.id.ll_starlist);
        llStarList.setVisibility(View.VISIBLE);
        LinearLayout llActorList = (LinearLayout) mRootView.findViewById(R.id.ll_starlist);
        llActorList.setVisibility(View.VISIBLE);

        // load star list
        loadStarList(starGateList);

        // display actor list
        List<DaoEpicActorBoard> actorBoardList = daoEpic.getActorBoardList();
        for (DaoEpicActorBoard starBoard : actorBoardList) {
            Log.d(TAG, starBoard.toString());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean loadStarList(List<DaoStarGate> starGateList) {
        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();
        mStarListSelected = new ArrayList<>();

        // for each story in repo
        for (DaoStarGate starGate : starGateList) {
            // build list of stars names, labels & images
            tagNameList.add(starGate.getStarMoniker());
            tagLabelList.add(starGate.getDeviceDescription());
            int imageResId = DaoDefs.DAOOBJ_TYPE_STARGATE_IMAGE_RESID;
            tagImageResIdList.add(imageResId);
            // set unselected color
            int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
            tagBgColorList.add(bgColor);
            // init selected state
            mStarListSelected.add(false);
        }

        // instantiate list adapter
        int resId = R.layout.tag_list_item;
        mStarListAdapter =
                new TagListAdapter(mRootView.getContext(),
                        resId,
                        tagNameList,
                        tagLabelList,
                        tagImageResIdList,
                        tagBgColorList);

        final ListView lv = (ListView) mRootView.findViewById(R.id.listview_starlist);
        if (mStarListAdapter != null && lv != null) {
            lv.setAdapter(mStarListAdapter);
        } else {
            Log.e(TAG, "NULL mStarListAdapter? " + mStarListAdapter + ", R.id.listview? " + lv);
            return false;
        }
        // establish listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                Log.d(TAG,"handleTagList item " + value + " at position " + position);
                int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
                // if not selected
                if (!mStarListSelected.get(position)) {
                    // set color selected
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                    mStarListSelected.set(position, true);
                }
                else {
                    mStarListSelected.set(position, false);
                }
                v.setBackgroundColor(bgColor);
                // if former selection defined & not current selection
                if (mPrevStarPosition != DaoDefs.INIT_INTEGER_MARKER && mPrevStarPosition != position) {
                    // toggle former selection
                    mStarListSelected.set(mPrevStarPosition, false);
                    mPrevStarView.setBackgroundColor(mRootView.getResources().getColor(R.color.colorTagListNotSelected));
                }
                // retain position
                mPrevStarPosition = position;
                mPrevStarView = v;
            }
        });
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean loadStoryList() {
        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();

        // dereference story repo dao list - all stories
        List<DaoStory> daoStoryList = (List<DaoStory>)(List<?>) mParent.getRepoProvider().getDalStory().getDaoRepo().getDaoList();
        // for each story in repo
        for (DaoStory story : daoStoryList) {
            // build list of story names, labels & images
            tagNameList.add(story.getMoniker());
            if (!story.getHeadline().equals(DaoDefs.INIT_STRING_MARKER)) {
                tagLabelList.add(story.getHeadline());
            }
            else {
                tagLabelList.add("epic headline activity here...");
            }
            int imageResId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
            tagImageResIdList.add(imageResId);
            // story is in tag list - set selected color
            int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
            if (mStoryList.contains(story.getMoniker())) {
                // highlight list item
                bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
            }
            tagBgColorList.add(bgColor);
        }

        // instantiate list adapter
        int resId = R.layout.tag_list_item;
        mStoryListAdapter =
                new TagListAdapter(mRootView.getContext(),
                        resId,
                        tagNameList,
                        tagLabelList,
                        tagImageResIdList,
                        tagBgColorList);

        ListView lv = (ListView) mRootView.findViewById(R.id.listview_stories);
        if (mStoryListAdapter != null && lv != null) {
            lv.setAdapter(mStoryListAdapter);
        } else {
            Log.e(TAG, "NULL mStoryListAdapter? " + mStoryListAdapter + ", R.id.listview? " + lv);
            return false;
        }
        // establish listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                Log.d(TAG,"handleTagList item " + value + " at position " + position);
                int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);

                // if taglist contains selection
                if (mStoryList.contains(value)) {
                    // find epic in taglist & remove
                    int i = mStoryList.indexOf(value);
                    mStoryList.remove(i);
                    Log.d(TAG,"handleTagList remove item " + value + " at position " + i);
                }
                else {
                    // add selection to taglist
                    mStoryList.add(value);
                    // set color selected
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                    Log.d(TAG,"handleTagList add item " + value + " at position " + (mStoryList.size()-1));
                }

                v.setBackgroundColor(bgColor);
            }
        });
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toEpic(String op, String moniker) {
        // active object
        DaoEpic activeEpic = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_MARQUEE)) {
            activeEpic = (DaoEpic) mParent.getRepoProvider().getDalEpic().getDaoRepo().get(moniker);
        }
        if (activeEpic == null) {
            // error: never should edit NULL object!  create a placeholder & carry on
            activeEpic = new DaoEpic();
            Log.e(TAG, "toEpic: new object....");
        }
//        // update star-to-actor mappings with edited values
//        activeEpic.setTagList(mStoryList);
//        mParent.getPlayListService().setActiveEpic(activeEpic);
//
//        // update repo
//        mParent.getRepoProvider().getDalEpic().update(activeEpic, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
