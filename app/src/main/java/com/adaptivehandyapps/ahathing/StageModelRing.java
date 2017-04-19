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

import android.util.Log;

import com.adaptivehandyapps.ahathing.dal.RepoProvider;
import com.adaptivehandyapps.ahathing.dao.DaoLocus;
import com.adaptivehandyapps.ahathing.dao.DaoLocusList;
import com.adaptivehandyapps.ahathing.dao.DaoStage;

import java.util.ArrayList;
import java.util.List;

//
// Created by mat on 1/31/2017.
//
///////////////////////////////////////////////////////////////////////////
// RingWorld model
public class StageModelRing {
    private static final String TAG = "StageModelRing";

    public static  final Double LOCUS_DIST = 64.0;
//    public static  final Double LOCUS_DIST = 32.0;
//    public static  final Double LOCUS_DIST = 128.0;
    private final Integer FUDGE_DIST = 2;

    // start & delta angles
    private final Double ANGLE_START = 0.0;      // start due east
    private final Double ANGLE_DELTA = 60.0;    // increment counter-clockwise by 60 degrees
//    private final Double ANGLE_DELTA = 30.0;  // many, many locus if angle results in unique locus around adjacent locus
//    private final Double ANGLE_DELTA = 45.0;
    // count of angles around center
    private Integer ANGLE_COUNT_TOTAL = (int)(360.0 / ANGLE_DELTA);
    // convert degrees to radians
    private Double RADIAN_START = (Math.PI * ANGLE_START)/180;
    private Double RADIAN_DELTA = (Math.PI * ANGLE_DELTA)/180;

    public static final Long RING_MAX_X = 1024l;
    public static final Long RING_MIN_X = 0l;
    public static final Long RING_MAX_Y = 1024l;
    public static final Long RING_MIN_Y = 0l;
    public static final Long RING_MAX_Z = 0l;
    public static final Long RING_MIN_Z = 0l;
    public static final Long RING_CENTER_X = (RING_MAX_X - RING_MIN_X)/2;
    public static final Long RING_CENTER_Y = (RING_MAX_Y - RING_MIN_Y)/2;
    public static final Long RING_CENTER_Z = 0l;

//    private RepoProvider mRepoProvider;
    private Integer mRingMax = 1;

    ///////////////////////////////////////////////////////////////////////////
    // constructor
//    public StageModelRing(RepoProvider repoProvider) {
//        mRepoProvider = repoProvider;
//    }
    public StageModelRing() {}
    ///////////////////////////////////////////////////////////////////////////
    // getters, setters, helpers
    private String setLocusName(Integer ring, Integer id) { return "R" + ring + "L" + id; }

    ///////////////////////////////////////////////////////////////////////////
    // builder
    // create a collection of locus & assign to active stage
//    public Boolean buildModel(Integer ringMax) {
    public Boolean buildModel(DaoStage activeStage, Integer ringMax) {
        mRingMax = ringMax;

//        DaoStage activeStage = mRepoProvider.getActiveStage();
//        activeStage.setMoniker(DaoStage.STAGE_TYPE_RING + mRepoProvider.getDaoStageRepo().size());
//        activeStage.setStageType(DaoStage.STAGE_TYPE_RING);

        // create stage locus list
        DaoLocusList daoLocusList = new DaoLocusList();
        activeStage.setLocusList(daoLocusList);
        // establish ring max id list
        Integer ring = 0;
        Integer ringId = 0;
        List<Integer> ringMaxId = new ArrayList<>();
        ringMaxId.add(0);

        // seed 1st locus at 0,0
        DaoLocus origin = new DaoLocus();
        daoLocusList.locii.add(origin);
        origin.setNickname(setLocusName(ring, ringMaxId.get(ring)));
        origin.setVertX(RING_CENTER_X);
        origin.setVertY(RING_CENTER_Y);
        origin.setVertZ(RING_CENTER_Z);
        Log.d(TAG, origin.toString() + " at origin.");

        // populate 1st ring around origin
        ++ring; // 1st
        ringId = populateLocii(ring, ringMaxId.get(ring-1), daoLocusList, origin);
        ringMaxId.add(ringId);

        // populate next ring by expanding around each locus in previous ring
        while (ring < ringMax) {
            ++ring;
            // for each locus in previous ring
            Integer locusIndex = ringMaxId.get(ring-2) + 1;
            ringId = ringMaxId.get(ring-1);
            while ( locusIndex < ringMaxId.get(ring-1)+1) {
                origin = daoLocusList.locii.get(locusIndex);
                ringId = populateLocii(ring, ringId, daoLocusList, origin);
                ++locusIndex;
            }
            ringMaxId.add(ringId);
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Integer populateLocii(Integer ring, Integer locusIdStart, DaoLocusList daoLocusList, DaoLocus origin) {
        Integer locusId = locusIdStart;
//        Log.d(TAG, origin.getNickname() + " origin: " + origin.toString());
        Double rad = RADIAN_START;
        Integer angleCount = 0;
        Long z = 0l;
        while (angleCount < ANGLE_COUNT_TOTAL) {
            Long x = (long)(origin.getVertX() + (LOCUS_DIST*Math.cos(rad)));
            Long y = (long)(origin.getVertY() + (LOCUS_DIST*Math.sin(rad)));

            if (findLocus(daoLocusList, x, y, z) == null) {
                ++locusId;
                DaoLocus locus = new DaoLocus();
                daoLocusList.locii.add(locus);
                locus.setNickname(setLocusName(ring, locusId));
                locus.setVertX(x);
                locus.setVertY(y);
                locus.setVertZ(z);
                Log.d(TAG, locus.toString() + " at " + rad + " radians from origin.");
            }
//            else {
//                Log.d(TAG, " non-unique at " + rad + " radians from origin.");
//            }
            // bump rad
            rad += RADIAN_DELTA;
            ++angleCount;
        }

        return locusId;
    }
    ///////////////////////////////////////////////////////////////////////////
    private DaoLocus findLocus(DaoLocusList daoLocusList, Long x, Long y, Long z) {
//        Log.d(TAG, "testing for match at x,y,z: " + x + ", " + y + ", " + z);
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
        // get active stage
        DaoStage daoStage = MainActivity.getPlayListInstance().getActiveStage();
        // get locus list
        DaoLocusList daoLocusList = daoStage.getLocusList();
        // create ringList
        List<Integer> ringList = new ArrayList<>();

        DaoLocus origin = daoLocusList.locii.get(selectIndex);
        Log.d(TAG, origin.getNickname() + " origin: " + origin.toString());

        Double rad = RADIAN_START;
        Integer angleCount = 0;
        Long z = 0l;
        while (angleCount < ANGLE_COUNT_TOTAL) {
            Long x = (long)(origin.getVertX() + (LOCUS_DIST*Math.cos(rad)));
            Long y = (long)(origin.getVertY() + (LOCUS_DIST*Math.sin(rad)));

            DaoLocus locus = findLocus(daoLocusList, x, y, z);
            if (locus != null) {
                int locusIndex = daoLocusList.locii.indexOf(locus);
                Log.d(TAG, locus.getNickname() + " at index " + locusIndex);
                ringList.add(locusIndex);
            }
            // bump rad
            rad += RADIAN_DELTA;
            ++angleCount;
        }

        return ringList;
    }
//    ///////////////////////////////////////////////////////////////////////////
//    private Boolean isUniqueLocus(DaoLocusList daoLocusList, Double x, Double y, Double z) {
////        Log.d(TAG, "testing for match at x,y,z: " + x + ", " + y + ", " + z);
//        // scan list of locus
//        for (DaoLocus l : daoLocusList.locii) {
//            // if X values are both positive or both negative
//            if ((l.getVertX() >= 0.0 && x >= 0.0) || (l.getVertX() < 0.0 && x < 0.0)) {
//                // if X similar
//                if (Math.abs(l.getVertX() - x) < FUDGE_DIST) {
//                    // if Y values are both positive or both negative
//                    if ((l.getVertY() >= 0.0 && y >= 0.0) || (l.getVertY() < 0.0 && y < 0.0)) {
//                        // if Y similar
//                        if (Math.abs(l.getVertY() - y) < FUDGE_DIST) {
//                            // if Z values are both positive or both negative
//                            if ((l.getVertZ() >= 0.0 && z >= 0.0) || (l.getVertZ() < 0.0 && z < 0.0)) {
//                                // if Z similar
//                                if (Math.abs(l.getVertZ() - z) < FUDGE_DIST) {
//                                    // non-unique
////                                    Log.d(TAG, "match found at " + l.toString());
//                                    return false;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return true;
//    }

///////////////////////////////////////////////////////////////////////////
}
