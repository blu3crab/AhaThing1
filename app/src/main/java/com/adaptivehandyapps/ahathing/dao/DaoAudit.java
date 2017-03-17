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
public class DaoAudit implements Serializable {

	private static final Long RECENT_TIMESTAMP = 8000L;
	private static final long serialVersionUID = 1L;

	@SerializedName("timestamp")	// timestamp
	private Long timestamp;

	@SerializedName("actor")		// actor
	private String actor;

	@SerializedName("action")		// action
	private String action;

	@SerializedName("outcome")		// outcome
	private String outcome;


	@SerializedName("reserve1")
	private String reserve1;

	///////////////////////////////////////////////////////////////////////////
	public DaoAudit() {
		this.timestamp = DaoDefs.INIT_LONG_MARKER;
		this.actor = DaoDefs.INIT_STRING_MARKER;
		this.action = DaoDefs.INIT_STRING_MARKER;
		this.actor = DaoDefs.INIT_STRING_MARKER;
		this.reserve1 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoAudit(
			Long timestamp,
			String actor,
			String action,
			String outcome,
            String reserve1
    ) {
		this.timestamp = timestamp;
		this.actor = actor;
		this.actor = action;
		this.outcome = outcome;
		this.reserve1 = reserve1;
	}

	/////////////////////////////helpers//////////////////////////////////
	public String toString() {
		return timestamp + ", " + actor + ", " + action + ", " + outcome +
				", " + reserve1;
	}
	public String toFormattedString() {
		return timestamp + ": " + actor + " " + action + " " + outcome;
	}
	public Boolean isRecent(Long timestamp) {
		if (timestamp - this.timestamp > RECENT_TIMESTAMP) return false;
		return true;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}
	///////////////////////////////////////////////////////////////////////////
}