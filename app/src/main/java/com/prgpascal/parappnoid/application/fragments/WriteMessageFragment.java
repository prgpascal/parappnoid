package com.prgpascal.parappnoid.application.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.activities.WriteMessageActivity;
import com.prgpascal.parappnoid.utils.MyUtils;

import static com.prgpascal.parappnoid.utils.Constants.EncryptionDecryptionConstants.PLAINTEXT_LENGTH;

/**
 * Created by prgpascal on 04/06/2016.
 */
public class WriteMessageFragment extends Fragment {
    private EditText contentEditText;           // The EditText for the message content.
    private TextView contentLengthCounter;      // TextView that shows the length of message.
    private FloatingActionButton mFab;

    public static WriteMessageFragment newInstance() {
        return new WriteMessageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.fragment_write_message, container, false);

        contentLengthCounter = (TextView) rootView.findViewById(R.id.messageLengthCounter);
        contentEditText = (EditText) rootView.findViewById(R.id.messageContent);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentLengthCounter.setText(0 + " / " + (PLAINTEXT_LENGTH - 1));

        contentEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                contentLengthCounter.setTextColor(ContextCompat.getColor(getActivity(),
                        (s.length() > PLAINTEXT_LENGTH - 1) ? R.color.red : R.color.black));

                // Update the characters count
                contentLengthCounter.setText(s.length() + " / " + (PLAINTEXT_LENGTH - 1));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String plaintext = contentEditText.getText().toString();
        if (isValid(plaintext))
            ((WriteMessageInterface) getActivity()).sendMessage(plaintext);
        else
            Toast.makeText(getActivity(), R.string.error_message_invalid, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check if the plaintext is valid.
     * (i.e. not null, doesn't start with " " and its length is < than the maximum available)
     *
     * @param plaintext the plaintext to be evaluated.
     * @return boolean value representing if it is valid or not.
     */
    private boolean isValid(String plaintext) {
        return (MyUtils.isValid(plaintext) && (plaintext.length() < PLAINTEXT_LENGTH));
    }


    public interface WriteMessageInterface {
        void sendMessage(String message);
    }
}



