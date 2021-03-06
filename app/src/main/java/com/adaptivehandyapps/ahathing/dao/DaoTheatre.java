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

	@SerializedName("soundFlourish")	// play flourish sounds
	private Boolean soundFlourish;
	@SerializedName("soundMusic")		// play music sounds
	private Boolean soundMusic;
	@SerializedName("soundAction")		// play action sounds
	private Boolean soundAction;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoTheatre() {
		super();
		this.soundFlourish = false;
		this.soundMusic = false;
		this.soundAction = false;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoTheatre(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
			String reserve1,
			Boolean soundFlourish,
			Boolean soundMusic,
			Boolean soundAction,
			String reserve2
    ) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.soundFlourish = soundFlourish;
		this.soundMusic = soundMusic;
		this.soundAction = soundAction;
	}

	///////////////////////////////////////////////////////////////////////////
	public Boolean getSoundFlourish() {
		return soundFlourish;
	}
	public void setSoundFlourish(Boolean soundFlourish) {
		this.soundFlourish = soundFlourish;
	}

	public Boolean getSoundMusic() {
		return soundMusic;
	}
	public void setSoundMusic(Boolean soundMusic) {
		this.soundMusic = soundMusic;
	}

	public Boolean getSoundAction() {
		return soundAction;
	}
	public void setSoundAction(Boolean soundAction) {
		this.soundAction = soundAction;
	}

	public String getReserve2() {
		return reserve2;
	}
	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}