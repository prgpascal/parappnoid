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

import android.content.Intent;
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
import com.prgpascal.parappnoid.utils.MyNotificationManager;

import static com.prgpascal.parappnoid.utils.Constants.PASSPHRASE;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.ACTIVITY_REQUEST_TYPE;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.EDIT_USERS;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.NEW_MESSAGE;

/**
 * Main Activity.
 */
public class MainActivity extends AppCompatActivity implements
        MainFragment.MainFragmentInterface {

    private char[] passphrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check and get all the Intent parameters.
        if (getIntent().hasExtra(PASSPHRASE)) {
            passphrase = getIntent().getCharArrayExtra(PASSPHRASE);
            createLayout();

        } else {
            // One or more parameters are missing!
            // Show an error message and finish the activity
            Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Create the layout
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
     * Edit the Toolbars
     */
    private void initToolbars() {
        // Toolbar TOP
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setTitle("MAIN"); //TODO rivedi titolo
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                editSettings();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void writeMessage() {
        Intent intent = new Intent(MainActivity.this, WriteMessageActivity.class);
        intent.putExtra(ACTIVITY_REQUEST_TYPE, NEW_MESSAGE);
        intent.putExtra(PASSPHRASE, passphrase);
        startActivity(intent);
    }

    @Override
    public void readMessage() {
        Intent intent = new Intent(MainActivity.this, ReadMessageActivity.class);
        intent.putExtra(PASSPHRASE, passphrase);
        startActivity(intent);
    }

    @Override
    public void editContacts() {
        Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
        intent.putExtra(ACTIVITY_REQUEST_TYPE, EDIT_USERS);
        intent.putExtra(PASSPHRASE, passphrase);
        startActivity(intent);
    }

    public void editSettings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
