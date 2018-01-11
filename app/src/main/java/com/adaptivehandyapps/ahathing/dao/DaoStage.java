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

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoStage extends DaoBase {

	private static final long serialVersionUID = 1L;

	public static final String STAGE_TYPE_RING = "RingWorld";
	public static final Integer STAGE_TYPE_RING_SIZE_DEFAULT = 4;
	public static final int STAGE_BG_COLOR = Color.BLUE;

//	public static final String PROP_TYPE_MIRROR = "Mirror";
//	public static final String PROP_TYPE_FORBIDDEN = "Forbidden";

	@SerializedName("stageType")		// stage type
	private String stageType;

	@SerializedName("ringSize")			// stage ring type: # rings
	private Integer ringSize;

	@SerializedName("markIndex")		// marked locus
	private Integer markIndex;

	@SerializedName("locusList")		// locus list
	private DaoLocusList locusList;

	@SerializedName("actorList")		// actor list mirrors locusList
	private List<String> actorList;

	@SerializedName("propList")			// props list
	private List<String> propList;
	@SerializedName("propFgColorList")	// props color list
	private List<Integer> propFgColorList;
	@SerializedName("propBgColorList")	// props color list
	private List<Integer> propBgColorList;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoStage() {
		super();
		this.stageType = STAGE_TYPE_RING;
		this.ringSize = STAGE_TYPE_RING_SIZE_DEFAULT;
		this.markIndex = DaoDefs.INIT_INTEGER_MARKER;
		this.locusList = new DaoLocusList();
		this.actorList = new ArrayList<>();
		this.propList = new ArrayList<>();
		this.propFgColorList = new ArrayList<>();
		this.propBgColorList = new ArrayList<>();

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoStage(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,

			String stageType,
			Integer ringSize,
			Integer markIndex,
			DaoLocusList locusList,
			List<String> actorList,
			List<String> propList,
			List<Integer> propFgColorList,
			List<Integer> propBgColorList,
            String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.stageType = stageType;
		this.ringSize = ringSize;
		this.markIndex = markIndex;
		this.locusList = locusList;
		this.actorList = actorList;
		this.propList = propList;
		this.propFgColorList = propFgColorList;
		this.propBgColorList = propBgColorList;
		this.reserve2 = reserve2;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return super.toString() + ", " + stageType + ", "  + ringSize + ", " + markIndex + ", " + locusList + ", " + actorList + ", " + propList + ", " + reserve2;
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

	public Integer getRingSize() {
		return ringSize;
	}
	public void setRingSize(Integer ringSize) {
		this.ringSize = ringSize;
	}

	public Integer getMarkIndex() {
		return markIndex;
	}
	public void setMarkIndex(Integer markIndex) {
		this.markIndex = markIndex;
	}

	public DaoLocusList getLocusList() {
		return locusList;
	}
	public void setLocusList(DaoLocusList locusList) {
		this.locusList = locusList;
	}

	public List<String> getActorList() {
		return actorList;
	}
	public void setActorList(List<String> actorList) {
		this.actorList = actorList;
	}

	public List<String> getPropList() {
		return propList;
	}
	public void setPropList(List<String> propList) {
		this.propList = propList;
	}

	public List<Integer> getPropFgColorList() {
		return propFgColorList;
	}
	public void setPropFgColorList(List<Integer> propFgColorList) {this.propFgColorList = propFgColorList;}

	public List<Integer> getPropBgColorList() {
		return propBgColorList;
	}
	public void setPropBgColorList(List<Integer> propBgColorList) {
		this.propBgColorList = propBgColorList;
	}

	public String getReserve2() {
		return reserve2;
	}
	public void setReserve2(String reserve1) {
		this.reserve2 = reserve1;
	}

	///////////////////////////////////////////////////////////////////////////
	public List<String> getUniqueActorList() {
		List<String> uniqueActorList = new ArrayList<>();
		// for actor at each locus
		for (String actor : getActorList()) {
			// if actor defined & not contained in unique list
			if (!actor.equals(DaoDefs.INIT_STRING_MARKER) && !uniqueActorList.contains(actor)) {
				// add actor to unique list
				uniqueActorList.add(actor);
			}
		}
		return uniqueActorList;
	}

	public Boolean togglePropList(Integer selectIndex, String propMoniker, int fgColor, int bgColor) {
		// if stage prop list empty at ring location
		if (getPropList().get(selectIndex).equals(DaoDefs.INIT_STRING_MARKER)) {
			// set stage to prop at selected location
            getPropList().set(selectIndex, propMoniker);
			// set fore/back color
			getPropFgColorList().set(selectIndex, fgColor);
			getPropBgColorList().set(selectIndex, bgColor);
		}
		else {
			// clear stage prop list at selected location
			getPropList().set(selectIndex, DaoDefs.INIT_STRING_MARKER);
		}
		return true;
	}
	public Boolean toggleActorList(String actorMoniker, Integer selectIndex) {
		// if stage actor list empty at ring location
		if (getActorList().get(selectIndex).equals(DaoDefs.INIT_STRING_MARKER)) {
			// set stage to active actor at selected location
			getActorList().set(selectIndex, actorMoniker);
		}
		else {
			// clear stage active actor at selected location
			getActorList().set(selectIndex, DaoDefs.INIT_STRING_MARKER);
		}
		return true;
	}
	public Boolean setActorList(String moniker) {
		// clear actors on stage
		for (int i = 0; i < getActorList().size(); i++) {
			// clear stage active actor at selected location
			getActorList().set(i, moniker);
		}
		return true;
	}

	///////////////////////////////////////////////////////////////////////////
}