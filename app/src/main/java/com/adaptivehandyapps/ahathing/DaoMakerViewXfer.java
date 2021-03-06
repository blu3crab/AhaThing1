/*
 * Project: AhaThing1
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
import com.adaptivehandyapps.ahathing.dao.DaoStory;
import com.adaptivehandyapps.ahathing.dao.DaoTheatre;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// DaoMakerViewXfer: transfer to/from DAO and view
public class DaoMakerViewXfer implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = DaoMakerViewXfer.class.getSimpleName();

    private static final Boolean TEST_SET_PLAYLIST = false;
    private static final Boolean LAUNCH_COLOR_PICKER = true;    // true - show old style color picker

    private static final String TALLY_LIMIT_TEXT = "Tally Limit ";
    private static final String TIC_LIMIT_TEXT   = "Tic Limit ";
    private static final int MAX_TALLY_DEFAULT = 64;
    private static final int MAX_TIC_DEFAULT = 128;

    private ContentFragment mParent;
    private View mRootView;
    // theatre controls
    private TagListAdapter mEpicListAdapter = null;
    private List<String> mEpicList;
    private CheckBox mCheckFlourishSound;
    private CheckBox mCheckMusicSound;
    private CheckBox mCheckActionSound;
    // epic controls
    private TagListAdapter mStoryListAdapter = null;
    private List<String> mStoryList;
    private RadioGroup mRadioGroupOrder;
    private SeekBar mTallySeekbar;
    private TextView mTvTally;
    private int mTallyMax = MAX_TALLY_DEFAULT;
    private int mTallyProgress = MAX_TALLY_DEFAULT/2;
    private SeekBar mTicSeekbar;
    private TextView mTvTic;
    private int mTicMax = MAX_TIC_DEFAULT;
    private int mTicProgress = MAX_TIC_DEFAULT/2;
    // story controls
    private ArrayAdapter<String> mStageListAdapter = null;
    private Spinner mSpinnerStages;
    private ArrayAdapter<String> mPreReqListAdapter = null;
    private Spinner mSpinnerPreReqs;
    private ArrayAdapter<String> mActorListAdapter = null;
    private Spinner mSpinnerActors;
    private ArrayAdapter<String> mActionListAdapter = null;
    private Spinner mSpinnerActions;
    private ArrayAdapter<String> mOutcomeListAdapter = null;
    private Spinner mSpinnerOutcomes;
    private ArrayAdapter<String> mPostOpListAdapter = null;
    private Spinner mSpinnerPostOps;
    // stage controls
    private ArrayAdapter<String> mStageTypeListAdapter = null;
    private Spinner mSpinnerStageType;
    private ArrayAdapter<String> mActionTypeListAdapter = null;
    private Spinner mSpinnerActionType;
    private ArrayAdapter<String> mOutcomeTypeListAdapter = null;
    private Spinner mSpinnerOutcomeType;
    // actor controls
    private ArrayAdapter<String> mActorTypeListAdapter = null;
    private Spinner mSpinnerActorTypes;
    private Boolean mIsForeColor;
    private Integer mForeColor = DaoDefs.INIT_INTEGER_MARKER;
    private Integer mBackColor = DaoDefs.INIT_INTEGER_MARKER;
    private Integer mSelectedColorRGB = DaoDefs.INIT_INTEGER_MARKER;

    private Button mButtonForeColor;
    private Button mButtonBackColor;
    private Button mButtonAcceptPalette;
    private Button mButtonQuitPalette;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public DaoMakerViewXfer(ContentFragment parent, View rootView) {

        Log.d(TAG, "DaoMakerUiHandler...");
        mParent = parent;
        mRootView = rootView;
    }
    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    private Boolean isForeColor() { return mIsForeColor; }
    private Boolean isForeColor(Boolean isForeColor) { mIsForeColor = isForeColor; return mIsForeColor; }

    public Integer getForeColor() {
        return mForeColor;
    }
    public void setForeColor(Integer foreColor) {
        // default color to 0
        this.mForeColor = 0;
        // if incoming color defined, assign color
        if (foreColor != DaoDefs.INIT_INTEGER_MARKER) this.mForeColor = foreColor;
        // if button defined, assign button color
        if (mButtonForeColor != null) mButtonForeColor.setBackgroundColor(getForeColor());
        Log.d(TAG, "fore color rgb " + getForeColor());

    }

    public Integer getBackColor() {
        return mBackColor;
    }
    public void setBackColor(Integer backColor) {
        // default color to 0
        this.mBackColor = 0;
        // if incoming color defined, assign color
        if (backColor != DaoDefs.INIT_INTEGER_MARKER) this.mBackColor = backColor;
        // if button defined, assign button color
        if (mButtonBackColor != null) mButtonBackColor.setBackgroundColor(getBackColor());
        Log.d(TAG, "Back color rgb " + getBackColor());
    }
    ///////////////////////////////////////////////////////////////////////////
    // helpers
    private void launchColorPicker(final Boolean isForeColor, int rgb) {
        // clear selected rgb
        mSelectedColorRGB = DaoDefs.INIT_INTEGER_MARKER;
        // extract component r,g,b
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        Log.d(TAG, "r,g,b,rgb " + red + ", " + green  + ", " + blue  + ", " + rgb);
        final ColorPicker cp = new ColorPicker(mParent.getActivity(), red, green, blue);
                /* Show color picker dialog */
        cp.show();

                /* On Click listener for the dialog, when the user select the color */
        Button okColor = (Button)cp.findViewById(R.id.okColorButton);

        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        /* You can get single channel (value 0-255) */
                int selectedColorR = cp.getRed();
                int selectedColorG = cp.getGreen();
                int selectedColorB = cp.getBlue();

                        /* Or the android RGB Color (see the android Color class reference) */
                int selectedColorRGB = cp.getColor();
                Log.d(TAG, "r,g,b,rgb " + selectedColorR + ", " + selectedColorG  + ", " + selectedColorB  + ", " + selectedColorRGB);

                int red = (selectedColorRGB >> 16) & 0xFF;
                int green = (selectedColorRGB >> 8) & 0xFF;
                int blue = selectedColorRGB & 0xFF;
                Log.d(TAG, "r,g,b,rgb " + red + ", " + green  + ", " + blue  + ", " + selectedColorRGB);

                cp.dismiss();

                mSelectedColorRGB = selectedColorRGB;
                // if fore color
                if (isForeColor) {
                    setForeColor(selectedColorRGB);
                }
                else {
                    setBackColor(selectedColorRGB);
                }
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean showActorMakerUI() {
        // title
        TextView tv = (TextView) mRootView.findViewById(R.id.tv_title);
        tv.setVisibility(View.VISIBLE);
        // moniker
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_moniker);
        ll.setVisibility(View.VISIBLE);
        // headline
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_headline);
        ll.setVisibility(View.VISIBLE);
        // remove
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_remove_checkbox);
        ll.setVisibility(View.VISIBLE);
        // last updated
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_last_update);
        ll.setVisibility(View.VISIBLE);
        // update/destroy/cancel buttons
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_buttons);
        ll.setVisibility(View.VISIBLE);
        // actor
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_actor);
        ll.setVisibility(View.VISIBLE);
        return true;
    }
    private Boolean hideActorMakerUI() {
        // title
        TextView tv = (TextView) mRootView.findViewById(R.id.tv_title);
        tv.setVisibility(View.GONE);
        // moniker
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_moniker);
        ll.setVisibility(View.GONE);
        // headline
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_headline);
        ll.setVisibility(View.GONE);
        // remove
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_remove_checkbox);
        ll.setVisibility(View.GONE);
        // last updated
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_last_update);
        ll.setVisibility(View.GONE);
        // update/destroy/cancel buttons
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_buttons);
        ll.setVisibility(View.GONE);
        // actor
        ll = (LinearLayout) mRootView.findViewById(R.id.ll_actor);
        ll.setVisibility(View.GONE);
        return true;
    }
    private Boolean showPaletteUI() {
        // palette
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_palette);
        ll.setVisibility(View.VISIBLE);
        return true;
    }
    private Boolean hidePaletteUI() {
        // palette
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_palette);
        ll.setVisibility(View.GONE);
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromTheatre(DaoTheatre daoTheatre) {

        // set theatre views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_theatre);
        ll.setVisibility(View.VISIBLE);

        // set checkboxes for flourish, music & action sounds
        mCheckFlourishSound = (CheckBox) mRootView.findViewById(R.id.cb_soundflourish);
        mCheckFlourishSound.setChecked(daoTheatre.getSoundFlourish());
        mCheckMusicSound = (CheckBox) mRootView.findViewById(R.id.cb_soundmusic);
        mCheckMusicSound.setChecked(daoTheatre.getSoundMusic());
        mCheckActionSound = (CheckBox) mRootView.findViewById(R.id.cb_soundaction);
        mCheckActionSound.setChecked(daoTheatre.getSoundAction());

        // default list item color to not selected
        int bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
        // check that all epics exist
        mEpicList = new ArrayList<>(daoTheatre.getTagList());
        List<String> daoEpicMonikerList = (List<String>)(List<?>) mParent.getRepoProvider().getDalEpic().getDaoRepo().getMonikerList();
        if (!daoEpicMonikerList.containsAll(mEpicList)) {
            Log.e(TAG, "Oops!  Orphan Epic in Theatre...");
            mParent.getPlayListService().repairAll(true, true);
        }

        // epic list adapter settings
        List<String> tagNameList = new ArrayList<>();
        List<String> tagLabelList = new ArrayList<>();
        List<Integer> tagImageResIdList = new ArrayList<>();
        List<Integer> tagBgColorList = new ArrayList<>();

        // dereference epic repo dao list of all epics
        List<DaoEpic> daoEpicList = (List<DaoEpic>)(List<?>) mParent.getRepoProvider().getDalEpic().getDaoRepo().getDaoList();
        // for each Epic in repo
        for (DaoEpic epic : daoEpicList) {
            // build list of epic names, labels & images
            tagNameList.add(epic.getMoniker());
            if (!epic.getHeadline().equals(DaoDefs.INIT_STRING_MARKER)) {
                tagLabelList.add(epic.getHeadline());
            }
            else {
                tagLabelList.add("epic headline activity here...");
            }
            int imageResId = DaoDefs.DAOOBJ_TYPE_EPIC_IMAGE_RESID;
            tagImageResIdList.add(imageResId);
            // epic is in tag list - set selected color
            bgColor = mRootView.getResources().getColor(R.color.colorTagListNotSelected);
            if (mEpicList.contains(epic.getMoniker())) {
                // highlight list item
                bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
            }
            tagBgColorList.add(bgColor);
        }

        // instantiate list adapter
        int resId = R.layout.tag_list_item;
        mEpicListAdapter =
                new TagListAdapter(mRootView.getContext(),
                        resId,
                        tagNameList,
                        tagLabelList,
                        tagImageResIdList,
                        tagBgColorList);

        ListView lv = (ListView) mRootView.findViewById(R.id.listview_epics);
        if (mEpicListAdapter != null && lv != null) {
            lv.setAdapter(mEpicListAdapter);
        } else {
            Log.e(TAG, "NULL mEpicListAdapter? " + mEpicListAdapter + ", R.id.listview? " + lv);
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
                if (mEpicList.contains(value)) {
                    // find epic in taglist & remove
                    int i = mEpicList.indexOf(value);
                    mEpicList.remove(i);
                    Log.d(TAG,"handleTagList remove item " + value + " at position " + i);
                }
                else {
                    // add selection to taglist
                    mEpicList.add(value);
                    // set color selected
                    bgColor = mRootView.getResources().getColor(R.color.colorTagListSelected);
                    Log.d(TAG,"handleTagList add item " + value + " at position " + (mEpicList.size()-1));
                }

                v.setBackgroundColor(bgColor);
            }
        });

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromEpic(DaoEpic daoEpic, DaoStage daoStage) {

        // set epic views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_epic);
        ll.setVisibility(View.VISIBLE);

        // Stage spinner
        // dereference repo dao list
        List<DaoStage> daoStageList = (List<DaoStage>)(List<?>) mParent.getRepoProvider().getDalStage().getDaoRepo().getDaoList();
        List<String> stageNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoStage stage : daoStageList) {
            // build list of names
            stageNameList.add(stage.getMoniker());
        }
        mStageListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                stageNameList);

        mSpinnerStages = (Spinner) mRootView.findViewById(R.id.spinner_stages);
        if (mStageListAdapter != null && mSpinnerStages != null) {
            mSpinnerStages.setAdapter(mStageListAdapter);
            if (stageNameList.contains(daoEpic.getStage())) {
                int i = stageNameList.indexOf(daoEpic.getStage());
                mSpinnerStages.setSelection(i);
            }
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mStageListAdapter? " + mStageListAdapter + ", R.id.spinner_stages? " + mSpinnerStages);
            return false;
        }

        // order radio group
        mRadioGroupOrder = (RadioGroup) mRootView.findViewById(R.id.rg_order);
        // determine order index from daoEpic
        String order = daoEpic.getOrder();
        int daoOrderInx = DaoEpic.EPIC_ORDER_LIST.indexOf(order);
        // if order not valid, default to last valid entry
        if (daoOrderInx < 0 || daoOrderInx > DaoEpic.EPIC_ORDER_LIST.size()) daoOrderInx = DaoEpic.EPIC_ORDER_LIST.size()-1;
        // set text based on order list
        for (int i = 0; i < DaoEpic.EPIC_ORDER_LIST.size(); i++) {
            RadioButton r = (RadioButton) mRadioGroupOrder.getChildAt(i);
            r.setText(DaoEpic.EPIC_ORDER_LIST.get(i));
            if (daoOrderInx == i) {
                // check dao order setting
                r.setChecked(true);
            }
            else {
                r.setChecked(false);
            }
        }
        // tally progress
        mTallySeekbar = (SeekBar) mRootView.findViewById(R.id.seekbar_tally);
        if (daoStage != null) mTallyMax = daoStage.getLocusList().locii.size();
//        mTallyProgress = (int)(((float)daoEpic.getTallyLimit()/(float) mTallyMax)*100.0);
        mTallyProgress = daoEpic.getTallyLimit();
        Log.d(TAG,"Tally progress " + mTallyProgress + " for limit/max (" + daoEpic.getTallyLimit() + "/" + mTallyMax + ")");
        mTallySeekbar.setMax(mTallyMax);
        mTallySeekbar.setProgress(mTallyProgress);
        mTvTally = (TextView) mRootView.findViewById(R.id.textview_tally);
        String label_tally = TALLY_LIMIT_TEXT + mTallyProgress;
        mTvTally.setText(label_tally);
        // establish listener
        mTallySeekbar.setOnSeekBarChangeListener(this);
        // tic progress
        mTicSeekbar = (SeekBar) mRootView.findViewById(R.id.seekbar_tic);
//        mTicProgress = (int)(((float)daoEpic.getTicLimit()/(float) mTicMax)*100.0);
        mTicProgress = daoEpic.getTicLimit();
        Log.d(TAG,"Tic progress " + mTicProgress + " for limit/max (" + daoEpic.getTicLimit() + "/" + mTicMax + ")");
        mTicSeekbar.setMax(mTicMax);
        mTicSeekbar.setProgress(mTicProgress);
        mTvTic = (TextView) mRootView.findViewById(R.id.textview_tic);
        String label_tic = TIC_LIMIT_TEXT + mTicProgress;
        mTvTic.setText(label_tic);
        // establish listener
        mTicSeekbar.setOnSeekBarChangeListener(this);

        // set local stories list for reverse xfer in fromEpic
        mStoryList = new ArrayList<>(daoEpic.getTagList());
        // check that all stories in epic exist
        List<String> daoStoryMonikerList = (List<String>)(List<?>) mParent.getRepoProvider().getDalStory().getDaoRepo().getMonikerList();
        if (!daoStoryMonikerList.containsAll(mStoryList)) {
            Log.e(TAG, "Oops!  Orphan Story in Epic...");
            mParent.getPlayListService().repairAll(true, true);
        }
        // establish storylist button
        handleStoryListButton();

        // display starboard list
        List<DaoEpicActorBoard> actorBoardList = daoEpic.getActorBoardList();
        for (DaoEpicActorBoard starBoard : actorBoardList) {
            Log.d(TAG, starBoard.toString());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleStoryListButton() {
        // establish button visibility & click listener
        final Button button = (Button) mRootView.findViewById(R.id.button_daomaker_storylist);
        button.setVisibility(View.VISIBLE);
        // button handlers
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonStoryList.setOnClickListener: ");
                // set epic views invisible
                LinearLayout ll_epic = (LinearLayout) mRootView.findViewById(R.id.ll_epic);
                ll_epic.setVisibility(View.GONE);
                // set storylist views visible
                LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_storylist);
                ll.setVisibility(View.VISIBLE);
                // load toggle button
                handleEpicSettingsButton();
                // load story list spinner
                loadStoryList();
            }
        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean handleEpicSettingsButton() {
        // establish  button visibility & click listener
        final Button button = (Button) mRootView.findViewById(R.id.button_daomaker_epicsettings);
        button.setVisibility(View.VISIBLE);
        // button handlers
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "buttonEpicSettings.setOnClickListener: ");
                // set storylist views invisible
                LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_storylist);
                ll.setVisibility(View.GONE);
                // set epic views visible
                LinearLayout ll_epic = (LinearLayout) mRootView.findViewById(R.id.ll_epic);
                ll_epic.setVisibility(View.VISIBLE);
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
    @Override
    public void onProgressChanged(SeekBar seekbar, int progress,
                                  boolean fromUser) {
        Log.d(TAG, " onProgressChanged progress " + progress);
        if (seekbar == mTallySeekbar) {
            mTallyProgress = progress;
            Log.d(TAG, " onProgressChanged TALLY progress " + mTallyProgress);
            String label_tally = TALLY_LIMIT_TEXT + mTallyProgress;
            mTvTally.setText(label_tally);

        }
        else if (seekbar == mTicSeekbar) {
            mTicProgress = progress;
            Log.d(TAG, " onProgressChanged TIC progress " + mTicProgress);
            String label_tic = TIC_LIMIT_TEXT + mTicProgress;
            mTvTic.setText(label_tic);
        }
        else {
            Log.e(TAG, "Oops! onProgressChanged UNKNOWN seekbar " + seekbar.toString());
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, " onStartTrackingTouch... ");
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, " onStopTrackingTouch  ");
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromStory(DaoStory daoStory) {

        // set story views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_story);
        ll.setVisibility(View.VISIBLE);

//        // Stage spinner
//        // dereference repo dao list
//        List<DaoStage> daoStageList = (List<DaoStage>)(List<?>) mParent.getRepoProvider().getDalStage().getDaoRepo().getDaoList();
//        List<String> stageNameList = new ArrayList<>();
//        // for each stage in repo
//        for (DaoStage stage : daoStageList) {
//            // build list of names
//            stageNameList.add(stage.getMoniker());
//        }
//        mStageListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
//                android.R.layout.simple_list_item_1,
//                stageNameList);
//
//        mSpinnerStages = (Spinner) mRootView.findViewById(R.id.spinner_stages);
//        if (mStageListAdapter != null && mSpinnerStages != null) {
//            mSpinnerStages.setAdapter(mStageListAdapter);
//            if (stageNameList.contains(daoStory.getStage())) {
//                int i = stageNameList.indexOf(daoStory.getStage());
//                mSpinnerStages.setSelection(i);
//            }
//        } else {
//            // null list adapter or spinner
//            Log.e(TAG, "NULL mStageListAdapter? " + mStageListAdapter + ", R.id.spinner_stages? " + mSpinnerStages);
//            return false;
//        }
        // prereq spinner
        List<String> prereqList = new ArrayList<>();
        prereqList.add(DaoStory.STORY_PREREQ_NONE);
        prereqList.add(DaoStory.STORY_PREREQ_VERT_OWNED);
        prereqList.add(DaoStory.STORY_PREREQ_VERT_BLOCKED);
        prereqList.add(DaoStory.STORY_PREREQ_VERT_EMPTY);
        mPreReqListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                prereqList);

        mSpinnerPreReqs = (Spinner) mRootView.findViewById(R.id.spinner_prereqs);
        if (mPreReqListAdapter != null && mSpinnerPreReqs != null) {
            mSpinnerPreReqs.setAdapter(mPreReqListAdapter);
            // TODO: ugh! refactor prereq list!
            // set current selection
            int selectInx = 0; // none
            if (daoStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_OWNED)) selectInx = 1;
            else if (daoStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_BLOCKED)) selectInx = 2;
            else if (daoStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_EMPTY)) selectInx = 3;
            mSpinnerPreReqs.setSelection(selectInx);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mPreReqListAdapter? " + mPreReqListAdapter + ", spinner PreReq? " + mSpinnerPreReqs);
            return false;
        }
        // Actor spinner
        // dereference repo dao list
        List<DaoActor> daoActorList = (List<DaoActor>)(List<?>) mParent.getRepoProvider().getDalActor().getDaoRepo().getDaoList();
        List<String> actorNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoActor actor : daoActorList) {
            // build list of names
            actorNameList.add(actor.getMoniker());
        }
        if (actorNameList.isEmpty()) {
            actorNameList.add("actors!  where are the actors?");
        }
        mActorListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                actorNameList);

        mSpinnerActors = (Spinner) mRootView.findViewById(R.id.spinner_actors);
        if (mActorListAdapter != null && mSpinnerActors != null) {
            mSpinnerActors.setAdapter(mActorListAdapter);
            if (actorNameList.contains(daoStory.getActor())) {
                int i = actorNameList.indexOf(daoStory.getActor());
                mSpinnerActors.setSelection(i);
            }
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mActorListAdapter? " + mActorListAdapter + ", R.id.spinner_Prereqs? " + mSpinnerActors);
            return false;
        }
        // Action spinner
        // dereference repo dao list
        List<DaoAction> daoActionList = (List<DaoAction>)(List<?>) mParent.getRepoProvider().getDalAction().getDaoRepo().getDaoList();
        List<String> actionNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoAction action : daoActionList) {
            // build list of names
            actionNameList.add(action.getMoniker());
        }
        if (actionNameList.isEmpty()) {
            actionNameList.add("actions!  where are the actions?");
        }
        mActionListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                actionNameList);

        mSpinnerActions = (Spinner) mRootView.findViewById(R.id.spinner_actions);
        if (mActionListAdapter != null && mSpinnerActions != null) {
            mSpinnerActions.setAdapter(mActionListAdapter);
            if (actionNameList.contains(daoStory.getAction())) {
                int i = actionNameList.indexOf(daoStory.getAction());
                mSpinnerActions.setSelection(i);
            }
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mActionListAdapter? " + mActionListAdapter + ", R.id.spinner_Action? " + mSpinnerActions);
            return false;
        }
        // Outcome spinner
        // dereference repo dao list
        List<DaoOutcome> daoOutcomeList = (List<DaoOutcome>)(List<?>) mParent.getRepoProvider().getDalOutcome().getDaoRepo().getDaoList();
        List<String> outcomeNameList = new ArrayList<>();
        // for each stage in repo
        for (DaoOutcome outcome : daoOutcomeList) {
            // build list of names
            outcomeNameList.add(outcome.getMoniker());
        }
        if (outcomeNameList.isEmpty()) {
            outcomeNameList.add("outcomes!  where are the outcomes?");
        }
        mOutcomeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                outcomeNameList);

        mSpinnerOutcomes = (Spinner) mRootView.findViewById(R.id.spinner_outcomes);
        if (mOutcomeListAdapter != null && mSpinnerOutcomes != null) {
            mSpinnerOutcomes.setAdapter(mOutcomeListAdapter);
            if (outcomeNameList.contains(daoStory.getOutcome())) {
                int i = outcomeNameList.indexOf(daoStory.getOutcome());
                mSpinnerOutcomes.setSelection(i);
            }
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mOutcomeListAdapter? " + mOutcomeListAdapter + ", R.id.spinner_Outcomes? " + mSpinnerOutcomes);
            return false;
        }

        // postop spinner
        List<String> postopList = new ArrayList<>();
        postopList.add(DaoStory.STORY_POSTOP_NONE);
        postopList.add(DaoStory.STORY_POSTOP_CURTAIN_CALL);
        mPostOpListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                postopList);

        mSpinnerPostOps = (Spinner) mRootView.findViewById(R.id.spinner_postops);
        if (mPostOpListAdapter != null && mSpinnerPostOps != null) {
            mSpinnerPostOps.setAdapter(mPostOpListAdapter);
            // set current selection
            int selectInx = 0; // none
            if (daoStory.getPostOp().equals(DaoStory.STORY_POSTOP_CURTAIN_CALL)) selectInx = 1;
            mSpinnerPostOps.setSelection(selectInx);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mPostOpListAdapter? " + mPostOpListAdapter + ", spinner PostOp? " + mSpinnerPostOps);
            return false;
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromStage(DaoStage daoStage) {

        // set Stage views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_stage);
        ll.setVisibility(View.VISIBLE);

        // ring type spinner
//        List<String> stageTypeList = new ArrayList<>();
//        stageTypeList.add(DaoStage.STAGE_TYPE_RING);
        List<String> stageTypeList = DaoStage.STAGE_TYPE_LIST;
        mStageTypeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                stageTypeList);

        mSpinnerStageType = (Spinner) mRootView.findViewById(R.id.spinner_ringtype);
        if (mStageTypeListAdapter != null && mSpinnerStageType != null) {
            mSpinnerStageType.setAdapter(mStageTypeListAdapter);
        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mStageTypeListAdapter? " + mStageTypeListAdapter + ", spinner stagetype? " + mSpinnerStageType);
            return false;
        }

        EditText etRingSize = (EditText) mRootView.findViewById(R.id.et_ringsize);
        TextView tvLociiSize = (TextView) mRootView.findViewById(R.id.tv_lociisize);
        Integer selectionInx = 0;
        if (daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            selectionInx = 0;
//            // set ring size, locii size
//            etRingSize.setText(daoStage.getRingSize().toString());
//            etRingSize.setVisibility(View.VISIBLE);
//
//            Integer size = daoStage.getLocusList().locii.size();
//            tvLociiSize.setText(size.toString());
//            tvLociiSize.setVisibility(View.VISIBLE);
        }
        else if (daoStage.getStageType().equals(DaoStage.STAGE_TYPE_ARCORE)) {
            selectionInx = 1;
//            etRingSize.setVisibility(View.GONE);
//            tvLociiSize.setVisibility(View.GONE);
        }
        mSpinnerStageType.setSelection(selectionInx);

        final DaoStage daoStageFinal = daoStage;
        mSpinnerStageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                LinearLayout llRingSize = (LinearLayout) mRootView.findViewById(R.id.ll_ringsize);
                LinearLayout llLociiSize = (LinearLayout) mRootView.findViewById(R.id.ll_lociisize);
                // STAGE_TYPE_RING
                if (DaoStage.STAGE_TYPE_LIST.get(position).equals(DaoStage.STAGE_TYPE_RING)) {
                    // set ring size, locii size
                    llRingSize.setVisibility(View.VISIBLE);
                    llLociiSize.setVisibility(View.VISIBLE);

                    EditText etRingSize = (EditText) mRootView.findViewById(R.id.et_ringsize);
                    etRingSize.setText(daoStageFinal.getRingSize().toString());

                    TextView tvLociiSize = (TextView) mRootView.findViewById(R.id.tv_lociisize);
                    Integer size = daoStageFinal.getLocusList().locii.size();
                    tvLociiSize.setText(size.toString());
                }
                else if (DaoStage.STAGE_TYPE_LIST.get(position).equals(DaoStage.STAGE_TYPE_ARCORE)) {
                    llRingSize.setVisibility(View.GONE);
                    llLociiSize.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromActor(DaoActor daoActor) {

        // set Actor views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_actor);
        ll.setVisibility(View.VISIBLE);

        if (daoActor != null) {
            // Actor type spinner
            mActorTypeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                    android.R.layout.simple_list_item_1,
                    DaoActor.ACTOR_TYPE_LIST);
            Log.d(TAG,"fromActor ACTOR_TYPE_LIST " + DaoActor.ACTOR_TYPE_LIST);
            mSpinnerActorTypes = (Spinner) mRootView.findViewById(R.id.spinner_actortypes);
            if (mActorTypeListAdapter != null && mSpinnerActorTypes != null) {
                mSpinnerActorTypes.setAdapter(mActorTypeListAdapter);
                int inx = DaoActor.speedToActorTypeInx(daoActor.getSpeed());
                mSpinnerActorTypes.setSelection(inx);
            } else {
                // null list adapter or spinner
                Log.e(TAG, "NULL mActorTypeListAdapter? " + mActorTypeListAdapter + ", R.id.spinner_Prereqs? " + mSpinnerActorTypes);
                return false;
            }

            // establish fore color button visibility & click listener
            mButtonForeColor = (Button) mRootView.findViewById(R.id.button_daomaker_forecolor);
            mButtonForeColor.setVisibility(View.VISIBLE);
            setForeColor(daoActor.getForeColor());
//        Log.d(TAG, "fore color rgb " + getForeColor());

            mButtonForeColor.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.v(TAG, "buttonForeColor.setOnClickListener: ");
                    Toast.makeText(mRootView.getContext(), "buttonForeColor...", Toast.LENGTH_SHORT).show();
                    if (LAUNCH_COLOR_PICKER) {
                        // launch color picker
                        isForeColor(true);
                        launchColorPicker(isForeColor(), getForeColor());
                    }
                    else {
                        // setup handler for accept palette
                        mButtonAcceptPalette = (Button) mRootView.findViewById(R.id.button_palette_accept);
                        mButtonAcceptPalette.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Log.v(TAG, "mButtonAcceptPalette.setOnClickListener: ");
                                Toast.makeText(mRootView.getContext(), "mButtonAcceptPalette...", Toast.LENGTH_SHORT).show();
                                // assign primary color from accept button
                                // TODO:
                                // hide palette UI & show actor UI
                                hidePaletteUI();
                                showActorMakerUI();
                            }
                        });

                        // set handler for cancel palette
                        mButtonQuitPalette = (Button) mRootView.findViewById(R.id.button_palette_quit);
                        mButtonQuitPalette.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Log.v(TAG, "mButtonQuitPalette.setOnClickListener: ");
                                Toast.makeText(mRootView.getContext(), "mButtonQuitPalette...", Toast.LENGTH_SHORT).show();
                                // hide palette UI & show actor UI
                                hidePaletteUI();
                                showActorMakerUI();
                            }
                        });
                        // hide maker UI elements
                        hideActorMakerUI();
                        // show palette UI elements
                        showPaletteUI();
                    }
                }
            });
            // establish back color button visibility & click listener
            mButtonBackColor = (Button) mRootView.findViewById(R.id.button_daomaker_backcolor);
            mButtonBackColor.setVisibility(View.VISIBLE);
            setBackColor(daoActor.getBackColor());
//        Log.d(TAG, "Back color rgb " + getBackColor());

            mButtonBackColor.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.v(TAG, "buttonBackColor.setOnClickListener: ");
                    Toast.makeText(mRootView.getContext(), "buttonBackColor...", Toast.LENGTH_SHORT).show();
                    // launch color picker
                    isForeColor(false);
                    launchColorPicker(isForeColor(), getBackColor());
                }
            });
        }
        else {
            Log.e(TAG, "Oops! actor NULL...");
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromAction(DaoAction daoAction) {

        // set Action views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_action);
        ll.setVisibility(View.VISIBLE);

        // action spinner
        final List<String> actionTypeList = DaoAction.getActionTypeList();
        mActionTypeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                actionTypeList);

        mSpinnerActionType = (Spinner) mRootView.findViewById(R.id.spinner_actiontype);
        if (mActionTypeListAdapter != null && mSpinnerActionType != null) {
            mSpinnerActionType.setAdapter(mActionTypeListAdapter);
            // set current selection
            if (actionTypeList.contains(daoAction.getActionType())) {
                int i = actionTypeList.indexOf(daoAction.getActionType());
                mSpinnerActionType.setSelection(i);
            }
            // establish listener
            mSpinnerActionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                    List<String> actionTypeList = DaoAction.getActionTypeList();
                    String actionType = actionTypeList.get(position);
                    Log.d(TAG,"ActionType " + actionType + " at position " + position);
                    EditText etMoniker = (EditText) mRootView.findViewById(R.id.et_moniker);
                    etMoniker.setText(actionType);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mActionTypeListAdapter? " + mActionTypeListAdapter + ", R.id.spinner_actiontype? " + mSpinnerActionType);
            return false;
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean fromOutcome(DaoOutcome daoOutcome) {
        // set Outcome views visible
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.ll_outcome);
        ll.setVisibility(View.VISIBLE);

        // outcome spinner
        final List<String> outcomeTypeList = DaoOutcome.getOutcomeTypeList();
        mOutcomeTypeListAdapter = new ArrayAdapter<String>(mRootView.getContext(),
                android.R.layout.simple_list_item_1,
                outcomeTypeList);

        mSpinnerOutcomeType = (Spinner) mRootView.findViewById(R.id.spinner_outcometype);
        if (mOutcomeTypeListAdapter != null && mSpinnerOutcomeType != null) {
            mSpinnerOutcomeType.setAdapter(mOutcomeTypeListAdapter);
            // set current selection
            if (outcomeTypeList.contains(daoOutcome.getOutcomeType())) {
                int i = outcomeTypeList.indexOf(daoOutcome.getOutcomeType());
                mSpinnerOutcomeType.setSelection(i);
            }
            // establish listener
            mSpinnerOutcomeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String outcomeType = outcomeTypeList.get(position);
                    Log.d(TAG,"OutcomeType " + outcomeType + " at position " + position);
                    EditText etMoniker = (EditText) mRootView.findViewById(R.id.et_moniker);
                    etMoniker.setText(outcomeType);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        } else {
            // null list adapter or spinner
            Log.e(TAG, "NULL mOutcomeTypeListAdapter? " + mOutcomeTypeListAdapter + ", R.id.spinner_outcometype? " + mSpinnerOutcomeType);
            return false;
        }

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toTheatre(String op, String moniker, String editedMoniker, String headline, Boolean removeOriginalOnMonikerChange, List<String> tagList) {
        // active object
        DaoTheatre activeTheatre = null;
        // xfer view to theatre object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeTheatre = (DaoTheatre) mParent.getRepoProvider().getDalTheatre().getDaoRepo().get(moniker);
            if (activeTheatre != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker) && removeOriginalOnMonikerChange) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalTheatre().remove(activeTheatre, true);
                }
            }
        }
        if (activeTheatre == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeTheatre = new DaoTheatre();
            Log.e(TAG, "toTheatre: editing NULL object?");
        }

        // update with edited values
        activeTheatre.setMoniker(editedMoniker);
        activeTheatre.setHeadline(headline);
//        activeTheatre.setTagList(tagList);
        activeTheatre.setTagList(mEpicList);
        // set checkboxes for flourish, music & action sounds
        mCheckFlourishSound = (CheckBox) mRootView.findViewById(R.id.cb_soundflourish);
        activeTheatre.setSoundFlourish(mCheckFlourishSound.isChecked());
        mCheckMusicSound = (CheckBox) mRootView.findViewById(R.id.cb_soundmusic);
        activeTheatre.setSoundMusic(mCheckMusicSound.isChecked());
        mCheckActionSound = (CheckBox) mRootView.findViewById(R.id.cb_soundaction);
        activeTheatre.setSoundAction(mCheckActionSound.isChecked());

        // set theatre active
        mParent.getPlayListService().setActiveTheatre(activeTheatre);
        // update repo
        mParent.getRepoProvider().getDalTheatre().update(activeTheatre, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toEpic(String op, String moniker, String editedMoniker, String headline, Boolean removeOriginalOnMonikerChange) {
        // active object
        DaoEpic activeEpic = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeEpic = (DaoEpic) mParent.getRepoProvider().getDalEpic().getDaoRepo().get(moniker);
            if (activeEpic != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker) && removeOriginalOnMonikerChange) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalEpic().remove(activeEpic, true);
                }
            }
        }
        if (activeEpic == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeEpic = new DaoEpic();
            Log.e(TAG, "toEpic: new object....");
        }
        // update with edited values
        activeEpic.setMoniker(editedMoniker);
        activeEpic.setHeadline(headline);

//        Log.d(TAG, "toEpic selected stage " + mSpinnerStages.getSelectedItem().toString());
//        activeEpic.setStage(mSpinnerStages.getSelectedItem().toString());
        String selectedStageMoniker = mSpinnerStages.getSelectedItem().toString();
        Log.d(TAG, "toEpic selected stage " + selectedStageMoniker);

        CheckBox cbRestart = (CheckBox) mRootView.findViewById(R.id.cb_restart);
        Boolean restartEpic = cbRestart.isChecked();
        if (restartEpic) {
            // if restart Epic selected, use selected stage as template for current stage
            DaoStage selectedDaoStage = (DaoStage) mParent.getRepoProvider().getDalStage().getDaoRepo().get(selectedStageMoniker);
            if (selectedDaoStage != null) {
                String currentStageMoniker = activeEpic.getStage();
                DaoStage currentDaoStage = (DaoStage) mParent.getRepoProvider().getDalStage().getDaoRepo().get(currentStageMoniker);
                if (currentDaoStage != null) {
                    // make a copy of the template using the existing stage moniker
                    toStage (op, selectedStageMoniker, currentStageMoniker, currentDaoStage.getHeadline(), false);
                }
                else {
                    Log.e(TAG, "Oops! toEpic unable to find current stage " + currentStageMoniker);
                }
            }
            else {
                Log.e(TAG, "Oops! toEpic unable to find selected stage " + selectedStageMoniker);
            }
        }
        else {
            Log.d(TAG, "toEpic set active stage to selected stage " + selectedStageMoniker);
            activeEpic.setStage(selectedStageMoniker);
        }

//        activeEpic.setTagList(tagList);
        activeEpic.setTagList(mStoryList);

        // set order radio group
        mRadioGroupOrder = (RadioGroup) mRootView.findViewById(R.id.rg_order);
        // get checked order index
        int checkedOrderIndex = mRadioGroupOrder.indexOfChild(mRootView.findViewById(mRadioGroupOrder.getCheckedRadioButtonId()));
        // if order not valid, default to last valid entry
        if (checkedOrderIndex < 0 || checkedOrderIndex > DaoEpic.EPIC_ORDER_LIST.size()) checkedOrderIndex = DaoEpic.EPIC_ORDER_LIST.size()-1;
        activeEpic.setOrder(DaoEpic.EPIC_ORDER_LIST.get(checkedOrderIndex));

        // set tally progress
        activeEpic.setTallyLimit(mTallyProgress);
        // tic progress
        activeEpic.setTicLimit(mTicProgress);

        if (TEST_SET_PLAYLIST) mParent.getPlayListService().setActiveEpic(activeEpic);
        // update repo
        mParent.getRepoProvider().getDalEpic().update(activeEpic, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toStory(String op, String moniker, String editedMoniker, String headline, Boolean removeOriginalOnMonikerChange) {
        // active object
        DaoStory activeStory = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeStory = (DaoStory) mParent.getRepoProvider().getDalStory().getDaoRepo().get(moniker);
            if (activeStory != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker) && removeOriginalOnMonikerChange) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalStory().remove(activeStory, true);
                }
            }
        }
        if (activeStory == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeStory = new DaoStory();
            Log.d(TAG, "toStory: new object....");
        }
        // update with edited values
        activeStory.setMoniker(editedMoniker);
        activeStory.setHeadline(headline);
//        Log.d(TAG, "toStory selected stage " + mSpinnerStages.getSelectedItem().toString());
//        activeStory.setStage(mSpinnerStages.getSelectedItem().toString());
        Log.d(TAG, "toStory selected prereq " + mSpinnerPreReqs.getSelectedItem().toString());
        activeStory.setPreReq(mSpinnerPreReqs.getSelectedItem().toString());
        Log.d(TAG, "toStory selected actor " + mSpinnerActors.getSelectedItem().toString());
        activeStory.setActor(mSpinnerActors.getSelectedItem().toString());
        Log.d(TAG, "toStory selected action " + mSpinnerActions.getSelectedItem().toString());
        activeStory.setAction(mSpinnerActions.getSelectedItem().toString());
        Log.d(TAG, "toStory selected outcome " + mSpinnerOutcomes.getSelectedItem().toString());
        activeStory.setOutcome(mSpinnerOutcomes.getSelectedItem().toString());
        Log.d(TAG, "toStory selected postop " + mSpinnerPostOps.getSelectedItem().toString());
        activeStory.setPostOp(mSpinnerPostOps.getSelectedItem().toString());

        if (TEST_SET_PLAYLIST) mParent.getPlayListService().setActiveStory(activeStory);
        // update repo
        mParent.getRepoProvider().getDalStory().update(activeStory, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toStage(String op, String moniker, String editedMoniker, String headline, Boolean removeOriginalOnMonikerChange) {
        // active object
        DaoStage activeStage = null;
        Boolean copyStageTemplate = false;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeStage = (DaoStage) mParent.getRepoProvider().getDalStage().getDaoRepo().get(moniker);
            if (activeStage != null) {
                // if existing stage renamed (moniker has been edited)
                if (!moniker.equals(editedMoniker)) {
                    // if replace original
                    if (removeOriginalOnMonikerChange) {
                        // remove obsolete entry
                        mParent.getRepoProvider().getDalStage().remove(activeStage, true);
                    }
                    else {
                        // copy stage as template in progress
                        copyStageTemplate = true;
                    }
                }
            }
        }
        if (activeStage == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeStage = new DaoStage();
            Log.e(TAG, "toStage: new object....");
        }
        // update with edited values
        activeStage.setMoniker(editedMoniker);
        activeStage.setHeadline(headline);
        activeStage.setStageType(DaoStage.STAGE_TYPE_RING);
        // ignore ring type
        EditText etRingSize = (EditText) mRootView.findViewById(R.id.et_ringsize);
        try {
            Integer ringSize = Integer.parseInt(etRingSize.getText().toString());
            activeStage.setRingSize(ringSize);
        }
        catch (Exception ex) {
            activeStage.setRingSize(DaoStage.STAGE_TYPE_RING_SIZE_DEFAULT);
        }
        if (TEST_SET_PLAYLIST) mParent.getPlayListService().setActiveStage(activeStage);
        // update repo
        mParent.getRepoProvider().getDalStage().update(activeStage, true);

        // copy stage template in progress
        if (copyStageTemplate) {
            // if active epic
            DaoEpic activeEpic = mParent.getPlayListService().getActiveEpic();
            if (activeEpic != null) {
                // update epic stage
                activeEpic.setStage(editedMoniker);
                // reset epic actor board & active actor
                activeEpic.resetActorBoard(activeStage, true);
                // set active actor
                DaoActor daoActor = (DaoActor) mParent.getRepoProvider().getDalActor().getDaoRepo().get(activeEpic.getActiveActor());
                Log.d(TAG,"epic active actor after reset " + activeEpic.getActiveActor());
                if (TEST_SET_PLAYLIST) mParent.getPlayListService().setActiveActor(daoActor);
                // update epic repo
                mParent.getRepoProvider().getDalEpic().update(activeEpic, true);
            }
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toActor(String op, String moniker, String editedMoniker, String headline, Boolean removeOriginalOnMonikerChange) {
        // active object
        DaoActor activeActor = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeActor = (DaoActor) mParent.getRepoProvider().getDalActor().getDaoRepo().get(moniker);
            if (activeActor != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker) && removeOriginalOnMonikerChange) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalActor().remove(activeActor, true);
                }
            }
        }
        if (activeActor == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeActor = new DaoActor();
            Log.e(TAG, "toActor: new object....");
        }
        // update with edited values
        activeActor.setMoniker(editedMoniker);
        activeActor.setHeadline(headline);
        // TODO mSpinnerActorTypes.getSelectedItem().toString() -1 exception
        Integer speed = DaoActor.actorTypeToSpeed(mSpinnerActorTypes.getSelectedItem().toString());
        activeActor.setSpeed(speed);
        Log.d(TAG, "toActor selected actor type(" + speed + ") " + mSpinnerActorTypes.getSelectedItem().toString());

        if (getForeColor() != activeActor.getForeColor()) activeActor.setForeColor(getForeColor());
        if (getBackColor() != activeActor.getBackColor()) activeActor.setBackColor(getBackColor());
//        // if starring on device
//        CheckBox cbStar = (CheckBox) mRootView.findViewById(R.id.cb_star);
//        DaoEpic daoEpic = mParent.getPlayListService().getActiveEpic();
//        if (daoEpic != null && cbStar != null && cbStar.isChecked()) {
//            // add to epic star list
//            daoEpic.addActorBoard(activeActor);
//            Log.d(TAG, "toActor: new STAR " + activeActor.getMoniker() + " on device " + DevUtils.getDeviceName() + "...");
//            // update repo
//            mParent.getRepoProvider().getDalEpic().update(daoEpic, true);
//        }

        if (TEST_SET_PLAYLIST) mParent.getPlayListService().setActiveActor(activeActor);
        // update repo
        mParent.getRepoProvider().getDalActor().update(activeActor, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toAction(String op, String moniker, String editedMoniker, String headline, Boolean removeOriginalOnMonikerChange) {
        // active object
        DaoAction activeAction = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeAction = (DaoAction) mParent.getRepoProvider().getDalAction().getDaoRepo().get(moniker);
            if (activeAction != null && removeOriginalOnMonikerChange) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker)) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalAction().remove(activeAction, true);
                }
            }
        }
        if (activeAction == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeAction = new DaoAction();
            Log.e(TAG, "toAction: new object....");
        }
        // update with edited values
        activeAction.setMoniker(editedMoniker);
        activeAction.setHeadline(headline);
        Log.d(TAG, "toAction selected action type " + mSpinnerActionType.getSelectedItem().toString());
        activeAction.setActionType(mSpinnerActionType.getSelectedItem().toString());
        if (TEST_SET_PLAYLIST) mParent.getPlayListService().setActiveAction(activeAction);
        // update repo
        mParent.getRepoProvider().getDalAction().update(activeAction, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toOutcome(String op, String moniker, String editedMoniker, String headline, Boolean removeOriginalOnMonikerChange) {
        // active object
        DaoOutcome activeOutcome = null;
        // xfer view to object
        if (op.equals(ContentFragment.ARG_CONTENT_VALUE_OP_EDIT)) {
            activeOutcome = (DaoOutcome) mParent.getRepoProvider().getDalOutcome().getDaoRepo().get(moniker);
            if (activeOutcome != null) {
                // if moniker has been edited
                if (!moniker.equals(editedMoniker) && removeOriginalOnMonikerChange) {
                    // remove obsolete entry
                    mParent.getRepoProvider().getDalOutcome().remove(activeOutcome, true);
                }
            }
            else {
                // error: never should edit NULL object!  create a placeholder & carry on
                activeOutcome = new DaoOutcome();
                Log.e(TAG, "toOutcome: editing NULL object?");
            }
        }
        if (activeOutcome == null) {
            // new op or error: never should edit NULL object!  create a placeholder & carry on
            activeOutcome = new DaoOutcome();
            Log.e(TAG, "toAction: new object....");
        }
        // update with edited values
        activeOutcome.setMoniker(editedMoniker);
        activeOutcome.setHeadline(headline);
        Log.d(TAG, "toOutcome selected action type " + mSpinnerOutcomeType.getSelectedItem().toString());
        activeOutcome.setOutcomeType(mSpinnerOutcomeType.getSelectedItem().toString());

        if (TEST_SET_PLAYLIST) mParent.getPlayListService().setActiveOutcome(activeOutcome);
        // update repo
        mParent.getRepoProvider().getDalOutcome().update(activeOutcome, true);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
