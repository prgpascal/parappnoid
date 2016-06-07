package com.prgpascal.parappnoid.application.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.prgpascal.parappnoid.R;

/**
 * Simple Alert Dialog Fragment.
 * It contains positive and negative buttons and a TextView that display a message.
 */
public class SimpleAlertDialogFragment extends DialogFragment {
    private static final String TAG_DIALOG_TYPE = "dialog_type";
    private static final String TAG_TITLE = "title";
    private static final String TAG_MESSAGE = "message";

    public static SimpleAlertDialogFragment newInstance(int dialogType, String title, String message) {
        SimpleAlertDialogFragment frag = new SimpleAlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TAG_DIALOG_TYPE, dialogType);
        args.putString(TAG_TITLE, title);
        args.putString(TAG_MESSAGE, message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int dialogType = getArguments().getInt(TAG_DIALOG_TYPE);
        String title = getArguments().getString(TAG_TITLE);
        String message = getArguments().getString(TAG_MESSAGE);

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