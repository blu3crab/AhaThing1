/*
 * Project: AhaThing1
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
package com.adaptivehandyapps.ahathing;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adaptivehandyapps.ahathing.ahautils.TimeUtils;
import com.adaptivehandyapps.ahathing.dao.DaoDefs;

import java.util.regex.Pattern;
////////////////////////////////////////////////////////////////////////////
// PrefsUtils: manage preferences
public class PrefsUtils {

    public static final String TAG = "PrefsUtils";

    ////////////////////////////////////////////////////////////////////////////
    // app preferences
    public static final String ACTIVE_THEATRE_KEY = "activeTheatre";
    public static final String ACTIVE_EPIC_KEY = "activeEpic";
    public static final String ACTIVE_STORY_KEY = "activeStory";
    public static final String ACTIVE_STAGE_KEY = "activeStage";
    public static final String ACTIVE_ACTOR_KEY = "activeActor";
    public static final String ACTIVE_ACTION_KEY = "activeAction";
    public static final String ACTIVE_OUTCOME_KEY = "activeOutcome";
    public static final String SCALE_FACTOR_KEY = "scaleFactor";
    public static final String STAGE_LOCK_KEY = "stageLockKey";

    ///////////////////////////////////////////////////////////////////////////
    public static void setDefaults(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit().putString(ACTIVE_THEATRE_KEY, DaoDefs.INIT_STRING_MARKER);
        prefs.edit().putString(ACTIVE_EPIC_KEY, DaoDefs.INIT_STRING_MARKER);
        prefs.edit().putString(ACTIVE_STORY_KEY, DaoDefs.INIT_STRING_MARKER);
        prefs.edit().putString(ACTIVE_STAGE_KEY, DaoDefs.INIT_STRING_MARKER);
        prefs.edit().putString(ACTIVE_ACTOR_KEY, DaoDefs.INIT_STRING_MARKER);
        prefs.edit().putString(ACTIVE_ACTION_KEY, DaoDefs.INIT_STRING_MARKER);
        prefs.edit().putString(ACTIVE_OUTCOME_KEY, DaoDefs.INIT_STRING_MARKER);
        prefs.edit().putFloat(SCALE_FACTOR_KEY, StageViewController.DEFAULT_SCALE_FACTOR);
        prefs.edit().putBoolean(STAGE_LOCK_KEY, StageViewRing.DEFAULT_STAGE_LOCK);
        return;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static String toString(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return TAG + "-->" + "\n" +
                ACTIVE_THEATRE_KEY + ": " +
                prefs.getString(ACTIVE_THEATRE_KEY, DaoDefs.INIT_STRING_MARKER)  + "\n" +
                ACTIVE_EPIC_KEY + ": " +
                prefs.getString(ACTIVE_EPIC_KEY, DaoDefs.INIT_STRING_MARKER)  + "\n" +
                ACTIVE_STORY_KEY + ": " +
                prefs.getString(ACTIVE_STORY_KEY, DaoDefs.INIT_STRING_MARKER)  + "\n" +
                ACTIVE_STAGE_KEY + ": " +
                prefs.getString(ACTIVE_STAGE_KEY, DaoDefs.INIT_STRING_MARKER)  + "\n" +
                ACTIVE_ACTOR_KEY + ": " +
                prefs.getString(ACTIVE_ACTOR_KEY, DaoDefs.INIT_STRING_MARKER)  + "\n" +
                ACTIVE_ACTION_KEY + ": " +
                prefs.getString(ACTIVE_ACTION_KEY, DaoDefs.INIT_STRING_MARKER)  + "\n" +
                ACTIVE_OUTCOME_KEY + ": " +
                prefs.getString(ACTIVE_OUTCOME_KEY, DaoDefs.INIT_STRING_MARKER)  + "\n" +
                SCALE_FACTOR_KEY + ": " +
                prefs.getFloat(SCALE_FACTOR_KEY, StageViewController.DEFAULT_SCALE_FACTOR)  + "\n" +
                STAGE_LOCK_KEY + ": " +
                prefs.getBoolean(STAGE_LOCK_KEY, StageViewRing.DEFAULT_STAGE_LOCK)  + "\n";
    }
    ///////////////////////////////////////////////////////////////////////////
    // getters & setters
    public static String getPrefs(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, DaoDefs.INIT_STRING_MARKER);
    }
    public static Float getFloatPrefs(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(key, StageViewController.DEFAULT_SCALE_FACTOR);
    }
    public static Boolean getBooleanPrefs(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, StageViewRing.DEFAULT_STAGE_LOCK);
    }
    public static void setPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(key, value).apply();
        Log.d(TAG,"setPrefs key->" + key + ", value->" + value);
        return;
    }
    public static void setPrefs(Context context, String key, float value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putFloat(key, value).apply();
        return;
    }
    public static void setPrefs(Context context, String key, Boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(key, value).apply();
        return;
    }
    ///////////////////////////////////////////////////////////////////////////
}