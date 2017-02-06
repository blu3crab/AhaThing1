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
public class DaoStage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String STAGE_TYPE_RING = "RingWorld";

	@SerializedName("stageType")		// stage type
	private String stageType;

	@SerializedName("moniker")		// name
	private String moniker;

	@SerializedName("locusList")		// locus list
	private DaoLocusList locusList;

	@SerializedName("propList")	// actor id list
	private List<String> propList;

	// DAO info
	@SerializedName("daoInfo")		// object info
	private DaoInfo daoInfo;

	// DAO locale info
	@SerializedName("daoLocale")		// site locate info
	private DaoLocale daoLocale;

	@SerializedName("tagList")		// tag list
	private List<String> tagList;

	@SerializedName("reserve1")
	private String reserve1;

	///////////////////////////////////////////////////////////////////////////
	public DaoStage() {
		this.stageType = DaoDefs.INIT_STRING_MARKER;
		this.moniker = DaoDefs.INIT_STRING_MARKER;
		this.locusList = new DaoLocusList();
		this.propList = new ArrayList<>();

		this.daoInfo = new DaoInfo();
		this.daoLocale = new DaoLocale();
		this.tagList = new ArrayList<>();
		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoStage(
			String stageType,
			String moniker,
            DaoInfo daoInfo,
			DaoLocale daoLocale,
            List<String> tagList,
			DaoLocusList locusList,
			List<String> propList,
            String reserve1
    ) {
		this.stageType = stageType;
		this.moniker = moniker;
		this.locusList = locusList;
		this.propList = propList;

		this.daoInfo = daoInfo;
		this.daoLocale = daoLocale;
		this.tagList = tagList;
		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return moniker + ", " + locusList + ", " + propList + ", " +
				daoInfo.toString() + ", " +
				daoLocale.toString() +
                tagList + ", " + reserve1;
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

	public String getMoniker() {
		return moniker;
	}

	public void setMoniker(String moniker) {
		this.moniker = moniker;
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

	public DaoInfo getDaoInfo() {
        return daoInfo;
    }

    public void setDaoInfo(DaoInfo daoInfo) {
        this.daoInfo = daoInfo;
    }

	public DaoLocale getDaoLocale() {
		return daoLocale;
	}

	public void setDaoLocale(DaoLocale daoLocale) {
		this.daoLocale = daoLocale;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}
	///////////////////////////////////////////////////////////////////////////
}