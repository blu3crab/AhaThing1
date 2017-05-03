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
import java.util.Arrays;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoOutcome extends DaoBase {

	private static final long serialVersionUID = 1L;

	public static final String OUTCOME_TYPE_NONE = "None";
	public static final String OUTCOME_TYPE_TOGGLE = "Toggle";
	public static final String OUTCOME_TYPE_TOGGLE_PLUS = "TogglePlus";
	public static final String OUTCOME_TYPE_TOGGLE_PATH = "TogglePath";

	@SerializedName("outcomeType")		// type
	private String outcomeType;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoOutcome() {
		super();
		this.outcomeType = OUTCOME_TYPE_NONE;
		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoOutcome(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
            String reserve1,
			String outcomeType,
			String reserve2
	) {
		super(moniker, headline, timestamp, tagList, reserve1);
		this.outcomeType = outcomeType;
		this.reserve2 = reserve2;
	}

	/////////////////////////////helpers//////////////////////////////////
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public static List<String> getOutcomeTypeList() {
		List<String> outcomeTypeList = new ArrayList<>(Arrays.asList(
				OUTCOME_TYPE_NONE,
				OUTCOME_TYPE_TOGGLE,
				OUTCOME_TYPE_TOGGLE_PLUS,
				OUTCOME_TYPE_TOGGLE_PATH
		));
		return outcomeTypeList;
	}

	public String getOutcomeType() {
		return outcomeType;
	}

	public void setOutcomeType(String outcomeType) {
		this.outcomeType = outcomeType;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}