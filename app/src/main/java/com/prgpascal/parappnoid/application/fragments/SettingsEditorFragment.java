package com.prgpascal.parappnoid.application.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.activities.SettingsEditorActivity.SettingsEditorInterface;
import com.prgpascal.parappnoid.application.activities.SettingsEditorActivity.WrongFieldException;
import com.prgpascal.parappnoid.utils.MyUtils;

/**
 * Created by prgpascal on 04/06/2016.
 */
public class SettingsEditorFragment extends Fragment implements
        SettingsEditorInterface {

    private EditText oldPassphraseEditText;         // EditText for the passphrase of current DB.
    private EditText newPassphraseEditText;         // EditText for the passphrase of new DB.
    private EditText newIterationsEditText;         // EditText for the number of iterations of new DB.

    public static SettingsEditorFragment newInstance() {
        return new SettingsEditorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.fragment_settings_editor, container, false);

        oldPassphraseEditText = (EditText) rootView.findViewById(R.id.oldPassphrase);
        newPassphraseEditText = (EditText) rootView.findViewById(R.id.newPassphrase);
        newIterationsEditText = (EditText) rootView.findViewById(R.id.newIterations);

        return rootView;
    }

    /**
     * Get the text char sequence from an EditText.
     */
    private char[] getValueFromEditText(EditText e) throws WrongFieldException {
        Editable text = e.getText();
        char[] value = new char[text.length()];
        text.getChars(0, text.length(), value, 0);

        if (!MyUtils.isValid(value))
            throw new WrongFieldException();

        return value;
    }

    @Override
    public char[] getOldPassphrase() throws WrongFieldException {
        return getValueFromEditText(oldPassphraseEditText);
    }

    @Override
    public char[] getNewPassphrase() throws WrongFieldException {
        return getValueFromEditText(newPassphraseEditText);
    }

    @Override
    public char[] getNewIterations() throws WrongFieldException {
        return getValueFromEditText(newIterationsEditText);
    }

}
