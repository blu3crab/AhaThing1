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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.adaptivehandyapps.ahathing.dal.RepoProvider;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoLocus;
import com.adaptivehandyapps.ahathing.dao.DaoLocusList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;

import java.util.ArrayList;
import java.util.List;

public class StageViewRing {

    private static final String TAG = StageViewRing.class.getSimpleName();

    private static final int DEFAULT_MINOR_TEXT_SIZE_DP = 12;
    private static final int DEFAULT_MAJOR_TEXT_SIZE_DP = 24;
    private static final float DECREMENT_TEXT_SIZE_DP = 24.0F;

    private static final float DEFAULT_RECT_SIZE_DP = 24.0F;

    private Context mContext;
    private StageViewController mParentViewController;

//    private RepoProvider mRepoProvider;

    private Canvas mCanvas;

    private int mCanvasWidth;
    private int mCanvasHeight;
    private float mDensity;

    private Paint mPaintMinorText;
    private Paint mPaintMajorText;
    private Paint mPaintMapRect;

    private int mMinorTextSize = DEFAULT_MINOR_TEXT_SIZE_DP;
    private int mMajorTextSize = DEFAULT_MAJOR_TEXT_SIZE_DP;

    // pinch zoom support - scale factor ranges from .1 to 1.9
    private float mScaleFactor = 1.0f;

    // list of rects cooresponding to locus translated to device coords
    List<RectF> mRectList;
    // list of selection markers for each locus
    List<Boolean> mSelectList;

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    public StageViewRing(Context context, StageViewController parentViewController) {
        mContext = context;
        mParentViewController = parentViewController;

        // get screen attributes
        mCanvasWidth = mParentViewController.getCanvasWidth();
        mCanvasHeight = mParentViewController.getCanvasHeight();
        mDensity = mParentViewController.getDensity();

        // ensure RepoProvider ready
//        mRepoProvider = parentViewController.getRepoProvider();
        if (MainActivity.getPlayListInstance().getActiveStory() != null) {
            Log.v(TAG, "RepoProvider ready for " + MainActivity.getPlayListInstance().getActiveStory().getMoniker() + "...");
            DaoStage daoStage = MainActivity.getPlayListInstance().getActiveStage();
            if (!daoStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                Log.e(TAG, "RepoProvider UNKNOWN stage type: " + daoStage.getStageType());
            }
        } else {
            Log.e(TAG, "RepoProvider NULL or NOT ready!");
        }
        init(context);
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

    public int getRingColor(DaoLocus daoLocus) {
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
    public float vertToDeviceX(Long vertX, float scaleFactor) {
        // derive delta x,y to shift from abstract locus center to device screen center
        float dx = (mCanvasWidth / 2) - StageModelRing.RING_CENTER_X.floatValue();
        // shift x,y from abstract locus center to device screen center
        float x = vertX.floatValue() + dx;
        // scale by applying dist from dev center by scale factor
        dx = (x - (mCanvasWidth / 2));
        dx = (dx * scaleFactor) - dx;
        x = x + dx;
        return x;
    }

    ///////////////////////////////////////////////////////////////////////////
    public float vertToDeviceY(Long vertY, float scaleFactor) {
        // derive delta x,y to shift from abstract locus center to device screen center
        float dy = (mCanvasHeight / 2) - StageModelRing.RING_CENTER_Y.floatValue();
        // shift x,y from abstract locus center to device screen center
        float y = vertY.floatValue() + dy;
        // scale by applying dist from dev center by scale factor
        dy = (y - (mCanvasHeight / 2));
        dy = (dy * scaleFactor) - dy;
        y = y + dy;
        return y;
    }

    ///////////////////////////////////////////////////////////////////////////
    public List<Boolean> setSelectLocus(DaoLocusList daoLocusList, Boolean select) {
        // create select list
        mSelectList = new ArrayList<>();
        // for each locus
        for (DaoLocus daoLocus : daoLocusList.locii) {
            mSelectList.add(select);
        }
        return mSelectList;
    }
    ///////////////////////////////////////////////////////////////////////////
    // TODO: add pan support
    public List<RectF> transformLocus(DaoLocusList daoLocusList, float scaleFactor) {
        Log.v(TAG, "transformLocus for " + scaleFactor + "...");
        // set scale factor
        setScaleFactor(scaleFactor);
        // create rect list
        mRectList = new ArrayList<>();
        // for each locus
        for (DaoLocus daoLocus : daoLocusList.locii) {
            // TODO: refactor canvas center to focus when panning
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

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean drawLocus(Canvas canvas) {
        if (MainActivity.getPlayListInstance().getActiveStory() != null) {
            Log.v(TAG, "RepoProvider ready for " + MainActivity.getPlayListInstance().getActiveStory().getMoniker() + "...");
        }
        else {
            Log.e(TAG, "RepoProvider NOT ready...");
            return false;
        }

        DaoStage daoStage = MainActivity.getPlayListInstance().getActiveStage();
        DaoLocusList daoLocusList = daoStage.getLocusList();

        int color;
        // for each locus
        for (DaoLocus daoLocus : daoLocusList.locii) {
            // find index of locus
            int i = daoLocusList.locii.indexOf(daoLocus);
            if (mSelectList.get(i)) {
                // if selected, set selected color & fill
                color = mContext.getResources().getColor(R.color.colorStageAccent);
                mPaintMapRect.setStyle(Paint.Style.FILL);
            }
            else {
                // set unselected color & no fill
                color = getRingColor(daoLocus);
                mPaintMapRect.setStyle(Paint.Style.STROKE);
            }
            mPaintMapRect.setColor(color);

            // draw locus
            canvas.drawOval(mRectList.get(i), mPaintMapRect);
//            canvas.drawOval(oval, mPaintMapRect);
//            canvas.drawRect(oval, mPaintMapRect);

            // annotate w/ name
            mPaintMinorText.setColor(color);
            String id = daoLocus.getNickname().substring(daoLocus.getNickname().indexOf("L"));
            id = id.substring(1);
            drawText(canvas, id, mPaintMinorText, mRectList.get(i).centerX(), mRectList.get(i).centerY());
        }
        return true;
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
    // gesture handlers
    public int toggleSelection(float touchX, float touchY, float z, Boolean plus) {
        Log.d(TAG, "toggleSelection touch (x,y) " + touchX + ", " + touchY);
        int selectIndex = DaoDefs.INIT_INTEGER_MARKER;

        // for each rect
        for (RectF r : mRectList) {
            // if rect touched
            if (r.contains(touchX, touchY)) {
                selectIndex = mRectList.indexOf(r);
                mSelectList.set(selectIndex, !mSelectList.get(selectIndex));
                // if selecting plus ring
                if (plus) {
                    if (MainActivity.getRepoProviderInstance().getStageModelRing() != null) {
                        List<Integer> ringIndexList = MainActivity.getRepoProviderInstance().getStageModelRing().findRing(selectIndex);
                        // toggle each rect in ring list
                        for (Integer i : ringIndexList) {
                            mSelectList.set(i, !mSelectList.get(i));
                        }
                    }
                    else {
                        Log.e(TAG, "Oops! for repo " + MainActivity.getRepoProviderInstance().toString() + " NULL getStageModelRing()...");
                    }
                }
            }
        }
        return selectIndex;
    }
    ///////////////////////////////////////////////////////////////////////////

}
