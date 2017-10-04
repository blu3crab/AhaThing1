/*
 * Project: Things
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

import android.graphics.RectF;
import android.util.Log;

import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.adaptivehandyapps.ahathing.dao.DaoLocus;
import com.adaptivehandyapps.ahathing.dao.DaoLocusList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// StarGate model
public class StarGateModel {
    private static final String TAG = StarGateModel.class.getSimpleName();

    public static final Double LOCUS_DIST = 64.0;
    private final Integer FUDGE_DIST = 16;  // precision loss for 8 rings ok

    // start & delta angles
    private final Double ANGLE_START = 0.0;      // start due east
    private final Double ANGLE_DELTA = 60.0;    // increment counter-clockwise by 60 degrees
    // count of angles around center
    private Integer ANGLE_COUNT_TOTAL = (int)(360.0 / ANGLE_DELTA);
    // convert degrees to radians
    private Double RADIAN_START = (Math.PI * ANGLE_START)/180;
    private Double RADIAN_DELTA = (Math.PI * ANGLE_DELTA)/180;

    public static final Long RING_MAX_X = 1024L;
    public static final Long RING_MIN_X = 0l;
    public static final Long RING_MAX_Y = 1024L;
    public static final Long RING_MIN_Y = 0L;
    public static final Long RING_MAX_Z = 0L;
    public static final Long RING_MIN_Z = 0L;
    public static final Long RING_CENTER_X = (RING_MAX_X - RING_MIN_X)/2;
    public static final Long RING_CENTER_Y = (RING_MAX_Y - RING_MIN_Y)/2;
    public static final Long RING_CENTER_Z = 0L;

    private Integer ringSize = 1;

    private DaoLocusList mLocusList;

    private List<String> mActivityList;
    private List<Integer> mForeColorList;
    private List<Integer> mBackColorList;

    ///////////////////////////////////////////////////////////////////////////
    // focus (center) of view
    private float mFocusX = StarGateModel.RING_CENTER_X;
    private float mFocusY = StarGateModel.RING_CENTER_Y;

    public float getFocusX() {
        return mFocusX;
    }
//    public void setFocusX(float focusX) {
//        this.mFocusX = focusX;
//    }
    public float getFocusY() {
        return mFocusY;
    }
//    public void setFocusY(float focusY) {
//        this.mFocusY = focusY;
//    }

    ///////////////////////////////////////////////////////////////////////////
    // getters, setters, helpers

    public DaoLocusList getLocusList() {
        return mLocusList;
    }
    public void setLocusList(DaoLocusList locusList) {
        this.mLocusList = locusList;
    }

    public List<String> getActivityList() {
        return mActivityList;
    }
    public void setActivityList(List<String> activityList) {
        this.mActivityList = activityList;
    }

    public List<Integer> getForeColorList() {
        return mForeColorList;
    }
    public void setForeColorList(List<Integer> foreColorList) {
        this.mForeColorList = foreColorList;
    }

    public List<Integer> getBackColorList() {
        return mBackColorList;
    }
    public void setBackColorList(List<Integer> backColorList) {
        this.mBackColorList = backColorList;
    }

    private String setLocusName(Integer ring, Integer id) { return "R" + ring + "L" + id; }

    ///////////////////////////////////////////////////////////////////////////
    // constructor
    public StarGateModel() {
        buildModel();
    }

    ///////////////////////////////////////////////////////////////////////////
    // builder
    // create a collection of locus & assign to active stage
    public Boolean buildModel() {

        // create locus list
        DaoLocusList daoLocusList = new DaoLocusList();
        setLocusList(daoLocusList);

        // create activity list mirroring locus list
        List<String> activityList = new ArrayList<>();
        setActivityList(activityList);
        List<Integer>  foreColorList = new ArrayList<>();
        setForeColorList(foreColorList);
        List<Integer>  backColorList = new ArrayList<>();
        setBackColorList(backColorList);

        // establish ring max id list
        Integer ring = 0;
        Integer ringId = 0;
        List<Integer> ringMaxId = new ArrayList<>();
        ringMaxId.add(0);

        // seed 1st locus at 0,0
        DaoLocus origin = new DaoLocus();
//        // mirror locus list add with actor & prop
//        daoLocusList.locii.add(origin);
//        // TODO: add activity moniker
//        activityList.add(DaoDefs.INIT_STRING_MARKER);
        mirrorLociiAdd(origin, getLocusList(), getActivityList(), getForeColorList(), getBackColorList());

        // set nickname, seed vert
        origin.setNickname(setLocusName(ring, ringMaxId.get(ring)));
        origin.setVertX(RING_CENTER_X);
        origin.setVertY(RING_CENTER_Y);
        origin.setVertZ(RING_CENTER_Z);
        Log.d(TAG, origin.toString() + " at origin.");

        // populate 1st ring around origin
        ++ring; // 1st
        ringId = populateLocii(ring, ringMaxId.get(ring - 1), daoLocusList, origin, activityList);
        ringMaxId.add(ringId);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Integer populateLocii(Integer ring, Integer locusIdStart, DaoLocusList daoLocusList, DaoLocus origin,
                                  List<String> activityList) {
        Integer locusId = locusIdStart;
        Log.d(TAG, origin.getNickname() + " origin: " + origin.toString());
        Double rad = RADIAN_START;
        Integer angleCount = 0;
        Long z = 0l;
        while (angleCount < ANGLE_COUNT_TOTAL) {
            Long x = (long)(origin.getVertX() + (LOCUS_DIST*Math.cos(rad)));
            Long y = (long)(origin.getVertY() + (LOCUS_DIST*Math.sin(rad)));

            if (findLocus(daoLocusList, x, y, z) == null) {
                ++locusId;
                DaoLocus locus = new DaoLocus();
                // mirror locus list add with actor & prop
                mirrorLociiAdd(locus, getLocusList(), getActivityList(), getForeColorList(), getBackColorList());
                // set locus name & verts
                locus.setNickname(setLocusName(ring, locusId));
                locus.setVertX(x);
                locus.setVertY(y);
                locus.setVertZ(z);
                Log.d(TAG, locus.toString() + " at " + rad + " radians from origin.");
            }
            // bump rad
            rad += RADIAN_DELTA;
            ++angleCount;
        }

        return locusId;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Boolean mirrorLociiAdd(DaoLocus locus,
                                   DaoLocusList daoLocusList,
                                   List<String> activityList,
                                   List<Integer> foreColorList,
                                   List<Integer> backColorList) {
        daoLocusList.locii.add(locus);
        activityList.add(DaoDefs.INIT_STRING_MARKER);
        foreColorList.add(DaoDefs.INIT_INTEGER_MARKER);
        backColorList.add(DaoDefs.INIT_INTEGER_MARKER);
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private DaoLocus findLocus(DaoLocusList daoLocusList, Long x, Long y, Long z) {
//        Log.d(TAG, "testing for match at x,y,z: " + x + ", " + y + ", " + z);
        // TODO: fudge should be LOCUS_DIST range
        // scan list of locus
        for (DaoLocus l : daoLocusList.locii) {
            if (Math.abs(l.getVertX() - x) < FUDGE_DIST &&
                    Math.abs(l.getVertY() - y) < FUDGE_DIST &&
                    Math.abs(l.getVertZ() - z) < FUDGE_DIST) {
                return l;
            }
        }
        return null;
    }
    ///////////////////////////////////////////////////////////////////////////
    public List<Integer> findRing(Integer selectIndex) {
        // create ringList
        List<Integer> ringList = new ArrayList<>();

        if (selectIndex < getLocusList().locii.size()) {
            DaoLocus origin = getLocusList().locii.get(selectIndex);
            Log.d(TAG, origin.getNickname() + " origin: " + origin.toString());

            Double rad = RADIAN_START;
            Integer angleCount = 0;
            Long z = 0l;
            while (angleCount < ANGLE_COUNT_TOTAL) {
                Long x = (long) (origin.getVertX() + (LOCUS_DIST * Math.cos(rad)));
                Long y = (long) (origin.getVertY() + (LOCUS_DIST * Math.sin(rad)));

                DaoLocus locus = findLocus(getLocusList(), x, y, z);
                if (locus != null) {
                    int locusIndex = getLocusList().locii.indexOf(locus);
                    Log.d(TAG, locus.getNickname() + " at index " + locusIndex);
                    ringList.add(locusIndex);
                }
                // bump rad
                rad += RADIAN_DELTA;
                ++angleCount;
            }
        }
        else {
            Log.e(TAG,"findRing -> selectIndex(" + selectIndex + ") out of bounds(" + getLocusList().locii.size() + ")");
        }

        return ringList;
    }
///////////////////////////////////////////////////////////////////////////
}
