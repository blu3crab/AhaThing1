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
public class DaoAction extends DaoBase {

	private static final long serialVersionUID = 1L;

	public static final String ACTION_TYPE_NONE = "None";
	public static final String ACTION_TYPE_SINGLE_TAP = "SingleTap";
	public static final String ACTION_TYPE_DOUBLE_TAP = "DoubleTap";
	public static final String ACTION_TYPE_LONG_PRESS = "LongPress";
	public static final String ACTION_TYPE_FLING = "Fling";

	@SerializedName("actionType")		// action type
	private String actionType;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoAction() {
		super();
		this.actionType = ACTION_TYPE_NONE;
		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoAction(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,
			String actionType,
			String reserve2
	) {
		super(moniker, headline, timestamp, tagList, reserve1);
		this.actionType = actionType;
		this.reserve2 = reserve2;
	}

	/////////////////////////////helpers//////////////////////////////////
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public static List<String> getActionTypeList() {
		List<String> actionTypeList = new ArrayList<>(Arrays.asList(
				ACTION_TYPE_NONE,
				ACTION_TYPE_SINGLE_TAP,
				ACTION_TYPE_DOUBLE_TAP,
				ACTION_TYPE_LONG_PRESS,
				ACTION_TYPE_FLING
		));
		return actionTypeList;
	}

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}