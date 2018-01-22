/*
 * Project: AhaThing1
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker JAN 2018
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

/**
 * Created by mat on 5/21/2015.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.adaptivehandyapps.ahathing.ahautils.BroadcastUtils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
///////////////////////////////////////////////////////////////////////////////
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Boolean mInitialLoad = true;

    ////////////////////////////////////////////////////////////////////////////
    // life-cycle methods
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_settings);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_order_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_pressure_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_date_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_show_alert_key)));
//        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_notification_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_ace_addr_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_org_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_site_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_zone_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sensor_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_scenario_key)));

        mInitialLoad = false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Registers a shared preference change listener that gets notified when preferences change
    @Override
    protected void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Unregisters a shared preference change listener
    @Override
    protected void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
    ////////////////////////////////////////////////////////////////////////////
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();
        Log.d(TAG, "setPreferenceSummary for " + key + " to " + stringValue);

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
                Log.d(TAG, "setPreferenceSummary (list) for " + key + " to " + listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
            Log.d(TAG, "setPreferenceSummary (edit) for " + key + " to " + stringValue);
        }

    }
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String key = preference.getKey();
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        else {
            // For other preferences, set the summary to the value's simple string representation.
            if (key.equals(getString(R.string.pref_scenario_key))) {
                try {
                    Integer si = Integer.valueOf(stringValue);
                } catch (Exception ex) {
                    stringValue = getString(R.string.pref_scenario_default);
//                    PrefsUtils.setPrefsScenario(this, stringValue);
//                    Toast.makeText(this, getString(R.string.prompt_enter_number), Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "onPreferenceChange non-ListPreference", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (!mInitialLoad) {
            // broadcast the change
            BroadcastUtils.broadcastResult(getBaseContext(),
                    BroadcastUtils.AHA_REFRESH,
                    key);
        }
        // set preference summary for change
        setPreferenceSummary(preference, stringValue);
        Log.d(TAG, "onPreferenceChange syncing " + preference.getKey() + " to " + stringValue +
                " w/ initialLoad = " + mInitialLoad);
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////
    // This gets called after the preference is changed, which is important because we
    // start our synchronization here
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged syncing " + key);
        if ( key.equals(getString(R.string.pref_date_key)) ) {
            // we've changed the location
        }
        else if ( key.equals(getString(R.string.pref_units_key)) ) {
            // units have changed. update lists of entries accordingly
        }
        else if ( key.equals(getString(R.string.pref_pressure_key)) ) {
            // pressure units have changed. update lists of entries accordingly
        }
        else if ( key.equals(getString(R.string.pref_show_alert_key)) ) {
            // pressure units have changed. update lists of entries accordingly
        }
    }
    ////////////////////////////////////////////////////////////////////////////
}