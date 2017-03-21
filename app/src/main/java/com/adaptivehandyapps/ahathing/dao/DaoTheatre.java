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
public class DaoTheatre extends DaoBase {

	private static final long serialVersionUID = 1L;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoTheatre() {
		super();
//		this.moniker = DaoDefs.INIT_STRING_MARKER;
//		this.headline = DaoDefs.INIT_STRING_MARKER;
//		this.tagList = new ArrayList<>();
////		this.daoLocale = new DaoLocale();
//		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoTheatre(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
//			DaoLocale daoLocale,
            String reserve1
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);
//		this.moniker = moniker;
//		this.headline = headline;
//		this.tagList = tagList;
////		this.daoLocale = daoLocale;
//		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
//	public String toString() {
//		return moniker + ", " + headline + ", " +
//				tagList + ", " +
////				daoLocale.toString() +
//                reserve1;
//	}
//
//	public static long getSerialVersionUID() {
//		return serialVersionUID;
//	}
//
//	public String getMoniker() {
//		return moniker;
//	}
//
//	public void setMoniker(String moniker) {
//		this.moniker = moniker;
//	}
//
//	public String getHeadline() {
//		return headline;
//	}
//
//	public void setHeadline(String headline) {
//		this.headline = headline;
//	}
//
//	public List<String> getTagList() {
//		return tagList;
//	}
//
//	public void setTagList(List<String> tagList) {
//		this.tagList = tagList;
//	}
//
//	public DaoLocale getDaoLocale() {
//		return daoLocale;
//	}
//
//	public void setDaoLocale(DaoLocale daoLocale) {
//		this.daoLocale = daoLocale;
//	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}