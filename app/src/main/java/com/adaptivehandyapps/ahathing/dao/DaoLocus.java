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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoLocus implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("nickname")	// nickname
	private String nickname;

	@SerializedName("vertX")	// X
	private Long vertX;

 	@SerializedName("vertY")	// Y
	private Long vertY;

	@SerializedName("vertZ")	// Z
	private Long vertZ;

	@SerializedName("lon")		// longitude
	private Double lon;

	@SerializedName("lat")		// latitude
	private Double lat;

	@SerializedName("elev")		// elevation
	private Double elev;

	@SerializedName("reserve1")
	private String reserve1;

	///////////////////////////////////////////////////////////////////////////
	public DaoLocus() {
		this.nickname = DaoDefs.INIT_STRING_MARKER;
		this.vertX = DaoDefs.INIT_LONG_MARKER;
		this.vertY = DaoDefs.INIT_LONG_MARKER;
		this.vertZ = DaoDefs.INIT_LONG_MARKER;
		this.lon = DaoDefs.INIT_DOUBLE_MARKER;
		this.lat = DaoDefs.INIT_DOUBLE_MARKER;
		this.elev = DaoDefs.INIT_DOUBLE_MARKER;

		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoLocus(
			String nickname,
			Long vertX,
			Long vertY,
			Long vertZ,
			Double lon,
			Double lat,
			Double elev,
            String reserve1
    ) {
		this.nickname = nickname;
		this.vertX = vertX;
		this.vertY = vertY;
		this.vertZ = vertZ;
		this.lon = lon;
		this.lat = lat;
		this.elev = elev;
		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return nickname + ", " + vertX + ", " + vertY + ", " + vertZ + ", " +
				lat + ", " + lon + ", " + elev + ", " +
				reserve1;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Long getVertX() {
		return vertX;
	}

	public void setVertX(Long vertX) {
		this.vertX = vertX;
	}

	public Long getVertY() {
		return vertY;
	}

	public void setVertY(Long vertY) {
		this.vertY = vertY;
	}

	public Long getVertZ() {
		return vertZ;
	}

	public void setVertZ(Long vertZ) {
		this.vertZ = vertZ;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getElev() {
		return elev;
	}

	public void setElev(Double elev) {
		this.elev = elev;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}
	///////////////////////////////////////////////////////////////////////////
}