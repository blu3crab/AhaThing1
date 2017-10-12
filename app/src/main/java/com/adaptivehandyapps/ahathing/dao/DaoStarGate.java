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

    // DeviceId base class moniker (key)
    @SerializedName("starMoniker")			// star moniker - displayname
    private String starMoniker;
//	@SerializedName("deviceId")				// deviceId where star signed in
//	private String deviceId;
	@SerializedName("deviceDescription")	// device description (manufacturer/model/build)
	private String deviceDescription;
	@SerializedName("email")				// email where star signed in
	private String email;
	@SerializedName("active")				// star is currently active on device
	private Boolean active;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoStarGate() {
		super();
		this.starMoniker = DaoDefs.INIT_STRING_MARKER;
//		this.deviceId = DaoDefs.INIT_STRING_MARKER;
		this.deviceDescription = DaoDefs.INIT_STRING_MARKER;
		this.email = DaoDefs.INIT_STRING_MARKER;
		this.active = false;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoStarGate(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,
			String star,
//            String deviceId,
			String deviceDescription,
			String email,
			Boolean active,
			String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.starMoniker = star;
//		this.deviceId = deviceId;
		this.deviceDescription = deviceDescription;
		this.email = email;
		this.active = active;
	}

	///////////////////////////////////////////////////////////////////////////

	public String toString() {
		return "StarGate deviceId " + getMoniker() + ", star " + getStarMoniker() +
				", description " + getDeviceDescription() + ", email " + getEmail() + ", active " + getActive();
	}
	public String getStarMoniker() {
		return starMoniker;
	}
	public void setStarMoniker(String starMoniker) {
		this.starMoniker = starMoniker;
	}

//	public String getDeviceId() {
//		return deviceId;
//	}
//	public void setDeviceId(String deviceId) {
//		this.deviceId = deviceId;
//	}

	public String getDeviceDescription() {
		return deviceDescription;
	}
	public void setDeviceDescription(String deviceDescription) {
		this.deviceDescription = deviceDescription;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getReserve2() {
		return reserve2;
	}
	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}