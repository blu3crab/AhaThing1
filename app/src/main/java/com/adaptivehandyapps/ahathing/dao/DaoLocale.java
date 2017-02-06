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

///////////////////////////////////////////////////////////////////////////
public class DaoLocale implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("nickname")
    private String nickname;			// nickname of site
    @SerializedName("street")
    private String street;			    // street address
    @SerializedName("citystate")
    private String citystate;			// city, state
    @SerializedName("location")
    private String location;			// lat/lon "39.547838,-79.351205"
    @SerializedName("imagename")
    private String imagename;			// image name

    @SerializedName("reserve1")
    private String reserve1;			// reserve


    ///////////////////////////////////////////////////////////////////////////
    public DaoLocale() {
        this.nickname = DaoDefs.INIT_STRING_MARKER;
        this.street = DaoDefs.INIT_STRING_MARKER;
        this.citystate = DaoDefs.INIT_STRING_MARKER;
        this.location = DaoDefs.INIT_STRING_MARKER;
        this.imagename = DaoDefs.INIT_STRING_MARKER;

        this.reserve1 = DaoDefs.INIT_STRING_MARKER;
    }

    public DaoLocale(
            String nickname,
            String street,
            String citystate,
            String location,
            String imagename,
            String reserve1
    ) {
        this.nickname = nickname;
        this.street = street;
        this.citystate = citystate;
        this.location = location;
        this.imagename = imagename;

        this.reserve1 = reserve1;
    }

    //////////////////////////////////helpers//////////////////////////////////
    public String toString() {
        return nickname + ", " + street + ", " + citystate  + ", " +
                location + ", " + imagename + ", " +
                reserve1;
    }
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCitystate() {
        return citystate;
    }

    public void setCitystate(String citystate) {
        this.citystate = citystate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }
}
///////////////////////////////////////////////////////////////////////////