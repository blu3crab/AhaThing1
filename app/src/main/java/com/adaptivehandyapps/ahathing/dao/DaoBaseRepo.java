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
public class DaoBaseRepo {
//	public static final String JSON_CONTAINER = "theatres";

	@SerializedName("monikerList")
	private List<String> monikerList;
	@SerializedName("daoList")
	private List<Object> daoList;

	public DaoBaseRepo(){
		monikerList = new ArrayList<>();
		daoList = new ArrayList<>();
	}

	///////////////////////////////////////////////////////////////////////////
	public Boolean set(Object object) {
		// copying results in object not updating in consumer after set
//		DaoBase daoBase = (DaoBase) object;
		// if repo contains object
		if (contains(((DaoBase) object).getMoniker())) {
			// update object
			daoList.set(indexOf(((DaoBase) object).getMoniker()), object);
		}
		else {
			// new object
			monikerList.add(((DaoBase) object).getMoniker());
			daoList.add(object);
		}
		return true;
	}
	///////////////////////////////////////////////////////////////////////////
	public Object get(String monikerTest) {
		Object object = null;
		int inx = monikerList.indexOf(monikerTest);
		if (inx != -1) {
			object = daoList.get(inx);
		}
		return object;
	}
	///////////////////////////////////////////////////////////////////////////
	public Object get(int inx) {
		Object object = null;
		if (inx > -1 && inx < daoList.size()) {
			object = daoList.get(inx);
		}
		return object;
	}
	///////////////////////////////////////////////////////////////////////////
	public List<String> getMonikerList() {
		return monikerList;
	}
	public List<Object> getDaoList() {
		return daoList;
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
