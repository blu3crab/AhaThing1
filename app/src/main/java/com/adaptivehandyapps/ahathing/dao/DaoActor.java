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

import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoActor extends DaoBase {

	private static final long serialVersionUID = 1L;

	@SerializedName("foreColor")
	private Integer foreColor;

	@SerializedName("backColor")
	private Integer backColor;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoActor() {
		super();
		this.foreColor = DaoDefs.INIT_INTEGER_MARKER;
		this.backColor = DaoDefs.INIT_INTEGER_MARKER;
		this.reserve2 = DaoDefs.INIT_STRING_MARKER;
	}

	public DaoActor(
			String moniker,
			String headline,
			Long timestamp,
			List<String> tagList,
            String reserve1,

			Integer foreColor,
			Integer backColor,
            String reserve2
	) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.foreColor = foreColor;
		this.backColor = backColor;
		this.reserve2 = reserve2;
	}

	/////////////////////////////helpers//////////////////////////////////
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String toString() {
		return super.toString() + ", " + foreColor + ", " + backColor + ", " + reserve2;
	}

	public Integer getForeColor() {
		return foreColor;
	}

	public void setForeColor(Integer foreColor) {
		this.foreColor = foreColor;
	}

	public Integer getBackColor() {
		return backColor;
	}

	public void setBackColor(Integer backColor) {
		this.backColor = backColor;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
}