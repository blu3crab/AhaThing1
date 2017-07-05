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

	private static final String TAG = "DaoEpic";
	private static final long serialVersionUID = 1L;

	public static final String EPIC_TYPE_NONE = "None";
	public static final String EPIC_TYPE_COMPETE = "Compete";
	public static final String EPIC_TYPE_COOPERATE = "Cooperate";

	public static final Integer EPIC_TALLY_LIMIT_DEFAULT = 24;
    public static final Integer EPIC_TIC_LIMIT_DEFAULT = 12;
    public static final float EPIC_PERCENT_LIMIT_DEFAULT = 0.2f;

	@SerializedName("epicType")			// determines how to tally: SumWithTic if sum>limit || tic>limit -> true
	private String epicType;

	@SerializedName("starBoardList")	// stars are active actors on a particular device
	private List<DaoEpicStarBoard> starBoardList;

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
		this.starBoardList = new ArrayList<>();
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
			List<DaoEpicStarBoard> starBoardList,
			List<String> deviceList,
			Integer tallyLimit,
			List<Integer> tallyList,
			Integer ticLimit,
			List<Integer> ticList,
            String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.epicType = epicType;
		this.starBoardList = starBoardList;
		this.tallyLimit = tallyLimit;
		this.ticLimit = ticLimit;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;

	}

	///////////////////////////////////////////////////////////////////////////
	// star registration
	public Boolean setStar(DaoActor daoActor, String deviceName) {
		// add star, device name, with initial tally & tics
		if (getStarList().contains(daoActor.getMoniker()) && getDeviceList().contains(deviceName)) {
			// if star present on same device
			if (getStarList().indexOf(daoActor.getMoniker()) == getDeviceList().indexOf(deviceName)) {
				Log.d(TAG,"setStar TRUE for existing star " + daoActor.getMoniker() + ".");
				return true;
			}
			else {
				// if star present but on different device, remove both star & device stale entries
				int staleStarInx = getStarList().indexOf(deviceName);
				Log.d(TAG,"setStar removing star " + starBoardList.get(staleStarInx).getStarMoniker() + " at inx " + staleStarInx);
				removeStar(staleStarInx);
				int staleDeviceInx = getDeviceList().indexOf(deviceName);
				Log.d(TAG,"setStar removing device " + starBoardList.get(staleDeviceInx).getDeviceId() + " at inx " + staleDeviceInx);
				removeStar(staleDeviceInx);
			}
		}
		else if (getDeviceList().contains(deviceName)) {
			// if new star but device is allocated, remove stale device entry
			int staleDeviceInx = getDeviceList().indexOf(deviceName);
			Log.d(TAG,"setStar removing device " + starBoardList.get(staleDeviceInx).getDeviceId() + " at inx " + staleDeviceInx);
			removeStar(staleDeviceInx);
		}
		// create star, device with init tally, tic
		Log.d(TAG,"setStar adding starBoard for " + daoActor.getMoniker() + " on device " + deviceName);
		DaoEpicStarBoard daoEpicStarBoard = new DaoEpicStarBoard();
		daoEpicStarBoard.setStarMoniker(daoActor.getMoniker());
		daoEpicStarBoard.setDeviceId(deviceName);
		daoEpicStarBoard.setTally(0);
		daoEpicStarBoard.setTic(0);
		starBoardList.add(daoEpicStarBoard);
		return true;
	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean isStar(DaoActor daoActor, String deviceName) {
		// if star & device are present
		if (getStarList().contains(daoActor.getMoniker()) && getDeviceList().contains(deviceName)) {
			// if star present on same device
			if (getStarList().indexOf(daoActor.getMoniker()) == getDeviceList().indexOf(deviceName)) {
				Log.d(TAG, "isStar TRUE for existing star " + daoActor.getMoniker() + ".");
				return true;
			}
		}
		return false;
	}
	///////////////////////////////////////////////////////////////////////////
	public List<String> getStarList() {
		List<String> starList = new ArrayList<>();
		for (DaoEpicStarBoard daoEpicStarBoard : starBoardList) {
			starList.add(daoEpicStarBoard.getStarMoniker());
		}
		return starList;
	}
	///////////////////////////////////////////////////////////////////////////
	public List<String> getDeviceList() {
		List<String> deviceList = new ArrayList<>();
		for (DaoEpicStarBoard daoEpicStarBoard : starBoardList) {
			deviceList.add(daoEpicStarBoard.getDeviceId());
		}
		return deviceList;
	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean removeStar(int staleInx) {
		if (staleInx < starBoardList.size()) {
			starBoardList.remove(staleInx);
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
        List<DaoEpicStarBoard> orderedStarBoardList = getTallyOrder(false);
		if (orderedStarBoardList.size() > 0) {
			Integer highTally = orderedStarBoardList.get(0).getTally();
			if (highTally > (getTallyLimit() / 2)) {
				// tally all others
				Integer otherTally = 0;
				for (int i = 1; i < orderedStarBoardList.size(); i++) {
					otherTally += orderedStarBoardList.get(i).getTally();
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
		for (DaoEpicStarBoard daoEpicStarBoard : getStarBoardList()) {
			// if epic tally for any star > limit
			if (daoEpicStarBoard.getTally() > getTallyLimit()) {
				// return tally at limit
				return true;
			}
		}
		return false;
	}
	public Boolean isEpicTicAtLimit() {
		for (DaoEpicStarBoard daoEpicStarBoard : getStarBoardList()) {
			// if epic tic for any star > limit
			if (daoEpicStarBoard.getTic() > getTicLimit()) {
				// return tic at limit
				return true;
			}
		}
		return false;
	}
	public List<DaoEpicStarBoard> getTallyOrder(final Boolean ascending) {
		Collections.sort(getStarBoardList(), new Comparator<DaoEpicStarBoard>(){
			public int compare(DaoEpicStarBoard o1, DaoEpicStarBoard o2){
				if(o1.getTally() == o2.getTally())
					return 0;
				// < is ascending,
				if (ascending) return o1.getTally() < o2.getTally() ? -1 : 1;
				// > is descending
				else return o1.getTally() > o2.getTally() ? -1 : 1;
			}
		});
		return getStarBoardList();
	}
	public List<DaoEpicStarBoard> getTicOrder() {
		Collections.sort(getStarBoardList(), new Comparator<DaoEpicStarBoard>(){
			public int compare(DaoEpicStarBoard o1, DaoEpicStarBoard o2){
				if(o1.getTally() == o2.getTally())
					return 0;
				// ascending?
				return o1.getTic() < o2.getTic() ? -1 : 1;
			}
		});
		return getStarBoardList();
	}
    public Boolean updateEpicTally(DaoStage daoStage) {
        // TODO: checkbox for cumulative tally option?
        // reset epic star board tally but not stage or tic (turn counter)
        resetEpicStageTallyTic(null, true, false);
		Boolean updateTally = false;
        // for each vert
        for (String vertActor : daoStage.getActorList()) {
            int vertActorInx = getStarList().indexOf(vertActor);
            if (vertActorInx > -1) {
                // increment tally for actor
                int tally = getStarBoardList().get(vertActorInx).getTally();
                getStarBoardList().get(vertActorInx).setTally(++tally);
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
        for (DaoEpicStarBoard daoEpicStarBoard : getStarBoardList()) {
            if (resetTally) daoEpicStarBoard.setTally(0);
            if (resetTic) daoEpicStarBoard.setTic(0);
        }
        return true;
    }
    public Boolean resetStarBoard() {
//		getStarBoardList().clear();
		List<DaoEpicStarBoard> starBoardList = new ArrayList<>();
		setStarBoardList(starBoardList);
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

	public List<DaoEpicStarBoard> getStarBoardList() {
		return starBoardList;
	}
	public void setStarBoardList(List<DaoEpicStarBoard> starList) {
		this.starBoardList = starList;
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