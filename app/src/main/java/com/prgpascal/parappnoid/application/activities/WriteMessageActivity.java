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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.utils.AssociatedUser;
import com.prgpascal.parappnoid.utils.DBUtils;
import com.prgpascal.parappnoid.utils.OneTimePad;
import com.prgpascal.parappnoid.utils.MyUtils;

import static com.prgpascal.parappnoid.utils.Constants.EncryptionDecryptionConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.PASSPHRASE;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity that allows the user to write and send a new encrypted message to a specified AssociatedUser.
 */
public class WriteMessageActivity extends AppCompatActivity implements DBUtils.DBResponseListener {
    private EditText contentEditText;           // The EditText for the message content.
    private TextView contentLengthCounter;      // TextView that shows the length of message.
    private AssociatedUser selectedUser;        // The selected AssociatedUser.
    public String activityRequestType;          // The type of request for this activity.
    private char[] passphrase = null;           // Passphrase inserted by the user
    private DBUtils dbUtils;                    // Object used for DB operations
    private String hexCiphertext;               // HEX version of the ciphertext.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the DBUtils object
        dbUtils = DBUtils.getNewInstance(WriteMessageActivity.this);

        // Check and get all the Intent parameters.
        // If a parameter is missing, show an error and finish the activity.
        Intent intent = getIntent();
        if (intent.hasExtra(ACTIVITY_REQUEST_TYPE)) {

            // Parameters OK, read them all!
            activityRequestType = intent.getStringExtra(ACTIVITY_REQUEST_TYPE);

            if (activityRequestType.equals(REPLY_MESSAGE)) {

                // The ActivityRequestType is "Reply message"
                // So read all the data necessary for creating the reply.
                if (intent.hasExtra(SELECTED_USER) &&
                        (intent.hasExtra(PASSPHRASE))) {

                    // Parameters OK, read them all!
                    selectedUser = intent.getParcelableExtra(SELECTED_USER);
                    passphrase = intent.getCharArrayExtra(PASSPHRASE);

                    // At this point the right AssociatedUser to be used is selected.
                    // Proceed with layout creation.
                    createLayout();

                } else {
                    // One or more parameters are missing!
                    // Show an error message and finish the activity
                    Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else if (activityRequestType.equals(NEW_MESSAGE)) {
                // The ActivityRequestType is "NEW message".
                // Pick the correct AssociatedUser to be used.
                intent = new Intent(WriteMessageActivity.this, UsersListActivity.class);
                intent.putExtra(ACTIVITY_REQUEST_TYPE, PICK_USER);
                startActivityForResult(intent, PICK_USER_REQUEST_CODE);

            } else {
                // Activity request type not available.
                // Show an error message and finish the activity
                Toast.makeText(getApplicationContext(), R.string.error_wrong_request_type, Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {
            // One or more parameters are missing!
            // Show an error message and finish the activity
            Toast.makeText(getApplicationContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_USER_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Read the associated user and passphrase
                selectedUser = data.getParcelableExtra(SELECTED_USER);
                passphrase = data.getCharArrayExtra(PASSPHRASE);

                // At this point the correct AssociatedUser to be used is selected.
                // Proceed with layout creation.
                createLayout();

            } else {
                // Error during the right AssociatedUser choice.
                // Show an error message and finish the Activity.
                Toast.makeText(getApplicationContext(), R.string.error_user_selection, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    /** Create the layout */
    private void createLayout() {
        // Set the layout
        setContentView(R.layout.write_message);

        // Toolbars
        initToolbars();

        // Content length Counter
        contentLengthCounter = (TextView) findViewById(R.id.messageLengthCounter);

        // Message content EditText
        contentEditText = (EditText) findViewById(R.id.messageContent);
        contentEditText.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if (s.length() > PLAINTEXT_LENGTH-1){
                    contentLengthCounter.setTextColor(ContextCompat.getColor(WriteMessageActivity.this, R.color.red));
                } else {
                    contentLengthCounter.setTextColor(ContextCompat.getColor(WriteMessageActivity.this, R.color.black));
                }

                // Update the characters count
                contentLengthCounter.setText(s.length() + " / " + (PLAINTEXT_LENGTH-1));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    }



    /** Edit the Toolbars */
    private void initToolbars() {
        // Toolbar TOP
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setTitle(selectedUser.getUsername());
        getSupportActionBar().setSubtitle(
                getResources().getString(R.string.keys_available) +
                selectedUser.getEncryptionKeys().size());

        // Avatar
        Drawable avatar = ContextCompat.getDrawable(getApplicationContext(), selectedUser.getAvatar());
        toolbarTop.setLogo(avatar);
        for (int i = 0; i < toolbarTop.getChildCount(); i++) {
            View child = toolbarTop.getChildAt(i);
            if (child != null)
                if (child.getClass() == ImageView.class) {
                    ImageView iv2 = (ImageView) child;
                    if ( iv2.getDrawable() == avatar ) {
                        iv2.setAdjustViewBounds(true);
                        iv2.setPadding(15, 15, 15, 15);
                    }
                }
        }


        // Toolbar BOTTOM
        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.bottomToolbar);
        toolbarBottom.inflateMenu(R.menu.write_message_bottom_items);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.send:
                        // Send button clicked
                        // Try the encryption of plaintext if it's valid.
                        String plaintext = contentEditText.getText().toString();
                        if (isValid(plaintext)) {
                            hexCiphertext = tryEncryption(plaintext, selectedUser);

                        } else {
                            // Message is not valid!
                            // Show an error message
                            Toast.makeText(getApplicationContext(), R.string.error_message_invalid, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });
    }



    /**
     * Check if the plaintext is valid.
     * (i.e. not null, doesn't start with " " and its length is < than the maximum available)
     *
     * @param plaintext the plaintext to be evaluated.
     * @return boolean value representing if it is valid or not.
     */
    private boolean isValid(String plaintext){
        return ( MyUtils.isValid(plaintext) && (plaintext.length() < PLAINTEXT_LENGTH) );
    }



    /**
     * Try the encryption of the plaintext message.
     *
     *  padID                   =  identifier of the OneTimePad key used.
     *  plaintext               =  the user plaintext to be sent.
     *  plaintextWithPadding    =  plaintext + some random padding bytes.
     *  plaintextLength         =  number of bytes of the plaintext.
     *  Kp                      =  OTP key used for the plaintext encryption.
     *  Km                      =  OTP key used for the MAC calculation.
     *  P                       =  Temporary part.
     *  C                       =  ciphertext.
     *  MAC                     =  Message authentication code.
     *  HEX(...)                =  HEX string representation.
     *
     *  P =                  (plaintext || padding || plaintextLength)      [101 bytes]
     *  C =                  (P XOR Kp)                                     [101 bytes]
     *  MAC =                SHA-256(Km || padID || C)                      [32 bytes]
     *  Message =            (padID || C || MAC)                            [135 bytes]
     *  HEX Message =        HEX(Message)                                   [270 bytes]
     *
     * @param plaintext the message to be encrypted.
     * @param selectedUser the recipient of the message.
     */
    private String tryEncryption(String plaintext, AssociatedUser selectedUser) {

        // Get the keys of the selected user.
        HashMap<Integer, OneTimePad> keys = selectedUser.getEncryptionKeys();

        // Check if there are available keys...
        if (keys.size() < 1) {
            // No keys available.
            // Show an error message and return null.
            Toast.makeText(getApplicationContext(), R.string.error_no_keys, Toast.LENGTH_SHORT).show();
            return null;
        }


        // Get the first available key
        Map.Entry<Integer, OneTimePad> entry = keys.entrySet().iterator().next();
        OneTimePad key = entry.getValue();

        // OneTimePad keys
        String Kp = key.getKeyForPlaintext();
        String Km = key.getKeyForMac();

        // PAD ID
        int padID = entry.getKey();
        String isoPadID = MyUtils.encodeISO88591(padID, 2);

        // Plaintext Length
        int plaintextLength = plaintext.length();
        String isoPlaintextLength = MyUtils.encodeISO88591(plaintextLength, 1);

        // PlaintextPadding
        String padding = MyUtils.createRandomString(PLAINTEXT_LENGTH - 1 - plaintextLength);

        // P
        String p = plaintext + padding + isoPlaintextLength;

        // C
        String c = MyUtils.encryptOrDecrypt(p, Kp);

        // MAC
        String mac = MyUtils.calculateMac(Km + isoPadID + c);

        // Message
        String message = isoPadID + c + mac;

        // HEX Message
        String hexMessage = MyUtils.encodeHEX(message);


        // Important!
        // The key must be deleted BEFORE the message delivery.
        // If the deletion fails, return null and the message will not be send.
        keys.remove(padID);
        dbUtils.deleteOtp(selectedUser.getUserID(), "E", padID, passphrase);

        return hexMessage;
    }



    /**
     * A Database operation has finished.
     *
     * @param result success or failure of database operation.
     */
    public void onDBResponse(boolean result){
        if (result) {
            // DB Operation OK
            if (hexCiphertext != null) {
                // Message correctly encrypted!
                // Re-create the layout, because the key used has been deleted
                createLayout();
                // The message is in the HEX format, send it.
                Intent intent = new Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, MESSAGE_URI_PREFIX + hexCiphertext)
                        .setType("text/plain");
                Intent openInChooser = Intent.createChooser(intent, getResources().getString(R.string.send));
                startActivity(openInChooser);

            } else {
                // DB Operation Error
                Toast.makeText(getApplicationContext(), R.string.error_encryption, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * A Database operation has finished.
     *
     * @param result the ArrayList of associated users.
     */
    public void onDBResponse(ArrayList<AssociatedUser> result){}
}
