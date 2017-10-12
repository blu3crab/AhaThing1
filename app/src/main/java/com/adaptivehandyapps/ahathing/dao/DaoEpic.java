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

package com.adaptivehandyapps.ahathing.dao;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoEpic extends DaoBase {

	private static final String TAG = DaoEpic.class.getSimpleName();
	private static final long serialVersionUID = 1L;

	public static final String EPIC_TYPE_NONE = "None";
	public static final String EPIC_TYPE_COMPETE = "Compete";
	public static final String EPIC_TYPE_COOPERATE = "Cooperate";

	public static final Integer EPIC_TALLY_LIMIT_DEFAULT = 24;
	public static final Integer EPIC_TIC_LIMIT_MAX = 96;
	public static final Integer EPIC_TIC_LIMIT_DEFAULT = 12;
    public static final float EPIC_PERCENT_LIMIT_DEFAULT = 0.2f;

	@SerializedName("epicType")			// determines how to tally: SumWithTic if sum>limit || tic>limit -> true
	private String epicType;

	@SerializedName("stage")
	private String stage;

	@SerializedName("actorBoardList")	// stars are active actors on a particular device
	private List<DaoEpicActorBoard> actorBoardList;

	@SerializedName("tallyLimit")		// tally limit (aka max score)
	private Integer tallyLimit;
	@SerializedName("ticLimit")			// tic limit (aka max turns)
	private Integer ticLimit;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoEpic() {
		super();

		this.epicType = DaoDefs.INIT_STRING_MARKER;
		this.stage = DaoDefs.INIT_STRING_MARKER;
		this.actorBoardList = new ArrayList<>();
		this.tallyLimit = EPIC_TALLY_LIMIT_DEFAULT;
		this.ticLimit = EPIC_TIC_LIMIT_DEFAULT;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoEpic(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,
			String epicType,
			String stage,
			List<DaoEpicActorBoard> actorBoardList,
			Integer tallyLimit,
			Integer ticLimit,
            String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.epicType = epicType;
		this.stage = stage;
		this.actorBoardList = actorBoardList;
		this.tallyLimit = tallyLimit;
		this.ticLimit = ticLimit;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	///////////////////////////////////////////////////////////////////////////
	// star registration
	public Boolean addActorBoard(DaoActor daoActor) {

//		// add star, device name, with initial tally & tics
//		if (getActorList().contains(daoActor.getMoniker()) && getDeviceList().contains(deviceName)) {
//			// if star present on same device
//			if (getActorList().indexOf(daoActor.getMoniker()) == getDeviceList().indexOf(deviceName)) {
//				Log.d(TAG,"addActorBoard TRUE for existing star " + daoActor.getMoniker() + ".");
//				return true;
//			}
//			else {
//				// if star present but on different device, remove both star & device stale entries
//				int staleStarInx = getActorList().indexOf(deviceName);
//				Log.d(TAG,"addActorBoard removing star " + actorBoardList.get(staleStarInx).getStarMoniker() + " at inx " + staleStarInx);
//				removeActor(daoStage, staleStarInx);
//				int staleDeviceInx = getDeviceList().indexOf(deviceName);
//				Log.d(TAG,"addActorBoard removing device " + actorBoardList.get(staleDeviceInx).getDeviceId() + " at inx " + staleDeviceInx);
//				removeActor(daoStage, staleDeviceInx);
//			}
//		}
//		else if (getDeviceList().contains(deviceName)) {
//			// if new star but device is allocated, remove stale device entry
//			int staleDeviceInx = getDeviceList().indexOf(deviceName);
//			Log.d(TAG,"addActorBoard removing device " + actorBoardList.get(staleDeviceInx).getDeviceId() + " at inx " + staleDeviceInx);
//			removeActor(daoStage, staleDeviceInx);
//		}
		// create star, device with init tally, tic
		Log.d(TAG,"addActorBoard adding to actorBoard for " + daoActor.getMoniker());
		DaoEpicActorBoard daoEpicActorBoard = new DaoEpicActorBoard();
		daoEpicActorBoard.setStarMoniker(daoActor.getMoniker());
		daoEpicActorBoard.setTally(0);
		daoEpicActorBoard.setTic(0);
		actorBoardList.add(daoEpicActorBoard);
		return true;
	}
//	///////////////////////////////////////////////////////////////////////////
//	public Boolean isStar(DaoActor daoActor, String deviceName) {
//		// if star & device are present
//		if (getActorList().contains(daoActor.getMoniker()) && getDeviceList().contains(deviceName)) {
//			// if star present on same device
//			if (getActorList().indexOf(daoActor.getMoniker()) == getDeviceList().indexOf(deviceName)) {
//				Log.d(TAG, "isStar TRUE for existing star " + daoActor.getMoniker() + ".");
//				return true;
//			}
//		}
//		return false;
//	}
	///////////////////////////////////////////////////////////////////////////
	// return list of all actors on the actor board
	public List<String> getActorList() {
		List<String> actorList = new ArrayList<>();
		for (DaoEpicActorBoard daoEpicActorBoard : actorBoardList) {
			actorList.add(daoEpicActorBoard.getStarMoniker());
		}
		return actorList;
	}
//	///////////////////////////////////////////////////////////////////////////
//	public List<String> getDeviceList() {
//		List<String> deviceList = new ArrayList<>();
//		for (DaoEpicActorBoard daoEpicActorBoard : starBoardList) {
//			deviceList.add(daoEpicActorBoard.getDeviceId());
//		}
//		return deviceList;
//	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean removeActor(DaoStage daoStage, int staleInx) {
		if (staleInx < actorBoardList.size()) {
			// scan stage actor list for stale actor - if found, reset stage locus
			String staleActor = actorBoardList.get(staleInx).getStarMoniker();
			if (daoStage != null) {
				if (daoStage.getActorList() != null) {
					for (String actor : daoStage.getActorList()) {
						if (actor.equals(staleActor)) {
							daoStage.getActorList().set(daoStage.getActorList().indexOf(staleActor), DaoDefs.INIT_STRING_MARKER);
							Log.d(TAG, "removeActor " + staleActor + " at stage locus " + daoStage.getActorList().indexOf(staleActor));
						}
					}
				}
			}
			actorBoardList.remove(staleInx);
			return true;
		}
		return false;
	}
	///////////////////////////////////////////////////////////////////////////
	// epic runtime
    public Boolean isCurtainClose() {
        if (isEpicTallyAtLimit() || isEpicTicAtLimit()) {
            Log.d(TAG, "Limit tally, tic " + isEpicTallyAtLimit() + ", " + isEpicTicAtLimit());
            return true;
        }
        // if highest tally is 1/2 to total
        List<DaoEpicActorBoard> orderedActorBoardList = getTallyOrder(false);
		if (orderedActorBoardList.size() > 0) {
			Integer highTally = orderedActorBoardList.get(0).getTally();
			if (highTally > (getTallyLimit() / 2)) {
				// tally all others
				Integer otherTally = 0;
				for (int i = 1; i < orderedActorBoardList.size(); i++) {
					otherTally += orderedActorBoardList.get(i).getTally();
				}
				float percent = (float) otherTally / (float) highTally;
				Log.d(TAG, "otherTally/highTally " + otherTally + "/" + highTally);
				// if highest tally is greater than percentage limit
				if (percent < EPIC_PERCENT_LIMIT_DEFAULT) return true;
			}
		}
        return false;
    }
	public Boolean isEpicTallyAtLimit() {
		// if tally limit defined
		if (getTallyLimit() > 0) {
			for (DaoEpicActorBoard daoEpicActorBoard : getActorBoardList()) {
				// if epic tally for any star > limit
				if (daoEpicActorBoard.getTally() > getTallyLimit()) {
					// return tally at limit
					return true;
				}
			}
		}
		return false;
	}
	public Boolean isEpicTicAtLimit() {
		// if tic limit defined
		if (getTicLimit() > 0) {
			for (DaoEpicActorBoard daoEpicActorBoard : getActorBoardList()) {
				// if epic tic for any star > limit
				if (daoEpicActorBoard.getTic() > getTicLimit()) {
					// return tic at limit
					return true;
				}
			}
		}
		return false;
	}
	public List<DaoEpicActorBoard> getTallyOrder(final Boolean ascending) {
		Collections.sort(getActorBoardList(), new Comparator<DaoEpicActorBoard>(){
			public int compare(DaoEpicActorBoard o1, DaoEpicActorBoard o2){
				if(o1.getTally() == o2.getTally())
					return 0;
				// < is ascending,
				if (ascending) return o1.getTally() < o2.getTally() ? -1 : 1;
				// > is descending
				else return o1.getTally() > o2.getTally() ? -1 : 1;
			}
		});
		return getActorBoardList();
	}
	public List<DaoEpicActorBoard> getTicOrder() {
		Collections.sort(getActorBoardList(), new Comparator<DaoEpicActorBoard>(){
			public int compare(DaoEpicActorBoard o1, DaoEpicActorBoard o2){
				if(o1.getTally() == o2.getTally())
					return 0;
				// ascending?
				return o1.getTic() < o2.getTic() ? -1 : 1;
			}
		});
		return getActorBoardList();
	}
    public Boolean updateEpicTally(DaoStage daoStage) {
        // TODO: checkbox for cumulative tally option?
        // reset epic star board tally but not stage or tic (turn counter)
        resetEpicStageTallyTic(null, true, false);
		Boolean updateTally = false;
        // for each vert
        for (String vertActor : daoStage.getActorList()) {
            int vertActorInx = getActorList().indexOf(vertActor);
            if (vertActorInx > -1) {
                // increment tally for actor
                int tally = getActorBoardList().get(vertActorInx).getTally();
                getActorBoardList().get(vertActorInx).setTally(++tally);
				updateTally = true;
            }
        }
        // if not tally updates, epic reset - clear tics
        if (!updateTally) resetEpicStageTallyTic(null, false, true);
        return true;
    }
    public Boolean resetEpicStageTallyTic(DaoStage daoStage, Boolean resetTally, Boolean resetTic) {
		// resetStage by resetting actor list to NADA...
		if (daoStage != null) daoStage.setActorList(DaoDefs.INIT_STRING_MARKER);

		// reset tally & tic list
        for (DaoEpicActorBoard daoEpicActorBoard : getActorBoardList()) {
            if (resetTally) daoEpicActorBoard.setTally(0);
            if (resetTic) daoEpicActorBoard.setTic(0);
        }
        return true;
    }
    public Boolean resetStarBoard() {
//		getActorBoardList().clear();
		List<DaoEpicActorBoard> actorBoardList = new ArrayList<>();
		setActorBoardList(actorBoardList);
		return true;
	}
	/////////////////////////////helpers//////////////////////////////////
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getEpicType() {
		return epicType;
	}
	public void setEpicType(String epicType) {
		this.epicType = epicType;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public List<DaoEpicActorBoard> getActorBoardList() {
		return actorBoardList;
	}
	public void setActorBoardList(List<DaoEpicActorBoard> starList) {
		this.actorBoardList = starList;
	}

	public Integer getTallyLimit() {
		return tallyLimit;
	}
	public void setTallyLimit(Integer tallyLimit) {
		this.tallyLimit = tallyLimit;
	}

	public Integer getTicLimit() {
		return ticLimit;
	}
	public void setTicLimit(Integer ticLimit) {
		this.ticLimit = ticLimit;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}