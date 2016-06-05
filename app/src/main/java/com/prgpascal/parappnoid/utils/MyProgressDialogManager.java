package com.prgpascal.parappnoid.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.prgpascal.parappnoid.R;

public class MyProgressDialogManager {
    private ProgressDialog progressDialog;

    public void showProgressDialog(boolean show, Context context) {
        if (show) {
            // Show the ProgressDialog
            progressDialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.please_wait), true);
            progressDialog.setCancelable(false);

        } else {
            try {
                // Stop showing ProgressDialog
                progressDialog.dismiss();
            } catch (NullPointerException e) {
                // Do nothing
            }
        }
    }
}
