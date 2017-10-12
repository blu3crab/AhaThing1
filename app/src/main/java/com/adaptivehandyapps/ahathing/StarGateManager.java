/*
 * Project: AhaThing1
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker SEP 2017
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adaptivehandyapps.ahathing.ahautils.DevUtils;
import com.adaptivehandyapps.ahathing.auth.AnonymousAuthActivity;
import com.adaptivehandyapps.ahathing.auth.EmailPasswordActivity;
import com.adaptivehandyapps.ahathing.auth.GoogleSignInActivity;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStarGate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

////////////////////////////////////////////////////////////////////////////
// StarGateManager: manage StarGate actions and outcomes
public class StarGateManager {
    private static final String TAG = StarGateManager.class.getSimpleName();

    public static final String STAR_MONIKER_NADA = "Signin Here!";

    // activity types
    @IntDef({ACTIVITY_TYPE_UNKNOWN,
            ACTIVITY_TYPE_PLAY,
            ACTIVITY_TYPE_SIGNING,
            ACTIVITY_TYPE_INVITE,
            ACTIVITY_TYPE_ACCEPT,
            ACTIVITY_TYPE_CHAT
    })
    public @interface DaoObjType {}

    public static final int ACTIVITY_TYPE_UNKNOWN = -1;
    public static final int ACTIVITY_TYPE_PLAY = 0;
    public static final int ACTIVITY_TYPE_SIGNING = 5;
    public static final int ACTIVITY_TYPE_INVITE = 1;
    public static final int ACTIVITY_TYPE_ACCEPT = 2;
    public static final int ACTIVITY_TYPE_CHAT = 3;

    public static final String STARGATE_ACTIVITY_PLAY = "Play!";
    public static final String STARGATE_ACTIVITY_SIGNIN = "SignIn";
    public static final String STARGATE_ACTIVITY_SIGNOUT = "SignOut";
    public static final String STARGATE_ACTIVITY_INVITE = "Invite";
    public static final String STARGATE_ACTIVITY_UNINVITE = "UnInvite";
    public static final String STARGATE_ACTIVITY_ACCEPT = "Accept";
    public static final String STARGATE_ACTIVITY_DECLINE = "Decline";
    public static final String STARGATE_ACTIVITY_CHAT = "Chat";

    private static final int STARGATE_SIGNIN_GOOGLE = 0;
    private static final int STARGATE_SIGNIN_EMAIL = 1;
    private static final int STARGATE_SIGNIN_ANON = 2;
    private static final int STARGATE_SIGNIN_OPTIONS = 3;

    private Context mContext;
    private MainActivity mParent;

    private StarGateView mStarGateView;
    private StarGateModel mStarGateModel;

    private Boolean mPlayActive = false;
    private Boolean mSignInActive = false;
    private Boolean mSignOutActive = false;
    private Boolean mInviteActive = false;
    private Boolean mAcceptActive = false;
    private Boolean mChatActive = false;
    ///////////////////////////////////////////////////////////////////////////
    // touch position
    private float mTouchX = 0.0f;
    private float mTouchY = 0.0f;
    private float mVelocityX = 0.0f;
    private float mVelocityY = 0.0f;
    private MotionEvent mEvent1;
    private MotionEvent mEvent2;

    private int mMarkIndex = DaoDefs.INIT_INTEGER_MARKER;
    ///////////////////////////////////////////////////////////////////////////
    // service setters/getters

    public Context getContext() {
        return mContext;
    }
    public void setContext(Context context) {
        this.mContext = context;
    }

    public MainActivity getParent() {
        return mParent;
    }
    public void setParent(MainActivity parent) {
        this.mParent = parent;
    }

    private PlayListService getPlayListService() {
        return getParent().getPlayListService();
    }
    private RepoProvider getRepoProvider() {
        return getParent().getRepoProvider();
    }
    private SoundManager getSoundManager() {
        return getParent().getSoundManager();
    }

    public StarGateView getStarGateView() {
        return mStarGateView;
    }
    public void setStarGateView(StarGateView starGateView) {
        this.mStarGateView = starGateView;
    }

    public StarGateModel getStarGateModel() {
        return mStarGateModel;
    }
    public void setStarGateModel(StarGateModel starGateModel) {
        this.mStarGateModel = starGateModel;
    }

    // stargate
    private DaoStarGate mDaoStarGate = new DaoStarGate();

    public String getStarMoniker() { return mDaoStarGate.getMoniker(); }

    public DaoStarGate setStarGate() {
        // default stargate to inactive (not signed in)
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String starMoniker = STAR_MONIKER_NADA;
        String deviceDescription = DevUtils.getDeviceName();
        String email = DaoDefs.INIT_STRING_MARKER;
        Boolean active = false;
        // if firebase user exists
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            Log.d(TAG, "Firebase DisplayName " + user.getDisplayName());
            // update active StarGate with display name, email
            starMoniker = user.getDisplayName();
            email = user.getEmail();
            active = true;
        }
        // create new
        DaoStarGate daoStarGate = new DaoStarGate();
        // update StarGate
        daoStarGate.setMoniker(deviceId);
        daoStarGate.setStarMoniker(starMoniker);
        daoStarGate.setDeviceDescription(deviceDescription);
        daoStarGate.setEmail(email);
        daoStarGate.setActive(active);
        Log.d(TAG,daoStarGate.toString());
        // retain stargate
        mDaoStarGate = daoStarGate;

        if (getRepoProvider() != null) {
            // update repo - device id is key to update or add new entry
            getRepoProvider().getDalStarGate().update(daoStarGate, true);

            // dereference epic repo dao list of all epics
            List<DaoStarGate> daoStarGateList = (List<DaoStarGate>) (List<?>) getRepoProvider().getDalStarGate().getDaoRepo().getDaoList();
            // for each stargate in repo
            for (DaoStarGate starGate : daoStarGateList) {
                // log
                Log.d(TAG, "setStarGate -> " + starGate.toString());
            }
        }
        return daoStarGate;
    }


    ///////////////////////////////////////////////////////////////////////////
    // sound settings
    private Boolean isSoundFlourish() {
        if (getPlayListService() != null && getPlayListService().getActiveTheatre() != null) {
            return getPlayListService().getActiveTheatre().getSoundFlourish();
        }
        return false;
    }
    private Boolean isSoundMusic() {
        if (getPlayListService() != null && getPlayListService().getActiveTheatre() != null) {
            return getPlayListService().getActiveTheatre().getSoundMusic();
        }
        return false;
    }
    private Boolean isSoundAction() {
        if (getPlayListService() != null && getPlayListService().getActiveTheatre() != null) {
            return getPlayListService().getActiveTheatre().getSoundAction();
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // activity setters/getters

    public Boolean getPlayActive() {
        return mPlayActive;
    }
    public void setPlayActive(Boolean playActive) {
        this.mPlayActive = playActive;
    }

    public Boolean getSignInActive() {
        return mSignInActive;
    }
    public void setSignInActive(Boolean signingActive) {
        this.mSignInActive = signingActive;
    }

    public Boolean getSignOutActive() {
        return mSignOutActive;
    }
    public void setSignOutActive(Boolean signingActive) {
        this.mSignOutActive = signingActive;
    }

    public Boolean getInviteActive() {
        return mInviteActive;
    }
    public void setInviteActive(Boolean inviteActive) {
        this.mInviteActive = inviteActive;
    }

    public Boolean getAcceptActive() {
        return mAcceptActive;
    }
    public void setAcceptActive(Boolean acceptActive) {
        this.mAcceptActive = acceptActive;
    }

    public Boolean getChatActive() {
        return mChatActive;
    }
    public void setChatActive(Boolean chatActive) {
        this.mChatActive = chatActive;
    }

    ///////////////////////////////////////////////////////////////////////////
    // touch setters/getters
    public float getTouchX() {
        return mTouchX;
    }
    public void setTouchX(float touchX) {
        this.mTouchX = touchX;
    }

    public float getTouchY() {
        return mTouchY;
    }
    public void setTouchY(float touchY) {
        this.mTouchY = touchY;
    }

    public float getVelocityX() {
        return mVelocityX;
    }
    public void setVelocityX(float velocityX) {
        this.mVelocityX = velocityX;
    }

    public float getVelocityY() {
        return mVelocityY;
    }

    public void setVelocityY(float mVelocityY) {
        this.mVelocityY = mVelocityY;
    }

    public MotionEvent getEvent1() {
        return mEvent1;
    }
    public void setEvent1(MotionEvent event1) {
        this.mEvent1 = event1;
    }

    public MotionEvent getEvent2() {
        return mEvent2;
    }
    public void setEvent2(MotionEvent event2) {
        this.mEvent2 = event2;
    }

    public int getMarkIndex() {
        return mMarkIndex;
    }
    public void setMarkIndex(int markIndex) {
        this.mMarkIndex = markIndex;
    }

    ///////////////////////////////////////////////////////////////////////////
    public StarGateManager(Context context) {

        setContext(context);
        setParent ((MainActivity) context);
        if (getParent() != null) {
            Log.v(TAG, "StarGateManager ready with parent " + getParent().toString() + "...");
            if (getSoundManager() != null && isSoundMusic()) {
                getSoundManager().startSound(
                        getSoundManager().getMpMusic(),
                        SoundManager.SOUND_VOLUME_QTR,
                        SoundManager.SOUND_VOLUME_QTR,
                        SoundManager.SOUND_START_TIC_NADA,
                        SoundManager.SOUND_START_TIC_NADA);
            }
//            else Log.e(TAG, "Oops!  SoundManager NULL?");
        }
        else {
            Log.e(TAG, "Oops!  StarGateManager Parent context (MainActivity) NULL!");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean updateModel(StarGateModel starGateModel) {
        Log.d(TAG, "updateModel...");
        setStarGateModel(starGateModel);
        // if stage active, enable play selection
        setPlayActive(getPlayListService().isActiveStage());
        starGateModel.getActivityList().set(ACTIVITY_TYPE_PLAY, STARGATE_ACTIVITY_PLAY);
        if (getPlayActive()) {
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_PLAY, getContext().getResources().getColor(R.color.colorStarGateAccent));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_PLAY, getContext().getResources().getColor(R.color.colorStarGatePrimary));
        }
        else {
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_PLAY, getContext().getResources().getColor(R.color.colorBrightGrey));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_PLAY, getContext().getResources().getColor(R.color.colorDarkGrey));
        }
        // if star active
        if (!getStarMoniker().equals(DaoDefs.INIT_STRING_MARKER) && !getStarMoniker().equals(STAR_MONIKER_NADA)) {
            // disable signin & enable signout activity
            setSignInActive(false);
            setSignOutActive(true);
            starGateModel.getActivityList().set(ACTIVITY_TYPE_SIGNING, STARGATE_ACTIVITY_SIGNOUT);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_SIGNING, getContext().getResources().getColor(R.color.colorStarGateAccent));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_SIGNING, getContext().getResources().getColor(R.color.colorStarGatePrimary));
            // enable invite activity
            setInviteActive(true);
            starGateModel.getActivityList().set(ACTIVITY_TYPE_INVITE, STARGATE_ACTIVITY_INVITE);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_INVITE, getContext().getResources().getColor(R.color.colorStarGateAccent));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_INVITE, getContext().getResources().getColor(R.color.colorStarGatePrimary));
            // enable chat activity
            setInviteActive(true);
            starGateModel.getActivityList().set(ACTIVITY_TYPE_CHAT, STARGATE_ACTIVITY_CHAT);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_CHAT, getContext().getResources().getColor(R.color.colorStarGateAccent));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_CHAT, getContext().getResources().getColor(R.color.colorStarGatePrimary));
        }
        else {
            // disable signout & enable signin activity
            setSignOutActive(false);
            setSignInActive(true);
            starGateModel.getActivityList().set(ACTIVITY_TYPE_SIGNING, STARGATE_ACTIVITY_SIGNIN);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_SIGNING, getContext().getResources().getColor(R.color.colorStarGateAccent));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_SIGNING, getContext().getResources().getColor(R.color.colorStarGatePrimary));
            // disable invite activity
            setInviteActive(false);
            starGateModel.getActivityList().set(ACTIVITY_TYPE_INVITE, STARGATE_ACTIVITY_INVITE);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_INVITE, getContext().getResources().getColor(R.color.colorBrightGrey));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_INVITE, getContext().getResources().getColor(R.color.colorDarkGrey));
            // disable chat activity
            setInviteActive(false);
            starGateModel.getActivityList().set(ACTIVITY_TYPE_CHAT, STARGATE_ACTIVITY_CHAT);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_CHAT, getContext().getResources().getColor(R.color.colorBrightGrey));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_CHAT, getContext().getResources().getColor(R.color.colorDarkGrey));
        }
        // if invitation received
        if (getSignInActive() && getAcceptActive()) {
            // enable accept activity
            // TODO: setInviteActive(true);  incoming notification enables accept activity
            starGateModel.getActivityList().set(ACTIVITY_TYPE_ACCEPT, STARGATE_ACTIVITY_ACCEPT);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_ACCEPT, getContext().getResources().getColor(R.color.colorStarGateAccent));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_ACCEPT, getContext().getResources().getColor(R.color.colorStarGatePrimary));
        }
        else {
            // disable accept activity
            setAcceptActive(false);
            starGateModel.getActivityList().set(ACTIVITY_TYPE_ACCEPT, STARGATE_ACTIVITY_ACCEPT);
            starGateModel.getForeColorList().set(ACTIVITY_TYPE_ACCEPT, getContext().getResources().getColor(R.color.colorBrightGrey));
            starGateModel.getBackColorList().set(ACTIVITY_TYPE_ACCEPT, getContext().getResources().getColor(R.color.colorDarkGrey));
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onAction(StarGateView starGateView, StarGateModel starGateModel, String action) {
        Log.d(TAG, "onAction action " + action);
        setStarGateModel(starGateModel);
        setStarGateView(starGateView);
        // if music not playing & theatre music is enabled, start background music
        if (!getSoundManager().getMpMusic().isPlaying() && isSoundMusic()) {
            getSoundManager().startSound(
                    getSoundManager().getMpMusic(),
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_VOLUME_QTR,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
        }

        // if an activity is associated with action
        if (onActivity(action, getTouchX(), getTouchY(), 0.0f)) {
            // if theatre action sounds enabled
            if (isSoundAction()) {
                // play sound associated with action
                switch (action) {
                    case DaoAction.ACTION_TYPE_SINGLE_TAP:
                        getSoundManager().startSound(
                                getSoundManager().getMpTap(),
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_START_TIC_SHORT,
                                SoundManager.SOUND_START_TIC_SHORT);
                        break;
                    case DaoAction.ACTION_TYPE_LONG_PRESS:
                        getSoundManager().startSound(
                                getSoundManager().getMpPress(),
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_START_TIC_MEDIUM,
                                SoundManager.SOUND_START_TIC_MEDIUM);
                        break;
                    case DaoAction.ACTION_TYPE_FLING:
                        getSoundManager().startSound(
                                getSoundManager().getMpFling(),
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_VOLUME_FULL,
                                SoundManager.SOUND_START_TIC_LONG,
                                SoundManager.SOUND_START_TIC_LONG);
                        break;
                    case DaoAction.ACTION_TYPE_DOUBLE_TAP:
                        break;
                    default:
                        Log.e(TAG, "Oops! Unknown action? " + action);
                        return false;
                }
            }
        }
        else if (isSoundFlourish()){
            // no activity associated with action, play uh-uh sound
            getSoundManager().startSound(
                    getSoundManager().getMpUhuh(),
                    SoundManager.SOUND_VOLUME_FULL,
                    SoundManager.SOUND_VOLUME_FULL,
                    SoundManager.SOUND_START_TIC_NADA,
                    SoundManager.SOUND_START_TIC_NADA);
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean onActivity(String action, float touchX, float touchY, float z) {
        Log.d(TAG, "onActivity action " + action + " touch (x,y) " + touchX + ", " + touchY);

        // get activity
        int activity = getStarGateView().getRingIndex(touchX, touchY, z);
        // if valid activity selection
        if (activity != DaoDefs.INIT_INTEGER_MARKER) {
            switch (activity) {
                case ACTIVITY_TYPE_PLAY:
                    if (getPlayActive()) {
                        // rebuild nav menu triggering launch stage fragment
                        getParent().buildNavMenu();
                    }
                    break;
                case ACTIVITY_TYPE_SIGNING:
                    if (getSignInActive()) {
                        getParent().startActivity(new Intent(getParent(), GoogleSignInActivity.class));
                    }
                    else if (getSignOutActive()) {
                        getParent().startActivity(new Intent(getParent(), GoogleSignInActivity.class));
                    }
                    break;
                case ACTIVITY_TYPE_INVITE:
                    if (getInviteActive()) {

                    }
                    break;
                case ACTIVITY_TYPE_ACCEPT:
                    if (getAcceptActive()) {

                    }
                    break;
                case ACTIVITY_TYPE_CHAT:
                    if (getChatActive()) {

                    }
                    break;
                default:
                    Log.e(TAG, "onActivity finds invalid activity index = " + activity);
            }
            return true;
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////
    public Boolean alertSignInOptions() {
        // alert for power options
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getContext().getString(R.string.signin_title));
        alert.setMessage(getContext().getString(R.string.signin_message));

        // establish layout for rado group
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        String radioText = "Nada";
        int color;
        final RadioButton[] rb = new RadioButton[STARGATE_SIGNIN_OPTIONS];
        RadioGroup rg = new RadioGroup(getContext()); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL

        for(int i = 0; i < STARGATE_SIGNIN_OPTIONS; i++){
            rb[i]  = new RadioButton(getContext());
            rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout
            switch (i) {
                case STARGATE_SIGNIN_GOOGLE:
                    radioText = getContext().getString(R.string.action_googleauth);
                    break;
                case STARGATE_SIGNIN_EMAIL:
                    radioText = getContext().getString(R.string.action_emailauth);
                    break;
                case STARGATE_SIGNIN_ANON:
                    radioText = getContext().getString(R.string.action_anonauth);
                    break;
                default:
                    break;
            }
            rb[i].setText(radioText);
        }
        rg.check(STARGATE_SIGNIN_GOOGLE);
        layout.addView(rg);//add RadioGroup to the layout

        TextView tv = new TextView(getContext());
        tv.setText("Please choose authentication method.");
        layout.addView(tv);

        alert.setView(layout);

        final RadioGroup radioGroup = rg;
        alert.setPositiveButton(STARGATE_ACTIVITY_SIGNIN, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // ok
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);
                int option = radioGroup.indexOfChild(radioButton);
                Log.d(TAG,"SignIn with selection " + option);
                // launch selected auth method
                switch (option) {
                    case STARGATE_SIGNIN_GOOGLE:
                        getParent().startActivity(new Intent(getParent(), GoogleSignInActivity.class));
                        break;
                    case STARGATE_SIGNIN_EMAIL:
                        getParent().startActivity(new Intent(getParent(), EmailPasswordActivity.class));
                        break;
                    case STARGATE_SIGNIN_ANON:
                        getParent().startActivity(new Intent(getParent(), AnonymousAuthActivity.class));
                        break;
                    default:
                        Log.e(TAG,"alertSignInOptions unknown option " + option);
                }
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Log.d(TAG,"SignIn cancelled...");
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
}
