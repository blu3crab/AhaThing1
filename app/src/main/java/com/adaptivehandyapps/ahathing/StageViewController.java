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
//
// Created by mat on 1/9/2017.
//
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.StringUtils;
import com.adaptivehandyapps.ahathing.dao.DaoAction;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.adaptivehandyapps.ahathing.dao.DaoLocus;
import com.adaptivehandyapps.ahathing.dao.DaoLocusList;
import com.adaptivehandyapps.ahathing.dao.DaoOutcome;
import com.adaptivehandyapps.ahathing.dao.DaoStage;
import com.adaptivehandyapps.ahathing.dao.DaoStory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
///////////////////////////////////////////////////////////////////////////
// StageViewController extends View to manage gestures on stage
//  HAS A
//      StageViewRing to draw view, transform device<->screen, scale functions
//          HAS A
//              StageModelRing with stage objects locii, props, colors; find locii, rings
public class StageViewController extends View implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final String TAG = StageViewController.class.getSimpleName();

    public static final float DEFAULT_SCALE_FACTOR = 1.0F;

    private static final int DEFAULT_MINOR_TEXT_SIZE_DP = 12;
    private static final int DEFAULT_MAJOR_TEXT_SIZE_DP = 24;
    private static final float DECREMENT_TEXT_SIZE_DP = 24.0F;
    private static final float DEFAULT_RECT_SIZE_DP = 24.0F;

    private Context mContext;
    private MainActivity mParent;

    private StageModelRing mStageModelRing;

    private DaoStage mActiveStage;

    private Boolean mInitLoad = true;

    private int mCanvasWidth;   // from canvas - padding
    private int mCanvasHeight;
    private float mDensity;

    private Paint mPaintMinorText;
    private Paint mPaintMajorText;

    private int mMinorTextSize = DEFAULT_MINOR_TEXT_SIZE_DP;
    private int mMajorTextSize = DEFAULT_MAJOR_TEXT_SIZE_DP;

    // pinch zoom support - scale factor ranges from .1 to 1.9
    private float mRawScaleFactor = DEFAULT_SCALE_FACTOR;   // retain raw scale factor to test for change
    private float mScaleFactor = DEFAULT_SCALE_FACTOR;
    private ScaleGestureDetector mScaleGestureDetector;

    // gesture marker: onLongPress, onSingleTapConfirmed, onDoubleTap, onScale
    private boolean mGestureDetected = false;
    private boolean mLongPressDown = true;
    // gesture detector
    private GestureDetectorCompat mDetector;
    // touch position
    private float mTouchX = 0.0f;
    private float mTouchY = 0.0f;
    private float mVelocityX = 0.0f;
    private float mVelocityY = 0.0f;
    private MotionEvent mEvent1;
    private MotionEvent mEvent2;

    private List<String> mHint = new ArrayList<String>(Arrays.asList(
            "Pinch or Zoom.",
            " -- or  --",
            "LongPress to toggle shrink-grow.",
            "SingleTap to bump.",
            "DoubleTap to double."
    ));

    ///////////////////////////////////////////////////////////////////////////
    // setters/getters
    public PlayListService getPlayListService() {
        return mParent.getPlayListService();
    }

    public RepoProvider getRepoProvider() {
        return mParent.getRepoProvider();
    }

    public StageManager getStageManager() {
        return mParent.getStageManager();
    }

    private StageViewRing mStageViewRing;
    public StageViewRing getStageViewRing() {
        return mStageViewRing;
    }
    public void setStageViewRing(StageViewRing stageViewRing) {
        this.mStageViewRing = stageViewRing;
    }

    public float getTouchX() {
        return mTouchX;
    }
    public void setTouchX(float touchX) {
        this.mTouchX = touchX;
        if (getStageManager() != null) getStageManager().setTouchX(touchX);
    }

    public float getTouchY() {
        return mTouchY;
    }
    public void setTouchY(float touchY) {
        this.mTouchY = touchY;
        if (getStageManager() != null) getStageManager().setTouchY(touchY);
    }

    public float getVelocityX() {
        return mVelocityX;
    }
    public void setVelocityX(float velocityX) {
        this.mVelocityX = velocityX;
        if (getStageManager() != null) getStageManager().setVelocityX(velocityX);
    }

    public float getVelocityY() {
        return mVelocityY;
    }
    public void setVelocityY(float velocityY) {
        this.mVelocityY = velocityY;
        if (getStageManager() != null) getStageManager().setVelocityY(velocityY);
    }

    public MotionEvent getEvent1() {
        return mEvent1;
    }
    public void setEvent1(MotionEvent event1) {
        this.mEvent1 = event1;
        if (getStageManager() != null) getStageManager().setEvent1(event1);
    }

    public MotionEvent getEvent2() {
        return mEvent2;
    }
    public void setEvent2(MotionEvent event2) {
        this.mEvent2 = event2;
        if (getStageManager() != null) getStageManager().setEvent2(event2);
    }

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    public StageViewController(Context context) {
        super(context);
        Log.d(TAG, "StageViewController constructor init context...");
        init(context);
    }

    public StageViewController(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "StageViewController constructor init context/attrs...");
        init(context);
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean init(Context context) {

        mContext = context;
        mParent = (MainActivity) context;
        if (mParent != null) {
            Log.v(TAG, "StageViewController ready with parent " + mParent.toString() + "...");
        } else {
            Log.e(TAG, "Oops!  StageViewController Parent context (MainActivity) NULL!");
        }
        // init draw resources
        initDrawResources();

//        // establish sound manager
//        mSoundManager = new SoundManager(mContext, this);
//        // establish stage manager
//        mStageManager = new StageManager(mContext, this);
//
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean initDrawResources() {

        // adjust text size
        // TODO: refactor to getDensity()
        setDensity(getResources().getDisplayMetrics().density);
        setMinorTextSize(Math.round(DEFAULT_MINOR_TEXT_SIZE_DP * getDensity()));
        setMajorTextSize(Math.round(DEFAULT_MAJOR_TEXT_SIZE_DP * getDensity()));

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

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(mContext, this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
        // set long press enabled
        mDetector.setIsLongpressEnabled(true);

        // pinch zoom support
        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleListener());

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters/setters
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
        PrefsUtils.setPrefs(mContext, PrefsUtils.SCALE_FACTOR_KEY, scaleFactor);
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

    public StageModelRing getStageModelRing() {
        return mStageModelRing;
    }
    public void setStageModelRing(StageModelRing stageModelRing) {
        this.mStageModelRing = stageModelRing;
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

        if (mInitLoad) {
            Log.d(TAG, "onMeasure width/height = " + getCanvasWidth() + "/" + getCanvasHeight() + " with density " + getDensity());
            Log.d(TAG, "onMeasure width/height mode " + modeToString(widthMode) + "/" + modeToString(heightMode));
            Log.d(TAG, "onMeasure width/height size = " + widthSize + "/ " + heightSize);
            mInitLoad = false;
            // get scale factor
            setScaleFactor(PrefsUtils.getFloatPrefs(mContext, PrefsUtils.SCALE_FACTOR_KEY));
            Log.d(TAG, "onMeasure scale factor = " + Float.toString(getScaleFactor()));

            // TODO: support multiple stage gracefully
            if (getPlayListService() != null && getStageViewRing() == null) {
                mActiveStage = getPlayListService().getActiveStage();
                if (mActiveStage != null && mActiveStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                    Log.v(TAG, "onMeasure NEW stage view type: " + mActiveStage.getStageType());
                    // create stage view helper
                    mStageViewRing = new StageViewRing(mContext, this);
                    DaoLocusList daoLocusList = mActiveStage.getLocusList();
                    // transform locus to device coords
                    mStageViewRing.transformLocus(daoLocusList, mScaleFactor);
                } else {
                    if (mActiveStage == null) Log.e(TAG, "Oops!  no active stage...");
                    else Log.e(TAG, "Oops!  UNKNOWN stage type: " + mActiveStage.getStageType());
                }
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
        }
        else  {
            // single touch event
            Log.v(TAG, "onTouchEvent single-touch x, y: " + event.getX(pointerIndex) + ", " + event.getY(pointerIndex));

            setTouchX(event.getX());
            setTouchY(event.getY());

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
                    setTouchX(0.0f);
                    setTouchY(0.0f);
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
//        Log.d(TAG, "onFling(" + StringUtils.actionToString(event1.getActionMasked()) + "," + StringUtils.actionToString(event2.getActionMasked()) + "): " +
//                "\nvelocity X,Y " + velocityX + ", " + velocityY +
//                ", \n(1) raw X,Y " + event1.getRawX() + ", " + event1.getRawY() +
//                ", \n(2) raw X,Y " + event2.getRawX() + ", " + event2.getRawY() +
//                ", \ndelta raw X,Y " + (event2.getRawX() - event1.getRawX()) + ", " + (event2.getRawY() - event1.getRawY()) );
//        toggleActorPath(velocityX, velocityY, event1.getRawX(), event1.getRawY(), event2.getRawX(), event2.getRawY());
        Log.d(TAG, "onFling plotpath (" + StringUtils.actionToString(event1.getActionMasked()) + "," + StringUtils.actionToString(event2.getActionMasked()) + "): " +
                "\nvelocity X,Y " + velocityX + ", " + velocityY +
                ", \n(1) raw X,Y " + event1.getX() + ", " + event1.getY() +
                ", \n(2) raw X,Y " + event2.getX() + ", " + event2.getY() +
                ", \ndelta raw X,Y " + (event2.getX() - event1.getX()) + ", " + (event2.getY() - event1.getY()) );
//        getStageManager().toggleActorPath(velocityX, velocityY, event1.getX(), event1.getY(), event2.getX(), event2.getY());

        setVelocityX(velocityX);
        setVelocityY(velocityY);
        setEvent1(event1);
        setEvent2(event2);
        setTouchX(event1.getX());
        setTouchY(event1.getY());
        getStageManager().onAction(getStageViewRing(), getStageModelRing(), DaoAction.ACTION_TYPE_FLING);
        invalidate();

        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(TAG, "onLongPress(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        Toast.makeText(getContext(), "LongPress: gesture detected...", Toast.LENGTH_SHORT).show();
        mGestureDetected = true;
        mLongPressDown = true;
//        setTouchX(event.getX());
//        setTouchY(event.getY());
//        Log.d(TAG, "onLongPress action(" + DaoAction.ACTION_TYPE_LONG_PRESS + ").");
//        getStageManager().onAction(getStageViewRing(), getStageModelRing(), DaoAction.ACTION_TYPE_LONG_PRESS);
//        invalidate();
    }

    @Override
    public void onShowPress(MotionEvent event) {
//        Log.d(TAG, "onShowPress: " + event.toString());
        Log.d(TAG, "onShowPress(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        mGestureDetected = true;
        mLongPressDown = true;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distX, float distY) {
//        Log.d(TAG, "onScroll event1: " + event1.toString() + "\nevent2: "+ event1.toString() +
//                "\ndistance X,Y " + distX + ", " + distY);
        Log.d(TAG, "onScroll(" + StringUtils.actionToString(event1.getActionMasked()) + "," + StringUtils.actionToString(event2.getActionMasked()) + "): " +
                "\ndistance X,Y " + distX + ", " + distY);
        Log.d(TAG, "onScroll event1(" + event1.getX() + "," + event1.getY() + ") event2(" + event2.getX() + "," + event2.getY() + ")");
        if (event1.getPointerCount() > 1) {
            Log.d(TAG, "onScroll event1 multi-touch? " + event1.getPointerCount());
        }
        if (event2.getPointerCount() > 1) {
            Log.d(TAG, "onScroll event2 multi-touch? " + event2.getPointerCount());
        }
        // establish stage lock fab
        Boolean lock = PrefsUtils.getBooleanPrefs(mContext,PrefsUtils.STAGE_LOCK_KEY);
        if (!lock) {
//            getRepoProvider().getStageModelRing().shiftFocus(distX / 2, distY / 2);
            getStageModelRing().shiftFocus(distX / 2, distY / 2);
            mActiveStage = getPlayListService().getActiveStage();
            if (mActiveStage != null && mActiveStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                Log.v(TAG, "onScroll stage type: " + mActiveStage.getStageType());
                DaoLocusList daoLocusList = mActiveStage.getLocusList();
                // transform locus to device coords
                if (mStageViewRing != null) {
                    mStageViewRing.transformLocus(daoLocusList, getScaleFactor());
                } else {
                    Log.e(TAG, "onScroll finds NULL StageViewRing ");
                }
            } else {
                if (mActiveStage == null) Log.e(TAG, "Oops!  no active stage...");
                else Log.e(TAG, "onScroll UNKNOWN stage type: " + mActiveStage.getStageType());
                return false;
            }
            // redraw after shift in focus
            invalidate();
        }
        return true;
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
//        Toast.makeText(getContext(), "onSingleTapConfirmed: gesture detected...", Toast.LENGTH_SHORT).show();
        if (!mGestureDetected) {
            mGestureDetected = true;
            setTouchX(event.getX());
            setTouchY(event.getY());
            Log.d(TAG, "onSingleTapConfirmed action(" + DaoAction.ACTION_TYPE_SINGLE_TAP + ").");
            getStageManager().onAction(getStageViewRing(), getStageModelRing(), DaoAction.ACTION_TYPE_SINGLE_TAP);
            invalidate();
        }
        else {
            if (mLongPressDown) {
                mGestureDetected = false;
                mLongPressDown = false;
                setTouchX(event.getX());
                setTouchY(event.getY());
                Log.d(TAG, "onSingleTapConfirmed action(" + DaoAction.ACTION_TYPE_LONG_PRESS + ").");
                getStageManager().onAction(getStageViewRing(), getStageModelRing(), DaoAction.ACTION_TYPE_LONG_PRESS);
                invalidate();
            }
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
//        Log.d(TAG, "onDoubleTap: " + event.toString());
        Log.d(TAG, "onDoubleTap(" + StringUtils.actionToString(event.getActionMasked()) + "): " + "\n X,Y " + event.getX() + ", " + event.getY());
        Toast.makeText(getContext(), "onDoubleTap: gesture detected...", Toast.LENGTH_SHORT).show();
        mGestureDetected = true;
        setTouchX(event.getX());
        setTouchY(event.getY());
        getStageManager().onAction(getStageViewRing(), getStageModelRing(), DaoAction.ACTION_TYPE_DOUBLE_TAP);
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
            setScaleFactor(Math.max(0.1f, Math.min(mScaleFactor, 1.9f)));
            Log.v(TAG, "ScaleListener : " + getScaleFactor() + ", detector: " + detector.getScaleFactor());
            mGestureDetected = true;

            // if scale factor changed
            if (mRawScaleFactor != detector.getScaleFactor()) {
                mRawScaleFactor = detector.getScaleFactor();
                // check stage type to find stage view helper
                mActiveStage = getPlayListService().getActiveStage();
                if (mActiveStage != null && mActiveStage.getStageType().equals(DaoStage.STAGE_TYPE_RING)) {
                    Log.v(TAG, "onScale stage type: " + mActiveStage.getStageType());
                    DaoLocusList daoLocusList = mActiveStage.getLocusList();
                    // transform locus to device coords
                    if (mStageViewRing != null) {
                        mStageViewRing.transformLocus(daoLocusList, getScaleFactor());
                    } else {
                        Log.e(TAG, "onScale finds NULL StageViewRing ");
                    }
                } else {
                    if (mActiveStage == null) Log.e(TAG, "Oops!  no active stage...");
                    else Log.e(TAG, "onScale UNKNOWN stage type: " + mActiveStage.getStageType());
                    return false;
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

}
