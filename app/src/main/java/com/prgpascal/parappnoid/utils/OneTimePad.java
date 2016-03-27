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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents a single One-Time key.
 * It contains the part used for the plaintext and the part for the MAC.
 * The purpose define if it must be used for encryption or decryption.
 */
public class OneTimePad implements Parcelable {
    private String keyForPlaintext;             // OTP key used for the plaintext encryption.
    private String keyForMac;                   // OTP key used for MAC calculation.
    private String purpose;                     // "E" = used for encryption / "D" = used for decryption

    /** Constructor */
    public OneTimePad(String keyForPlaintext, String keyForMac, String purpose){
        this.keyForPlaintext = keyForPlaintext;
        this.keyForMac = keyForMac;
        this.purpose = purpose;
    }

    /** Parcel constructor */
    public OneTimePad(Parcel parcel) {
        this.keyForPlaintext = parcel.readString();
        this.keyForMac = parcel.readString();
        this.purpose = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyForPlaintext);
        dest.writeString(keyForMac);
        dest.writeString(purpose);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /** Parcel creator */
    public final static Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public OneTimePad createFromParcel(Parcel source) {
            return new OneTimePad(source);
        }

        @Override
        public OneTimePad[] newArray(int size) {
            return new OneTimePad[size];
        }
    };


    /** Get methods */
    public String getKeyForPlaintext(){
        return keyForPlaintext;
    }

    public String getKeyForMac(){
        return keyForMac;
    }

    public String getPurpose(){
        return purpose;
    }

}