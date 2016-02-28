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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.prgpascal.parappnoid.R;

/**
 * Class that contains a custom AlertDialogFragment.
 */
public class MyAlertDialogs {

    /**
     * Public Dialog Interface.
     * Contains all the methods that must be implemented by Activities that use AlertDialogs.
     */
    public interface MyAlertDialogInterface {
        void doPositiveClick(int dialogType, char[] result);
        void doNegativeClick(int dialogType, char[] result);
        void showNewDialog(int dialogType);
    }



    /**
     * Simple Alert Dialog Fragment.
     * It contains positive and negative buttons and a TextView for the message.
     */
    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int dialogType, String title, String message) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("dialogType", dialogType);
            args.putString("title", title);
            args.putString("message", message);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int dialogType = getArguments().getInt("dialogType");
            String title = getArguments().getString("title");
            String message = getArguments().getString("message");

            // Activity instance
            final MyAlertDialogInterface activity = (MyAlertDialogInterface) getActivity();

            return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                activity.doPositiveClick(dialogType, null);
                            }
                        }
                )
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                activity.doNegativeClick(dialogType, null);
                            }
                        }
                )
                .create();
        }
    }



    /**
     * AlertDialog Fragment that contains an EditText for passphrase input.
     */
    public static class MyPassphraseDialogFragment extends DialogFragment {

        public static MyPassphraseDialogFragment newInstance(int dialogType, String title, String message) {
            MyPassphraseDialogFragment frag = new MyPassphraseDialogFragment();
            Bundle args = new Bundle();
            args.putInt("dialogType", dialogType);
            args.putString("title", title);
            args.putString("message", message);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int dialogType = getArguments().getInt("dialogType");
            String title = getArguments().getString("title");
            String message = getArguments().getString("message");

            // Activity instance
            final MyAlertDialogInterface activity = (MyAlertDialogInterface) getActivity();

            // Customize the EditText
            final View parent = getActivity().getLayoutInflater().inflate(R.layout.passphrase_input, null);
            final EditText inputText = (EditText) parent.findViewById(R.id.passphrase);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setView(parent)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Get the passphrase
                                    Editable sequence = inputText.getText();
                                    char[] passphrase = new char[sequence.length()];
                                    sequence.getChars(0, sequence.length(), passphrase, 0);

                                    activity.doPositiveClick(dialogType, passphrase);
                                }
                            }
                    )
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    activity.doNegativeClick(dialogType, null);
                                }
                            }
                    )
                    .create();
        }
    }
}



