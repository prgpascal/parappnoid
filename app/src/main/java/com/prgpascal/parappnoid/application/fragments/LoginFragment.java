package com.prgpascal.parappnoid.application.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;

/**
 * Fragment that provides login features to the user.
 */
public class LoginFragment extends Fragment {
    private ImageView logoImage;
    private EditText passphraseEditText;
    private Button loginButton;

    public static LoginFragment newInstance() {
        return new LoginFragment();
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
     * Called when the user inserts a wrong passphrase. It shows an animation.
     */
    public void wrongPassphraseInserted() {
        Toast.makeText(getActivity(), R.string.error_wrong_passphrase, Toast.LENGTH_SHORT).show();
        Animation errorAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.field_error_anim);
        passphraseEditText.startAnimation(errorAnimation);
        logoImage.startAnimation(errorAnimation);
    }

    public interface LoginFragmentInterface {
        void loginButtonClicked(char[] passphrase);
    }

}
