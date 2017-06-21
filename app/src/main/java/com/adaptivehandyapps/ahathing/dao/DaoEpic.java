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

	@SerializedName("epicType")			// determines how to tally
	private String epicType;

	@SerializedName("starList")			// stars are active actors on a particular device
	private List<String> starList;
	@SerializedName("deviceList")		// device where stars are active
	private List<String> deviceList;

	@SerializedName("tallyLimit")		// tally limit (aka max score)
	private Integer tallyLimit;
	@SerializedName("tallyList")		// tally for each actor (aka score)
	private List<Integer> tallyList;

	@SerializedName("ticLimit")			// tic limit (aka max turns)
	private Integer ticLimit;
	@SerializedName("ticList")			// tic for each actor (aka turn counter)
	private List<Integer> ticList;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoEpic() {
		super();

		this.epicType = DaoDefs.INIT_STRING_MARKER;
		this.starList = new ArrayList<>();
		this.deviceList = new ArrayList<>();
		this.tallyLimit = EPIC_TALLY_LIMIT_DEFAULT;
		this.tallyList = new ArrayList<>();
		this.ticLimit = EPIC_TIC_LIMIT_DEFAULT;
		this.ticList = new ArrayList<>();

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoEpic(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,
			String epicType,
			List<String> starList,
			List<String> deviceList,
			Integer tallyLimit,
			List<Integer> tallyList,
			Integer ticLimit,
			List<Integer> ticList,
            String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.epicType = epicType;
		this.starList = starList;
		this.deviceList = deviceList;
		this.tallyLimit = tallyLimit;
		this.tallyList = tallyList;
		this.ticLimit = ticLimit;
		this.ticList = ticList;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;

	}

	///////////////////////////////////////////////////////////////////////////
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
				int staleStarInx = getDeviceList().indexOf(deviceName);
				Log.d(TAG,"setStar removing star " + starList.get(staleStarInx) + " at inx " + staleStarInx);
				removeStar(staleStarInx);
//				starList.remove(staleStarInx);
//				deviceList.remove(staleStarInx);
//				tallyList.remove(staleStarInx);
//				ticList.remove(staleStarInx);
				int staleDeviceInx = getDeviceList().indexOf(deviceName);
				Log.d(TAG,"setStar removing device " + deviceList.get(staleDeviceInx) + " at inx " + staleDeviceInx);
				removeStar(staleDeviceInx);
//				starList.remove(staleDeviceInx);
//				deviceList.remove(staleDeviceInx);
//				tallyList.remove(staleDeviceInx);
//				ticList.remove(staleDeviceInx);
			}
		}
		else if (getDeviceList().contains(deviceName)) {
			// if new star but device is allocated, remove stale device entry
			int staleDeviceInx = getDeviceList().indexOf(deviceName);
			Log.d(TAG,"setStar removing device " + deviceList.get(staleDeviceInx) + " at inx " + staleDeviceInx);
			removeStar(staleDeviceInx);
//			starList.remove(staleDeviceInx);
//			deviceList.remove(staleDeviceInx);
//			tallyList.remove(staleDeviceInx);
//			ticList.remove(staleDeviceInx);
		}
		// create star, device with init tally, tic
		Log.d(TAG,"setStar adding star device " + daoActor.getMoniker() + " on device " + deviceName);
		starList.add(daoActor.getMoniker());
		deviceList.add(deviceName);
		tallyList.add(0);
		ticList.add(0);
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
	public Boolean removeStar(int staleInx) {
		if (staleInx < starList.size()) {
			starList.remove(staleInx);
			deviceList.remove(staleInx);
			tallyList.remove(staleInx);
			ticList.remove(staleInx);
			return true;
		}
		return false;
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

	public List<String> getStarList() {
		return starList;
	}
	public void setStarList(List<String> starList) {
		this.starList = starList;
	}

	public List<String> getDeviceList() {
		return deviceList;
	}
	public void setDeviceList(List<String> deviceList) {
		this.deviceList = deviceList;
	}

	public Integer getTallyLimit() {
		return tallyLimit;
	}
	public void setTallyLimit(Integer tallyLimit) {
		this.tallyLimit = tallyLimit;
	}

	public List<Integer> getTallyList() {
		return tallyList;
	}
	public void setTallyList(List<Integer> tallyList) {
		this.tallyList = tallyList;
	}

	public Integer getTicLimit() {
		return ticLimit;
	}
	public void setTicLimit(Integer ticLimit) {
		this.ticLimit = ticLimit;
	}

	public List<Integer> getTicList() {
		return ticList;
	}
	public void setTicList(List<Integer> ticList) {
		this.ticList = ticList;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}