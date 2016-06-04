package com.prgpascal.parappnoid.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.fragments.LoginFragment;
import com.prgpascal.parappnoid.utils.Constants;
import com.prgpascal.parappnoid.utils.DBUtils;

/**
 * Activity that allows the user to insert the passphrase.
 */
public class LoginActivity extends AppCompatActivity implements
        LoginFragment.LoginFragmentInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        DBUtils dbUtils = DBUtils.getNewInstance(this); // TODO singleton

        if (dbUtils.performLogin(passphrase)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(Constants.PASSPHRASE, passphrase);
            startActivity(intent);

            //TODO rivedi se questo da errori o meno.
            dbUtils.eraseCharArray(passphrase);

            finish();

        } else {
            // Login error
            LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            fragment.wrongPassphraseInserted();
        }
    }
}
