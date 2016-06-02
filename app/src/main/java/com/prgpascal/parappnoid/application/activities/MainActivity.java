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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.utils.MyNotificationManager;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.*;

/**
 * Main Activity, You're welcome :)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the layout
        createLayout();

        // Show Notification in Notification Bar
        new MyNotificationManager(MainActivity.this).showNotificationIfRequested(true);
    }



    /** Create the layout */
    private void createLayout(){
        // Set the layout
        setContentView(R.layout.main_activity);

        // Write Message Button
        Button writeMessage = (Button) findViewById(R.id.writeMessageButton);
        writeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteMessageActivity.class);
                intent.putExtra(ACTIVITY_REQUEST_TYPE, NEW_MESSAGE);
                startActivity(intent);
            }
        });


        // Users Editor Button
        Button editUsersButton = (Button)findViewById(R.id.editUsersButton);
        editUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                intent.putExtra(ACTIVITY_REQUEST_TYPE, EDIT_USERS);
                startActivity(intent);
            }
        });


        // Settings Button
        Button editDBSettings = (Button)findViewById(R.id.editDatabaseButton);
        editDBSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
