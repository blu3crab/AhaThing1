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

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoAuditRepo {
	private static final String TAG = "DaoAuditRepo";

	public static final String JSON_CONTAINER = "auditTrail";

	@SerializedName("daoList")
	private List<DaoAudit> daoList;

	public DaoAuditRepo(){
		daoList = new ArrayList<>();
	}

	///////////////////////////////////////////////////////////////////////////
	public Boolean set(DaoAudit daoAudit) {
		daoList.add(daoAudit);
		Log.d(TAG, "set->" + daoAudit.toString());
		return true;
	}
	///////////////////////////////////////////////////////////////////////////
	public DaoAudit get(int inx) {
		DaoAudit daoAudit = null;
		if (inx > -1 && inx < daoList.size()) {
			daoAudit = daoList.get(inx);
		}
		return daoAudit;
	}
	///////////////////////////////////////////////////////////////////////////
	public DaoAudit get(String search) {
		ArrayList<DaoAudit> reversedList = new ArrayList(daoList);
		Collections.reverse(reversedList);
		for (DaoAudit audit : reversedList) {
			if (audit.getActor().equals(search) ||
					audit.getAction().equals(search) ||
					audit.getOutcome().equals(search)) {
				return audit;
			}
		}
		return null;
	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean contains(String search) {
		ArrayList<DaoAudit> reversedList = new ArrayList(daoList);
		Collections.reverse(reversedList);
		for (DaoAudit audit : reversedList) {
			if (audit.getActor().equals(search) ||
					audit.getAction().equals(search) ||
					audit.getOutcome().equals(search)) {
				return true;
			}
		}
		return false;
	}
	///////////////////////////////////////////////////////////////////////////
	public int indexOf(String search) {
		ArrayList<DaoAudit> reversedList = new ArrayList(daoList);
		Collections.reverse(reversedList);
		int index = daoList.size() - 1;
		for (DaoAudit audit : reversedList) {
			if (audit.getActor().equals(search) ||
					audit.getAction().equals(search) ||
					audit.getOutcome().equals(search)) {
				return index;
			}
		}
		return DaoDefs.INIT_INTEGER_MARKER;
	}
	///////////////////////////////////////////////////////////////////////////
	public int size() {
		return daoList.size();
	}
	///////////////////////////////////////////////////////////////////////////
	public Boolean remove(int inx) {
		if (inx > -1 && inx < daoList.size()) {
			daoList.remove(inx);
			return true;
		}
		return false;
	}
	///////////////////////////////////////////////////////////////////////////
}
///////////////////////////////////////////////////////////////////////////
