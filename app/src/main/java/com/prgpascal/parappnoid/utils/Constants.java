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

/**
 * Class that provides constants used by more than an Activity.
 * I use a final class because it is considered a better approach instead of using interfaces.
 *
 * http://stackoverflow.com/questions/2659593/what-is-the-use-of-interface-constants
 * https://en.wikipedia.org/wiki/Constant_interface
 */
public final class Constants {

    /** Private constructor */
    private Constants() {
        // restrict instantiation
    }


    /** DATABASE CONSTANTS */
    public static final class DatabaseConstants {

        /**
         * Private constructor
         */
        private DatabaseConstants() {
            // restrict instantiation
        }

        /** SharedPreferences */
        public static final String ITERATIONS = "iterations";               // Number of iterations for the KDF
        public static final int DEFAULT_ITERATIONS = 64000;                 // Default number of iterations
        public static final String DB_NAME = "db_name";                     // Tag for database name
        public static final String DEFAULT_DB_NAME = "parappnoid_DB.db";    // Default database name

        /** USERS TABLE */
        public static final String TABLE_USERS = "Users";           // Table that contains all the users information
        public static final String KEY_USER_ID = "user_id";         // Unique identifier for the user
        public static final String KEY_USERNAME = "username";       // Username of the user
        public static final String KEY_AVATAR = "avatar";           // Selected avatar for the user

        /** KEYS TABLES */
        public static final String TABLE_ENCRYPTION_KEYS = "EncryptionKeys";    // Table that contains the encryption keys
        public static final String TABLE_DECRYPTION_KEYS = "DecryptionKeys";    // Table that contains the decryption keys
        public static final String KEY_PAD_ID = "pad_id";                       // Unique identifier for the one-time pad key
        public static final String KEY_KEY_FOR_PLAINTEXT = "key_for_plaintext"; // Part of key used for the plaintext encryption
        public static final String KEY_KEY_FOR_MAC = "key_for_mac";             // Part of key used for the MAC encryption

    }



    /** ENCRYPTION & DECRYPTION CONSTANTS */
    public static final class EncryptionDecryptionConstants {
        /**
         * Private constructor
         */
        private EncryptionDecryptionConstants() {
            // restrict instantiation
        }

        public static final int MAX_PAD_ID = 65536;              // Maximum PadID integer
        public static final int PAD_ID_LENGTH = 2;               // Number of bytes for the PadID
        public static final int PLAINTEXT_LENGTH = 100 + 1;      // Number of bytes for the plaintext (1 byte for the plaintext length)
        public static final int DIGEST_LENGTH = 32;              // Number of bytes (characters) for the digest
        public static final int PADS_LENGTH = 101 + 32;          // Total amount of bytes for the OTP key
        public static final int PURPOSE_LENGTH = 1;              // Number of bytes for the purpose tag
        public static final String MESSAGE_URI_PREFIX = "http://parappnoid.riccardoleschiutta.com/"; // Prefix used for message exchange
    }



    /** KEY EXCHANGE CONSTANTS */
    public static final class KeyExchangeConstants {
        /** Private constructor */
        private KeyExchangeConstants() {
            // restrict instantiation
        }

        public static final String I_AM_THE_SERVER = "i_am_the_server";     // If true the user is the server during transmission
        public static final String MESSAGES = "messages";                   // Name of keys parameter
        public static final int KEY_EXCHANGE_REQUEST_CODE = 5377;           // Request code

    }



    /** USER MANAGER CONSTANTS */
    public static final class UserManagerConstants {
        /** Private constructor */
        private UserManagerConstants() {
            // restrict instantiation
        }

        public static final String SELECTED_USER = "user";                          // Selected AssociatedUser
        public static final String ACTIVITY_REQUEST_TYPE = "type_of_action";        // Request type for an activity
        public static final String PICK_USER = "pick_user";                         // Request type: pick an AssociatedUser from users list
        public static final String EDIT_USERS = "edit_users";                       // Request type: edit and existing AssociatedUser
        public static final String NEW_USER = "new_user";                           // Request type: create a new AssociatedUser
        public static final String NEW_MESSAGE = "new_message";                     // Request type: create a new message
        public static final String REPLY_MESSAGE = "reply_message";                 // Request type: create a new reply message
        public static final int PICK_USER_REQUEST_CODE = 5007;                      // Request code
        public static final int DIALOG_TYPE_REQUEST_PASSPHRASE = 308;                 // Request code

    }



    // Other constants
    public static final String PREFERENCES = "parappnoid_my_prefs";         // Name of the SharedPreferences
    public static final String SHOW_NOTIFICATION = "show_notification";     // Preference: show notification or not
    public static final String PASSPHRASE = "passphrase";                   // Passphrase typed by the user
    public static final String TAG_DIALOG = "dialog";                       // General tag dialog
    public static final String DEBUG_TAG = "Parappnoid_debug";              // Debug tag

}