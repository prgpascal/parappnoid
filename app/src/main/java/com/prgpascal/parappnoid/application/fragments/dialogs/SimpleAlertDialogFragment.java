package com.prgpascal.parappnoid.application.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.prgpascal.parappnoid.R;

/**
 * Simple Alert Dialog Fragment.
 * It contains positive and negative buttons and a TextView for the message.
 */
public class SimpleAlertDialogFragment extends DialogFragment {

    public static SimpleAlertDialogFragment newInstance(int dialogType, String title, String message) {
        SimpleAlertDialogFragment frag = new SimpleAlertDialogFragment();
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