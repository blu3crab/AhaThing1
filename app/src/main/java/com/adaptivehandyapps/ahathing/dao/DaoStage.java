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
public class DaoStage extends DaoBase {

	private static final long serialVersionUID = 1L;

	public static final String STAGE_TYPE_RING = "RingWorld";

	@SerializedName("stageType")		// stage type
	private String stageType;

	@SerializedName("locusList")		// locus list
	private DaoLocusList locusList;

	@SerializedName("propList")			// props list
	private List<String> propList;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoStage() {
		super();
		this.stageType = DaoDefs.INIT_STRING_MARKER;
		this.locusList = new DaoLocusList();
		this.propList = new ArrayList<>();

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoStage(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,

			String stageType,
			DaoLocusList locusList,
			List<String> propList,
            String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.stageType = stageType;
		this.locusList = locusList;
		this.propList = propList;
		this.reserve2 = reserve2;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return super.toString() + ", " + stageType + ", " + locusList + ", " + propList + ", " + reserve2;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getStageType() {
		return stageType;
	}

	public void setStageType(String stageType) {
		this.stageType = stageType;
	}

	public DaoLocusList getLocusList() {
		return locusList;
	}

	public void setLocusList(DaoLocusList locusList) {
		this.locusList = locusList;
	}

	public List<String> getPropList() {
		return propList;
	}

	public void setPropList(List<String> propList) {
		this.propList = propList;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve1;
	}
	///////////////////////////////////////////////////////////////////////////
}