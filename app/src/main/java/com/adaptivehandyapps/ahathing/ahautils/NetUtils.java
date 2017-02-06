/**
 * Copyright © 2015 Adaptive Handy Apps, LLC.
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
 **/
// Project: AHA Smart Energy Explorer
// Contributor(s): M.A.Tucker
// Origination: SEP 2015
package com.adaptivehandyapps.ahathing.ahautils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by mat on 12/1/2015.
 */
public class NetUtils {
    private static final String TAG = "NetUtils";

    public static boolean isNetworkAvailable(Context c) {
        boolean isWifiConn = false;
        boolean isMobileConn = false;

        ConnectivityManager connMgr = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {
            isWifiConn = networkInfo.isConnectedOrConnecting();
        }
        Log.d(TAG, "Wifi connected: " + isWifiConn);
//        return networkInfo != null && networkInfo.isConnectedOrConnecting();

        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null) {
            isMobileConn = networkInfo.isConnectedOrConnecting();
        }
        Log.d(TAG, "Mobile connected: " + isMobileConn);

        return isWifiConn || isMobileConn;
    }
    public static boolean isWifiNetworkAvailable(Context c) {
        boolean isWifiConn = false;

        ConnectivityManager connMgr = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {
            isWifiConn = networkInfo.isConnectedOrConnecting();
        }
        Log.d(TAG, "Wifi connected: " + isWifiConn);
        return isWifiConn;
    }
    public static boolean isMobileNetworkAvailable(Context c) {
        boolean isMobileConn = false;

        ConnectivityManager connMgr = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null) {
            isMobileConn = networkInfo.isConnectedOrConnecting();
        }
        Log.d(TAG, "Mobile connected: " + isMobileConn);
        return isMobileConn;
    }

    public static boolean isBleSupported(Context c) {
        boolean support = false;
        support = c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        Log.d(TAG,"BLE support: " + support);
        return support;
    }
//    ///////////////////////////////////////////////////////////////////////////
//    private Boolean isNodeIdValid(Integer nodeId) {
//        Boolean isValid = true;
//        if (nodeId.compareTo(DaoZwave.ZW_INCLUDE_OK_START) <= 0 ||
//                nodeId.compareTo(DaoZwave.ZW_INCLUDE_OK_END) >= 0) {
//            // if node id not in valid range, flag
//            isValid = false;
//        }
//        return isValid;
//    }
}
