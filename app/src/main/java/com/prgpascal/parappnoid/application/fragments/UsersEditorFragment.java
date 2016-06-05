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

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.adapters.AvatarImageAdapter;

/**
 * Created by prgpascal on 04/06/2016.
 */
public class UsersEditorFragment extends Fragment {
    private EditText usernameEditText;
    private ImageView avatarImageView;
    private RadioGroup radioGroup;
    private RelativeLayout serverArea;
    private EditText numberOfKeysEditText;
    private EditText keysPerQrEditText;
    private Button associateButton;

    private int selectedAvatar;
    private AlertDialog avatarDialog;

    public static UsersEditorFragment newInstance() {
        return new UsersEditorFragment();
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
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.radio_client:
                        performClient();
                        break;
                    case R.id.radio_server:
                        performServer();
                        break;
                }
            }
        });
    }

    private void performClient() {
        // Get the values
        String username = usernameEditText.getText().toString();
        int avatar = selectedAvatar;

        // TODO check the values
        // Send to the activity
    }


    private void performServer() {
        // Get the values
        String username = usernameEditText.getText().toString();
        int avatar = selectedAvatar;
        int numberOfKeys = Integer.valueOf(numberOfKeysEditText.getText().toString());
        int keysPerQR = Integer.valueOf(String.valueOf(keysPerQrEditText.getText().toString()));

        // TODO check the values
        // Check if valid and >0
//    } else {
//        // Error: numbers must be > 0
//        Toast.makeText(UsersEditorActivity.this, R.string.error_negative_num, Toast.LENGTH_SHORT).show();
//    }
//
//} else {
//        // Error: the username is not valid
//        Toast.makeText(UsersEditorActivity.this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
//        }
        // Send to the activity
    }

    /**
     * Show new Avatar Picker Dialog.
     */
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
}
