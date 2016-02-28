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

package com.prgpascal.parappnoid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.prgpascal.parappnoid.utils.AssociatedUser;
import com.prgpascal.parappnoid.utils.DBUtils;
import com.prgpascal.parappnoid.utils.MyUtils;
import java.util.ArrayList;
import com.prgpascal.parappnoid.utils.DBUtils.*;

/**
 * Activity that allows the user to edit the DB settings.
 * Here the user can choose new values for the passphrase and/or the number of iterations used
 * by the KDF, implemented by SQLCipher.
 */
public class DBSettingsEditorActivity extends AppCompatActivity implements DBResponseListener {
    private EditText oldPassphraseEditText;         // EditText for the passphrase of current DB.
    private EditText newPassphraseEditText;         // EditText for the passphrase of new DB.
    private EditText newIterationsEditText;         // EditText for the number of iterations of new DB.
    private DBUtils dbUtils;                        // Object used for DB operations.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the DBUtils object
        dbUtils = DBUtils.getNewInstance(DBSettingsEditorActivity.this);

        // Create the layout
        createLayout();
    }



    /** Create the layout */
    private void createLayout(){
        // Set the layout
        setContentView(R.layout.db_settings_editor);

        // Toolbars
        initToolbars();

        // EditTexts
        oldPassphraseEditText = (EditText)findViewById(R.id.oldPassphrase);
        newPassphraseEditText = (EditText)findViewById(R.id.newPassphrase);
        newIterationsEditText = (EditText)findViewById(R.id.newIterations);
    }



    /** Edit the Toolbars */
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



    /** Save the settings into DB */
    private void saveDbSettings(){
        // Get data from EditTexts
        char[] oldPassphrase = getValueFromEditText(oldPassphraseEditText);
        char[] newPassphrase = getValueFromEditText(newPassphraseEditText);
        char[] newIterations = getValueFromEditText(newIterationsEditText);

        // Check if the input is valid or not
        if (MyUtils.isValid(oldPassphrase) &&
                MyUtils.isValid(newPassphrase) &&
                MyUtils.isValid(newIterations)) {

            // Convert iterations to Integer value
            int newIterationsInt = Integer.valueOf(String.valueOf(newIterations));

            // Edit the DB Settings
            dbUtils.editDBSettings(oldPassphrase, newPassphrase, newIterationsInt);

        } else {
            // Wrong parameters
            Toast.makeText(getApplicationContext(), R.string.error_wrong_input , Toast.LENGTH_SHORT).show();
        }
    }



    /** Get the text char sequence from an EditText. */
    private char[] getValueFromEditText(EditText e){
        Editable text = e.getText();
        char[] value = new char[text.length()];
        text.getChars(0, text.length(), value, 0);

        return value;
    }



    /**
     * A Database operation has finished.
     *
     * @param result success or failure of database operation.
     */
    public void onDBResponse(boolean result){
        if (result){
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
    public void onDBResponse(ArrayList<AssociatedUser> result){}
}