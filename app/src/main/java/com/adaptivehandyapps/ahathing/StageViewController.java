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
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.StringUtils;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoLocus;
import com.adaptivehandyapps.ahathing.dao.DaoLocusList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StageViewController extends View implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final String TAG = StageViewController.class.getSimpleName();

    private static final int DEFAULT_MINOR_TEXT_SIZE_DP = 12;
    private static final int DEFAULT_MAJOR_TEXT_SIZE_DP = 24;
    private static final float DECREMENT_TEXT_SIZE_DP = 24.0F;

    private static final float DEFAULT_RECT_SIZE_DP = 24.0F;

    private Context mContext;
    //    private RepoProvider mRepoProvider;
    private DaoStage mActiveStage;

    private StageViewRing mStageViewRing;

    private Boolean mInitLoad = true;

    private int mCanvasWidth;   // from canvas - padding
    private int mCanvasHeight;
    private float mDensity;

    private Paint mPaintMinorText;
    private Paint mPaintMajorText;
    private Paint mPaintMapRect;

    private int mMinorTextSize = DEFAULT_MINOR_TEXT_SIZE_DP;
    private int mMajorTextSize = DEFAULT_MAJOR_TEXT_SIZE_DP;

    private RectF mMapRect;
//    private Integer mMapVertCountX;
//    private Integer mMapVertCountY;

    // pinch zoom support - scale factor ranges from .1 to 1.9
    private float mRawScaleFactor = 1.0f;   // retain raw scale factor to test for change
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleGestureDetector;

    // gesture marker: onLongPress, onSingleTapConfirmed, onDoubleTap, onScale
    private boolean mGestureDetected = false;
    // gesture detector
    private GestureDetectorCompat mDetector;
    // touch position
    private float mTouchX = 0.0f;
    private float mTouchY = 0.0f;

    private List<String> mHint = new ArrayList<String>(Arrays.asList(
            "Pinch or Zoom.",
            " -- or  --",
            "LongPress to toggle shrink-grow.",
            "SingleTap to bump.",
            "DoubleTap to double."
    ));

//    private int mVertResizeFactor = 1;
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
    // constructors
    public StageViewController(Context context) {
        super(context);
        init(context);
    }

    public StageViewController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean init(Context context) {

        mContext = context;
        MainActivity parent = (MainActivity) context;
        if (parent != null) {
            setPlayListService(parent.getPlayListService());
            if (getPlayListService() != null && getPlayListService().getActiveStory() != null) {
                Log.v(TAG, "Story ready for " + getPlayListService().getActiveStory().getMoniker() + "...");
            }
            else {
                Log.e(TAG, "Story NULL or NOT ready!");
                return false;
            }
            setRepoProvider(parent.getRepoProvider());
            if (getRepoProvider() != null) {
                Log.v(TAG, "Repo Provider ready at " + getRepoProvider() + "...");
            }
            else {
                Log.e(TAG, "Repo Provider NULL!");
                return false;
            }
        }
        else {
            Log.e(TAG, "Parent context (MainActivity) NULL!");
            return false;
        }

//        MainActivity parent = (MainActivity) context;
//        if (parent != null) {
//            MainActivity.getRepoProviderInstance() = parent.getRepoProvider();
////            if (mRepoProvider != null && MainActivity.getPlayListInstance().getActiveStory() != null) {
//                Log.v(TAG, "RepoProvider ready for " + MainActivity.getPlayListInstance().getActiveStory().getMoniker() + "...");
//            }
//            else {
//                Log.e(TAG, "RepoProvider NULL or NOT ready!");
//                return false;
//            }
//        }
//        else {
//            Log.e(TAG, "Parent context (MainActivity) NULL!");
//            return false;
//        }

        // adjust text size
        // TODO: refactor to getDensity()
        setDensity(getResources().getDisplayMetrics().density);
        setMinorTextSize(Math.round(DEFAULT_MINOR_TEXT_SIZE_DP * getDensity()));
        setMajorTextSize(Math.round(DEFAULT_MAJOR_TEXT_SIZE_DP * getDensity()));
//        float density = getResources().getDisplayMetrics().density;
//        setMinorTextSize(Math.round(DEFAULT_MINOR_TEXT_SIZE_DP * density));
//        setMajorTextSize(Math.round(DEFAULT_MAJOR_TEXT_SIZE_DP * density));

        // minor text paint
        mPaintMinorText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMinorText.setStyle(Paint.Style.FILL);
        mPaintMinorText.setTextSize(getMinorTextSize());
        mPaintMinorText.setColor(getResources().getColor(R.color.colorStagePrimary));
        // major text paint
        mPaintMajorText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMajorText.setStyle(Paint.Style.FILL);
        mPaintMajorText.setTextSize(getMajorTextSize());
        mPaintMajorText.setColor(getResources().getColor(R.color.colorStagePrimary));
//        mPaintMajorText.setColor(Color.rgb(255, 255, 255));


        mPaintMapRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMapRect.setStyle(Paint.Style.STROKE);
        mPaintMapRect.setStrokeWidth(4);
        mPaintMapRect.setColor(Color.CYAN);

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(context, this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
        // set long press enabled
        mDetector.setIsLongpressEnabled(true);

        // pinch zoom support
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
//    public RepoProvider getRepoProvider() { return mRepoProvider; }
//
//    public Boolean setRepoProvider(RepoProvider repoProvider) { mRepoProvider = repoProvider; return true; }
//
    public int getCanvasWidth() {
        return mCanvasWidth;
    }

    private void setCanvasWidth(int canvasWidth) {
        this.mCanvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return mCanvasHeight;
    }

    private void setCanvasHeight(int canvasHeight) {
        this.mCanvasHeight = canvasHeight;
    }

    public float getDensity() {
        return mDensity;
    }

    private void setDensity(float density) {
        this.mDensity = density;
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    private void setScaleFactor(float scaleFactor) {
        this.mScaleFactor = scaleFactor;
    }

    public int getMinorTextSize() {
        return mMinorTextSize;
    }

    private void setMinorTextSize(int minorTextSize) {
        Log.d(TAG, "setMinorTextSize from " + this.mMinorTextSize + " to " + minorTextSize);
        this.mMinorTextSize = minorTextSize;
    }

    public int getMajorTextSize() {
        return mMajorTextSize;
    }

    private void setMajorTextSize(int majorTextSize) {
        Log.d(TAG, "setMajorTextSize from " + this.mMajorTextSize + " to " + majorTextSize);
        this.mMajorTextSize = majorTextSize;
    }
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        setCanvasWidth(widthSize);
        setCanvasHeight(heightSize);

//        setDensity(getResources().getDisplayMetrics().density);
//
        if (mInitLoad) {
            Log.d(TAG, "onMeasure width/height = " + getCanvasWidth() + "/" + getCanvasHeight() + " with density " + getDensity());
            Log.d(TAG, "onMeasure width/height mode " + modeToString(widthMode) + "/" + modeToString(heightMode));
            Log.d(TAG, "onMeasure width/height size = " + widthSize + "/ " + heightSize);
            mInitLoad = false;
            // TODO: support multiple stage gracefully
            mActiveStage = getPlayListService().getActiveStage();
            if (mActiveStage != null && mActiveStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                Log.v(TAG, "RepoProvider stage type: " + mActiveStage.getStageType());
                // create stage view helper
                mStageViewRing = new StageViewRing(mContext, this);
                DaoLocusList daoLocusList = mActiveStage.getLocusList();
                // transform locus to device coords
                mStageViewRing.transformLocus(daoLocusList, mScaleFactor);
                // clear selection list
                mStageViewRing.setSelectLocus(daoLocusList, false);
            }
            else if (mActiveStage != null) {
                Log.e(TAG, "RepoProvider UNKNOWN stage type: " + mActiveStage.getStageType());
            }

        }

        //MUST CALL THIS
        setMeasuredDimension(getCanvasWidth(), getCanvasHeight());
    }
    ///////////////////////////////////////////////////////////////////////////
    private String modeToString(int mode) {
        if (mode == MeasureSpec.EXACTLY) return "MeasureSpec.EXACTLY";
        if (mode == MeasureSpec.AT_MOST) return "MeasureSpec.AT_MOST";
        return "MeasureSpec.UNSPECIFIED";
    }
    ///////////////////////////////////////////////////////////////////////////
    // gesture handlers
    private Boolean toggleSelection(float touchX, float touchY, float z, Boolean plus) {
        if (mActiveStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
            int i = mStageViewRing.toggleSelection(touchX, touchY, z, plus);
            if (i > DaoDefs.INIT_INTEGER_MARKER) {
                DaoLocus daoLocus = mActiveStage.getLocusList().locii.get(i);
                Log.v(TAG, "toggleSelection stage type: " + mActiveStage.getStageType() + " toggle at " + daoLocus.getNickname());
            }
            else {
                Log.e(TAG, "toggleSelection UNKNOWN invalid selection for " + mActiveStage.getStageType());
            }
        }
        else {
            Log.e(TAG, "toggleSelection UNKNOWN stage type: " + mActiveStage.getStageType());
            return false;
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // gesture detectors
    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.d(TAG, "onTouchEvent: " + event.toString());
        // gesture detector
        this.mDetector.onTouchEvent(event);
        // pinch-zoom gesture detector
        mScaleGestureDetector.onTouchEvent(event);

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();
        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);
        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        Log.v(TAG, "onTouchEvent id: " + pointerId + ", action: " + StringUtils.actionToString(maskedAction) + ", index: " + pointerIndex );

        if (event.getPointerCount() > 1) {
            // if multi-touch event
            Log.d(TAG, "Multi-touch event getPointerCount " + event.getPointerCount());
            // TODO: vert counts are inherently dependent on the map - refactor with MapRect class
            mMapRect = scaleRect();
            // redraw to reflect scaling on pinch/zoom
            invalidate();
        }
        else  {
            // single touch event
            Log.v(TAG, "onTouchEvent single-touch x, y: " + event.getX(pointerIndex) + ", " + event.getY(pointerIndex));

            mTouchX = event.getX();
            mTouchY = event.getY();

            // TODO: add pan support
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // if no gestures detected, start move
                    if (!mGestureDetected) {
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    // if no gestures detected, refine move
                    if (!mGestureDetected) {
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // if no gestures detected, complete shape or move
                    if (!mGestureDetected) {
                    }
                    // clear any gesture
                    mGestureDetected = false;
                    // clear current touch X,Y
                    mTouchX = 0.0f;
                    mTouchY = 0.0f;
                    break;
                default:
                    return false;
            }
        }

        // MAT - prevents overrides executing
        // Be sure to call the superclass implementation
//        return super.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent event) {
//        Log.d(TAG, "onDown: " + event.toString());
        Log.d(TAG, "onDown(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
//        Log.d(TAG, "onFling event1: " + event1.toString() + "\nevent2: "+ event1.toString() +
//                "\nvelocity X,Y " + velocityX + ", " + velocityY);
        Log.d(TAG, "onFling(" + StringUtils.actionToString(event1.getActionMasked()) + "," + StringUtils.actionToString(event2.getActionMasked()) + "): " +
                "\nvelocity X,Y " + velocityX + ", " + velocityY);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
//        Log.d(TAG, "onLongPress: " + event.toString());
        Log.d(TAG, "onLongPress(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        Toast.makeText(getContext(), "LongPress: gesture detected...", Toast.LENGTH_SHORT).show();
        mGestureDetected = true;
        // toggle selection plus adjacent
        mTouchX = event.getX();
        mTouchY = event.getY();
        toggleSelection(mTouchX, mTouchY, 0.0f, true);
        invalidate();
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distX, float distY) {
//        Log.d(TAG, "onScroll event1: " + event1.toString() + "\nevent2: "+ event1.toString() +
//                "\ndistance X,Y " + distX + ", " + distY);
        Log.d(TAG, "onScroll(" + StringUtils.actionToString(event1.getActionMasked()) + "," + StringUtils.actionToString(event2.getActionMasked()) + "): " +
                "\ndistance X,Y " + distX + ", " + distY);
        if (event1.getPointerCount() > 1) {
            Log.d(TAG, "onScroll event1 multi-touch? " + event1.getPointerCount());
        }
        if (event2.getPointerCount() > 1) {
            Log.d(TAG, "onScroll event2 multi-touch? " + event2.getPointerCount());
        }

        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
//        Log.d(TAG, "onShowPress: " + event.toString());
        Log.d(TAG, "onLongPress(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
   }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
//        Log.d(TAG, "onSingleTapUp: " + event.toString());
        Log.d(TAG, "onSingleTapUp(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
//        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        Log.d(TAG, "onSingleTapConfirmed(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        Toast.makeText(getContext(), "onSingleTapConfirmed: gesture detected...", Toast.LENGTH_SHORT).show();
        mGestureDetected = true;
        // toggle selection but not adjacent
        mTouchX = event.getX();
        mTouchY = event.getY();
        toggleSelection(mTouchX, mTouchY, 0.0f, false);
        invalidate();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
//        Log.d(TAG, "onDoubleTap: " + event.toString());
        Log.d(TAG, "onDoubleTap(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        Toast.makeText(getContext(), "onDoubleTap: gesture detected...", Toast.LENGTH_SHORT).show();
        mGestureDetected = true;
//        doubleVerts();
        invalidate();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
//        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        Log.d(TAG, "onDoubleTapEvent(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            // prevent scaling too small or too large
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 1.9f));
            Log.v(TAG, "ScaleListener : " + mScaleFactor + ", detector: " + detector.getScaleFactor());
            mGestureDetected = true;

            // if scale factor changed
            if (mRawScaleFactor != detector.getScaleFactor()) {
                mRawScaleFactor = detector.getScaleFactor();
                // check stage type to find stage view helper
                mActiveStage = getPlayListService().getActiveStage();
                if (mActiveStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                    Log.v(TAG, "onScale stage type: " + mActiveStage.getStageType());
                    DaoLocusList daoLocusList = mActiveStage.getLocusList();
                    // transform locus to device coords
                    if (mStageViewRing != null) {
                        mStageViewRing.transformLocus(daoLocusList, mScaleFactor);
                    }
                    else {
                        Log.e(TAG, "onScale finds NULL StageViewRing ");
                    }
                }
                else {
                    Log.e(TAG, "onScale UNKNOWN stage type: " + mActiveStage.getStageType());
                }

            }
            return true;
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Clear canvas
        canvas.drawColor(Color.TRANSPARENT);
//        canvas.drawColor(Color.BLUE);

        if (mStageViewRing != null) {
            mStageViewRing.onDraw(canvas);
        }

        if (mMapRect == null) {
            // set current map & vert counts
            mMapRect = scaleRect();
        }
        // draw map rect
        drawMapRect(canvas);

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

        // center text
//        float x = (canvas.getWidth()/2) - (textWidth/2);
//        canvas.drawText(greeting, x + getPaddingLeft(), getPaddingTop()*16, paint);
//        canvas.drawText(greeting, x + getPaddingLeft(), y, paint);
        canvas.drawText(greeting, x, y, paint);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private boolean drawMapRect(Canvas canvas) {
        canvas.drawRoundRect(mMapRect, 0.0f, 0.0f, mPaintMapRect);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private RectF scaleRect() {

        // define map rect - scale factor ranges from .1 to 1.9
        float left = (mCanvasWidth / 2) - ((mCanvasWidth / 4) * getScaleFactor());
        float right = (mCanvasWidth / 2) + ((mCanvasWidth / 4) * getScaleFactor());
        float top = (mCanvasHeight / 2) - ((mCanvasWidth / 4) * getScaleFactor());
        float bottom = (mCanvasHeight / 2) + ((mCanvasWidth / 4) * getScaleFactor());

//        float left = (mCanvasWidth / 2) - (DEFAULT_RECT_SIZE_DP * mDensity * getScaleFactor());
//        float right = (mCanvasWidth / 2) + (DEFAULT_RECT_SIZE_DP * mDensity * getScaleFactor());
//        float top = (mCanvasHeight / 2) - (DEFAULT_RECT_SIZE_DP * mDensity * getScaleFactor());
//        float bottom = (mCanvasHeight / 2) + (DEFAULT_RECT_SIZE_DP * mDensity * getScaleFactor());
        // create scaled rect
        RectF rect = new RectF(left, top, right, bottom);
        return rect;
    }
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

}
