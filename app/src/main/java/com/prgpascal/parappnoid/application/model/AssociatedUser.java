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

package com.prgpascal.parappnoid.application.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents an associated user (contact)
 *
 * It implements the Parcelable interface, so objects of this class can be transferred efficiently
 * between Activities.
 */
public class AssociatedUser implements Parcelable {
    private String userID;                                                  // UNIQUE string that represents the user
    private String username;                                                // uman-readable username
    private int avatarResourceImage;                                        // int image resource (ex. R.drawable.avatar1)
    private HashMap<Integer, OneTimePad> encryptionKeys = new HashMap<>();  // One-Time Pads used for encryption
    private HashMap<Integer, OneTimePad> decryptionKeys = new HashMap<>();  // One-Time Pads used for decryption


    /** Constructor */
    public AssociatedUser(String userID, String username, int avatarResourceImage){
        this.userID = userID;
        this.username = username;
        this.avatarResourceImage = avatarResourceImage;
    }


    /** Parcel constructor */
    public AssociatedUser(Parcel parcel) {
        this.userID = parcel.readString();
        this.username = parcel.readString();
        this.avatarResourceImage = parcel.readInt();

        int encryptionKeysSize = parcel.readInt();
        int decryptionKeysSize = parcel.readInt();

        for (int i=0; i<encryptionKeysSize; i++){
            int key = parcel.readInt();
            OneTimePad value = parcel.readParcelable(getClass().getClassLoader());
            encryptionKeys.put(key, value);
        }

        for (int i=0; i<decryptionKeysSize; i++){
            int key = parcel.readInt();
            OneTimePad value = parcel.readParcelable(getClass().getClassLoader());
            decryptionKeys.put(key, value);
        }
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(username);
        dest.writeInt(avatarResourceImage);

        dest.writeInt(encryptionKeys.size());
        dest.writeInt(decryptionKeys.size());

        for (Map.Entry<Integer,OneTimePad> entry : encryptionKeys.entrySet()) {
            dest.writeInt(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }

        for (Map.Entry<Integer,OneTimePad> entry : decryptionKeys.entrySet()) {
            dest.writeInt(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }


    /** Parcel creator */
    public final static Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public AssociatedUser createFromParcel(Parcel source) {
            return new AssociatedUser(source);
        }

        @Override
        public AssociatedUser[] newArray(int size) {
            return new AssociatedUser[size];
        }
    };



    /** Set methods */
    public void setUsername(String username){
        this.username = username;
    }

    public void setAvatar(int avatarResourceImage){
        this.avatarResourceImage = avatarResourceImage;
    }

    /** Append a OTP key to the encryption keys */
    public void addEncryptionKey(int padID, OneTimePad key){
        encryptionKeys.put(padID, key);
    }

    /** Append a OTP key to the decryption keys */
    public void addDecryptionKey(int padID, OneTimePad key){
        decryptionKeys.put(padID, key);
    }


    /** Get methods */
    public String getUserID(){
        return userID;
    }

    public String getUsername(){
        return username;
    }

    public int getAvatar(){
        return avatarResourceImage;
    }

    public HashMap<Integer, OneTimePad> getEncryptionKeys(){
        return encryptionKeys;
    }

    public HashMap<Integer, OneTimePad> getDecryptionKeys(){
        return decryptionKeys;
    }

}