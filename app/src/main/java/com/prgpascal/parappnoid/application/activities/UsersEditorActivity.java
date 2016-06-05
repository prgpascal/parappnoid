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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.fragments.UsersEditorFragment;
import com.prgpascal.parappnoid.application.fragments.dialogs.MyAlertDialogInterface;
import com.prgpascal.parappnoid.application.fragments.dialogs.SimpleAlertDialogFragment;
import com.prgpascal.parappnoid.model.AssociatedUser;
import com.prgpascal.parappnoid.model.OneTimePad;
import com.prgpascal.parappnoid.utils.DBUtils;
import com.prgpascal.parappnoid.utils.MyUtils;
import com.prgpascal.qrdatatransfer.TransferActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static com.prgpascal.parappnoid.utils.Constants.EncryptionDecryptionConstants.PADS_LENGTH;
import static com.prgpascal.parappnoid.utils.Constants.KeyExchangeConstants.I_AM_THE_SERVER;
import static com.prgpascal.parappnoid.utils.Constants.KeyExchangeConstants.KEY_EXCHANGE_REQUEST_CODE;
import static com.prgpascal.parappnoid.utils.Constants.KeyExchangeConstants.MESSAGES;
import static com.prgpascal.parappnoid.utils.Constants.PASSPHRASE;
import static com.prgpascal.parappnoid.utils.Constants.TAG_DIALOG;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.ACTIVITY_REQUEST_TYPE;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.EDIT_USERS;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.NEW_USER;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.SELECTED_USER;

/**
 * Activity that allows the creation of new AssociatedUsers or the editing of an existing one.
 */
public class UsersEditorActivity extends AppCompatActivity implements
        MyAlertDialogInterface,
        DBUtils.DbResponseCallback {

    private AssociatedUser userToEdit;              // The AssociatedUser to be edited or created.
    private HashMap<Integer, OneTimePad> keys;      // Array of generated keys of the specified user.
    private ArrayList<String> groupsOfKeys;         // Generated keys grouped in Strings.

    public String activityRequestType;              // The type of request for this activity: new user or edit an existing one
    private char[] passphrase;                      // Passphrase inserted by the user

    private DBUtils dbUtils;                                // Object used for DB operations
    private int dbRequest;                                  // Request tags for the DB operations
    private static final int DB_REQUEST_SAVE_USER = 1;      //...
    private static final int DB_REQUEST_UPDATE_USER = 2;    //...
    private static final int DB_REQUEST_DELETE_USER = 3;    //...

    int DEFAULT_AVATAR = R.drawable.avatar0;

    // Dialogs request codes
    private static final int DIALOG_TYPE_CONFIRM_DELETE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbUtils = DBUtils.getInstance(getApplicationContext());

        // Get the Intent parameters.
        if (getIntent().hasExtra(ACTIVITY_REQUEST_TYPE) && (getIntent().hasExtra(PASSPHRASE))) {
            activityRequestType = getIntent().getStringExtra(ACTIVITY_REQUEST_TYPE);
            passphrase = getIntent().getCharArrayExtra(PASSPHRASE);

            if (activityRequestType.equals(NEW_USER)) {
                userToEdit = new AssociatedUser(null, null, DEFAULT_AVATAR);
                createLayout();

            } else if (activityRequestType.equals(EDIT_USERS)) {
                if (getIntent().hasExtra(SELECTED_USER)) {
                    userToEdit = getIntent().getParcelableExtra(SELECTED_USER);
                    createLayout();

                } else {
                    // Param missing!
                    Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                // Wrong request type
                Toast.makeText(getApplicationContext(), R.string.error_wrong_request_type, Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {
            // One or more parameters are missing!
            // Show an error message and finish the activity.
            Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Create the layout
     */
    private void createLayout() {
        setContentView(R.layout.activity_toolbar_top_bottom);

        Fragment fragment = UsersEditorFragment.newInstance();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.commit();

        initToolbars();
    }

    public void performClientRequest(String username, int avatar) {
        userToEdit.setUsername(username);
        userToEdit.setAvatar(avatar);

        // Start the Activity for keys exchange
        Intent intent = new Intent(UsersEditorActivity.this, TransferActivity.class);
        intent.putExtra(I_AM_THE_SERVER, false);
        startActivityForResult(intent, KEY_EXCHANGE_REQUEST_CODE);
    }

    public void performServerRequest(String username, int avatar, int numberOfKeys, int keysPerQR) {
        userToEdit.setUsername(username);
        userToEdit.setAvatar(avatar);

        //TODO generate keys by CSPRNG TEMP
        keys = new MyUtils().generateOneTimePads(numberOfKeys, PADS_LENGTH);

        if (keys != null) {
            // Group the generated keys
            groupsOfKeys = MyUtils.encodeGroupsOfKeys(keys, keysPerQR);

            // Set up the QR TransferActivity
            Bundle b = new Bundle();
            Intent intent = new Intent(UsersEditorActivity.this, TransferActivity.class);
            b.putBoolean(I_AM_THE_SERVER, true);
            b.putStringArrayList(MESSAGES, groupsOfKeys);
            intent.putExtras(b);

            // Start the activity for result
            startActivityForResult(intent, KEY_EXCHANGE_REQUEST_CODE);

        } else {
            // Error: keys not created!
            Toast.makeText(UsersEditorActivity.this, R.string.error_keys_generation, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Edit the Toolbars.
     */
    private void initToolbars() {
        // Toolbar TOP
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setTitle(
                activityRequestType.equals(EDIT_USERS) ? R.string.edit_user : R.string.new_user
        );

        // Toolbar BOTTOM
        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.bottomToolbar);
        toolbarBottom.inflateMenu(R.menu.editor_bottom_items);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Request the confirm to the user
                        showNewDialog(DIALOG_TYPE_CONFIRM_DELETE);
                        break;
                }
                return true;
            }
        });
        toolbarBottom.setVisibility(activityRequestType.equals(EDIT_USERS) ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu only if an existing user must be edited
        if (activityRequestType.equals(EDIT_USERS)) {
            getMenuInflater().inflate(R.menu.editor_top_items, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                // Save the AssociatedUser to DB
                if (MyUtils.isValid(userToEdit.getUsername())) {
                    dbRequest = DB_REQUEST_UPDATE_USER;
                    dbUtils.updateUser(userToEdit, passphrase, this);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == KEY_EXCHANGE_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Keys successfully exchanged

                // Show a completion message
                Toast.makeText(getApplicationContext(), R.string.operation_ok, Toast.LENGTH_SHORT).show();

                if (!data.getBooleanExtra(I_AM_THE_SERVER, false)) {
                    // I'm the client, read the incoming messages.
                    // Decode the groups of keys and store them into "keys".
                    groupsOfKeys = data.getStringArrayListExtra(MESSAGES);
                    keys = MyUtils.decodeGroupsOfKeys(groupsOfKeys);
                }

                // At this point Server and Client have set the right AssociatedUser and exchanged the keys.
                // Proceed saving the AssociatedUser and keys into DB.
                dbRequest = DB_REQUEST_SAVE_USER;
                dbUtils.saveAssociatedUser(userToEdit, keys, passphrase, this);

            } else {
                // Error during keys exchange
                Toast.makeText(getApplicationContext(), R.string.error_keys_exchange, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Show new dialog.
     */
    public void showNewDialog(int dialogType) {
        switch (dialogType) {
            case DIALOG_TYPE_CONFIRM_DELETE:
                // Confirm delete Dialog.
                DialogFragment dialog = SimpleAlertDialogFragment.newInstance(
                        dialogType,
                        getResources().getString(R.string.delete_contact_req), null);
                dialog.show(getSupportFragmentManager(), TAG_DIALOG);
                break;
        }
    }

    /**
     * A positive button has been clicked
     */
    public void doPositiveClick(int dialogType, char[] result) {
        switch (dialogType) {
            case DIALOG_TYPE_CONFIRM_DELETE:
                // Delete the AssociatedUser from DB.
                dbRequest = DB_REQUEST_DELETE_USER;
                dbUtils.deleteUser(userToEdit, passphrase, this);
                break;
        }
    }

    /**
     * A negative button has been clicked
     */
    public void doNegativeClick(int dialogType, char[] result) {
        // do nothing
    }

    /**
     * A Database operation has finished.
     *
     * @param result success or failure of database operation.
     */
    public void onDBResponse(boolean result) {
        if (result) {
            // DB Operation OK
            Toast.makeText(getApplicationContext(), R.string.operation_ok, Toast.LENGTH_SHORT).show();
            switch (dbRequest) {
                case DB_REQUEST_DELETE_USER:
                case DB_REQUEST_UPDATE_USER:
                    finish();
                    break;
                case DB_REQUEST_SAVE_USER:
                    finish();
                    break;
            }
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
    }
}