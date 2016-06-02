package com.prgpascal.parappnoid.application.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.fragments.LoginFragment;

/**
 * Activity that allows the user to insert the passphrase.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the layout
        createLayout();
    }

    private void createLayout() {
        setContentView(R.layout.new_activity_login);

        Fragment loginFragment = LoginFragment.getNewInstance();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container, loginFragment);
        trans.commit();
    }

}
