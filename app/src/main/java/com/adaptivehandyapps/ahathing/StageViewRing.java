/*
 * Project: Things
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
//
// Created by mat on 1/9/2017.
//
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoEpicActorBoard;
import com.adaptivehandyapps.ahathing.dao.DaoLocus;
import com.adaptivehandyapps.ahathing.dao.DaoLocusList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// StageViewController extends View to manage gestures on stage
//  HAS A
//      StageViewRing to draw view, transform device<->screen, scale functions
//          HAS A
//              StageModelRing with stage objects locii, props, colors; find locii, rings
public class StageViewRing {

    private static final String TAG = StageViewRing.class.getSimpleName();

    public static final Boolean DEFAULT_STAGE_LOCK = false;

    private static final int DEFAULT_MINOR_TEXT_SIZE_DP = 12;
    private static final int DEFAULT_MAJOR_TEXT_SIZE_DP = 24;
    private static final float DECREMENT_TEXT_SIZE_DP = 24.0F;

    private static final float DEFAULT_RECT_SIZE_DP = 24.0F;

    private static final float DEFAULT_LOWLIGHT_DP = 2.0F;
    private static final float DEFAULT_HIGHLIGHT_DP = 8.0F;

    private Context mContext;
    private StageViewController mParentViewController;

    private Canvas mCanvas;

    private int mCanvasWidth;
    private int mCanvasHeight;
    private float mDensity;

    private Paint mPaintMinorText;
    private Paint mPaintMajorText;
    private Paint mPaintMapRect;
    private Paint mPaintBoundingRect;

    private int mMinorTextSize = DEFAULT_MINOR_TEXT_SIZE_DP;
    private int mMajorTextSize = DEFAULT_MAJOR_TEXT_SIZE_DP;

    // pinch zoom support - scale factor ranges from .1 to 1.9
    private float mScaleFactor = 1.0f;

    ///////////////////////////////////////////////////////////////////////////
    // bounding rect
    private RectF mBoundingRect;
    public RectF getBoundingRect() { return mBoundingRect;}
    public Boolean setBoundingRect(RectF boundingRect) { mBoundingRect = boundingRect; return true;}

    ///////////////////////////////////////////////////////////////////////////
    // list of rects cooresponding to locus translated to device coords
    List<RectF> mRectList;
    public List<RectF> getRectList() {
        return mRectList;
    }
    public void setRectList(List<RectF> rectList) {
        this.mRectList = rectList;
    }

    ///////////////////////////////////////////////////////////////////////////
    private PlayListService mPlayListService;
    public PlayListService getPlayListService() {
        return mPlayListService;
    }
    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
    }

    private RepoProvider mRepoProvider;
    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
        Log.d(TAG, "setRepoProvider " + mRepoProvider);
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters

    public int getCanvasWidth() {
        return mCanvasWidth;
    }
    public void setCanvasWidth(int canvasWidth) {
        this.mCanvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return mCanvasHeight;
    }
    public void setCanvasHeight(int canvasHeight) {
        this.mCanvasHeight = canvasHeight;
    }

    public float getDensity() {
        return mDensity;
    }
    public void setDensity(float density) {
        this.mDensity = density;
    }

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    public StageViewRing(Context context, StageViewController parentViewController) {
        mContext = context;
        mParentViewController = parentViewController;
        setPlayListService(mParentViewController.getPlayListService());
        setRepoProvider(mParentViewController.getRepoProvider());

        // get screen attributes
        setCanvasWidth(mParentViewController.getCanvasWidth());
        setCanvasHeight(mParentViewController.getCanvasHeight());
        setDensity(mParentViewController.getDensity());

        // ensure stage ready
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoStage != null && daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            Log.v(TAG, "Active stage ready for " + getPlayListService().getActiveStage().getMoniker() + "...");
            // create new stage model
//            mParentViewController.setStageModelRing(new StageModelRing(mRepoProvider.getPlayListService()));
            mParentViewController.setStageModelRing(new StageModelRing(getRepoProvider()));
            mParentViewController.getStageModelRing().buildModel(daoStage);
            Log.d(TAG, "NEW StageModelRing at " + mParentViewController.getStageModelRing().toString());
            // create bounding rect
            initBoundingRect(daoStage);
            // ensure stage model bounding rect set
            mParentViewController.getStageModelRing().setBoundingRect(getBoundingRect());
            // establish stage lock fab
            Boolean lock = PrefsUtils.getBooleanPrefs(mContext,PrefsUtils.STAGE_LOCK_KEY);
            String lockImage = "ic_lock_open_black_48dp";
            if (lock) {
                lockImage = "ic_lock_black_48dp";
            }
            Activity activity = (Activity) context;
            FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab_lock);
            fab.setImageResource(getImageId(context, lockImage));
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Patience, Grasshopper.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.d(TAG, "execute fab operation...");
                    // toggle lock preference
                    toggleLock();
                }
            });

        }
        else {
            if (daoStage == null) Log.e(TAG, "Oops!  no active stage...");
            else Log.e(TAG, "Oops! Unknown stage type " + daoStage.getStageType());
        }
        // init local objects
        init(context);
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    private Boolean toggleLock() {
        Activity activity = (Activity) mContext;
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab_lock);
        Boolean lock = !PrefsUtils.getBooleanPrefs(mContext, PrefsUtils.STAGE_LOCK_KEY);
        PrefsUtils.setPrefs(mContext, PrefsUtils.STAGE_LOCK_KEY, lock);
        String lockImage = "ic_lock_open_black_48dp";
        if (lock) {
            lockImage = "ic_lock_black_48dp";
        }
        fab.setImageResource(getImageId(mContext, lockImage));
        return lock;
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean initBoundingRect(DaoStage daoStage) {
        // create bounding rect
        setBoundingRect(new RectF(StageModelRing.RING_MAX_X, StageModelRing.RING_MAX_Y, StageModelRing.RING_MIN_X, StageModelRing.RING_MIN_Y));

        for (DaoLocus locus : daoStage.getLocusList().locii) {
            // update bounding rect
            if ( locus.getVertX() < getBoundingRect().left ) getBoundingRect().left = locus.getVertX();
            if ( locus.getVertY() < getBoundingRect().top ) getBoundingRect().top = locus.getVertY();
            if ( locus.getVertX() > getBoundingRect().right ) getBoundingRect().right = locus.getVertX();
            if ( locus.getVertY() > getBoundingRect().bottom ) getBoundingRect().bottom = locus.getVertY();
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean init(Context context) {

        // minor text paint
        mPaintMinorText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMinorText.setStyle(Paint.Style.FILL);
        mPaintMinorText.setTextSize(mParentViewController.getMinorTextSize());
        mPaintMinorText.setColor(context.getResources().getColor(R.color.colorStagePrimary));
        // major text paint
        mPaintMajorText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMajorText.setStyle(Paint.Style.FILL);
        mPaintMajorText.setTextSize(mParentViewController.getMajorTextSize());
        mPaintMajorText.setColor(context.getResources().getColor(R.color.colorStagePrimary));
//        mPaintMajorText.setColor(Color.rgb(255, 255, 255));

        mPaintMapRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMapRect.setStyle(Paint.Style.STROKE);
        mPaintMapRect.setStrokeWidth(4);
        mPaintMapRect.setColor(Color.CYAN);

        mPaintBoundingRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBoundingRect.setStyle(Paint.Style.STROKE);
        mPaintBoundingRect.setStrokeWidth(4);
        mPaintBoundingRect.setColor(Color.CYAN);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // getters/setters/helpers
    private float getScaleFactor() {
        return mScaleFactor;
    }

    private float setScaleFactor(float scaleFactor) {
        // exponentially expand scale factor
        Double power = Math.pow(scaleFactor, 2);
        mScaleFactor = power.floatValue();
        return mScaleFactor;
    }

    private int getRingColor(DaoLocus daoLocus) {
        String ring = daoLocus.getNickname().substring(daoLocus.getNickname().indexOf("R"));
        ring = ring.substring(1, 2);
        int color;
        switch (ring) {
            case "0":
                color = Color.rgb(255, 255, 255);
                break;
            case "1":
                color = Color.rgb(0, 255, 255);
                break;
            case "2":
                color = Color.rgb(0, 255, 0);
                break;
            case "3":
                color = Color.rgb(255, 255, 0);
                break;
            case "4":
                color = Color.rgb(255, 0, 0);
                break;
            default:
                color = mContext.getResources().getColor(R.color.colorStageAccent);
                break;
        }
        return color;
    }

    ///////////////////////////////////////////////////////////////////////////
    public Integer getRingIndex(float touchX, float touchY, float z) {
        Integer ringIndex = DaoDefs.INIT_INTEGER_MARKER;
        for (RectF r : getRectList()) {
            // if rect touched
            if (r.contains(touchX, touchY)) {
                ringIndex = getRectList().indexOf(r);
                Log.d(TAG, "getRingIndex (" + ringIndex + ") for X,Y " + touchX + ", " + touchY);
                return ringIndex;
            }
        }
        return ringIndex;
    }
    ///////////////////////////////////////////////////////////////////////////
    public float vertToDeviceX(Long vertX, float scaleFactor) {
        // derive delta x,y to shift from abstract locus center to device screen center
//        float dx = (getCanvasWidth() / 2) - StageModelRing.RING_CENTER_X.floatValue();
        float dx = (getCanvasWidth() / 2) - mParentViewController.getStageModelRing().getFocusX();
        // shift x,y from abstract locus center to device screen center
        float x = vertX.floatValue() + dx;
        // scale by applying dist from dev center by scale factor
        dx = (x - (getCanvasWidth() / 2));
        dx = (dx * scaleFactor) - dx;
        x = x + dx;
        // test inverse transform
        float inverseX = deviceToVertX(x, scaleFactor);
//        Log.d(TAG,"X delta (" + (vertX - inverseX) + ") virtual -> device -> inverse " + vertX + "->" + x +"->" + inverseX);
        return x;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Long deviceToVertX(float x, float scaleFactor) {
        float dx;
        Long vertX;
        // scale by applying dist from dev center by scale factor
        dx = (x - (getCanvasWidth() / 2));
        dx = (dx * scaleFactor) - dx;
        x = x - dx;
        // derive delta x,y to shift from abstract locus center to device screen center
//        dx = (getCanvasWidth() / 2) - StageModelRing.RING_CENTER_X.floatValue();
        dx = (getCanvasWidth() / 2) - mParentViewController.getStageModelRing().getFocusX();
        // shift x,y from device screen center to abstract locus center
        x = x - dx;
        vertX = (long) x;
        return vertX;
    }

    ///////////////////////////////////////////////////////////////////////////
    public float vertToDeviceY(Long vertY, float scaleFactor) {
        // derive delta x,y to shift from abstract locus center to device screen center
//        float dy = (getCanvasHeight() / 2) - StageModelRing.RING_CENTER_Y.floatValue();
        float dy = (getCanvasHeight() / 2) - mParentViewController.getStageModelRing().getFocusY();
        // shift x,y from abstract locus center to device screen center
        float y = vertY.floatValue() + dy;
        // scale by applying dist from dev center by scale factor
        dy = (y - (getCanvasHeight() / 2));
        dy = (dy * scaleFactor) - dy;
        y = y + dy;
        float inverseY = deviceToVertY(y, scaleFactor);
//        Log.d(TAG,"Y delta (" + (vertY - inverseY) + ") virtual -> device -> inverse " + vertY + "->" + y +"->" + inverseY);
        return y;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Long deviceToVertY(float y, float scaleFactor) {
        float dy;
        Long vertY;
        // scale by applying dist from dev center by scale factor
        dy = (y - (getCanvasHeight() / 2));
        dy = (dy * scaleFactor) - dy;
        y = y - dy;
        // derive delta x,y to shift from abstract locus center to device screen center
//        dy = (getCanvasHeight() / 2) - StageModelRing.RING_CENTER_Y.floatValue();
        dy = (getCanvasHeight() / 2) - mParentViewController.getStageModelRing().getFocusY();
        // shift x,y from device screen center to abstract locus center
        y = y - dy;
        vertY = (long) y;
        return vertY;
    }

    ///////////////////////////////////////////////////////////////////////////
    // tranform locus list to device coords
    ///////////////////////////////////////////////////////////////////////////
    public List<RectF> transformLocus(DaoLocusList daoLocusList, float scaleFactor) {
        Log.v(TAG, "transformLocus for " + scaleFactor + "...");
        // set scale factor
        setScaleFactor(scaleFactor);
        // create rect list
        mRectList = new ArrayList<>();
        // for each locus
        for (DaoLocus daoLocus : daoLocusList.locii) {
            // transform abstract vert coords to device coords
            float x = vertToDeviceX(daoLocus.getVertX(), getScaleFactor());
            float y = vertToDeviceY(daoLocus.getVertY(), getScaleFactor());
            // make circle (oval) sized based on scale factor
            final float LOCUS_DIAMETER = StageModelRing.LOCUS_DIST.intValue()/2;
            float ovalOffset = (LOCUS_DIAMETER * getScaleFactor());
            float left = x - ovalOffset;
            float top = y - ovalOffset;
            float right = x + ovalOffset;
            float bottom = y + ovalOffset;
            RectF oval = new RectF(left,top,right,bottom);
            mRectList.add(oval);
        }
        return mRectList;
    }

    ///////////////////////////////////////////////////////////////////////////
    // onDraw
    public Boolean onDraw(Canvas canvas) {
        // retain canvas
        mCanvas = canvas;

        // Clear canvas
        canvas.drawColor(Color.TRANSPARENT);

        // draw locii
        drawLocus(canvas);

        // draw banner
//        drawBanner(canvas);

        if (mBoundingRect == null) {
            // set current bounding rect
            mBoundingRect = scaleRect();
        }
        // draw map rect
        drawBoundingRect(canvas);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean drawLocus(Canvas canvas) {
        int color;
        // if stage established
        DaoEpic daoEpic = getPlayListService().getActiveEpic();
        DaoStage daoStage = getPlayListService().getActiveStage();
        if (daoEpic != null && daoStage != null) {
            Log.v(TAG, "Stage ready for " + getPlayListService().getActiveStage().getMoniker() + "...");

            // if stage locus list defined
            DaoLocusList daoLocusList = daoStage.getLocusList();
            if (daoLocusList != null) {
                Log.v(TAG, "stage actor list -> " + daoStage.getActorList().toString());

                // for each locus
                for (DaoLocus daoLocus : daoLocusList.locii) {
                    // default color to signal incoherence if null actor
                    color = mContext.getResources().getColor(R.color.colorLightGrey);
                    // default to fill
                    mPaintMapRect.setStyle(Paint.Style.FILL);
                    // find index of locus
                    int i = daoLocusList.locii.indexOf(daoLocus);
                    // indexOf should never be -1 or >= size
                    if (!daoStage.getActorList().get(i).equals(DaoDefs.INIT_STRING_MARKER)) {
                        // if actor present, set actor color & fill
                        DaoActor daoActor = (DaoActor) getRepoProvider().getDalActor().getDaoRepo().get(daoStage.getActorList().get(i));
                        if (daoActor != null) {
                            // highlight locus if nothing marked, active actor present
                            if (daoStage.getMarkIndex() == DaoDefs.INIT_INTEGER_MARKER &&
                                    daoStage.getActorList().get(i).equals(daoEpic.getActiveActor())){
                                Log.d(TAG, "drawMark " + daoStage.getActorList().get(i) + " at locus " + i);
                                // paint filled rect with primary color
                                color = daoActor.getBackColor();
                                mPaintMapRect.setStyle(Paint.Style.FILL);
                                mPaintMapRect.setColor(color);
                                canvas.drawOval(mRectList.get(i), mPaintMapRect);
                                // set stroke for foreground color
                                color = daoActor.getForeColor();
                                mPaintMapRect.setStrokeWidth(DEFAULT_HIGHLIGHT_DP);
                                mPaintMapRect.setStyle(Paint.Style.STROKE);
                                mPaintMapRect.setColor(color);
//                                canvas.drawOval(mRectList.get(i), mPaintMapRect);
                            }
                            else {
                                color = daoActor.getForeColor();
                                mPaintMapRect.setStyle(Paint.Style.FILL);
                            }
                        }
                    }
                    else if (!daoStage.getPropList().get(i).equals(DaoDefs.INIT_STRING_MARKER)) {
                        if (daoStage.getPropFgColorList().size() > i &&
                                daoStage.getPropBgColorList().size() > i) {
                            // if prop present, set selected color & fill
                            if (daoStage.getPropList().get(i).equals(DaoActor.ACTOR_MONIKER_FORBIDDEN)) {
                                // set fill for background color
                                color = daoStage.getPropBgColorList().get(i);
                                mPaintMapRect.setStyle(Paint.Style.FILL);
                            } else if (daoStage.getPropList().get(i).equals(DaoActor.ACTOR_MONIKER_MIRROR)) {
                                // paint filled rect with primary color
                                color = daoStage.getPropBgColorList().get(i);
                                mPaintMapRect.setStyle(Paint.Style.FILL);
                                mPaintMapRect.setColor(color);
                                canvas.drawOval(mRectList.get(i), mPaintMapRect);
                                // set stroke for foreground color
                                color = daoStage.getPropFgColorList().get(i);
                                mPaintMapRect.setStrokeWidth(DEFAULT_LOWLIGHT_DP);
                                mPaintMapRect.setStyle(Paint.Style.STROKE);
                                mPaintMapRect.setColor(color);
                                Log.d(TAG, "drawLocus draws mirror at " + i);
                            }
                        }
                        else {
                            Log.e(TAG, "drawLocus finds de-populated Prop Fg,Bg Color List " +
                                    daoStage.getPropFgColorList().size() + ", " + daoStage.getPropBgColorList().size());
                        }
                    }
//                    else if (i >= daoStage.getActorList().size() || i >= daoStage.getActorList().size()) {
//                        Log.e(TAG, "invalid locii index " + i +
//                                " for stage actor list size = " + daoStage.getActorList().size() +
//                                " for stage prop list size = " + daoStage.getPropList().size());
//                    }
                    else {
                        // set unselected color & no fill
                        //color = getRingColor(daoLocus);
                        mPaintMapRect.setStrokeWidth(DEFAULT_LOWLIGHT_DP);
                        mPaintMapRect.setStyle(Paint.Style.STROKE);

                    }
                    mPaintMapRect.setColor(color);

                    // draw locus
                    canvas.drawOval(mRectList.get(i), mPaintMapRect);

                    // annotate w/ name
                    mPaintMinorText.setColor(color);
                    String id = daoLocus.getNickname().substring(daoLocus.getNickname().indexOf("L"));
                    id = id.substring(1);
                    drawText(canvas, id, mPaintMinorText, mRectList.get(i).centerX(), mRectList.get(i).centerY());
                }
                // if locus is marked, highlight
                if (daoStage.getMarkIndex() != DaoDefs.INIT_INTEGER_MARKER) {
                    int i = daoStage.getMarkIndex();
                    // if actor present, set selected color & fill
                    DaoActor daoActor = (DaoActor) getRepoProvider().getDalActor().getDaoRepo().get(daoStage.getActorList().get(i));
                    if (daoActor != null) {
                        Log.d(TAG, "drawMark " + daoStage.getActorList().get(i) + " at locus " + i);
                        // paint filled rect with primary color
                        color = daoActor.getBackColor();
                        mPaintMapRect.setStyle(Paint.Style.FILL);
                        mPaintMapRect.setColor(color);
                        canvas.drawOval(mRectList.get(i), mPaintMapRect);
                        // paint stroke rect in secondary color
                        color = daoActor.getForeColor();
                        float strokeWidth = mPaintMapRect.getStrokeWidth();
                        mPaintMapRect.setStrokeWidth(DEFAULT_HIGHLIGHT_DP);
                        mPaintMapRect.setStyle(Paint.Style.STROKE);
                        mPaintMapRect.setColor(color);
                        canvas.drawOval(mRectList.get(i), mPaintMapRect);
//                        mPaintMapRect.setStrokeWidth(strokeWidth);
                    }
                }
                return true;
            }
            else {
                Log.e(TAG, "Oops! no locus list...");
            }
        }
        else {
            Log.e(TAG, "Oops!  no active epic or stage...");
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean drawText(Canvas canvas, String text, Paint paint, float x, float y) {
        float textPaintSize = paint.getTextSize();

        String greeting = text;
        float textWidth = paint.measureText(greeting);

        // if text overflows canvas, decrement until text size underfills screen width
        while (textWidth > canvas.getWidth() && textPaintSize > DECREMENT_TEXT_SIZE_DP) {
            textPaintSize -= DECREMENT_TEXT_SIZE_DP;
            paint.setTextSize(textPaintSize);
            textWidth = paint.measureText(greeting);
        }

        canvas.drawText(greeting, x, y, paint);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // draw NowPlaying banner: stars, tally & tic
    private Boolean drawBanner(Canvas canvas) {
        // border
        float xBorder = 32.0f;
        float yBorder = 64.0f;
        float xPad = 16.0f;
        float yPad = 32.0f;
        // title origin X,Y
        float xTitle = xBorder;
        float yTitle = yBorder;
        // tally origin X,Y
        float xTally = xBorder;
        float yTally = yBorder;
        // tic origin X,Y
        float xTic = xBorder;
        float yTic = yBorder;

        // title width (max) title text length (longest actor name)
        float maxTitleWidth = 0.0f;
        // default color
        int color = mContext.getResources().getColor(R.color.colorLightGrey);
        // dereference active epic & stage
        DaoEpic daoEpic = getPlayListService().getActiveEpic();
        DaoStage daoStage = getPlayListService().getActiveStage();

        if (daoEpic != null && daoStage != null) {
            // set epic tally & tic
            daoEpic.updateEpicTally(daoStage);

            // for each star in star board, find max star name length
            for (DaoEpicActorBoard daoEpicActorBoard : daoEpic.getActorBoardList()) {
                // format title: moniker, tally, tic
                String title = daoEpicActorBoard.getActorMoniker() + "  " +
                        daoEpicActorBoard.getTally().toString() + "  " +
                        daoEpicActorBoard.getTic().toString();
                float titleWidth = mPaintMinorText.measureText(title);
                if (titleWidth > maxTitleWidth) maxTitleWidth = titleWidth;
            }

            // order starboard by descending (false) tally
            List<DaoEpicActorBoard> orderedActorBoard = daoEpic.getTallyOrder(false);
            // for each star in star board
            for (DaoEpicActorBoard daoEpicActorBoard : orderedActorBoard) {
                // format title: moniker, tally, tic
                String title = daoEpicActorBoard.getActorMoniker() + "  " +
                        daoEpicActorBoard.getTally().toString() + "  " +
                        daoEpicActorBoard.getTic().toString();
                // find actor color
                DaoActor daoActor = (DaoActor) getRepoProvider().getDalActor().getDaoRepo().get(daoEpicActorBoard.getActorMoniker());
                if (daoActor != null) {
                    color = daoActor.getForeColor();
                }
                mPaintMinorText.setColor(color);
                drawText(canvas, title, mPaintMinorText, xTitle, yTitle);
                // get rect bounding title
                Rect titleBounds = new Rect();
                mPaintMinorText.getTextBounds(title, 0, title.length(), titleBounds);
                // set left X horizontal draw cursor to after title text
                xTally = xTitle + maxTitleWidth;
                // set lower y vertical draw cursor
                float titleHeight = titleBounds.bottom - titleBounds.top;
                // set dims for full tally rect
                float left = xTally + xPad;
                float top = yTitle - titleHeight;
                float right = xTally + maxTitleWidth;
                float bottom = yTitle;
                // draw full tally rect w/ neutral color & no fill for border only
                int colorEdge = mContext.getResources().getColor(R.color.colorBrightBlue);
                mPaintMinorText.setColor(colorEdge);
                mPaintMinorText.setStyle(Paint.Style.STROKE);
                canvas.drawRect(left, top, right, bottom, mPaintMinorText);

                // determine percent progress
                Integer starTally = daoEpicActorBoard.getTally();
                float percentTally = (float) starTally / (float) daoEpic.getTallyLimit();
                right = xTally + (maxTitleWidth * percentTally);

                // draw progress tally rect fill for progress bar
                mPaintMinorText.setColor(color);
                mPaintMinorText.setStyle(Paint.Style.FILL);
                canvas.drawRect(left, top, right, bottom, mPaintMinorText);

                // set left X horizontal draw cursor to after title text
                xTic = xTally + maxTitleWidth;
                // set lower y vertical draw cursor
                titleHeight = titleBounds.bottom - titleBounds.top;
                // set dims for full tic rect
                left = xTic + xPad;
                top = yTitle - titleHeight;
                right = xTic + maxTitleWidth;
                bottom = yTitle;
                // draw full tic rect w/ actor color
                mPaintMinorText.setColor(color);
                mPaintMinorText.setStyle(Paint.Style.FILL);
                canvas.drawRect(left, top, right, bottom, mPaintMinorText);

                // determine percent progress finding remaining tics
                Integer starTic = daoEpicActorBoard.getTic();
                float percentTic = ((float) daoEpic.getTicLimit() - (float) starTic) / (float) daoEpic.getTicLimit();
                right = xTic + (maxTitleWidth * percentTic);

                // draw progress tic rect fill for progress bar
                mPaintMinorText.setColor(colorEdge);
                mPaintMinorText.setStyle(Paint.Style.FILL);
                canvas.drawRect(left, top, right, bottom, mPaintMinorText);


                // advance draw cursor for next star
                yTitle = yTitle + yPad;
            }
        }
        else {
            if (daoEpic == null) Log.e(TAG, "Oops!  no active epic...");
            else Log.e(TAG, "Oops!  no active stage...");

        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    private boolean drawBoundingRect(Canvas canvas) {
//        Log.d(TAG, "Bounding rect: " + getBoundingRect().toString());
        RectF scaledBoundingRect = scaleRect();
//        Log.d(TAG, "Scaled Bounding rect: " + scaledBoundingRect.toString());
        canvas.drawRoundRect(scaledBoundingRect, 0.0f, 0.0f, mPaintBoundingRect);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private RectF scaleRect() {

        float left = vertToDeviceX((long)getBoundingRect().left, getScaleFactor());
        float top = vertToDeviceY((long)getBoundingRect().top, getScaleFactor());
        float right = vertToDeviceX((long)getBoundingRect().right, getScaleFactor());
        float bottom = vertToDeviceY((long)getBoundingRect().bottom, getScaleFactor());
        // create scaled rect
        RectF rect = new RectF(left, top, right, bottom);
        return rect;
    }
    ///////////////////////////////////////////////////////////////////////////

}
