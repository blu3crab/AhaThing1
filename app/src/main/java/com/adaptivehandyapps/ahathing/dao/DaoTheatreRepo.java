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
public class DaoTheatreRepo {
	public static final String JSON_CONTAINER = "theatres";

	@SerializedName("monikerList")
	private List<String> monikerList;
	@SerializedName("daoList")
	private List<DaoTheatre> daoList;

	public DaoTheatreRepo(){
		monikerList = new ArrayList<>();
		daoList = new ArrayList<>();
	}

	///////////////////////////////////////////////////////////////////////////
	public Boolean set(DaoTheatre daoTheatre) {
		// if repo contains object
		if (contains(daoTheatre.getMoniker())) {
			// update object
			daoList.set(indexOf(daoTheatre.getMoniker()), daoTheatre);
		}
		else {
			// new object
			monikerList.add(daoTheatre.getMoniker());
			daoList.add(daoTheatre);
		}
		return true;
	}
	///////////////////////////////////////////////////////////////////////////
	public DaoTheatre get(String monikerTest) {
		DaoTheatre daoTheatre = null;
		int inx = monikerList.indexOf(monikerTest);
		if (inx != -1) {
			daoTheatre = daoList.get(inx);
		}
		return daoTheatre;
	}
	///////////////////////////////////////////////////////////////////////////
	public DaoTheatre get(int inx) {
		DaoTheatre daoTheatre = null;
		if (inx > -1 && inx < daoList.size()) {
			daoTheatre = daoList.get(inx);
		}
		return daoTheatre;
	}
	///////////////////////////////////////////////////////////////////////////
	public List<String> getMonikerList() {
		return monikerList;
	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean remove(String moniker) {
		int inx = monikerList.indexOf(moniker);
		if (inx != -1) {
			DaoTheatre daoTheatre = daoList.get(inx);

			return true;
		}
		return false;
	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean contains(String moniker) {
		return monikerList.contains(moniker);
	}
	///////////////////////////////////////////////////////////////////////////
	public int indexOf(String moniker) {
		return monikerList.indexOf(moniker);
	}
	///////////////////////////////////////////////////////////////////////////
	public int size() {
		return monikerList.size();
	}
}
///////////////////////////////////////////////////////////////////////////
