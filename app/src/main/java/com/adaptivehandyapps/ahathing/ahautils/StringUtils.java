/**
 * Copyright © 2015 Intelligent Water Management Inc.
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
// Project: Brain Pipes
// Contributor(s): M.A.Tucker
// Origination: M.A.Tucker NOV 2015
package com.adaptivehandyapps.ahathing.ahautils;

/**
 * Created by mat on 6/27/2015.
 */

import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static final String TAG = "StringUtils";

    ///////////////////////////////////////////////////////////////////////////
    // map event action to string
    public static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "ACTION_DOWN";
            case MotionEvent.ACTION_MOVE: return "ACTION_MOVE";
            case MotionEvent.ACTION_POINTER_DOWN: return "ACTION_POINTER_DOWN";
            case MotionEvent.ACTION_UP: return "ACTION_UP";
            case MotionEvent.ACTION_POINTER_UP: return "ACTION_POINTER_UP";
            case MotionEvent.ACTION_OUTSIDE: return "ACTION_OUTSIDE";
            case MotionEvent.ACTION_CANCEL: return "ACTION_CANCEL";

            // onGenericMotionEvent
            case MotionEvent.ACTION_HOVER_ENTER: return "ACTION_HOVER_ENTER";
            case MotionEvent.ACTION_HOVER_EXIT: return "ACTION_HOVER_EXIT";
            case MotionEvent.ACTION_HOVER_MOVE: return "ACTION_HOVER_MOVE";

        }
        return "other";
    }
    ///////////////////////////////////////////////////////////////////////////
    public static String listToString(List<String> list, String separator) {
        String cat = "";
        for (String s : list) {
            cat = cat.concat(s);
            if (list.indexOf(s) < list.size()-1) {
                cat = cat.concat(separator);
            }
        }
        return cat;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static List<String> stringToList(String s, String separator) {
        String[] list;
        list = s.split(separator);
        List<String> stringList = new ArrayList<String>(Arrays.asList(list));
        return stringList;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static Integer toInteger(String s) {
//        Integer si = DaoDefs.INIT_INTEGER_MARKER;
        Integer si = -1;
        try {
            si = Integer.valueOf(s);
        } catch (Exception ex) {
            Log.e(TAG, "toInteger exception for: " + s);
        }
        return si;
    }
    ///////////////////////////////////////////////////////////////////////////
}