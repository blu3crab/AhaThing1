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

import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
public class DaoActor extends DaoBase {

	private static final long serialVersionUID = 1L;

	//
	// @DaoDefs.ObjType int type = DaoDefs.getObjType(some object);
	@Retention(RetentionPolicy.SOURCE)
	// actor types
	@IntDef({ACTOR_TYPE_FORBIDDEN,
			ACTOR_TYPE_MIRROR,
			ACTOR_TYPE_STAR
	})
	public @interface ActorType {}

	public static final int ACTOR_TYPE_FORBIDDEN = -1;
	public static final int ACTOR_TYPE_MIRROR = 0;
	public static final int ACTOR_TYPE_STAR = 1;

	public static final String ACTOR_MONIKER_FORBIDDEN = "Forbidden";
	public static final String ACTOR_MONIKER_MIRROR = "Mirror";
	public static final String ACTOR_MONIKER_STAR = "Star";

	public static final List<String> ACTOR_TYPE_LIST = Arrays.asList(ACTOR_MONIKER_STAR, ACTOR_MONIKER_MIRROR, ACTOR_MONIKER_FORBIDDEN);

	@SerializedName("foreColor")
	private Integer foreColor;

	@SerializedName("backColor")
	private Integer backColor;

	@SerializedName("speed")		// actor>0; prop=0; forbidden<0
	private Integer speed;

	@SerializedName("resistance")
	private Integer resistance;

	@SerializedName("reserve2")
	private String reserve2;

	///////////////////////////////////////////////////////////////////////////
	public DaoActor() {
		super();
		this.foreColor = DaoDefs.INIT_INTEGER_MARKER;
		this.backColor = DaoDefs.INIT_INTEGER_MARKER;
		this.speed = ACTOR_TYPE_STAR;	// default to star speed > 0
		this.resistance = DaoDefs.INIT_INTEGER_MARKER;
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
			Integer speed,
			Integer resistance,
            String reserve2
	) {
		super(moniker, headline, timestamp, tagList, reserve1);

		this.foreColor = foreColor;
		this.backColor = backColor;
		this.speed = speed;
		this.resistance = resistance;
		this.reserve2 = reserve2;
	}

	/////////////////////////////helpers//////////////////////////////////
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String toString() {
		return super.toString() + ", " + foreColor + ", " + backColor + ", " + speed + ", " + resistance + ", " + reserve2;
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

	public Integer getSpeed() {return speed;}
	public void setSpeed(Integer speed) {this.speed = speed;}

	public Integer getResistance() {return resistance;}
	public void setResistance(Integer resistance) {this.resistance = resistance;}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve1) {
		this.reserve2 = reserve2;
	}
	///////////////////////////////////////////////////////////////////////////
	public static Integer speedToActorTypeInx(Integer speed) {
		if (speed < 0) {
			return ACTOR_TYPE_LIST.indexOf(ACTOR_MONIKER_FORBIDDEN);
		}
		else if (speed == 0) {
			return ACTOR_TYPE_LIST.indexOf(ACTOR_MONIKER_MIRROR);
		}
		return ACTOR_TYPE_LIST.indexOf(ACTOR_MONIKER_STAR);
	}
	public static Integer actorTypeToSpeed(String actorType) {
		if (actorType.equals(ACTOR_MONIKER_FORBIDDEN)) {
			return ACTOR_TYPE_FORBIDDEN;
		}
		else if (actorType.equals(ACTOR_MONIKER_MIRROR)) {
			return ACTOR_TYPE_MIRROR;
		}
		return ACTOR_TYPE_STAR;
	}

	///////////////////////////////////////////////////////////////////////////
}