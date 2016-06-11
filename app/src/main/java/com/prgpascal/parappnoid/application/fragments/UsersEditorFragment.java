package com.prgpascal.parappnoid.application.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.adapters.AvatarImageAdapter;
import com.prgpascal.parappnoid.utils.MyUtils;

/**
 * Fragment that allows the user to edit the information about an AssociatedUser, or to create
 * a new one.
 */
public class UsersEditorFragment extends Fragment {
    private EditText usernameEditText;
    private ImageView avatarImageView;
    private RadioGroup radioGroup;
    private RelativeLayout serverArea;
    private EditText numberOfKeysEditText;
    private EditText keysPerQrEditText;
    private Button associateButton;

    private int selectedAvatar = R.drawable.avatar0;
    private AlertDialog avatarDialog;

    private boolean mIsEditUserRequestType;
    private static final String IS_EDIT_USER_REQ_TYPE = "is_edit_user_req_type";

    public static UsersEditorFragment newInstance(boolean isEditUserRequestType) {
        Bundle b = new Bundle();
        b.putBoolean(IS_EDIT_USER_REQ_TYPE, isEditUserRequestType);
        UsersEditorFragment fragment = new UsersEditorFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.fragment_users_editor, container, false);

        usernameEditText = (EditText) rootView.findViewById(R.id.username);
        avatarImageView = (ImageView) rootView.findViewById(R.id.avatar);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);
        serverArea = (RelativeLayout) rootView.findViewById(R.id.server_area);
        numberOfKeysEditText = (EditText) rootView.findViewById(R.id.n_keys);
        keysPerQrEditText = (EditText) rootView.findViewById(R.id.keys_per_qr);
        associateButton = (Button) rootView.findViewById(R.id.associate_btn);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIsEditUserRequestType = getArguments().getBoolean(IS_EDIT_USER_REQ_TYPE, false);

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAvatarPickerDialog();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                serverArea.setVisibility(
                        radioGroup.getCheckedRadioButtonId() == R.id.radio_server ? View.VISIBLE : View.GONE
                );
            }
        });

        associateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsEditUserRequestType) {
                    performSaveUser();

                } else {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_client:
                            performClientOperation();
                            break;
                        case R.id.radio_server:
                            performServerOperation();
                            break;
                    }
                }
            }
        });

        // Hide some elements..
        if (mIsEditUserRequestType){
            radioGroup.setVisibility(View.GONE);
            serverArea.setVisibility(View.GONE);
        }
    }

    private void performSaveUser(){
        String username = usernameEditText.getText().toString();
        int avatar = selectedAvatar;

        if (MyUtils.isValid(username)) {
            ((UsersEditorInterface) getActivity()).saveUser(username, avatar);

        } else {
            Toast.makeText(getActivity(), R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
        }
    }

    private void performClientOperation() {
        String username = usernameEditText.getText().toString();
        int avatar = selectedAvatar;

        if (MyUtils.isValid(username)) {
            ((UsersEditorInterface) getActivity()).performClientRequest(username, avatar);

        } else {
            Toast.makeText(getActivity(), R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
        }
    }

    private void performServerOperation() {
        String username = usernameEditText.getText().toString();
        int avatar = selectedAvatar;
        try {
            int numberOfKeys = Integer.valueOf(numberOfKeysEditText.getText().toString());
            int keysPerQR = Integer.valueOf(String.valueOf(keysPerQrEditText.getText().toString()));

            if (MyUtils.isValid(username) &&
                    (numberOfKeys > 0) &&
                    (keysPerQR > 0)) {

                ((UsersEditorInterface) getActivity()).performServerRequest(username, avatar, numberOfKeys, keysPerQR);

            } else {
                Toast.makeText(getActivity(), R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAvatarPickerDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.grid_layout, null);
        final GridView gridView = (GridView) view.findViewById(R.id.gridView);
        final AvatarImageAdapter adapter = new AvatarImageAdapter(getActivity());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAvatar = adapter.getImage(position);
                avatarImageView.setImageResource(selectedAvatar);
                avatarDialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.pick_avatar);
        avatarDialog = builder.create();
        avatarDialog.show();
    }

    public interface UsersEditorInterface {
        void saveUser(String username, int avatar);

        void performClientRequest(String username, int avatar);

        void performServerRequest(String username, int avatar, int numberOfKeys, int keysPerQR);
    }

}
