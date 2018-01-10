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

import android.graphics.RectF;
import android.util.Log;

import com.adaptivehandyapps.ahathing.dao.DaoActor;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;
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

    public static final Double LOCUS_DIST = 64.0;
//    public static  final Double LOCUS_DIST = 32.0;
//    public static  final Double LOCUS_DIST = 128.0;
//    private final Integer FUDGE_DIST = 2; // precision for 4 rings ok...
    private final Integer FUDGE_DIST = 16;  // precision loss for 8 rings ok

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

    public static final Long RING_MAX_X = 1024L;
    public static final Long RING_MIN_X = 0l;
    public static final Long RING_MAX_Y = 1024L;
    public static final Long RING_MIN_Y = 0L;
    public static final Long RING_MAX_Z = 0L;
    public static final Long RING_MIN_Z = 0L;
    public static final Long RING_CENTER_X = (RING_MAX_X - RING_MIN_X)/2;
    public static final Long RING_CENTER_Y = (RING_MAX_Y - RING_MIN_Y)/2;
    public static final Long RING_CENTER_Z = 0L;

    ///////////////////////////////////////////////////////////////////////////
    // getters, setters, helpers
    private RepoProvider mRepoProvider;
    public RepoProvider getRepoProvider() {
        return mRepoProvider;
    }
    public void setRepoProvider(RepoProvider repoProvider) {
        mRepoProvider = repoProvider;
    }
    private PlayListService mPlayListService;
    public PlayListService getPlayListService() {
        return mPlayListService;
    }
    public void setPlayListService(PlayListService playListService) {
        mPlayListService = playListService;
    }

    private String setLocusName(Integer ring, Integer id) { return "R" + ring + "L" + id; }

    ///////////////////////////////////////////////////////////////////////////
    // bounding rect
    private RectF mBoundingRect;
    public RectF getBoundingRect() { return mBoundingRect;}
    public Boolean setBoundingRect(RectF boundingRect) { mBoundingRect = boundingRect; return true;}
    ///////////////////////////////////////////////////////////////////////////
    // focus (center) of view
    private float mFocusX = StageModelRing.RING_CENTER_X;
    private float mFocusY = StageModelRing.RING_CENTER_Y;

    public float getFocusX() {
        return mFocusX;
    }
    public void setFocusX(float focusX) {
        this.mFocusX = focusX;
    }
    public float getFocusY() {
        return mFocusY;
    }
    public void setFocusY(float focusY) {
        this.mFocusY = focusY;
    }

    public Boolean shiftFocus(float distX, float distY) {
        float focusX = getFocusX() + distX;
        float focusY = getFocusY() + distY;
        if (getBoundingRect() != null && getBoundingRect().contains(focusX, focusY)) {
            setFocusX(focusX);
            setFocusY(focusY);
            return true;
        }
        else {
            setFocusX(StageModelRing.RING_CENTER_X);
            setFocusY(StageModelRing.RING_CENTER_Y);
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // constructor
//    public StageModelRing(PlayListService playListService) { setPlayListService(playListService); }
    public StageModelRing(RepoProvider repoProvider) {
        setRepoProvider(repoProvider);
        setPlayListService(getRepoProvider().getPlayListService());
    }

    ///////////////////////////////////////////////////////////////////////////
    // builder
    // create a collection of locus & assign to active stage
    public Boolean buildModel(DaoStage activeStage) {

        // if no previous model, create locus, actor, prop lists
        if (activeStage.getLocusList().locii.size() == 0) {
            // create actor list mirroring locus list
            List<String> actorList = new ArrayList<>();
            activeStage.setActorList(actorList);
            // create stage locus list  mirroring locus list
            List<String> propList = new ArrayList<>();
            activeStage.setPropList(propList);
            List<Integer> propFgColorList = new ArrayList<>();
            activeStage.setPropFgColorList(propFgColorList);
            List<Integer> propBgColorList = new ArrayList<>();
            activeStage.setPropBgColorList(propBgColorList);

            // create prop list
            DaoLocusList daoLocusList = new DaoLocusList();
            activeStage.setLocusList(daoLocusList);
            // establish ring max id list
            Integer ring = 0;
            Integer ringId = 0;
            List<Integer> ringMaxId = new ArrayList<>();
            ringMaxId.add(0);

            // seed 1st locus at 0,0
            DaoLocus origin = new DaoLocus();
            // mirror locus list add with actor & prop
            mirrorLociiAdd(origin, daoLocusList, actorList, propList, propFgColorList, propBgColorList);
            // set nickname, seed vert
            origin.setNickname(setLocusName(ring, ringMaxId.get(ring)));
            origin.setVertX(RING_CENTER_X);
            origin.setVertY(RING_CENTER_Y);
            origin.setVertZ(RING_CENTER_Z);
            Log.d(TAG, origin.toString() + " at origin.");

            // populate 1st ring around origin
            ++ring; // 1st
            ringId = populateLocii(ring, ringMaxId.get(ring - 1), daoLocusList, origin, actorList,
                                    propList, propFgColorList, propBgColorList);
            ringMaxId.add(ringId);

            // populate next ring by expanding around each locus in previous ring
            while (ring < activeStage.getRingSize()) {
                ++ring;
                // for each locus in previous ring
                Integer locusIndex = ringMaxId.get(ring - 2) + 1;
                ringId = ringMaxId.get(ring - 1);
                while (locusIndex < ringMaxId.get(ring - 1) + 1) {
                    origin = daoLocusList.locii.get(locusIndex);
                    ringId = populateLocii(ring, ringId, daoLocusList, origin, actorList,
                                            propList, propFgColorList, propBgColorList);
                    ++locusIndex;
                }
                ringMaxId.add(ringId);
            }
            // create bounding rect with min/max inverted
            setBoundingRect(new RectF(RING_MAX_X, RING_MAX_Y, RING_MIN_X, RING_MIN_Y));
            // create bounding rect
            initBoundingRect(activeStage);
        }
        else if (activeStage.getPropFgColorList() == null ||
                activeStage.getPropBgColorList() == null ||
                activeStage.getPropFgColorList().size() != activeStage.getPropList().size() ||
                activeStage.getPropBgColorList().size() != activeStage.getPropList().size()) {
            Log.e(TAG, "BuildModel finds depopulated FG,BG Color Lists...repairing...");
            List<Integer> propFgColorList = new ArrayList<>();
            activeStage.setPropFgColorList(propFgColorList);
            List<Integer> propBgColorList = new ArrayList<>();
            activeStage.setPropBgColorList(propBgColorList);
            // for each prop
            for (int i = 0; i < activeStage.getPropList().size(); i++) {
                // if prop defined
                if (!activeStage.getPropList().get(i).equals(DaoDefs.INIT_STRING_MARKER)) {
                    Log.d(TAG, "buildModel repairing prop(" + i + ") " + activeStage.getPropList().get(i));
                    if (activeStage.getPropList().get(i).equals(DaoActor.ACTOR_MONIKER_FORBIDDEN)) {
                        propFgColorList.add(DaoStage.STAGE_BG_COLOR);
                        propBgColorList.add(DaoStage.STAGE_BG_COLOR);
                    } else if (activeStage.getPropList().get(i).equals(DaoActor.ACTOR_MONIKER_MIRROR)) {
                        String actorMoniker = activeStage.getActorList().get(i);
                        if (!actorMoniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                            DaoActor daoActor = (DaoActor) getRepoProvider().getDalActor().getDaoRepo().get(actorMoniker);
                            if (daoActor != null) {
                                propFgColorList.add(daoActor.getForeColor());
                                propBgColorList.add(DaoStage.STAGE_BG_COLOR);
                                Log.d(TAG,"buildModel adds mirror for actor " + daoActor.getMoniker() + " at " + i);
                            }
                            else {
                                // add empty fg/bg color
                                propFgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
                                propBgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
                            }
                        }
                    }
                }
                else {
                    String actorMoniker = activeStage.getActorList().get(i);
                    if (!actorMoniker.equals(DaoDefs.INIT_STRING_MARKER)) {
                        DaoActor daoActor = (DaoActor) getRepoProvider().getDalActor().getDaoRepo().get(actorMoniker);
                        if (daoActor != null) {
                            activeStage.getPropList().set(i, DaoActor.ACTOR_MONIKER_MIRROR);
                            propFgColorList.add(daoActor.getForeColor());
                            propBgColorList.add(DaoStage.STAGE_BG_COLOR);
                            Log.d(TAG,"buildModel adds mirror for actor " + daoActor.getMoniker() + " at " + i);
                        }
                        else {
                            // add empty fg/bg color
                            propFgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
                            propBgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
                        }
                    }
                    else {
                        // add empty fg/bg color
                        propFgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
                        propBgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
                    }
                }
            }
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    private Integer populateLocii(Integer ring, Integer locusIdStart, DaoLocusList daoLocusList, DaoLocus origin,
                                  List<String> actorList,
                                  List<String> propList, List<Integer> propFgColorList, List<Integer> propBgColorList) {
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
                mirrorLociiAdd(locus, daoLocusList, actorList, propList, propFgColorList, propBgColorList);
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
    private Boolean mirrorLociiAdd(DaoLocus locus, DaoLocusList daoLocusList, List<String> actorList,
                                   List<String> propList, List<Integer> propFgColorList, List<Integer> propBgColorList) {
        daoLocusList.locii.add(locus);
        actorList.add(DaoDefs.INIT_STRING_MARKER);
        propList.add(DaoDefs.INIT_STRING_MARKER);
        propFgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
        propBgColorList.add(DaoDefs.INIT_INTEGER_MARKER);
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
        // get active stage
        DaoStage daoStage = getPlayListService().getActiveStage();

        if (daoStage != null) {
            // get locus list
            DaoLocusList daoLocusList = daoStage.getLocusList();

            if (daoLocusList != null) {

                DaoLocus origin = daoLocusList.locii.get(selectIndex);
                Log.d(TAG, origin.getNickname() + " origin: " + origin.toString());

                Double rad = RADIAN_START;
                Integer angleCount = 0;
                Long z = 0l;
                while (angleCount < ANGLE_COUNT_TOTAL) {
                    Long x = (long) (origin.getVertX() + (LOCUS_DIST * Math.cos(rad)));
                    Long y = (long) (origin.getVertY() + (LOCUS_DIST * Math.sin(rad)));

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
            }
            else {
                Log.e(TAG, "Oops! no locus list...");
            }
        }
        else {
             Log.e(TAG, "Oops!  no active stage...");
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
