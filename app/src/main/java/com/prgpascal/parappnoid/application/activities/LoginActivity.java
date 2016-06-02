package com.prgpascal.parappnoid.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.fragments.LoginFragment;
import com.prgpascal.parappnoid.utils.Constants;
import com.prgpascal.parappnoid.utils.DBUtils;

import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.ACTIVITY_REQUEST_TYPE;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.EDIT_USERS;

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
        setContentView(R.layout.new_activity_login);

        Fragment loginFragment = LoginFragment.newInstance();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container, loginFragment);
        trans.commit();
    }

    @Override
    public void loginButtonClicked(char[] passphrase) {
        DBUtils dbUtils = DBUtils.getNewInstance(this); //TODO singleton

        if (dbUtils.performLogin(passphrase)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(Constants.PASSPHRASE, passphrase);
            startActivity(intent);

            //TODO erase passphrase

            finish();

        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }
}
