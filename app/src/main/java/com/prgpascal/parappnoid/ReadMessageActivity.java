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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.prgpascal.parappnoid.utils.AssociatedUser;
import com.prgpascal.parappnoid.utils.DBUtils;
import com.prgpascal.parappnoid.utils.OneTimePad;
import com.prgpascal.parappnoid.utils.MyUtils;
import static com.prgpascal.parappnoid.utils.Constants.PASSPHRASE;
import static com.prgpascal.parappnoid.utils.Constants.EncryptionDecryptionConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity instantiated when a message arrived and must be decrypted.
 */
public class ReadMessageActivity extends AppCompatActivity implements DBUtils.DBResponseListener {
    private String hexCiphertext;               // Original incoming message (HEX String).
    private String plaintext;                   // The plaintext
    private TextView contentTextView;           // TextView for message content.
    private AssociatedUser selectedUser;        // The selected AssociatedUser.
    private char[] passphrase;                  // Passphrase inserted by the user.
    private DBUtils dbUtils;                    // Object used for DB operations.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the DBUtils object
        dbUtils = DBUtils.getNewInstance(ReadMessageActivity.this);

        // Check the Intent Action.
        // It must be an ACTION_VIEW.
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {

            // Get the HEX ciphertext.
            // discard the prefix...
            hexCiphertext = intent.getDataString().substring(MESSAGE_URI_PREFIX.length());

            // Pick the AssociatedUser to be used.
            intent = new Intent(ReadMessageActivity.this, UsersListActivity.class);
            intent.putExtra(ACTIVITY_REQUEST_TYPE, PICK_USER);
            startActivityForResult(intent, PICK_USER_REQUEST_CODE);

        } else {
            // Wrong Intent Action!
            // Show an error message and finish the activity.
            Toast.makeText(getApplicationContext(), R.string.error_intent_action, Toast.LENGTH_SHORT).show();
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

                // At this point we have the HEX ciphertext and the right AssociatedUser.
                // We also have all the passphrase to access the DB.
                // Proceed creating the layout.
                createLayout();

                // Let's try the decryption.
                plaintext = tryDecryption(hexCiphertext);

            } else {
                // Error during the selection of AssociatedUser.
                // Show an error message and finish the Activity.
                Toast.makeText(getApplicationContext(), R.string.error_user_selection, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    /** Create the layout */
    private void createLayout(){
        // Set the layout
        setContentView(R.layout.read_message);

        // Toolbars
        initToolbars();

        // Message content TextView
        contentTextView = (TextView)findViewById(R.id.messageContent);
    }



    /** Edit the Toolbars */
    private void initToolbars() {
        // Toolbar TOP
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setTitle(selectedUser.getUsername());

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
        toolbarBottom.inflateMenu(R.menu.read_message_bottom_items);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.reply:
                        // Want to write a reply to this user.
                        // Pass as parameters the selected user and the passphrase.
                        // So it's not needed to request them to the user again.
                        Intent intent = new Intent(ReadMessageActivity.this, WriteMessageActivity.class);
                        intent.putExtra(ACTIVITY_REQUEST_TYPE, REPLY_MESSAGE);
                        intent.putExtra(SELECTED_USER, selectedUser);
                        intent.putExtra(PASSPHRASE, passphrase);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });
    }



    /**
     * Check if the message length is correct.
     *
     * If the length of the message is different from expected, false is returned.
     * Expected length = (PAD_ID_LENGTH + PADS_LENGTH) * 2.
     * The message is encoded in HEX (i.e. 2 characters per byte).
     *
     * @param message the HEX ciphertext input message.
     * @return true or false if the message length is correct or not.
     */
    private boolean checkMessageLength(String message){
        if (message.length() != (PAD_ID_LENGTH + PADS_LENGTH)*2){
            return false;
        }
        return true;
    }



    /**
     * Try the decryption of the message.
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
     * @param hexCiphertext the message to be decrypted.
     */
    private String tryDecryption(String hexCiphertext) {
        final int PAD_ID = 0;
        final int C = 1;
        final int MAC = 2;

        // Check the message length.
        if (!checkMessageLength(hexCiphertext)){
            // Message length is wrong.
            // Show an error message and return null.
            Toast.makeText(getApplicationContext(), R.string.error_message_length, Toast.LENGTH_SHORT).show();
            return null;
        }

        // Get the keys of the selected user.
        HashMap<Integer, OneTimePad> keys = selectedUser.getDecryptionKeys();

        // Check if there are available keys...
        if (keys.size() < 1) {
            // No keys available.
            // Show an error message and return null.
            Toast.makeText(getApplicationContext(), R.string.error_no_keys, Toast.LENGTH_SHORT).show();
            return null;
        }

        // Split the message
        String[] message = MyUtils.splitMessage(MyUtils.encodeISO88591(hexCiphertext));

        // Pad ID
        int padID = MyUtils.encodeInt(message[PAD_ID]);

        // Get the correct key
        OneTimePad key = keys.get(padID);
        if (key == null) {
            // Key not available
            Toast.makeText(getApplicationContext(), R.string.error_wrong_key, Toast.LENGTH_SHORT).show();
            return null;
        }

        // OneTimePad keys
        String Kp = key.getKeyForPlaintext();
        String Km = key.getKeyForMac();

        // Check the MAC
        String calculatedMac = MyUtils.calculateMac(Km + message[PAD_ID] + message[C]);
        if (message[MAC].equals(calculatedMac)) {
            // MAC is valid
            String p = MyUtils.encryptOrDecrypt(message[C], Kp);

            // Plaintext with padding
            String isoPlaintextLength = p.charAt(p.length()-1) + "";
            int plaintextLength = MyUtils.encodeInt(isoPlaintextLength);

            String plaintext = p.substring(0, plaintextLength);

            // Important!
            // Delete the keys
            keys.remove(padID);
            dbUtils.deleteOtp(selectedUser.getUserID(), "D", padID, passphrase);

            return plaintext;

        } else {
            // MAC is NOT valid
            Toast.makeText(getApplicationContext(), R.string.error_mac, Toast.LENGTH_SHORT).show();
            return null;
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
            // Show the decrypted message (or error) to the user.
            contentTextView.append(plaintext);

        } else {
            // DB Operation Error
            contentTextView.append(getResources().getString(R.string.error));
        }
    }

    /**
     * A Database operation has finished.
     *
     * @param result the ArrayList of associated users.
     */
    public void onDBResponse(ArrayList<AssociatedUser> result){}
}