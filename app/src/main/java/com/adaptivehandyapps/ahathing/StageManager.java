/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker MAY 2017
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
 */package com.adaptivehandyapps.ahathing;
//
// Created by mat on 5/24/2017.
//

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;

import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicStarBoard;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;

import java.util.List;

public class StageManager {
    private static final String TAG = StageManager.class.getSimpleName();

//    private StageViewController mStageViewController;

    private Context mContext;

    ///////////////////////////////////////////////////////////////////////////
    private PlayListService mPlayListService;
    private RepoProvider mRepoProvider;

    // touch position
    private float mTouchX = 0.0f;
    private float mTouchY = 0.0f;
    private float mVelocityX = 0.0f;
    private float mVelocityY = 0.0f;
    private MotionEvent mEvent1;
    private MotionEvent mEvent2;

//    private MediaPlayer mediaPlayer;
//    private Boolean mediaPlayerReady;

//    private MediaPlayer mpTap;
//    private Boolean mpTapReady;
//    private MediaPlayer mpPress;
//    private Boolean mpPressReady;
//    private MediaPlayer mpFling;
//    private Boolean mpFlingReady;
//
//    private MediaPlayer mpMusic;
//    private Boolean mpMusicReady;
//    private Boolean mpMusicPlaying;

    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    public PlayListService getPlayListService() {
        return mPlayListService;
    }
    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
    }

    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
        Log.d(TAG, "setRepoProvider " + mRepoProvider);
    }
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

//    public MediaPlayer getMpTap() {
//        return mpTap;
//    }
//    public void setMpTap(MediaPlayer mpTap) {
//        this.mpTap = mpTap;
//    }
//
//    public Boolean getMpTapReady() {
//        return mpTapReady;
//    }
//    public void setMpTapReady(Boolean mpTapReady) {
//        this.mpTapReady = mpTapReady;
//    }
//
//    public MediaPlayer getMpPress() {
//        return mpPress;
//    }
//    public void setMpPress(MediaPlayer mpPress) {
//        this.mpPress = mpPress;
//    }
//
//    public Boolean getMpPressReady() {
//        return mpPressReady;
//    }
//    public void setMpPressReady(Boolean mpPressReady) {
//        this.mpPressReady = mpPressReady;
//    }
//
//    public MediaPlayer getMpFling() {
//        return mpFling;
//    }
//    public void setMpFling(MediaPlayer mpFling) {
//        this.mpFling = mpFling;
//    }
//
//    public Boolean getMpFlingReady() {
//        return mpFlingReady;
//    }
//    public void setMpFlingReady(Boolean mpFlingReady) {
//        this.mpFlingReady = mpFlingReady;
//    }
//
//
//    public Boolean getMpMusicReady() {
//        return mpMusicReady;
//    }
//    public void setMpMusicReady(Boolean mpMusicReady) {
//        this.mpMusicReady = mpMusicReady;
//    }

//    public Boolean getMpMusicPlaying() {
//        return mpMusicPlaying;
//    }
//    public void setMpMusicPlaying(Boolean mpMusicPlaying) {
//        this.mpMusicPlaying = mpMusicPlaying;
//    }

//    private Boolean isMediaPlayerReady() {return mediaPlayerReady;}
//    private void setMediaPlayerReady(Boolean ready) {
//        mediaPlayerReady = ready;
//    }


    ///////////////////////////////////////////////////////////////////////////
    public StageManager(Context context, PlayListService playListService, RepoProvider repoProvider) {
//    public StageManager(StageViewController stageViewController, PlayListService playListService, RepoProvider repoProvider) {
//        mStageViewController = stageViewController;
        mContext = context;
        setPlayListService(playListService);
        setRepoProvider(repoProvider);

//        mediaPlayer = MediaPlayer.create(mContext, R.raw.uhuh);
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
////                mp.start();
//                setMediaPlayerReady(true);
//            }
//        });
//        setMpTap (MediaPlayer.create(mContext, R.raw.tap));
//        getMpTap().setAudioStreamType(AudioManager.STREAM_MUSIC);
//        getMpTap().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                setMpTapReady(true);
//            }
//        });
//
//        setMpPress (MediaPlayer.create(mContext, R.raw.press));
//        getMpPress().setAudioStreamType(AudioManager.STREAM_MUSIC);
//        getMpPress().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                setMpPressReady(true);
//            }
//        });
//
//        setMpFling (MediaPlayer.create(mContext, R.raw.fling));
//        getMpFling().setAudioStreamType(AudioManager.STREAM_MUSIC);
//        getMpFling().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                setMpFlingReady(true);
//            }
//        });
//
//        mpMusic = MediaPlayer.create(mContext, R.raw.dintro);
//        mpMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mpMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                setMpMusicReady(true);
//            }
//        });

    }

    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onAction(StageViewRing stageViewRing, String action) {
        Log.d(TAG, "onAction action " + action);
//        // turn off music for test
//        setMpMusicReady(false);
//        if (getMpMusicReady()) {
//            if (!mpMusic.isPlaying()) {
//                mpMusic.start();
//            } else {
//                mpMusic.setVolume(1.0f, 1.0f);
//            }
//        }

        // if story exists associating the active actor (or all actor) with the action
        if (updatePlaylist(action)) {
            int musicPosition = 0;

            // if prereq satisfied
            if (isPreReqSatisfied(stageViewRing)) {
//                // pause background sounds
//                if (getMpMusicReady() && mpMusic.isPlaying()) {
//                    mpMusic.pause();
//                    mpMusic.setVolume(0.5f, 0.5f);
//                    musicPosition = mpMusic.getCurrentPosition();
//                }
                // execute outcome
                switch (action) {
                    case DaoAction.ACTION_TYPE_SINGLE_TAP:
//                        setMpTap (MediaPlayer.create(mContext, R.raw.tap));
//                        getMpTap().setAudioStreamType(AudioManager.STREAM_MUSIC);
//                        getMpTap().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mp) {
//                                setMpTapReady(true);
//                                mpTap.start();
//                                CountDownTimer countdown = new CountDownTimer(100, 100) {
//                                    public void onTick(long millisUntilFinished) {
//                                        mpTap.start();
//                                    }
//                                    public void onFinish() {
//                                        mpTap.stop();
//                                        mpTap.release();
//                                    }
//                                };
//                                countdown.start();
//                            }
//                        });
//                        if (getMpTapReady()) {
//                            mpTap.start();
//                            CountDownTimer countdown = new CountDownTimer(100, 100) {
//                                public void onTick(long millisUntilFinished) {
//                                    mpTap.start();
//                                }
//                                public void onFinish() {
//                                    mpTap.stop();
//                                }
//                            };
//                            countdown.start();
//                        }
//                        else {
//                            Log.e(TAG,"Oops!  TAP media player not ready...");
//                        }
                        break;
                    case DaoAction.ACTION_TYPE_LONG_PRESS:
//                        if (getMpPressReady()) {
//                            mpPress.start();
//                            CountDownTimer countdown = new CountDownTimer(500, 500) {
//                                public void onTick(long millisUntilFinished) {
//                                    mpPress.start();
//                                }
//                                public void onFinish() {
//                                    mpPress.stop();
//                                }
//                            };
//                            countdown.start();
//                        }
//                        else {
//                            Log.e(TAG,"Oops!  Press media player not ready...");
//                        }
                        break;
                    case DaoAction.ACTION_TYPE_FLING:
//                        if (getMpFlingReady()) {
//                            mpFling.start();
//                            CountDownTimer countdown = new CountDownTimer(1000, 1000) {
//                                public void onTick(long millisUntilFinished) {
//                                    mpFling.start();
//                                }
//                                public void onFinish() {
//                                    mpFling.stop();
//                                }
//                            };
//                            countdown.start();
//                        }
//                        else {
//                            Log.e(TAG,"Oops!  Fling media player not ready...");
//                        }
                        break;
                    case DaoAction.ACTION_TYPE_DOUBLE_TAP:
                        break;
                    default:
                        Log.e(TAG, "Oops! Unknown action? " + action);
                        return false;
                }

                // increment active actors star board tic
                DaoEpic daoEpic = getPlayListService().getActiveEpic();
                DaoStage daoStage = getPlayListService().getActiveStage();
                if (daoEpic != null && daoStage != null && getPlayListService().getActiveActor() != null) {
                    int starInx = daoEpic.getStarList().indexOf(getPlayListService().getActiveActor().getMoniker());
                    if (starInx > -1) {
                        // increment tic for actor
                        int tic = daoEpic.getStarBoardList().get(starInx).getTic();
                        daoEpic.getStarBoardList().get(starInx).setTic(++tic);
                        Log.d(TAG, "Tic " + tic + " for actor " + daoEpic.getStarBoardList().get(starInx).getStarMoniker());
                        // update epic tally based on stage ring locations occupied
                        daoEpic.updateEpicTally(daoStage);
                        // update epic repo
                        getRepoProvider().getDalEpic().update(daoEpic, true);
                    }
                    // deliver outcome
                    DaoOutcome daoOutcome = getPlayListService().getActiveOutcome();
                    if (daoOutcome != null) {
                        onOutcome(stageViewRing, daoOutcome.getMoniker());
                    }
                    else {
                        Log.e(TAG,"Oops! no active outcome...");
                    }

                    // if post-operation indicated
                    onPostOp();
                }
                else {
                    if (daoEpic == null ) Log.e(TAG, "Oops! Active epic NULL! ");
                    else if (daoStage == null) Log.e(TAG, "Oops! Active stage NULL! ");
                    else Log.e(TAG, "Oops! Active actor NULL! ");
                }
            }
            else {
//                // prereq not satisfied
//                int resIdAudio = R.raw.uhuh;
//                // play audio associated with nogo
//                final MediaPlayer mediaPlayer = MediaPlayer.create(mContext, resIdAudio);
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mediaPlayer.start();
//                    }
//                });
            }
//            // resume background sounds
//            if (getMpMusicReady() && mpMusic.isPlaying()) {
//                mpMusic.setVolume(1.0f, 1.0f);
//                mpMusic.seekTo(musicPosition);
//                mpMusic.start();
//            }

            return true;
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean isPreReqSatisfied(StageViewRing stageViewRing) {
        DaoStory activeStory = getPlayListService().getActiveStory();
        if (stageViewRing != null && activeStory != null && getPlayListService().getActiveActor() != null) {
            if (activeStory.getPreReq().equals(DaoDefs.INIT_STRING_MARKER) ||
                    activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_NONE) ||
                    stageViewRing == null) {
                Log.d(TAG, "isPreReqSatisfied-> no pre-req defined...");
                return true;
            } else {
                DaoStage daoStage = getPlayListService().getActiveStage();
                if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                    // get ring index of touch to determine actor at vert
                    float touchX = getTouchX();
                    float touchY = getTouchY();
                    float z = 0.0f;
                    int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                    if (selectIndex < daoStage.getActorList().size()) {
                        String vertActor = daoStage.getActorList().get(selectIndex);
                        // get active actor
                        String activeActor = getPlayListService().getActiveActor().getMoniker();
                        Log.d(TAG, "isPreReqSatisfied(" + activeStory.getPreReq() + ")-> vertActor, activeActor= " + vertActor + ", " + activeActor);
                        if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_OWNED)) {
                            // if vert is owned by active player, return true
                            if (vertActor.equals(activeActor)) return true;
                        } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_BLOCKED)) {
                            // if vert is blocked by another player, return true
                            if (!vertActor.equals(activeActor) && !vertActor.equals(DaoDefs.INIT_STRING_MARKER))
                                return true;
                        } else if (activeStory.getPreReq().equals(DaoStory.STORY_PREREQ_VERT_EMPTY)) {
                            // if vert is empty, return true
                            if (vertActor.equals(DaoDefs.INIT_STRING_MARKER)) return true;
                        }
                    } else {
                        // ArrayIndexOutOfBoundsException
                        Log.e(TAG, "Selected ring index (" + selectIndex + ") out of bounds for stage actorList size " + daoStage.getActorList().size());
                        return false;
                    }
                } else {
                    if (activeStory == null) Log.e(TAG, "Oops!  no active story...");
                    else if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
                    else if (getPlayListService().getActiveActor() == null) Log.e(TAG, "Oops!  no active actor...");
                    else Log.e(TAG, "Oops! Unknown stage type " + daoStage.getStageType());
                }
            }
        }
        else {
            if (stageViewRing == null) Log.e(TAG, "Oops!  stageViewRing NULL...");
            else Log.e(TAG, "Oops! No active actor...");
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean onPostOp() {
        DaoStory activeStory = getPlayListService().getActiveStory();
        if (activeStory != null) {
            // return true if no post-op defined
            if (activeStory.getPostOp().equals(DaoDefs.INIT_STRING_MARKER) ||
                    activeStory.getPreReq().equals(DaoStory.STORY_POSTOP_NONE)) {
                Log.d(TAG, "onPostOp-> no post-op defined...");
                return true;
            } else {
                DaoStage daoStage = getPlayListService().getActiveStage();
                if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                    if (activeStory.getPostOp().equals(DaoStory.STORY_POSTOP_CURTAIN_CALL)) {
                        DaoEpic daoEpic = getPlayListService().getActiveEpic();
                        if (daoEpic != null) {
                            // curtains should come down
                            if (daoEpic.isCurtainClose()) {
                                // bring down the curtain!  prompt for an encore
                                List<DaoEpicStarBoard> starBoardList = daoEpic.getTallyOrder(false);
                                String title = starBoardList.get(0).getStarMoniker() + " dominates the universe of Marbles!";
                                postCurtainCloseDialog(mContext, title, daoEpic, daoStage);
                            }
                        }
                        else {
                            Log.e(TAG, "Oops!  no active epic...");
                        }

//                    for (String star : daoEpic.getStarList()) {
//                        int starInx = daoEpic.getStarList().indexOf(star);
//                        Log.d(TAG, " star " + star + "(" + starInx + ") has tally " + daoEpic.getStarBoardList().get(starInx).getTally());
//                    }
                    }
                    else {
                        Log.e(TAG, "Oops!  Unknown postop " + activeStory.getPostOp());
                    }
                }
                else {
                    if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
                    else Log.e(TAG, "Oops! Unknown stage type " + daoStage.getStageType());
                }
            }
        }
        else {
            Log.e(TAG, "Oops!  no active story...");
        }
        return false;
    }
    public Boolean postCurtainCloseDialog(Context c, String title, DaoEpic epic, DaoStage stage) {
//    public static Boolean postCurtainCloseDialog(Context c, String title, DaoEpic epic, DaoStage stage) {
        final Context context = c;
        final DaoEpic daoEpic = epic;
        final DaoStage daoStage = stage;


        // post alert dialog
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage("Play an encore?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                .setPositiveButton("You Bet!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "curtain closing dialog - yes...");
                        // reset epic
                        daoEpic.resetEpicStageTallyTic(daoStage, true, true);
//                        daoEpic.resetStarBoard();
                        // update repo
                        getRepoProvider().getDalEpic().update(daoEpic, true);
                        getRepoProvider().getDalStage().update(daoStage, true);

                    }
                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                .setNegativeButton("Not yet, take me back.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "curtain closing dialog - no...");
                        // leave it be...
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Boolean updatePlaylist(String action) {
        Log.d(TAG, "updatePlaylist action " + action);
            // if story exists associating the active actor (or all actor) with the action
            DaoStory daoStory = isStory(action);
            if (daoStory != null) {
                // set active: story, action, outcome  (actor is already active)
                getPlayListService().setActiveStory(daoStory);
                DaoAction daoAction = (DaoAction) getRepoProvider().getDalAction().getDaoRepo().get(daoStory.getAction());
                getPlayListService().setActiveAction(daoAction);
                DaoOutcome daoOutcome = (DaoOutcome) getRepoProvider().getDalOutcome().getDaoRepo().get(daoStory.getOutcome());
                getPlayListService().setActiveOutcome(daoOutcome);
                return true;
            }
//        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // if active actor is associated with incoming action - return story
    public DaoStory isStory(String action) {
        DaoEpic activeEpic = getPlayListService().getActiveEpic();
        if (activeEpic != null) {
            // for each story in epic
            for (Integer i = 0; i < activeEpic.getTagList().size(); i++) {
                String storyMoniker = activeEpic.getTagList().get(i);
                DaoStory daoStory = (DaoStory) getRepoProvider().getDalStory().getDaoRepo().get(storyMoniker);
                if (daoStory != null && getPlayListService().getActiveActor() != null) {
                    Log.v(TAG, "test story " + daoStory);
                    // if any actor or active actor  &&  action match
                    if ((daoStory.getActor().contains("*") ||
                            daoStory.getActor().equals(getPlayListService().getActiveActor().getMoniker())) &&
                            daoStory.getAction().equals(action)) {
                        Log.v(TAG, "returning outcome " + daoStory.getOutcome());
                        return daoStory;
                    }
                }
                else {
                    if (daoStory == null) Log.e(TAG, "oops! no story found matching " + activeEpic.getTagList().get(i) + "...");
                    else Log.e(TAG,"Oops! no active actor");
                }
            }
        }
        else {
            Log.e(TAG, "Oops! no active epic...");
        }
        return null;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Outcomes
    ///////////////////////////////////////////////////////////////////////////
    // Actions
    public Boolean onOutcome(StageViewRing stageViewRing, String outcome) {
        Log.d(TAG, "onOutcome action " + outcome);
        // execute outcome
        switch (outcome) {
            case DaoOutcome.OUTCOME_TYPE_TOGGLE:
                // toggle selection
                if (stageViewRing == null) return false;
                toggleSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f, false);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PLUS:
                if (stageViewRing == null) return false;
                // toggle selection plus adjacent
                toggleSelection(stageViewRing, getTouchX(), getTouchY(), 0.0f, true);
                return true;
            case DaoOutcome.OUTCOME_TYPE_TOGGLE_PATH:
                if (stageViewRing == null) return false;
                // toggle selection plus adjacent
                plotPath(stageViewRing,
                        getVelocityX(), getVelocityY(),
                        getEvent1().getX(), getEvent1().getY(),
                        getEvent2().getX(), getEvent2().getY());
                return true;
            case DaoOutcome.OUTCOME_TYPE_RESET_EPIC:
                // clear actors on stage
                clearActors();
                return true;
            default:
                Log.e(TAG, "Oops! Unknown outcome? " + outcome);
        }

        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean toggleSelection(StageViewRing stageViewRing, float touchX, float touchY, float z, Boolean plus) {
        Log.d(TAG, "toggleSelection touch (x,y) " + touchX + ", " + touchY);
//        int selectIndex = DaoDefs.INIT_INTEGER_MARKER;
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            if (stageViewRing != null) {
                // get ring index
                int selectIndex = stageViewRing.getRingIndex(touchX, touchY, z);
                // if touch found
                if (selectIndex != DaoDefs.INIT_INTEGER_MARKER) {
                    if (getPlayListService().getActiveActor() != null) {
                        Log.d(TAG, "toggleSelection (" + selectIndex + ") for actor " + daoStage.getActorList().get(selectIndex));
                        if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), selectIndex)) {
                            Log.e(TAG, "Ooops! toggleSelection UNKNOWN stage type? " + daoStage.getStageType());
                        }
                        // if selecting plus ring
                        if (plus) {
                            if (getRepoProvider().getStageModelRing() != null) {
                                List<Integer> ringIndexList = getRepoProvider().getStageModelRing().findRing(selectIndex);
                                // toggle each rect in ring list
                                for (Integer i : ringIndexList) {
                                    if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), i)) {
                                        Log.e(TAG, "Ooops! toggleSelection UNKNOWN stage type? " + daoStage.getStageType());
                                    }
                                }
                            } else {
                                Log.e(TAG, "Oops! for repo " + getRepoProvider().toString() + " NULL getStageModelRing()...");
                            }
                        }
                        // update object
                        getRepoProvider().getDalStage().update(daoStage, true);
                    }
                    else {
                        Log.e(TAG,"Oops! No active actor...");
                    }
                }
            }
            else {
                Log.e(TAG,"Oops! stageViewRing NULL...");
            }
        }
        else {
            if (daoStage == null) Log.e(TAG,"Oops! No active stage...");
            else Log.e(TAG, "toggleSelection UNKNOWN stage type: " + daoStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean plotPath(StageViewRing stageViewRing, float velocityX, float velocityY,
                             float event1X, float event1Y, float event2X, float event2Y) {

        // sum velocity ignoring direction (sign)
        float velocity = Math.abs(velocityX) + Math.abs(velocityY);
        Log.d(TAG, "plotPath velocity sum = " + velocity);
        // set # intervals based on velocity
        float intervalCount = velocity / 1000.0f;
        Log.d(TAG, "plotPath intervalCount = " + intervalCount);
        // find angle of fling
        double deltaX = event2X - event1X;
        double deltaY =  -(event2Y - event1Y);
        double thetaRad = Math.atan2(deltaY, deltaX);
        double angle = thetaRad * (180.0f/Math.PI);
        Log.d(TAG, "plotPath thetaRad = " + thetaRad + ", angle = " + angle);

        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && stageViewRing != null) {
            // get ring index
            Log.d(TAG, "plotPath origin X, Y " + event1X + ", " + event1Y);
            int ringIndex = stageViewRing.getRingIndex(event1X, event1Y, 0.0f);
            // toggle initial position
            if (ringIndex != DaoDefs.INIT_INTEGER_MARKER) {
                if (getPlayListService().getActiveActor() != null) {
                    if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                        Log.e(TAG, "Ooops! plotPath UNKNOWN stage type? " + daoStage.getStageType());
                    }
                    // for each interval, generate point along angle
                    for (int i = 1; i < intervalCount; i++) {
                        int prevRingIndex = ringIndex;
                        float x = (float) (event1X + ((StageModelRing.LOCUS_DIST * i) * Math.cos(thetaRad)));
                        float y = (float) (event1Y - ((StageModelRing.LOCUS_DIST * i) * Math.sin(thetaRad)));
                        Log.d(TAG, "plotPath toggle for interval " + i + " at X, Y " + x + ", " + y);
                        // find ring index at interval position
                        ringIndex = stageViewRing.getRingIndex(x, y, 0.0f);
                        // if not previously toggled
                        if (ringIndex != DaoDefs.INIT_INTEGER_MARKER && ringIndex != prevRingIndex) {
                            // toggle at interval position
                            if (!daoStage.toggleActorList(getPlayListService().getActiveActor().getMoniker(), ringIndex)) {
                                Log.e(TAG, "Ooops! plotPath UNKNOWN stage type? " + daoStage.getStageType());
                            }
                        }
                    }
                    // update object
                    getRepoProvider().getDalStage().update(daoStage, true);
                }
                else {
                    Log.e(TAG, "Oops!  no active actor...");
                }
            } else {
                Log.e(TAG, "plotPath touch out of bounds...");
            }
        }
        else {
            if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
            else Log.e(TAG, "Oops!  unkonwn stage type..." + daoStage.getStageType());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean clearActors() {
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.setActorList(DaoDefs.INIT_STRING_MARKER)) {
            // update object
            getRepoProvider().getDalStage().update(daoStage, true);
            return false;
        }
        else {
            if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
            else Log.e(TAG, "Oops! UNKNOWN stage type? " + daoStage.getStageType());
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
}
