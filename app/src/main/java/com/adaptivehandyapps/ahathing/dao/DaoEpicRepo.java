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

import com.adaptivehandyapps.ahathing.dao.DaoEpic;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoEpicRepo {
	public static final String JSON_CONTAINER = "epics";

	@SerializedName("monikerList")
	private List<String> monikerList;
	@SerializedName("daoList")
	private List<DaoEpic> daoList;

	public DaoEpicRepo(){
		monikerList = new ArrayList<>();
		daoList = new ArrayList<>();
	}

	///////////////////////////////////////////////////////////////////////////
	public Boolean set(DaoEpic daoEpic) {
		// if repo contains object
		if (contains(daoEpic.getMoniker())) {
			// update object
			daoList.set(indexOf(daoEpic.getMoniker()), daoEpic);
		}
		else {
			// new object
			monikerList.add(daoEpic.getMoniker());
			daoList.add(daoEpic);
		}
		return true;
	}
	///////////////////////////////////////////////////////////////////////////
	public DaoEpic get(String monikerTest) {
		DaoEpic daoEpic = null;
		int inx = monikerList.indexOf(monikerTest);
		if (inx != -1) {
			daoEpic = daoList.get(inx);
		}
		return daoEpic;
	}
	///////////////////////////////////////////////////////////////////////////
	public DaoEpic get(int inx) {
		DaoEpic daoEpic = null;
		if (inx > -1 && inx < daoList.size()) {
			daoEpic = daoList.get(inx);
		}
		return daoEpic;
	}
	///////////////////////////////////////////////////////////////////////////
	public List<String> getMonikerList() {
		return monikerList;
	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean remove(String moniker) {
		int inx = monikerList.indexOf(moniker);
		if (inx != -1) {
			monikerList.remove(inx);
			daoList.remove(inx);
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
