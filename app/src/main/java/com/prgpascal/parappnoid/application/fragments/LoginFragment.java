package com.prgpascal.parappnoid.application.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;

/**
 * Created by prgpascal on 02/06/2016.
 */
public class LoginFragment extends Fragment {
    private ImageView logoImage;
    private EditText passphraseEditText;
    private Button loginButton;

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        logoImage = (ImageView) rootView.findViewById(R.id.logo);
        passphraseEditText = (EditText) rootView.findViewById(R.id.passphrase_edit_text);
        loginButton = (Button) rootView.findViewById(R.id.login_button);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginButton.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Get the passphrase
            Editable sequence = passphraseEditText.getText();
            char[] passphrase = new char[sequence.length()];
            sequence.getChars(0, sequence.length(), passphrase, 0);

            ((LoginFragmentInterface) getActivity()).loginButtonClicked(passphrase);
        }
    }

    /**
     * Called when the user has inserted the wrong passphrase.
     */
    public void wrongPassphraseInserted() {
        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        //TODO show animation

    }

    public interface LoginFragmentInterface {
        void loginButtonClicked(char[] passphrase);
    }

}
