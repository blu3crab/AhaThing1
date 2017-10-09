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
package com.adaptivehandyapps.ahathing.ahautils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static final String TAG = "StringUtils";

    ///////////////////////////////////////////////////////////////////////////
    // get text width & height based on paint
    public static int getPaintTextWidth(String text, Paint paint) {
//        String finalVal ="Hello";
//        Paint paint = new Paint();
//        paint.setTextSize(18);
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.FILL);

        Rect result = new Rect();
        paint.getTextBounds(text, 0, text.length(), result);

//        Log.d("WIDTH        :", String.valueOf(result.width()));
//        Log.d("HEIGHT       :", String.valueOf(result.height()));
        return result.width();
    }
    public static int getPaintTextHeight(String text, Paint paint) {
        Rect result = new Rect();
        paint.getTextBounds(text, 0, text.length(), result);

//        Log.d("WIDTH        :", String.valueOf(result.width()));
//        Log.d("HEIGHT       :", String.valueOf(result.height()));
        return result.height();
    }

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