/*
 * Copyright (C) 2016 Riccardo Leschiutta.
 *
 * This file is part of Parappnoid.
 *
 * Parappnoid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Parappnoid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Parappnoid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.prgpascal.parappnoid.application.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.utils.MyNotificationManager;
import static com.prgpascal.parappnoid.utils.Constants.*;

/**
 * http://stackoverflow.com/questions/11380051/single-page-preferenceactivity-w-no-headers-fragments
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }



    /** My Custom PreferenceFragment */
    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Set the correct preferences name
            PreferenceManager prefMgr = getPreferenceManager();
            prefMgr.setSharedPreferencesName(PREFERENCES);
            prefMgr.setSharedPreferencesMode(Activity.MODE_PRIVATE);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.user_preferences);
        }


        /** Capture the click events on Preference buttons */
        public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference){

            /** Show notifications */
            if (preference.getKey().matches("show_notification")){
                CheckBoxPreference pref = (CheckBoxPreference) preference;

                if (pref.isChecked()){
                    // Show the notification
                    new MyNotificationManager(getActivity()).showNotification(true);

                } else {
                    // Stop showing the notification
                    new MyNotificationManager(getActivity()).showNotification(false);
                }
            }

            /** Edit DB settings */
            if (preference.getKey().matches("edit_DB")){
                Intent intent = new Intent(getActivity(), DBSettingsEditorActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            return true;
        }
    }
}  