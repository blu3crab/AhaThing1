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
public class DaoPlayList {
	public static final String JSON_CONTAINER = "plays";

	@SerializedName("plays")
	public List<DaoPlay> plays;
	
	public DaoPlayList(){plays = new ArrayList<>();}

	///////////////////////////////////////////////////////////////////////////
	// returns DAO if found in list or null if not found
	// TODO: use hashmap rather than serial scan
	public DaoPlay getDao(String ahaId) {
		for (DaoPlay daoPlay : plays) {
			if (daoPlay.getDaoInfo().getAhaId().equals(ahaId)) {
				return daoPlay;
			}
		}
		return null;
	}
}
///////////////////////////////////////////////////////////////////////////
