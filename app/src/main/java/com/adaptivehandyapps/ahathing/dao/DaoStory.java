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
public class DaoStory implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("moniker")		// name
	private String moniker;

	@SerializedName("stageList")		// stage list
	private DaoStageList stageList;

	@SerializedName("actorList")	// actor list
	private List<String> actorList;

	@SerializedName("ruleList")	// rule list
	private List<String> ruleList;

	@SerializedName("tagList")		// tag list
	private List<String> tagList;

	@SerializedName("reserve1")
	private String reserve1;

	///////////////////////////////////////////////////////////////////////////
	public DaoStory() {
		this.moniker = DaoDefs.INIT_STRING_MARKER;
		this.stageList = new DaoStageList();
		this.actorList = new ArrayList<>();
		this.ruleList = new ArrayList<>();

		this.tagList = new ArrayList<>();
		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoStory(
			String moniker,
			DaoStageList stageList,
			List<String> actorList,
			List<String> ruleList,
            List<String> tagList,
            String reserve1
    ) {
		this.moniker = moniker;
		this.stageList = stageList;
		this.actorList = actorList;
		this.ruleList = ruleList;

		this.tagList = tagList;
		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return moniker + ", " + stageList + ", " + actorList + ", " + ruleList + ", " +
                tagList + ", " + reserve1;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getMoniker() {
		return moniker;
	}

	public void setMoniker(String moniker) {
		this.moniker = moniker;
	}

	public DaoStageList getStageList() {
		return stageList;
	}

	public void setStageList(DaoStageList stageList) {
		this.stageList = stageList;
	}

	public List<String> getActorList() {
		return actorList;
	}

	public void setActorList(List<String> actorList) {
		this.actorList = actorList;
	}

	public List<String> getRuleList() {
		return ruleList;
	}

	public void setRuleList(List<String> ruleList) {
		this.ruleList = ruleList;
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