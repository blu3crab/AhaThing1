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

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoEpic extends DaoBase {

	private static final long serialVersionUID = 1L;

	public static final String EPIC_TYPE_NONE = "None";
	public static final String EPIC_TYPE_COMPETE = "Compete";
	public static final String EPIC_TYPE_COOPERATE = "Cooperate";

	public static final Integer EPIC_TALLY_LIMIT_DEFAULT = 24;
	public static final Integer EPIC_TIC_LIMIT_DEFAULT = 12;

	@SerializedName("epicType")			// determines how to tally
	private String epicType;

	@SerializedName("actorList")		// actors in epic
	private List<String> actorList;

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
		this.actorList = new ArrayList<>();
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
			List<String> actorList,
			Integer tallyLimit,
			List<Integer> tallyList,
			Integer ticLimit,
			List<Integer> ticList,
            String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.epicType = epicType;
		this.actorList = actorList;
		this.tallyLimit = tallyLimit;
		this.tallyList = tallyList;
		this.ticLimit = ticLimit;
		this.ticList = ticList;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;

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

	public List<String> getActorList() {
		return actorList;
	}
	public void setActorList(List<String> actorList) {
		this.actorList = actorList;
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