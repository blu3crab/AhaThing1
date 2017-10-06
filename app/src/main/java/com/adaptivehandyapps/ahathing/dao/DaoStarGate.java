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

package com.adaptivehandyapps.ahathing.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoStarGate extends DaoBase {

	private static final long serialVersionUID = 1L;

	@SerializedName("starMoniker")	// star is active actor on a particular deviceId
	private String starMoniker;
	@SerializedName("deviceId")		// deviceId where star are active
	private String deviceId;

	@SerializedName("tally")		// tally for each actor (aka score)
	private Integer tally;

	@SerializedName("tic")			// tic for each actor (aka turn counter)
	private Integer tic;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoStarGate() {
		super();
		this.starMoniker = DaoDefs.INIT_STRING_MARKER;
		this.deviceId = DaoDefs.INIT_STRING_MARKER;
		this.tally = DaoDefs.INIT_INTEGER_MARKER;
		this.tic = DaoDefs.INIT_INTEGER_MARKER;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoStarGate(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,
			String star,
			String deviceId,
			Integer tally,
			Integer tic,
			String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.starMoniker = star;
		this.deviceId = deviceId;
		this.tally = tally;
		this.tic = tic;
	}

	///////////////////////////////////////////////////////////////////////////

	public String getStarMoniker() {
		return starMoniker;
	}

	public void setStarMoniker(String starMoniker) {
		this.starMoniker = starMoniker;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getTally() {
		return tally;
	}

	public void setTally(Integer tally) {
		this.tally = tally;
	}

	public Integer getTic() {
		return tic;
	}

	public void setTic(Integer tic) {
		this.tic = tic;
	}

	public String getReserve2() {
		return reserve2;
	}
	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}