package com.prgpascal.parappnoid.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.fragments.LoginFragment;
import com.prgpascal.parappnoid.application.fragments.UsersListFragment;
import com.prgpascal.parappnoid.model.AssociatedUser;
import com.prgpascal.parappnoid.utils.Constants;
import com.prgpascal.parappnoid.utils.DBUtils;
import com.prgpascal.parappnoid.utils.MyProgressDialogManager;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Activity that allows the user to insert the passphrase.
 */
public class LoginActivity extends AppCompatActivity implements
        LoginFragment.LoginFragmentInterface,
        DBUtils.DbResponseCallback {

    private MyProgressDialogManager progressDialog = new MyProgressDialogManager();
    private DBUtils dbUtils;
    private char[] passphrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbUtils = DBUtils.getInstance(getApplicationContext());

        createLayout();
    }

    private void createLayout() {
        setContentView(R.layout.activity);

        Fragment fragment = LoginFragment.newInstance();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.commit();
    }

    @Override
    public void loginButtonClicked(char[] passphrase) {
        this.passphrase = passphrase;
        progressDialog.showProgressDialog(true, this);
        dbUtils.performLogin(passphrase, this);
    }

    public void onDBResponse(boolean result) {
        progressDialog.showProgressDialog(false, this);
        if (result) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(Constants.PASSPHRASE, passphrase);
            startActivity(intent);

            //TODO rivedi se questo da errori o meno.
            dbUtils.eraseCharArray(passphrase);

            finish();

        } else {
            Toast.makeText(getApplicationContext(), "" + result, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A Database operation has finished.
     *
     * @param result the ArrayList of associated users.
     */
    public void onDBResponse(ArrayList<AssociatedUser> result) {
        // Do nothing
    }

}
