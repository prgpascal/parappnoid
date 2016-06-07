package com.prgpascal.parappnoid.application.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.activities.SettingsEditorActivity;
import com.prgpascal.parappnoid.utils.MyNotificationManager;

import static com.prgpascal.parappnoid.utils.Constants.PREFERENCES;

/**
 * PreferenceFragment that allows the user to edit some of Parappnoid settings.
 */
public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

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

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        /** Show notifications */
        if (preference.getKey().matches("show_notification")) {
            CheckBoxPreference pref = (CheckBoxPreference) preference;

            if (pref.isChecked()) {
                // Show the notification
                new MyNotificationManager(getActivity()).showNotification(true);

            } else {
                // Stop showing the notification
                new MyNotificationManager(getActivity()).showNotification(false);
            }
        }

        /** Edit DB settings */
        if (preference.getKey().matches("edit_DB")) {
            Intent intent = new Intent(getActivity(), SettingsEditorActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        return true;
    }
}
