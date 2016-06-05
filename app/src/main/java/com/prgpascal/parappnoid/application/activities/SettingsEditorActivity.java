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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.fragments.MainFragment;
import com.prgpascal.parappnoid.model.AssociatedUser;
import com.prgpascal.parappnoid.utils.DBUtils;

import java.util.ArrayList;

/**
 * Activity that allows the user to edit the DB settings.
 * Here the user can choose new values for the passphrase and/or the number of iterations used
 * by the KDF, implemented by SQLCipher.
 */
public class SettingsEditorActivity extends AppCompatActivity implements
        DBUtils.DbResponseCallback {

    private DBUtils dbUtils;                        // Object used for DB operations. //TODO singleton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbUtils = DBUtils.getInstance(getApplicationContext());
        createLayout();
    }

    /**
     * Create the layout.
     */
    private void createLayout() {
        setContentView(R.layout.activity_toolbar_top);

        Fragment fragment = MainFragment.newInstance();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.commit();

        initToolbars();
    }

    /**
     * Edit the Toolbars.
     */
    private void initToolbars() {
        // Toolbar TOP
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setTitle(R.string.db_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_top_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                // Save the DB Settings
                saveDbSettings();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the settings into DB
     */
    private void saveDbSettings() {
        // Get the data from Fragments
        try {
            SettingsEditorInterface fragment = (SettingsEditorInterface) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            char[] oldPassphrase = fragment.getOldPassphrase();
            char[] newPassphrase = fragment.getNewPassphrase();
            char[] newIterations = fragment.getNewIterations();
            int newIterationsInt = Integer.valueOf(String.valueOf(newIterations));

            // Edit the DB Settings
            dbUtils.editDBSettings(oldPassphrase, newPassphrase, newIterationsInt, this);

        } catch (WrongFieldException e) {
            // Wrong parameters
            Toast.makeText(getApplicationContext(), R.string.error_wrong_input, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A Database operation has finished.
     *
     * @param result success or failure of database operation.
     */
    public void onDBResponse(boolean result) {
        if (result) {
            // DB Operation OK
            // Show a confirm message and finish the Activity.
            Toast.makeText(getApplicationContext(), R.string.operation_ok, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // DB Operation Error
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A Database operation has finished.
     *
     * @param result the ArrayList of associated users.
     */
    public void onDBResponse(ArrayList<AssociatedUser> result) {
        // Do nothing
    }

    public interface SettingsEditorInterface {
        char[] getOldPassphrase() throws WrongFieldException;

        char[] getNewPassphrase() throws WrongFieldException;

        char[] getNewIterations() throws WrongFieldException;
    }

    public static class WrongFieldException extends Exception {
        // Empty class
    }
}