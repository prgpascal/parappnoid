/*
 * Copyright (C) 2016 Riccardo Leschiutta.
 *
 * This file is part of Parappnoid.
 *
 * Parappnoid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Parappnoid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Parappnoid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.prgpascal.parappnoid.application.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.activities.UsersListActivity;
import com.prgpascal.parappnoid.model.AssociatedUser;

import java.util.ArrayList;

import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.PICK_USER;

/**
 * Adapter for the ListView, used to show the associated users.
 */
public class UsersListAdapter extends BaseAdapter {
    private static final int TAG_ITEM_POSITION = R.id.item_position;
    private ArrayList<AssociatedUser> mDataset;
    private UsersListActivity mActivity;
    private LayoutInflater mInflater = null;
    private MyOnClickListener mOnClickListener = new MyOnClickListener();

    public UsersListAdapter(ArrayList<AssociatedUser> dataset, Context context) {
        mDataset = dataset;
        mActivity = (UsersListActivity) context;
        mInflater = mActivity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataset.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AssociatedUser user = mDataset.get(i);

        if (view == null)
            view = mInflater.inflate(R.layout.users_list_row, null);

        // Edit the contents of the views with the correct AssociatedUser info.
        ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar);
        TextView usernameTextView = (TextView) view.findViewById(R.id.username);
        TextView keysAvailable1 = (TextView) view.findViewById(R.id.keysAvailable1);
        TextView keysAvailable2 = (TextView) view.findViewById(R.id.keysAvailable2);
        View containerView = view.findViewById(R.id.container);
        View editButton = view.findViewById(R.id.edit);

        avatarImageView.setImageResource(user.getAvatar());
        usernameTextView.setText(user.getUsername());
        keysAvailable1.setText(
                mActivity.getResources().getString(R.string.encryption_keys) +
                        user.getEncryptionKeys().size());
        keysAvailable2.setText(
                mActivity.getResources().getString(R.string.decryption_keys) +
                        user.getDecryptionKeys().size());

        // Set the the onClick listener
        containerView.setTag(TAG_ITEM_POSITION, i);
        editButton.setTag(TAG_ITEM_POSITION, i);
        containerView.setOnClickListener(mOnClickListener);
        editButton.setOnClickListener(mOnClickListener);

        // Hide some elements if not necessary and handle onClick events.
        if (mActivity.activityRequestType.equals(PICK_USER)) {
            keysAvailable2.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }

        return view;
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(TAG_ITEM_POSITION);
            mActivity.userSelected(mDataset.get(position));
        }
    }

    public void setData(ArrayList<AssociatedUser> newDataset) {
        mDataset.clear();
        mDataset.addAll(newDataset);
        notifyDataSetChanged();
    }
}