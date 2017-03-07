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

import com.adaptivehandyapps.ahathing.dao.DaoDefs;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoEpic implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("moniker")		// name
	private String moniker;

	@SerializedName("headline")		// headline
	private String headline;

	@SerializedName("tagList")		// tag list - stories
	private List<String> tagList;

	@SerializedName("reserve1")
	private String reserve1;

	///////////////////////////////////////////////////////////////////////////
	public DaoEpic() {
		this.moniker = DaoDefs.INIT_STRING_MARKER;
		this.headline = DaoDefs.INIT_STRING_MARKER;
		this.tagList = new ArrayList<>();
		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoEpic(
			String moniker,
			String headline,
			List<String> tagList,
            String reserve1
    ) {
		this.moniker = moniker;
		this.headline = headline;
		this.tagList = tagList;
		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return moniker + ", " + headline + ", " +
				tagList + ", " +
                reserve1;
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

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
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