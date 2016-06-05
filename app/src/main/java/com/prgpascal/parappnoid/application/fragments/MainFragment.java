package com.prgpascal.parappnoid.application.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prgpascal.parappnoid.R;

/**
 * Created by prgpascal on 02/06/2016.
 */
public class MainFragment extends Fragment {
    private Button writeMessageButton;
    private Button contactsButton;
    private Button readMessageButton;

    public static MainFragment newInstance() {
        MainFragment loginFragment = new MainFragment();
        return loginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        writeMessageButton = (Button) rootView.findViewById(R.id.write_message_btn);
        contactsButton = (Button) rootView.findViewById(R.id.contacts_btn);
        readMessageButton = (Button) rootView.findViewById(R.id.read_message_btn);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyOnClickListener onClickListener = new MyOnClickListener();

        writeMessageButton.setOnClickListener(onClickListener);
        contactsButton.setOnClickListener(onClickListener);
        readMessageButton.setOnClickListener(onClickListener);
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.write_message_btn:
                    ((MainFragmentInterface) getActivity()).writeMessage();
                    break;
                case R.id.read_message_btn:
                    ((MainFragmentInterface) getActivity()).readMessage();
                    break;
                case R.id.contacts_btn:
                    ((MainFragmentInterface) getActivity()).editContacts();
                    break;
            }
        }
    }

    public interface MainFragmentInterface {
        void writeMessage();

        void readMessage();

        void editContacts();
    }

}

