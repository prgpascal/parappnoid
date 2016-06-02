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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.fragments.UsersListFragment;
import com.prgpascal.parappnoid.application.model.AssociatedUser;
import com.prgpascal.parappnoid.utils.DBUtils;
import com.prgpascal.parappnoid.utils.MyAlertDialogs.*;
import java.util.ArrayList;
import static com.prgpascal.parappnoid.utils.Constants.TAG_DIALOG;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.PASSPHRASE;

/**
 * Activity that allows the user to select an AssociatedUser.
 * It returns the selected AssociatedUser if the activityRequestType is "PICK_USER".
 * Or pass the AssociatedUser to UsersEditorActivity if activityRequestType is "EDIT_USER".
 */
public class UsersListActivity extends AppCompatActivity implements MyAlertDialogInterface, DBUtils.DBResponseListener {
    private UsersListFragment usersFragment;            // The fragment that will contain the users list.
    public String activityRequestType;                  // The type of request for this activity.
    private char[] passphrase;                          // Passphrase inserted by the user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check and get all the Intent parameters.
        // If a parameter is missing, show an error and finish the activity.
        Intent intent = getIntent();
        if (intent.hasExtra(ACTIVITY_REQUEST_TYPE)){

            // Parameters OK, read them all!
            activityRequestType = getIntent().getStringExtra(ACTIVITY_REQUEST_TYPE);

            // IMPORTANT!
            // createLayout() is called after the user inserted the passphrase and only if it is correct.

            // Request the passphrase to the user
            showNewDialog(DIALOG_TYPE_REQUEST_PASSPHRASE);

        } else {
            // One or more parameters are missing!
            // Show an error message and finish the activity
            Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    /** Show new dialog. */
    public void showNewDialog(int dialogType){
        switch (dialogType) {
            case DIALOG_TYPE_REQUEST_PASSPHRASE:
                // Ask the passphrase to the user.
                DialogFragment dialog1 = MyPassphraseDialogFragment.newInstance(
                        dialogType,
                        getResources().getString(R.string.insert_passphprase), null);
                dialog1.setCancelable(false);
                dialog1.show(getSupportFragmentManager(), TAG_DIALOG);
                break;
        }
    }


    /** A positive button has been clicked */
    public void doPositiveClick(int dialogType, char[] result){
        switch (dialogType) {
            case DIALOG_TYPE_REQUEST_PASSPHRASE:

                // At this point we have the passphrase inserted.
                // update the reference.
                passphrase = result;

                // Proceed creating the layout.
                createLayout();
        }
    }

    /** A negative button has been clicked */
    public void doNegativeClick(int dialogType, char[] result){
        // The user canceled the passphrase input.
        // Show an error message than finish the Activity.
        Toast.makeText(getApplicationContext(), R.string.error_operation_cancelled, Toast.LENGTH_SHORT).show();
        finish();
    }



    /** Create the layout */
    private void createLayout(){
        // Set the layout
        setContentView(R.layout.users_list_activity);

        // Toolbar
        initToolbars();

        // PickUsers Fragment
        Bundle b = new Bundle();
        b.putCharArray(PASSPHRASE, passphrase);
        usersFragment = new UsersListFragment();
        usersFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, usersFragment).commit();

        // FAB
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Proceed with creation of new AssociatedUser
                Intent intent = new Intent(UsersListActivity.this, UsersEditorActivity.class);
                intent.putExtra(ACTIVITY_REQUEST_TYPE, NEW_USER);
                intent.putExtra(PASSPHRASE, passphrase);
                startActivity(intent);
            }
        });


        // Hide the FAB if the request type is PICK_USER
        if (activityRequestType.equals(PICK_USER)){
            fab.setVisibility(View.GONE);
        }
    }



    /** Edit the Toolbars */
    private void initToolbars() {
        // Toolbar TOP
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(toolbarTop);

        if (activityRequestType.equals(EDIT_USERS))
            getSupportActionBar().setTitle(R.string.users);
        else if (activityRequestType.equals(PICK_USER))
            getSupportActionBar().setTitle(R.string.pick_user);
    }



    /**
     * Method called by the RecyclerViewAdapter when a user is selected.
     * Is called either if the user want to pick an AssociatedUser or edit it.
     *
     * @param user the selected user.
     */
    public void userSelected(AssociatedUser user){
        if (activityRequestType.equals(PICK_USER)) {
            // User selected, return to the calling Activity.
            Intent returnIntent = new Intent();
            returnIntent.putExtra(SELECTED_USER, user);
            returnIntent.putExtra(PASSPHRASE, passphrase);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } else {
            // Edit the selected user
            Intent intent = new Intent(UsersListActivity.this, UsersEditorActivity.class);
            intent.putExtra(ACTIVITY_REQUEST_TYPE, EDIT_USERS);
            intent.putExtra(SELECTED_USER, user);
            intent.putExtra(PASSPHRASE, passphrase);
            startActivity(intent);
        }
    }



    /**
     * A Database operation has finished.
     *
     * @param result success or failure of database operation.
     */
    public void onDBResponse(boolean result){
        if (result){
            // DB Operation OK
            // Do nothing
        } else {
            // DB Operation Error
            Toast.makeText(getApplicationContext(), R.string.error_wrong_passphrase, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * A Database operation has finished.
     *
     * @param result the ArrayList of associated users.
     */
    public void onDBResponse(ArrayList<AssociatedUser> result){
        if (result != null){
            usersFragment.updateLayout(result);
        }
    }
}