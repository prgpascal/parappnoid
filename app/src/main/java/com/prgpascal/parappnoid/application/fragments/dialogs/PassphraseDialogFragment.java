package com.prgpascal.parappnoid.application.fragments.dialogs;

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
 * AlertDialog Fragment that contains an EditText for passphrase input.
 */
public class PassphraseDialogFragment extends DialogFragment {

    public static PassphraseDialogFragment newInstance(int dialogType, String title, String message) {
        PassphraseDialogFragment frag = new PassphraseDialogFragment();
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
