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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.prgpascal.parappnoid.utils.*;
import com.prgpascal.parappnoid.utils.MyAlertDialogs.*;
import com.prgpascal.qrdatatransfer.TransferActivity;
import java.util.ArrayList;
import java.util.HashMap;

import static com.prgpascal.parappnoid.utils.Constants.DEBUG_TAG;
import static com.prgpascal.parappnoid.utils.Constants.EncryptionDecryptionConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.KeyExchangeConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.PASSPHRASE;
import static com.prgpascal.parappnoid.utils.Constants.TAG_DIALOG;

/**
 * Activity that allows the creation of new AssociatedUsers or the modification of an existing one.
 */
public class UsersEditorActivity extends AppCompatActivity implements MyAlertDialogInterface, DBUtils.DBResponseListener {
    private HashMap<Integer, OneTimePad> keys;      // Array of generated keys of the specified user
    private ArrayList<String> groupsOfKeys;         // Generated keys grouped in Strings and stored inside an ArrayList

    private EditText usernameEditText;              // EditText for username input
    private ImageView avatarImageView;              // ImageView for Avatar selection
    private EditText numberOfKeysEditText;          // EditText for the number of keys selection
    private EditText keysPerQREditText;             // EditText for the number of keys per QR code selection
    private RadioGroup radioGroup;                  // RadioGroup for the number generator type selection

    private int selectedGenerator = R.id.radioTRNG; // The default generator is the TRNG
    private AssociatedUser userToEdit;              // The AssociatedUser to be edited or created.

    private AlertDialog avatarDialog;                           // AlertDialog for the Avatar selection
    private final int DEFAULT_AVATAR = R.drawable.avatar0;      // Default Avatar resource image

    public String activityRequestType;              // The type of request for this activity: new user or edit an existing one
    private char[] passphrase;                      // Passphrase inserted by the user

    private DBUtils dbUtils;                                // Object used for DB operations
    private int dbRequest;                                  // Request tags for the DB operations
    private static final int DB_REQUEST_SAVE_USER = 1;      //...
    private static final int DB_REQUEST_UPDATE_USER = 2;    //...
    private static final int DB_REQUEST_DELETE_USER = 3;    //...

    // Dialogs request codes
    private static final int DIALOG_TYPE_CONFIRM_DELETE = 111;
    private static final int DIALOG_TYPE_AVATAR_PICKER = 333;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the DBUtils object
        dbUtils = DBUtils.getNewInstance(UsersEditorActivity.this);

        // Check and get all the Intent parameters.
        // If a parameter is missing, show an error and finish the Activity.
        Intent intent = getIntent();
        if (intent.hasExtra(ACTIVITY_REQUEST_TYPE) &&
            (intent.hasExtra(PASSPHRASE))) {

            // Parameters OK, read them all!
            activityRequestType = intent.getStringExtra(ACTIVITY_REQUEST_TYPE);
            passphrase = intent.getCharArrayExtra(PASSPHRASE);

            if (activityRequestType.equals(NEW_USER)) {
                // A new user must be created.
                userToEdit = new AssociatedUser(null, null, DEFAULT_AVATAR);

            } else if (activityRequestType.equals(EDIT_USERS)) {
                // Edit an existing AssociatedUser.
                if (intent.hasExtra(SELECTED_USER)) {
                    userToEdit = getIntent().getParcelableExtra(SELECTED_USER);

                } else {
                    // Param missing!
                    Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            // Create the layout
            createLayout();

        } else {
            // One or more parameters are missing!
            // Show an error message and finish the activity.
            Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    /** Create the layout */
    private void createLayout() {
        // Set the layout
        setContentView(R.layout.users_editor);

        // Toolbars
        initToolbars();

        // Username EditText
        usernameEditText = (EditText)findViewById(R.id.username);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Update the AssociatedUser username.
                userToEdit.setUsername(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        // Avatar ImageView
        avatarImageView = (ImageView)findViewById(R.id.avatar);
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Avatar Picker Dialog
                showNewDialog(DIALOG_TYPE_AVATAR_PICKER);
            }
        });


        // Number generator RadioGroup
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // Update the selected key generator
                selectedGenerator = i;
            }
        });


        // Keys EditTexts
        numberOfKeysEditText = (EditText)findViewById(R.id.numberOfKeys);
        keysPerQREditText = (EditText)findViewById(R.id.keysPerQR);


        // Create and send keys (Server) button
        Button sendKeysButton = (Button)findViewById(R.id.sendKeys);
        sendKeysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.isValid(userToEdit.getUsername()) &&
                    MyUtils.isValid(numberOfKeysEditText.getText().toString()) &&
                    MyUtils.isValid(keysPerQREditText.getText().toString())) {

                    int numberOfKeys = Integer.valueOf(numberOfKeysEditText.getText().toString());
                    int keysPerQR = Integer.valueOf(String.valueOf(keysPerQREditText.getText().toString()));

                    if ((numberOfKeys > 0) && (keysPerQR > 0)) {
                        // Generate the keys
                        if (selectedGenerator == R.id.radioTRNG){
                            // TRNG selected (Read from external text file)
                            keys = new MyUtils().readOneTimePadsFromSD(numberOfKeys, PADS_LENGTH);

                        } else {
                            // Generate from CSPRNG (SecureRandom)
                            keys = new MyUtils().generateOneTimePads(numberOfKeys, PADS_LENGTH);
                        }

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
                    } else {
                        // Error: numbers must be > 0
                        Toast.makeText(UsersEditorActivity.this, R.string.error_negative_num, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Error: the username is not valid
                    Toast.makeText(UsersEditorActivity.this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Read keys (Client) button
        Button readKeysButton = (Button) findViewById(R.id.readKeys);
        readKeysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.isValid(userToEdit.getUsername())) {

                    // Set up the QR TransferActivity
                    Intent intent = new Intent(UsersEditorActivity.this, TransferActivity.class);
                    intent.putExtra(I_AM_THE_SERVER, false);

                    // Start the activity for result
                    startActivityForResult(intent, KEY_EXCHANGE_REQUEST_CODE);

                } else {
                    // Error: the username is not valid
                    Toast.makeText(UsersEditorActivity.this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Edit the layout in case that a new user must be edited or a new one created
        if (activityRequestType.equals(EDIT_USERS)){
            // Toolbar title
            getSupportActionBar().setTitle(R.string.edit_user);

            // Update username EditText and avatar ImageView
            usernameEditText.setText(userToEdit.getUsername());
            avatarImageView.setImageResource(userToEdit.getAvatar());

            // Set some elements as invisible
            sendKeysButton.setVisibility(View.GONE);
            readKeysButton.setVisibility(View.GONE);
            findViewById(R.id.labelNumberOfKeys).setVisibility(View.GONE);
            findViewById(R.id.labelKeysPerQR).setVisibility(View.GONE);
            numberOfKeysEditText.setVisibility(View.GONE);
            keysPerQREditText.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);

        } else {
            // Toolbar title
            getSupportActionBar().setTitle(R.string.new_user);

            // Set the Toolbar bottom as invisible
            findViewById(R.id.bottomToolbar).setVisibility(View.GONE);
        }
    }



    /** Edit the Toolbars */
    private void initToolbars() {
        // Toolbar TOP
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(toolbarTop);

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
                    dbUtils.updateUser(userToEdit, passphrase);
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

                if (!data.getBooleanExtra(I_AM_THE_SERVER, false)){
                    // I'm the client, read the incoming messages.
                    // Decode the groups of keys and store them into "keys".
                    groupsOfKeys = data.getStringArrayListExtra(MESSAGES);
                    keys = MyUtils.decodeGroupsOfKeys(groupsOfKeys);
                }

                // At this point Server and Client have set the right AssociatedUser and exchanged the keys.
                // Proceed saving the AssociatedUser and keys into DB.
                dbRequest = DB_REQUEST_SAVE_USER;
                dbUtils.saveAssociatedUser(userToEdit, keys, passphrase);

                //TMP
                //Toast.makeText(getApplicationContext(), "COPIATO TUTTO IN DB", Toast.LENGTH_SHORT).show();
                //users = new MyUtils().loadAssociatedUsers(getApplicationContext());
                //MyUtils.printUsers(users, output);
                //TextView output = (TextView)findViewById(R.id.output);
                //MyUtils.printOtpKeys(keys, output);
                //TMP

            } else {
                // Error during keys exchange
                Toast.makeText(getApplicationContext(), R.string.error_keys_exchange, Toast.LENGTH_SHORT).show();
            }
        }
    }



    /** Show new dialog. */
    public void showNewDialog(int dialogType){
        switch (dialogType) {
            case DIALOG_TYPE_CONFIRM_DELETE:
                // Confirm delete Dialog.
                DialogFragment dialog = MyAlertDialogFragment.newInstance(
                        dialogType,
                        getResources().getString(R.string.delete_contact_req), null);
                dialog.show(getSupportFragmentManager(), TAG_DIALOG);
                break;

            case DIALOG_TYPE_AVATAR_PICKER:
                // Show the Avatar Picker Dialog.
                showAvatarPickerDialog();
                break;
        }
    }

    /** A positive button has been clicked */
    public void doPositiveClick(int dialogType, char[] result){
        switch (dialogType) {
            case DIALOG_TYPE_CONFIRM_DELETE:
                // Delete the AssociatedUser from DB.
                dbRequest = DB_REQUEST_DELETE_USER;
                dbUtils.deleteUser(userToEdit, passphrase);
                break;
        }
    }

    /** A negative button has been clicked */
    public void doNegativeClick(int dialogType, char[] result){
        // do nothing
    }



    /**
     * Show new Avatar Picker Dialog
     */
    private void showAvatarPickerDialog() {
        // Inflate the AlertDialog Layout
        View view = getLayoutInflater().inflate(R.layout.grid_layout, null);
        final GridView gridView = (GridView) view.findViewById(R.id.gridView);

        final AvatarImageAdapter adapter = new AvatarImageAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Avatar selected!
                // Update the AssociatedUser and the ImageView.
                // than dismiss the Dialog.
                userToEdit.setAvatar(adapter.getImage(position));
                avatarImageView.setImageResource(adapter.getImage(position));
                avatarDialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(R.string.pick_avatar);
        avatarDialog = builder.create();
        avatarDialog.show();
    }



    /**
     * A Database operation has finished.
     *
     * @param result success or failure of database operation.
     */
    public void onDBResponse(boolean result){
        if (result){
            // DB Operation OK
            Toast.makeText(getApplicationContext(), R.string.operation_ok, Toast.LENGTH_SHORT).show();
            switch (dbRequest){
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
    public void onDBResponse(ArrayList<AssociatedUser> result){}
}