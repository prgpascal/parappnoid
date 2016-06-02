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

package com.prgpascal.parappnoid.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.prgpascal.parappnoid.utils.Constants.DatabaseConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.*;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.model.AssociatedUser;
import com.prgpascal.parappnoid.application.model.OneTimePad;

/**
 * Class that performs all the DB operations.
 * The constructor is not public.
 * To instantiate it, use getNewInstance with an Activity context parameter.
 */
public class DBUtils {
    private Context context;                // The Activity context
    private String dbName;                  // The Database name
    private int iterations;                 // The number of iterations for the KDF implemented by SQLCipher
    private ProgressDialog progressDialog;  // ProgressDialog used during operations


    private String CREATE_USERS_TABLE =
            "create table if not exists " +
                    TABLE_USERS + " (" +
                    KEY_USER_ID + " text, " +
                    KEY_USERNAME + " text not null , " +
                    KEY_AVATAR + " integer, " +
                    "PRIMARY KEY (" + KEY_USER_ID + "));";

    private String CREATE_ENCRYPTION_KEYS_TABLE =
            "create table if not exists " +
                    TABLE_ENCRYPTION_KEYS + " (" +
                    KEY_USER_ID + " text, " +
                    KEY_PAD_ID + " integer, " +
                    KEY_KEY_FOR_PLAINTEXT + " text not null , " +
                    KEY_KEY_FOR_MAC + " text not null , " +
                    "FOREIGN KEY (" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE , " +
                    "PRIMARY KEY (" + KEY_USER_ID + " , " + KEY_PAD_ID + "));";

    private String CREATE_DECRYPTION_KEYS_TABLE =
            "create table if not exists " +
                    TABLE_DECRYPTION_KEYS + " (" +
                    KEY_USER_ID + " text, " +
                    KEY_PAD_ID + " integer, " +
                    KEY_KEY_FOR_PLAINTEXT + " text not null , " +
                    KEY_KEY_FOR_MAC + " text not null , " +
                    "FOREIGN KEY (" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE , " +
                    "PRIMARY KEY (" + KEY_USER_ID + " , " + KEY_PAD_ID + "));";


    /**
     * Show or stop the ProgressDialog.
     *
     * @param show true if the start the ProgressDialog, false to stop it.
     */
    public void showProgressDialog(boolean show) {
        if (show) {
            // Show the ProgressDialog
            progressDialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.please_wait) , true);
            progressDialog.setCancelable(false);

        } else {
            try {
                // Stop showing ProgressDialog
                progressDialog.dismiss();
            } catch (NullPointerException e) {}
        }
    }



    /** Interface implemented by the activities that perform DB operations */
    public interface DBResponseListener {
        void onDBResponse(boolean result);
        void onDBResponse(ArrayList<AssociatedUser> result);
    }



    /** Static constructor */
    public static DBUtils getNewInstance(Context context){
        // Read dbName and iterations from the SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
        String dbName = prefs.getString(DB_NAME, DEFAULT_DB_NAME);
        int iterations = prefs.getInt(ITERATIONS, DEFAULT_ITERATIONS);

        return new DBUtils(context, dbName, iterations);
    }



    /** Private Constructor */
    private DBUtils(Context context, String dbName, int iterations){
        this.context = context;
        this.dbName = dbName;
        this.iterations = iterations;
    }



    /**
     * Open the database.
     * If it not exists, this method will create it.
     *
     * @param passphrase the passphrase for the Database.
     * @return opened SQLite Database instance.
     */
    private SQLiteDatabase openDatabase(char[] passphrase) {

        // Set the number of iterations for the KDF
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            public void postKey(SQLiteDatabase database) {
                database.rawExecSQL("PRAGMA kdf_iter = " + iterations);
                database.rawExecSQL("PRAGMA foreign_keys=ON");
            }

            public void preKey(SQLiteDatabase database) {
            }
        };

        // Open the SQLite Database
        SQLiteDatabase.loadLibs(context);
        String dbPath = context.getDatabasePath(dbName).getPath();
        File dbPathFile = new File(dbPath);
        if (!dbPathFile.exists())
            dbPathFile.getParentFile().mkdirs();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, passphrase, null, hook);

        // Create the tables if not exist
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ENCRYPTION_KEYS_TABLE);
        db.execSQL(CREATE_DECRYPTION_KEYS_TABLE);

        return db;
    }



    /**
     * Insert the AssociatedUser and generated One-Time Pads into DB.
     *
     * @param user the AssociatedUser.
     * @param keys the HashMap with OtpKeys objects.
     * @param passphrase the passphrase for the Database.
     * @return boolean value representing the success of operation.
     */
    public void saveAssociatedUser(final AssociatedUser user, final HashMap<Integer, OneTimePad> keys, final char[] passphrase) {
        showProgressDialog(true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Open the Database
                SQLiteDatabase db = openDatabase(passphrase);

                // Get an available UserID
                String userID;
                do {
                    // Generate new UserID
                    userID = MyUtils.encodeHEX(MyUtils.createRandomString(5));

                    // Check if it already exists in DB
                    String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_USER_ID + "='" + userID+"'";
                    Cursor cursor = db.rawQuery(query, null);

                    if(cursor.getCount() > 0){
                        // The generated UserID already exists in DB.
                        // reset it to null.
                        userID = null;
                    }

                    cursor.close();

                } while (userID == null);


                // Save the AssociatedUser into DB
                ContentValues newValues = new ContentValues();
                newValues.put(KEY_USER_ID, userID);
                newValues.put(KEY_USERNAME, user.getUsername());
                newValues.put(KEY_AVATAR, user.getAvatar());
                db.insert(TABLE_USERS, null, newValues);


                // Save the One-Time Pad keys into DB
                for (Map.Entry<Integer, OneTimePad> entry : keys.entrySet()) {
                    OneTimePad value = entry.getValue();

                    newValues = new ContentValues();
                    newValues.put(KEY_USER_ID, userID);
                    newValues.put(KEY_PAD_ID, entry.getKey());
                    newValues.put(KEY_KEY_FOR_PLAINTEXT, value.getKeyForPlaintext());
                    newValues.put(KEY_KEY_FOR_MAC, value.getKeyForMac());

                    if (value.getPurpose().equals("E")){
                        // This is an encryption key
                        db.insert(TABLE_ENCRYPTION_KEYS, null, newValues);
                    } else {
                        // This is a decryption key
                        db.insert(TABLE_DECRYPTION_KEYS, null, newValues);
                    }
                }

                db.close();

                // Send response to the Activity
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(false);
                        ((DBResponseListener) context).onDBResponse(true);
                    }
                });
            }
        }).start();
    }



    /**
     * Read from DB all the Associated Users and One-Time Pad keys.
     *
     * @param passphrase the passphrase for the Database.
     * @return ArrayList of AssociatedUser objects.
     */
    public void loadAssociatedUsers(final char[] passphrase){
        showProgressDialog(true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Open the Database
                // If an exception is catch here, the passphrase is wrong.
                SQLiteDatabase db;
                try {
                    db = openDatabase(passphrase);

                } catch (SQLiteException e) {
                    e.printStackTrace();

                    // Send response to the Activity
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog(false);
                            ((DBResponseListener) context).onDBResponse(false);
                        }
                    });
                    return;
                }

                // Get the Associated Users
                final ArrayList<AssociatedUser> usersList = new ArrayList<>();
                String query = "SELECT * FROM " + TABLE_USERS;
                Cursor cursor = db.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    usersList.add(new AssociatedUser(
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AVATAR))));
                }


                // Get the keys for each AssociatedUser
                for (AssociatedUser user : usersList) {
                    try {

                        // Encryption keys
                        query = "SELECT * FROM " + TABLE_ENCRYPTION_KEYS + " WHERE " + KEY_USER_ID + "='" + user.getUserID() +"'";
                        cursor = db.rawQuery(query, null);

                        while (cursor.moveToNext()) {
                            OneTimePad key = new OneTimePad(
                                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEY_FOR_PLAINTEXT)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEY_FOR_MAC)),
                                    "E");

                            user.addEncryptionKey(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PAD_ID)), key);
                        }


                        // Decryption keys
                        query = "SELECT * FROM " + TABLE_DECRYPTION_KEYS + " WHERE " + KEY_USER_ID + "='" + user.getUserID() +"'";
                        cursor = db.rawQuery(query, null);

                        while (cursor.moveToNext()) {
                            OneTimePad key = new OneTimePad(
                                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEY_FOR_PLAINTEXT)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEY_FOR_MAC)),
                                    "D");

                            user.addDecryptionKey(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PAD_ID)), key);
                        }

                    } catch (SQLiteException e) {
                        // No Keys for the user are available
                        e.printStackTrace();
                    }
                }

                cursor.close();
                db.close();

                // Send response to the Activity
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(false);
                        ((DBResponseListener) context).onDBResponse(usersList);
                    }
                });
            }
        }).start();
    }



    /**
     * Delete a single One-Time Pad key from database.
     *
     * @param userID the unique user ID.
     * @param keyType the type of key ("E" or "D").
     * @param padID the ID of the One-Time Pad to be deleted.
     * @param passphrase the passphrase for the Database.
     * @return boolean value representing the success of operation.
     */
    public void deleteOtp(final String userID, final String keyType, final int padID, final char[] passphrase){
        showProgressDialog(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Open the Database
                SQLiteDatabase db = openDatabase(passphrase);

                // Delete the selected OTP key
                try {
                    String table = null;
                    if (keyType.equals("E"))
                        table = TABLE_ENCRYPTION_KEYS;
                    else if (keyType.equals("D"))
                        table = TABLE_DECRYPTION_KEYS;

                    String where = KEY_PAD_ID + "='" + (padID) + "' AND " + KEY_USER_ID + "='" + userID + "'";
                    db.delete(table, where, null);

                    // Send response to the Activity
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog(false);
                            ((DBResponseListener) context).onDBResponse(true);
                        }
                    });
                    return;

                } catch (SQLiteException e) {
                    // Error during deletion
                    e.printStackTrace();
                } finally {
                    db.close();
                }

                // Send response to the Activity
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(false);
                        ((DBResponseListener) context).onDBResponse(false);
                    }
                });
            }
        }).start();
    }



    /**
     * Delete an Associated User from database.
     * This method will also remove all the One-Time Pad keys of that user.
     *
     * @param user the user to be deleted.
     * @param passphrase the passphrase for the Database.
     * @return boolean value representing the success of operation.
     */
    public void deleteUser(final AssociatedUser user, final char[] passphrase){
        showProgressDialog(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Open the Database
                SQLiteDatabase db = openDatabase(passphrase);

                try {
                    // Delete the AssociatedUser from DB
                    String where = KEY_USER_ID + "='" + (user.getUserID()+"'");
                    db.delete(TABLE_USERS, where, null);

                    // Because of "ON DELETE CASCADE" the one-time pad keys associated
                    // with this user will be deleted too.

                    // Send response to the Activity
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog(false);
                            ((DBResponseListener) context).onDBResponse(true);
                        }
                    });
                    return;

                } catch (SQLiteException e) {
                    // An error occured
                    e.printStackTrace();
                } finally {
                    db.close();
                }

                // Send response to the Activity
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(false);
                        ((DBResponseListener) context).onDBResponse(false);
                    }
                });
            }
        }).start();
    }



    /**
     * Update an AssciatedUser inside the DB.
     *
     * @param user the user to be updated.
     * @param passphrase the passphrase for the Database
     */
    public void updateUser(final AssociatedUser user, final char[] passphrase) {
        showProgressDialog(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Open the Database
                SQLiteDatabase db = openDatabase(passphrase);

                try {
                    // Update the user
                    String where = KEY_USER_ID + "=" + "'" + user.getUserID() + "'";

                    ContentValues newValues = new ContentValues();
                    newValues.put(KEY_USERNAME, user.getUsername());
                    newValues.put(KEY_AVATAR, user.getAvatar());

                    db.update(TABLE_USERS, newValues, where, null);

                    // Send response to the Activity
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog(false);
                            ((DBResponseListener) context).onDBResponse(true);
                        }
                    });
                    return;

                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    db.close();
                }

                // Send response to the Activity
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(false);
                        ((DBResponseListener) context).onDBResponse(false);
                    }
                });
            }
        }).start();
    }



    /**
     * Edit the DB settings.
     *
     * @param oldPassphrase the actual passphrase for the DB.
     * @param newPassphrase the passphrase for the new DB.
     * @param newIterations the number of iterations for the new DB.
     * @return boolean value representing the success of operation.
     */
    public void editDBSettings(final char[] oldPassphrase, final char[] newPassphrase, final int newIterations){
        showProgressDialog(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Open the OLD Database
                // If an exception is catched here, the passphrase is wrong.
                SQLiteDatabase db;
                try {
                    db = openDatabase(oldPassphrase);

                } catch (SQLiteException e) {
                    e.printStackTrace();

                    // Send response to the Activity
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog(false);
                            ((DBResponseListener) context).onDBResponse(false);
                        }
                    });
                    return;
                }


                // Create the NEW database.
                // The name is 6 random HEX characters (3 bytes).
                String newDBName = MyUtils.encodeHEX(MyUtils.createRandomString(3)) + ".db";
                String newDBPath = context.getDatabasePath(newDBName).getPath();


                // Export OLD database to the new one.
                db.execSQL("ATTACH database ? AS newdb KEY ?", new Object[]{newDBPath, String.valueOf(newPassphrase)});
                db.rawExecSQL("PRAGMA newdb.kdf_iter = " + newIterations + ";");
                db.rawExecSQL("SELECT sqlcipher_export('newdb');");
                db.rawExecSQL("DETACH DATABASE newdb;");
                db.close();


                // Update the SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(DB_NAME, newDBName);
                editor.putInt(ITERATIONS, newIterations);
                editor.commit();

                // Delete the OLD database file
                File oldDB = context.getDatabasePath(dbName);
                if (oldDB.exists()) {
                    oldDB.delete();
                }

                // Update this instance values
                dbName = newDBName;
                iterations = newIterations;

                // Send response to the Activity
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(false);
                        ((DBResponseListener) context).onDBResponse(true);
                    }
                });
            }

        }).start();
    }

}