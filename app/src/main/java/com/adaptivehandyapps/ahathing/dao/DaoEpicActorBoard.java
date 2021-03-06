/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker 2017
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

///////////////////////////////////////////////////////////////////////////
public class DaoEpicActorBoard {

	private static final String TAG = DaoEpicActorBoard.class.getSimpleName();
	private static final long serialVersionUID = 1L;

	@SerializedName("actorMoniker")	// actor active on epic
	private String actorMoniker;

	@SerializedName("starMoniker")	// star linked to actor
	private String starMoniker;

	@SerializedName("starLabel")	// label of star linked to actor
	private String starLabel;

	@SerializedName("tally")		// tally for each actor (aka score)
	private Integer tally;

	@SerializedName("tic")			// tic for each actor (aka turn counter)
	private Integer tic;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoEpicActorBoard() {
		super();

		this.actorMoniker = DaoDefs.INIT_STRING_MARKER;
		this.starMoniker = DaoDefs.INIT_STRING_MARKER;
		this.starLabel = DaoDefs.INIT_STRING_MARKER;
		this.tally = DaoDefs.INIT_INTEGER_MARKER;
		this.tic = DaoDefs.INIT_INTEGER_MARKER;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoEpicActorBoard(
			String actor,
			String star,
			String starLabel,
			Integer tally,
			Integer tic,
            String reserve2
    ) {
		this.actorMoniker = actor;
		this.starMoniker = star;
		this.starLabel = star;
		this.tally = tally;
		this.tic = tic;

		this.reserve2 = DaoDefs.INIT_STRING_MARKER;

	}

	/////////////////////////////helpers//////////////////////////////////
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String toString() {
		return "moniker: " + getActorMoniker() +
				"star moniker " + getStarMoniker() + " star label " + getStarLabel() +
				", tally/tic: " + getTally() + "/" + getTic();
	}

	public String getActorMoniker() {
		return actorMoniker;
	}
	public void setActorMoniker(String actorMoniker) {
		this.actorMoniker = actorMoniker;
	}

	public String getStarMoniker() {
		return starMoniker;
	}
	public void setStarMoniker(String starMoniker) {
		this.starMoniker = starMoniker;
	}

	public String getStarLabel() {
		return starLabel;
	}
	public void setStarLabel(String starLabel) {
		this.starLabel = starLabel;
	}

	public Integer getTally() {
		return tally;
	}
	public void setTally(Integer tally) {
		this.tally = tally;
	}

	public Integer getTic() {
		return tic;
	}
	public void setTic(Integer tic) {
		this.tic = tic;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}