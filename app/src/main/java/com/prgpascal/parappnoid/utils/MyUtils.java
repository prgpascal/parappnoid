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

import android.util.Log;
import android.widget.TextView;

import com.prgpascal.parappnoid.model.OneTimePad;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.prgpascal.parappnoid.utils.Constants.EncryptionDecryptionConstants.*;
import static com.prgpascal.parappnoid.utils.Constants.DEBUG_TAG;

/**
 * Class that provides some useful methods.
 * All methods are static.
 */
public class MyUtils {

    /**
     * Encode an HEX String into a ISO-8859-1 String.
     * Null if encode failed.
     *
     * @param hexString HEX String to be encoded in ISO-8859-1 format.
     * @return ISO-8859-1 String representation.
     */
    public static String encodeISO88591(String hexString){
        try {
            // HEX String to byte[]
            byte[] bytes = Hex.decodeHex(hexString.toCharArray());

            return encodeISO88591(bytes);

        } catch (DecoderException e){
            e.printStackTrace();
        }

        return null;
    }



    /**
     * Encode a byte Array into ISO-8859-1 String.
     * Null if encode failed.
     *
     * @param bytes byte Array to be encoded.
     * @return ISO-8859-1 String representation.
     */
    public static String encodeISO88591(byte[] bytes){
        try {
            // byte[] to ISO-8859-1 String
            String encoded = new String(bytes, "ISO-8859-1");

            return encoded;

        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return null;
    }



    /**
     * Encode an Integer number to ISO-8859-1 String.
     * Null if encode failed.
     *
     * @param number integer to be encoded.
     * @param size number of bytes of returned String.
     * @return ISO-8859-1 String representation.
     */
    public static String encodeISO88591(int number, int size){
        // Integer to HEX String
        String hexNumber = Integer.toHexString(number);

        // Add 0 if the HEX String has less than (size * 2) chars
        while (hexNumber.length() < size*2){
            hexNumber = "0" + hexNumber;
        }

        return encodeISO88591(hexNumber);
    }



    /**
     * Encode a ISO-8859-1 String to Integer value.
     *
     * @param iso88591String string to be encoded.
     * @return Integer representation.
     */
    public static int encodeInt(String iso88591String){
        return Integer.parseInt(encodeHEX(iso88591String), 16);
    }



    /**
     * Encode a ISO-8859-1 String to HEX String.
     * Null if encode failed.
     *
     * @param iso88591String String encoded in ISO-8859-1.
     * @return String in HEX format.
     */
    public static String encodeHEX(String iso88591String){
        try {
            // ISO-8859-1 String to byte[]
            byte[] bytes = iso88591String.getBytes("ISO-8859-1");

            return new String(Hex.encodeHex(bytes));

        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return null;
    }



    /**
     * Split the message into 3 different Strings.
     * [0] = PAD_ID
     * [1] = C (ciphertext)
     * [2] = MAC
     *
     * @param message to be split.
     * @return String output array.
     */
    public static String[] splitMessage(String message){
        String[] output = new String[3];
        output[0] = message.substring(0, PAD_ID_LENGTH);
        output[1] = message.substring(PAD_ID_LENGTH, PAD_ID_LENGTH + PLAINTEXT_LENGTH);
        output[2] = message.substring(PAD_ID_LENGTH + PLAINTEXT_LENGTH);

        return output;
    }



    /**
     * Create an ISO-8859-1 String of random bytes.
     *
     * @param numberOfBytes number of bytes of result.
     * @return ISO-8859-1 String with random bytes.
     */
    public static String createRandomString(int numberOfBytes){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[numberOfBytes];
        random.nextBytes(bytes);

        return encodeISO88591(bytes);
    }



    /**
     * Method that calculates the SHA-256 digest for a ISO-8859-1 String.
     *
     * @param message ISO-8859-1 String.
     * @return ISO-8859-1 digest String (32 bytes)
     */
    public static String calculateMac(String message) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] messageBytes = message.getBytes("ISO-8859-1");
            byte[] hash = sha256.digest(messageBytes);

            return encodeISO88591(hash);

        } catch (UnsupportedEncodingException ue){
            ue.printStackTrace();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return null;
    }



    /**
     * Encrypt or Decrypt a ISO-8859-1 String using a OTP key.
     *
     * @param message ISO-8859-1 message String.
     * @param key ISO-8859-1 One-Time Pad key.
     * @return encrypted/decrypted ISO-8859-1 String.
     */
    public static String encryptOrDecrypt(String message, String key) {
        try {
            // ISO-8859-1 String to byte[]
            byte m[] = message.getBytes("ISO-8859-1");
            byte k[] = key.getBytes("ISO-8859-1");

            // get result
            byte result[] = xor(m, k);

            return new String(result, "ISO-8859-1");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }



    /**
     * XOR two byte arrays.
     *
     * @param a first byte[] array
     * @param b second byte[] array
     * @return XORed byte[] array
     */
    private static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[Math.min(a.length, b.length)];

        for (int i = 0; i < result.length; i++)
            result[i] = (byte) (a[i] ^ b[i]);

        return result;
    }



    /**
     * Read the random characters from external file, located in the main root of
     * the external removable device (ex. microSD).
     * Its name must be: "keys.txt".
     *
     * @param numberOfPads maximum number of OneTimePad keys to be generated.
     * @param padsLength length of each OneTimePad key.
     * @return HashMap containing all the OneTimePad objects created.
     */
    public static HashMap readOneTimePadsFromSD(int numberOfPads, int padsLength){
        // Define the external storage path
        File sdCard = getExternalSdCard();
        File keysFile = new File(sdCard.getAbsolutePath() + "/" + "keys.txt");

        // HashMap containing the OneTimePad objects
        HashMap<Integer, OneTimePad> pads = new HashMap<>();

        if ((sdCard != null)&&(keysFile.exists())){
            try {
                InputStreamReader in = new InputStreamReader(new FileInputStream(keysFile), Charset.forName("ISO-8859-1"));

                String pad = "";
                int readChar;
                int i = 0;
                while ((readChar = in.read()) != -1) {
                    // Read a char
                    String iso = encodeISO88591(readChar, 1);
                    pad = pad + iso;

                    if (pad.length() == padsLength) {
                        // New pad composed
                        addOneTimePad(pads, pad, i);
                        i++;
                        pad = "";

                        if (i == numberOfPads){
                            // Max number of keys to be generated reached.
                            break;
                        }
                    }
                }

                in.close();

                // delete the file
                boolean deleted = keysFile.delete();

                // return the keys
                if (pads.size()>0 && deleted)
                    return pads;

            } catch(IOException e) {
                Log.d(DEBUG_TAG, e.toString());
            }

        } else {
            Log.d(DEBUG_TAG, "Error");
        }

        return null;
    }



    /**
     * Try possible paths for the removable external storage (ex. microSD).
     * Returns null if path not found.
     *
     * @return File instance representing the removable storage.
     */
    public static File getExternalSdCard(){
        String[] possibilePaths = new String[]{
            "/storage/extSdCard/",
            "/storage/sdcard1/",
            "/storage/usbcard1/"};

        for (String p : possibilePaths){
            File path = new File(p);
            if (path.exists()){
                return path;
            }
        }

        return null;
    }



    /**
     * Create a new OneTimePad object and store it to the HashMap.
     *
     * @param pads the HashMap that will store the new OneTimePad object.
     * @param pad the new key to be added to a new OneTimePad object.
     * @param i number used to define if the new OneTimePad will be used for encryption or decryption.
     */
    public static void addOneTimePad(HashMap<Integer, OneTimePad> pads, String pad, int i){
        // Initiate the Secure Random Number Generator
        SecureRandom random = new SecureRandom();

        // Unique Pad ID
        int padID;
        do {
            padID = random.nextInt(MAX_PAD_ID);    // Generate new random Integer into the range [0 .. 65536)
        } while (pads.containsKey(padID));

        // Split the Pad
        String keyForPlaintext = pad.substring(0, PLAINTEXT_LENGTH);
        String keyForMac = pad.substring(PLAINTEXT_LENGTH);

        // Purpose
        String purpose = (i % 2 == 0 ? "E" : "D");

        // Create the OneTimePad object and put it into the HashMap
        pads.put(padID, new OneTimePad(keyForPlaintext, keyForMac, purpose));
    }



    /**
     * Generate OneTimePad objects.
     *
     * @param numberOfPads the number of pads to be generated.
     * @param padsLength the length of each OTP key.
     * @return HashMap containing the generated keys.
     */
    public static HashMap generateOneTimePads(int numberOfPads, int padsLength){
        // HashMap containing the OneTimePad objects
        HashMap<Integer, OneTimePad> pads = new HashMap<>();

        // Initiate the Secure Random Number Generator
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[padsLength];

        try {
            for (int i = 0; i < numberOfPads; i++) {
                // Generate new pad
                random.nextBytes(bytes);
                String pad = new String(bytes, "ISO-8859-1");

                // Add the pad to the HashMap
                addOneTimePad(pads, pad, i);
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return pads;
    }



    /**
     * Create groups ok keys end store them into Strings.
     * Each group of keys is composed concatenating multiple keys.
     * Each single key is equal to:
     *      key = isoPadID + isoPad + purpose;
     *
     * The total length of each group is set as:
     *      (MAX_CHARS_PER_QR - DIGEST_LENGTH)
     * because the QR messages transfer library will append a digest to each
     * group before the transmission.
     *
     * NB: "keys" refers to OneTimePad objects...
     *
     * @param keys HashMap containing the OneTimePad objects
     * @param keysPerQR number of keys that have to be encoded in a single QR.
     * @return the grouped keys.
     */
    public static ArrayList<String> encodeGroupsOfKeys(HashMap<Integer, OneTimePad> keys, int keysPerQR){

        // ArrayList (Strings)
        ArrayList<String> groupsOfKeys = new ArrayList<>();

        // Current group ok keys
        String currentGroup = "";
        int currentKeysInGroup = 0;

        // Insert each key into groups
        for (Map.Entry<Integer, OneTimePad> entry : keys.entrySet()) {

            // Get the OneTimePad object
            OneTimePad value = entry.getValue();

            String isoPadID = MyUtils.encodeISO88591(entry.getKey(), 2);
            String isoPad = value.getKeyForPlaintext() + value.getKeyForMac();
            String purpose = value.getPurpose();
            purpose = purpose.equals("E")?"D":"E"; //invert the purpose

            // Compose the key
            String key = isoPadID + isoPad + purpose;

            // Add the key to the current group of keys or create a new one
            if (currentKeysInGroup < keysPerQR){
                // Max length not reached, I can append it to the current chunk
                currentGroup = currentGroup + key;
                currentKeysInGroup++;

            } else {
                // I need to create a new group
                // Flush the current group to the ArrayList
                groupsOfKeys.add(currentGroup);

                // Create new group
                currentGroup = key;
                currentKeysInGroup = 1;
            }
        }

        // Flush the remaining group of keys to the ArrayList
        groupsOfKeys.add(currentGroup);

        return groupsOfKeys;
    }



    /**
     * Decode the group of keys received.
     * Each group of keys is composed concatenating multiple keys.
     * Each key is:
     *      key = isoPadID + isoPad + purpose;
     *
     * @param groupsOfKeys
     * @return HashMap with OneTimePad objects.
     */
    public static HashMap<Integer, OneTimePad> decodeGroupsOfKeys(ArrayList<String> groupsOfKeys){

        // HashMap for the keys
        HashMap<Integer, OneTimePad> keys = new HashMap<>();

        for (String currentGroup : groupsOfKeys) {

            while (currentGroup.length() > 0) {
                // Create the OTP Key
                String isoPadID = currentGroup.substring(0, PAD_ID_LENGTH);
                String isoPad = currentGroup.substring(PAD_ID_LENGTH, (PAD_ID_LENGTH + PADS_LENGTH));
                String purpose = currentGroup.charAt(PAD_ID_LENGTH + PADS_LENGTH) + "";

                // Group read, delete it!
                currentGroup = currentGroup.substring(PAD_ID_LENGTH + PADS_LENGTH + PURPOSE_LENGTH);

                // Convert the padID to Integer
                int padID = encodeInt(isoPadID);

                // Split the Pad
                String keyForPlaintext = isoPad.substring(0, PLAINTEXT_LENGTH);
                String keyForMac = isoPad.substring(PLAINTEXT_LENGTH);

                // Put the OneTimePad into the HashMap
                keys.put(padID, new OneTimePad(keyForPlaintext, keyForMac, purpose));
            }
        }

        return keys;
    }



    /**
     * Method that checks if a given String is valid.
     * (i.e. is not null, is not empty, nor it starts with a space.
     *
     * @param stringToCheck the String to be evaluated.
     * @return boolean value representing if it's valid or not.
     */
    public static boolean isValid(String stringToCheck){
        if      ((stringToCheck == null) ||
                (stringToCheck.equals("")) ||
                (stringToCheck.equals(" ")) ||
                (stringToCheck.startsWith(" "))){

            return false;
        }
        return true;
    }



    /**
     * Method that checks if a given char array is valid.
     * (i.e. is not null, is not empty, nor it starts with a space.
     *
     * @param valuesToCheck the char array to be evaluated.
     * @return boolean value representing if it's valid or not.
     */
    public static boolean isValid(char[] valuesToCheck){
        String stringToCheck = String.valueOf(valuesToCheck);
        return isValid(stringToCheck);
    }






    // TMP TMP TMP
    public static void printOtpKey(OneTimePad key, TextView output, int padID){
        output.append("\n\nPAD_ID: "+padID);
        output.append("\nKp: "+key.getKeyForPlaintext());
        output.append("\nKm: "+key.getKeyForMac());
        output.append("\nPURPOSE: " + key.getPurpose());
    }

    public static void printOtpKeys(HashMap<Integer, OneTimePad> keys, TextView output) {
        //--- Print each key ---
        for (Map.Entry<Integer, OneTimePad> entry : keys.entrySet()) {

            //--- Get the OneTimePad object ---
            int padID = entry.getKey();
            OneTimePad value = entry.getValue();

            //--- Print this key ---
            printOtpKey(value, output, padID);
        }
    }
}


