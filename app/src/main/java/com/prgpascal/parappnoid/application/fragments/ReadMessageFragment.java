package com.prgpascal.parappnoid.application.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.activities.ReadMessageActivity;

/**
 * Fragment that allows the user to read a cleartext message.
 */
public class ReadMessageFragment extends Fragment implements ReadMessageActivity.ReadMessageInterface {
    private TextView messageContent;

    public static ReadMessageFragment newInstance() {
        return new ReadMessageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.fragment_read_message, container, false);
        messageContent = (TextView) rootView.findViewById(R.id.messageContent);

        return rootView;
    }

    @Override
    public void setMessageContent(String s) {
        messageContent.setText(s);
    }
}
