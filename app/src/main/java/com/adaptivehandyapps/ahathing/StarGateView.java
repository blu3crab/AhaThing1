/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker FEB 2017
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import com.adaptivehandyapps.ahathing.ahautils.StringUtils;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoLocus;
import com.adaptivehandyapps.ahathing.dao.DaoLocusList;

import java.util.ArrayList;
import java.util.List;

public class StarGateView {

    private static final String TAG = StarGateView.class.getSimpleName();

    private static final int DEFAULT_MINOR_TEXT_SIZE_DP = 12;
    private static final int DEFAULT_MAJOR_TEXT_SIZE_DP = 24;
    private static final float DECREMENT_TEXT_SIZE_DP = 24.0F;

    private static final float DEFAULT_RECT_SIZE_DP = 24.0F;

    private static final float DEFAULT_LOWLIGHT_DP = 2.0F;
    private static final float DEFAULT_HIGHLIGHT_DP = 8.0F;

    private Context mContext;
    private StarGateViewController mParentViewController;

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
    private float mScaleFactor = 2.75f;

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
    // getters/setters
    private float getScaleFactor() {
        return mScaleFactor;
    }
    private float setScaleFactor(float scaleFactor) {
//        // exponentially expand scale factor
//        Double power = Math.pow(scaleFactor, 2);
//        mScaleFactor = power.floatValue();
        mScaleFactor = scaleFactor;
        return mScaleFactor;
    }

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

    public StarGateModel getStarGateModel() { return mParentViewController.getStarGateModel(); }
    public StarGateManager getStarGateManager() { return mParentViewController.getStarGateManager(); }
    ///////////////////////////////////////////////////////////////////////////
    // constructors
    public StarGateView(Context context, StarGateViewController parentViewController) {
        mContext = context;
        mParentViewController = parentViewController;

        // get screen attributes
        setCanvasWidth(mParentViewController.getCanvasWidth());
        setCanvasHeight(mParentViewController.getCanvasHeight());
        setDensity(mParentViewController.getDensity());
        Log.d(TAG, "width=" + getCanvasWidth() + ", height=" + getCanvasHeight() + ", density=" + getDensity());

        // set scale factor
        float min = Math.min(getCanvasWidth(), getCanvasHeight());
        float scaleFactor = (min/3)/StarGateModel.LOCUS_DIST.floatValue();
        setScaleFactor(scaleFactor);
        // create new StarGate model
        mParentViewController.setStarGateModel(new StarGateModel());
        Log.d(TAG, "NEW StarGateModel at " + mParentViewController.getStarGateModel().toString() + " with scale factor " + getScaleFactor());
        transformLocus(getStarGateModel().getLocusList(), getScaleFactor());

        // init local objects
        init(context);
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    ///////////////////////////////////////////////////////////////////////////
    private Boolean init(Context context) {

        // minor text paint
        mPaintMinorText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMinorText.setStyle(Paint.Style.FILL);
        mPaintMinorText.setTextSize(mParentViewController.getMinorTextSize());
        mPaintMinorText.setTypeface(Typeface.SANS_SERIF);
        mPaintMinorText.setColor(context.getResources().getColor(R.color.colorStarGatePrimary));
        // major text paint
        mPaintMajorText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMajorText.setStyle(Paint.Style.FILL);
        mPaintMajorText.setTextSize(mParentViewController.getMajorTextSize());
        mPaintMajorText.setTypeface(Typeface.SANS_SERIF);
        mPaintMajorText.setColor(context.getResources().getColor(R.color.colorStarGatePrimary));
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
        float dx = (getCanvasWidth() / 2) - mParentViewController.getStarGateModel().getFocusX();
        // shift x,y from abstract locus center to device screen center
        float x = vertX.floatValue() + dx;
        // scale by applying dist from dev center by scale factor
        dx = (x - (getCanvasWidth() / 2));
        dx = (dx * scaleFactor) - dx;
        x = x + dx;
        // test inverse transform
        float inverseX = deviceToVertX(x, scaleFactor);
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
        dx = (getCanvasWidth() / 2) - mParentViewController.getStarGateModel().getFocusX();
        // shift x,y from device screen center to abstract locus center
        x = x - dx;
        vertX = (long) x;
        return vertX;
    }

    ///////////////////////////////////////////////////////////////////////////
    public float vertToDeviceY(Long vertY, float scaleFactor) {
        // derive delta x,y to shift from abstract locus center to device screen center
        float dy = (getCanvasHeight() / 2) - mParentViewController.getStarGateModel().getFocusY();
        // shift x,y from abstract locus center to device screen center
        float y = vertY.floatValue() + dy;
        // scale by applying dist from dev center by scale factor
        dy = (y - (getCanvasHeight() / 2));
        dy = (dy * scaleFactor) - dy;
        y = y + dy;
        float inverseY = deviceToVertY(y, scaleFactor);
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
        dy = (getCanvasHeight() / 2) - mParentViewController.getStarGateModel().getFocusY();
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
//        // set scale factor
//        setScaleFactor(scaleFactor);
        // create rect list
        mRectList = new ArrayList<>();
        // for each locus
        for (DaoLocus daoLocus : daoLocusList.locii) {
            // transform abstract vert coords to device coords
            float x = vertToDeviceX(daoLocus.getVertX(), getScaleFactor());
            float y = vertToDeviceY(daoLocus.getVertY(), getScaleFactor());
            // make circle (oval) sized based on scale factor
            final float LOCUS_DIAMETER = StarGateModel.LOCUS_DIST.intValue()/2;
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
        int color;

            // if stage locus list defined
            DaoLocusList daoLocusList = getStarGateModel().getLocusList();
            if (daoLocusList != null) {
                // for each locus
                for (DaoLocus daoLocus : daoLocusList.locii) {
                    // default to fill
                    mPaintMapRect.setStyle(Paint.Style.FILL);
                    // find index of locus
                    int i = daoLocusList.locii.indexOf(daoLocus);
                    if (i >= 0 && i < daoLocusList.locii.size()) {
                        // paint filled rect with primary color
                        color = getStarGateModel().getForeColorList().get(i);
                        mPaintMapRect.setStyle(Paint.Style.FILL);
                        mPaintMapRect.setColor(color);
                        canvas.drawOval(mRectList.get(i), mPaintMapRect);
                        // paint stroke rect in secondary color
                        color = getStarGateModel().getBackColorList().get(i);
                        float strokeWidth = mPaintMapRect.getStrokeWidth();
                        mPaintMapRect.setStrokeWidth(DEFAULT_HIGHLIGHT_DP);
                        mPaintMapRect.setStyle(Paint.Style.STROKE);
                        mPaintMapRect.setColor(color);
                        canvas.drawOval(mRectList.get(i), mPaintMapRect);
                    }
                    else {
                        Log.e(TAG, "drawLocus invalid inx " + i + " with size " + daoLocusList.locii.size());
                        // set unselected color & no fill to signal incoherence
                        color = mContext.getResources().getColor(R.color.colorLightGrey);
                        mPaintMapRect.setColor(color);
                        mPaintMapRect.setStrokeWidth(DEFAULT_LOWLIGHT_DP);
                        mPaintMapRect.setStyle(Paint.Style.STROKE);
                        // draw locus
                        canvas.drawOval(mRectList.get(i), mPaintMapRect);
                    }

                    // annotate w/ activity or label
                    Paint paint;
                    String label;
                    int width;
                    if (!getStarGateModel().getActivityList().get(i).equals(DaoDefs.INIT_STRING_MARKER)) {
                        mPaintMajorText.setColor(Color.BLACK);
                        paint = mPaintMajorText;
                        label = getStarGateModel().getActivityList().get(i);
                        width = StringUtils.getPaintTextWidth(label, mPaintMajorText);
                    }
                    else {
                        mPaintMinorText.setColor(Color.BLACK);
                        paint = mPaintMinorText;
                        label = daoLocus.getNickname().substring(daoLocus.getNickname().indexOf("L"));
                        label = label.substring(1);
                        width = StringUtils.getPaintTextWidth(label, mPaintMinorText);
                    }
                    int x = (int)mRectList.get(i).centerX() - (width/2);
//                    Log.v(TAG, "drawLocus text width " + width + " offset X at " + x + " with center X at " + mRectList.get(i).centerX());
                    drawText(canvas, label, paint, x, mRectList.get(i).centerY());
                }
                return true;
            }
            else {
                Log.e(TAG, "Oops! no locus list...");
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

}
