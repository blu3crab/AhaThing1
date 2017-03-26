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
public class DaoStory extends DaoBase {

	private static final long serialVersionUID = 1L;

	@SerializedName("stage")
	private String stage;

	@SerializedName("prereq")
	private String prereq;

	@SerializedName("actor")
	private String actor;

	@SerializedName("action")
	private String action;

	@SerializedName("outcome")
	private String outcome;

	@SerializedName("trigger")
	private String trigger;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoStory() {
		super();
		this.stage = DaoDefs.INIT_STRING_MARKER;
		this.prereq = DaoDefs.INIT_STRING_MARKER;
		this.actor = DaoDefs.INIT_STRING_MARKER;
		this.action = DaoDefs.INIT_STRING_MARKER;
		this.outcome = DaoDefs.INIT_STRING_MARKER;
		this.trigger = DaoDefs.INIT_STRING_MARKER;
		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoStory(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,
			String stage,
			String prereq,
			String actor,
			String action,
			String outcome,
			String trigger,
			String reserve2
	) {
		super(moniker, headline, timestamp, tagList, reserve1);
		this.stage = stage;
		this.prereq = prereq;
		this.actor = actor;
		this.action = action;
		this.outcome = outcome;
		this.trigger = trigger;
		this.reserve2 = reserve2;
	}

	/////////////////////////////helpers//////////////////////////////////
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPrereq() {
		return prereq;
	}

	public void setPrereq(String prereq) {
		this.prereq = prereq;
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

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}