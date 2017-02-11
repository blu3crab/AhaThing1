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
public class DaoTheatre implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("moniker")		// name
	private String moniker;

	@SerializedName("playList")		// play list
	private List<String>  playList;

//	// DAO locale info
//	@SerializedName("daoLocale")		// theatre location info
//	private DaoLocale daoLocale;

	@SerializedName("tagList")		// tag list
	private List<String> tagList;

	@SerializedName("reserve1")
	private String reserve1;

	///////////////////////////////////////////////////////////////////////////
	public DaoTheatre() {
		this.moniker = DaoDefs.INIT_STRING_MARKER;
		this.playList = new ArrayList<>();
//		this.daoLocale = new DaoLocale();
		this.tagList = new ArrayList<>();
		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoTheatre(
			String moniker,
			List<String> playList,
//			DaoLocale daoLocale,
            List<String> tagList,
            String reserve1
    ) {
		this.moniker = moniker;
		this.playList = playList;
//		this.daoLocale = daoLocale;
		this.tagList = tagList;
		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return moniker + ", " + playList + ", " +
//				daoLocale.toString() +
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

	public List<String> getPlayList() {
		return playList;
	}

	public void setPlayList(List<String> actorList) {
		this.playList = actorList;
	}

//	public DaoLocale getDaoLocale() {
//		return daoLocale;
//	}
//
//	public void setDaoLocale(DaoLocale daoLocale) {
//		this.daoLocale = daoLocale;
//	}

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