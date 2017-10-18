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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicActorBoard;
import com.adaptivehandyapps.ahathing.dao.DaoStarGate;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// DaoMakerViewXfer: transfer to/from DAO and view
public class MarqueeViewXfer {
    private static final String TAG = MarqueeViewXfer.class.getSimpleName();

    private static final int RADIOGROUP_LINK_EPIC = 0;
    private static final int RADIOGROUP_LINK_STAR = 1;

    private ContentFragment mParent;
    private View mRootView;
    private DaoEpic mDaoEpic;
    private List<DaoStarGate> mStarGateList;

    // epic controls
    private RadioGroup mRadioGroupLink;
    private int mCheckedRadioIndex;

    private TagListAdapter mStarListAdapter = null;
    private List<String> mStarLabelList;
    private List<Boolean> mStarSelected;
    private int mStarSelectedPosition = DaoDefs.INIT_INTEGER_MARKER;
    private View mSelectedStarView = null;

    private TagListAdapter mActorListAdapter = null;

    private List<Boolean> mActorEpicSelected;
    private List<String> mActorStarMap;
    private List<String> mActorStarLabelMap;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public MarqueeViewXfer(ContentFragment parent, View rootView) {

        Log.d(TAG, "DaoMakerUiHandler...");
        mParent = parent;
        mRootView = rootView;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromEpic(DaoEpic daoEpic, List<DaoStarGate> starGateList) {
        mDaoEpic = daoEpic;
        mStarGateList = starGateList;

        // handle actor link radio group
        mRadioGroupLink = (RadioGroup) mRootView.findViewById(R.id.rg_link);
        mCheckedRadioIndex = mRadioGroupLink.indexOfChild(mRootView.findViewById(mRadioGroupLink.getCheckedRadioButtonId()));
        // get selected option
        if (mCheckedRadioIndex == RADIOGROUP_LINK_STAR) {
            // load star list
            loadStarList(starGateList);
        }
        // load actor list
        loadActorList(daoEpic, starGateList);

        final List<DaoStarGate> finalStarGateList = starGateList;
        // This overrides the radiogroup onCheckListener
        mRadioGroupLink.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                Log.d(TAG,"Link radio group checked id: " + checkedId);
                // radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // if radio button is now checked...
                if (checkedRadioButton.isChecked())
                {
                    mCheckedRadioIndex = mRadioGroupLink.indexOfChild(checkedRadioButton);
                    if (mCheckedRadioIndex == RADIOGROUP_LINK_STAR) {
                        // load star list
                        loadStarList(finalStarGateList);
                    }
                    else {
                        // link to epic - banish star list
                        LinearLayout llStarList = (LinearLayout) mRootView.findViewById(R.id.ll_starlist);
                        llStarList.setVisibility(View.GONE);
                        // load actor list
                        loadActorList(mDaoEpic, mStarGateList);
                    }
                }
            }
        });
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean loadStarList(List<DaoStarGate> starGateList) {
        LinearLayout llStarList = (LinearLayout) mRootView.findViewById(R.id.ll_starlist);
        llStarList.setVisibility(View.VISIBLE);

        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();
        mStarSelected = new ArrayList<>();
        mStarLabelList = new ArrayList<>();

        // for each star in repo
        for (DaoStarGate starGate : starGateList) {
            // build list of stars names, labels & images
            tagNameList.add(starGate.getStarMoniker());
            tagLabelList.add(starGate.getDeviceDescription());
            int imageResId = DaoDefs.DAOOBJ_TYPE_MARQUEE_IMAGE_RESID;
            tagImageResIdList.add(imageResId);
            // set unselected color
            int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
            tagBgColorList.add(bgColor);
            // init selected state
            mStarSelected.add(false);
            // cat star name & description list
            String label = starGate.getStarMoniker() + "(" + starGate.getDeviceDescription() + ")";
            mStarLabelList.add(label);
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
                if (!mStarSelected.get(position)) {
                    // set color selected
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                    mStarSelected.set(position, true);
                }
                else {
                    mStarSelected.set(position, false);
                }
                v.setBackgroundColor(bgColor);
                // if former selection defined & not current selection
                if (mStarSelectedPosition != DaoDefs.INIT_INTEGER_MARKER && mStarSelectedPosition != position) {
                    // toggle former selection
                    mStarSelected.set(mStarSelectedPosition, false);
                    mSelectedStarView.setBackgroundColor(mRootView.getResources().getColor(R.color.colorTagListNotSelected));
                }
                // retain position
                mStarSelectedPosition = position;
                mSelectedStarView = v;
            }
        });
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean loadActorList(DaoEpic daoEpic, final List<DaoStarGate> starGateList) {
        LinearLayout llActorList = (LinearLayout) mRootView.findViewById(R.id.ll_actorlist);
        llActorList.setVisibility(View.VISIBLE);

        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();
        if (mActorEpicSelected == null) {
            mActorEpicSelected = new ArrayList<>();
            mActorStarMap = new ArrayList<>();
            mActorStarLabelMap = new ArrayList<>();
        }

        // display actor list
        List<String> actorMonikerList = (List<String>) (List<?>) mParent.getRepoProvider().getDalActor().getDaoRepo().getMonikerList();
        Integer position = 0;
        for (String actorMoniker : actorMonikerList) {
            Log.d(TAG, actorMoniker + " at position " + position);
//            // skip actor wildcards
//            if (!actorMoniker.contains(DaoDefs.ANY_ACTOR_MARKER)) {
                // build list of actor names, labels & images
                tagNameList.add(actorMoniker);
                tagLabelList.add("");
                int imageResId = DaoDefs.DAOOBJ_TYPE_ACTOR_IMAGE_RESID;
                tagImageResIdList.add(imageResId);
                // story is in tag list - set selected color
                int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
                if (mActorEpicSelected.size() < actorMonikerList.size()) {
                    // init selected state
                    mActorEpicSelected.add(false);
                    mActorStarMap.add(DaoDefs.INIT_STRING_MARKER);
                    mActorStarLabelMap.add(DaoDefs.INIT_STRING_MARKER);
                    // if actor board list contains this actor
                    Integer actorBoardInx = daoEpic.isActorBoard(actorMoniker);
                    if (actorBoardInx != DaoDefs.INIT_INTEGER_MARKER) {
                        // set actor selected
                        mActorEpicSelected.set(position, true);
                    }
                }
                // if actor selected for epic
                if (mActorEpicSelected.get(position)) {
                    // highlight list item
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                    // if actor linked to star
                    Integer actorBoardInx = daoEpic.isActorBoard(actorMoniker);
                    if (actorBoardInx != DaoDefs.INIT_INTEGER_MARKER && mActorStarMap.get(position) == DaoDefs.INIT_STRING_MARKER) {
                        mActorStarMap.set(position, daoEpic.getActorBoardList().get(actorBoardInx).getStarMoniker());
                        String label = daoEpic.getActorBoardList().get(actorBoardInx).getStarLabel();
                        mActorStarLabelMap.set(position, daoEpic.getActorBoardList().get(actorBoardInx).getStarLabel());
                        tagLabelList.set(tagLabelList.size()-1, mActorStarLabelMap.get(position));
                    }
                    else {
                        tagLabelList.set(tagLabelList.size()-1, mActorStarLabelMap.get(position));
                    }

                }
                tagBgColorList.add(bgColor);
                ++position;
//            }
        }

        // instantiate list adapter
        int resId = R.layout.tag_list_item;
        mActorListAdapter =
                new TagListAdapter(mRootView.getContext(),
                        resId,
                        tagNameList,
                        tagLabelList,
                        tagImageResIdList,
                        tagBgColorList);

        ListView lv = (ListView) mRootView.findViewById(R.id.listview_actorlist);
        if (mActorListAdapter != null && lv != null) {
            lv.setAdapter(mActorListAdapter);
        } else {
            Log.e(TAG, "NULL mActorListAdapter? " + mActorListAdapter + ", R.id.listview? " + lv);
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

                // epic selection checked
                if (mCheckedRadioIndex == RADIOGROUP_LINK_EPIC) {
                    // if epic not selected
                    if (!mActorEpicSelected.get(position)) {
                        // set epic selected
                        bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                        mActorEpicSelected.set(position, true);
                    } else {
                        // unselect epic
                        mActorEpicSelected.set(position, false);
                    }
                }
                else if (mCheckedRadioIndex == RADIOGROUP_LINK_STAR) {
                    String label = DaoDefs.INIT_STRING_MARKER;
                    // star selection checked & star selected at position
                    if (mStarSelectedPosition >= 0 && mStarSelectedPosition < mStarSelected.size() &&
                            mStarSelected.get(mStarSelectedPosition)) {
                        // set star
                        mActorStarMap.set(position, starGateList.get(mStarSelectedPosition).getMoniker());
                        label = mStarLabelList.get(mStarSelectedPosition);
                        mActorStarLabelMap.set(position, label);
                    } else {
                        mActorStarMap.set(position, DaoDefs.INIT_STRING_MARKER);
                        label = "bot";
                        mActorStarLabelMap.set(position, label);
                    }
                    mActorListAdapter.setLabel(position, label);
                    TextView txtLabel = (TextView) v.findViewById(R.id.tv_tag_label);
                    txtLabel.setText(label);
                }
                v.setBackgroundColor(bgColor);
            }
        });
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toEpic(String op, String moniker) {
        // dereference list of all actors
        List<String> actorMonikerList = (List<String>) (List<?>) mParent.getRepoProvider().getDalActor().getDaoRepo().getMonikerList();
        // for each actor
        Integer position = 0;
        Boolean update = false;
        for (Boolean actorSelected : mActorEpicSelected) {
            String actorMoniker = actorMonikerList.get(position);
            // if actor selected
            if (actorSelected) {
                Integer actorBoardIndex = mDaoEpic.isActorBoard(actorMoniker);
                // if actor not in actor board list, add star
                if (actorBoardIndex == DaoDefs.INIT_INTEGER_MARKER) {
                    // add actor to actor board list
                    actorBoardIndex = mDaoEpic.addActorBoard(actorMoniker);
                }
                // if update actor board
                if (!mActorStarMap.get(position).equals(DaoDefs.INIT_STRING_MARKER)) {
                    mDaoEpic.getActorBoardList().get(actorBoardIndex).setStarMoniker(mActorStarMap.get(position));
                    mDaoEpic.getActorBoardList().get(actorBoardIndex).setStarLabel(mActorStarLabelMap.get(position));
                }
                Log.d(TAG, "toEpic update -> " + mDaoEpic.getActorBoardList().get(actorBoardIndex).toString());
                update = true;
            }
//            // skip actor wildcards
//            if (!actorMoniker.contains(DaoDefs.ANY_ACTOR_MARKER))
            ++position;
        }
        if (update) {
            // update repo
            mParent.getRepoProvider().getDalEpic().update(mDaoEpic, true);
        }
        return update;
    }
    ///////////////////////////////////////////////////////////////////////////
}
