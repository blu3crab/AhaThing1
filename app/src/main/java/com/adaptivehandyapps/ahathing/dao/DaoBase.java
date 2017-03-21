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
public class DaoBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("moniker")		// name
	private String moniker;

	@SerializedName("headline")		// headline
	private String headline;

	@SerializedName("timestamp")	// timestamp
	private Long timestamp;

	@SerializedName("tagList")		// tag list - epics
	private List<String> tagList;

//	// DAO locale info
//	@SerializedName("daoLocale")		// theatre location info
//	private DaoLocale daoLocale;

	@SerializedName("reserve1")
	private String reserve1;

	///////////////////////////////////////////////////////////////////////////
	public DaoBase() {
		this.moniker = DaoDefs.INIT_STRING_MARKER;
		this.headline = DaoDefs.INIT_STRING_MARKER;
		this.timestamp = DaoDefs.INIT_LONG_MARKER;
		this.tagList = new ArrayList<>();
//		this.daoLocale = new DaoLocale();
		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoBase(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
//			DaoLocale daoLocale,
            String reserve1
    ) {
		this.moniker = moniker;
		this.headline = headline;
		this.timestamp = timestamp;
		this.tagList = tagList;
//		this.daoLocale = daoLocale;
		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return moniker + ", " + headline + ", " +
				tagList + ", " +
//				daoLocale.toString() +
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

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	//	public DaoLocale getDaoLocale() {
//		return daoLocale;
//	}
//
//	public void setDaoLocale(DaoLocale daoLocale) {
//		this.daoLocale = daoLocale;
//	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}
	///////////////////////////////////////////////////////////////////////////
}