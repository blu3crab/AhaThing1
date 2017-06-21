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
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by mat on 9/10/2015.
 */
public class DevUtils {
    private static final String TAG = "DevUtils";

    // GT-P5210 Samsung Galaxy Tablet landscape 1280/1280 x 800/775 pixel/dp, density 1.0
    // GT-P5210 Samsung Galaxy Tablet portrait 800/800 x 1280/1255 pixel/dp, density 1.0
    // SM-N900V Samsung Note Phablet landscape 1920/640 x 1080/335 pixel/dp, density 3.0
    // SM-N900V Samsung Note Phablet portrait 1080/360 x 1920/615 pixel/dp, density 3.0
    // Piranha tablet landscape 1024/1024x552/552 pixel/dp, density 1.0
    // Piranha tablet portrait 600/600x976/976 pixel/dp, density 1.0

    public static float getDevTextSize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, metrics);
        Log.v(TAG, "text size = " + size);
        return size;
    }

//    public static int getDevTextResIdSize(Context context) {
//        // gather screen dimensions - dp & pixels
//        Configuration configuration = context.getResources().getConfiguration();
//        int screenWidthDp = configuration.screenWidthDp;
//        int screenHeightDp = configuration.screenHeightDp;
//        Log.v(TAG, "screen w/h dp: " + screenWidthDp + ", " + screenHeightDp);
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        int pixelWidth = displayMetrics.widthPixels;
//        int pixelHeight = displayMetrics.heightPixels;
//        float density = displayMetrics.density;
//        Log.v(TAG, "Pixel w,h - density: " + pixelWidth + ", " + pixelHeight + " - " + density);
//        // if pixel or dp dimensions less than 600, set text size to small
//        int resIdSize = R.style.TextAppearance_AppCompat_Medium;
//        if (pixelWidth < 600 || pixelHeight < 600 ||
//                screenWidthDp < 600 || screenHeightDp < 600) {
//            resIdSize = R.style.TextAppearance_AppCompat_Small;
//            Log.v(TAG, "Low pixel dimensions, setting resIdSize to small: " + resIdSize);
//        }
////        Toast.makeText(context, "W x H (pixel-dp), density: " + pixelWidth + "-" + screenWidthDp + " x " +
////                pixelHeight + "-" + screenHeightDp + ", " + density, Toast.LENGTH_LONG).show();
//        return resIdSize;
//    }

    public static int getScreenWidthDp(Context context) {
        // gather screen dimensions
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.screenWidthDp;
    }
    public static int getScreenHeightDp(Context context) {
        // gather screen dimensions
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.screenHeightDp;
    }

    public static int getDisplayWidthPixels(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
    public static int getDisplayHeightPixels(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
    public static float getDisplayDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    ///////////////////////////////////////////////////////////////////////////
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName;
        if (model.startsWith(manufacturer)) {
            deviceName = model.toUpperCase();
        } else {
            deviceName = manufacturer.toUpperCase() + " " + model.toUpperCase();
        }
        Log.d(TAG, "getDeviceName " + deviceName);
        return deviceName;
    }
    ///////////////////////////////////////////////////////////////////////////
}
