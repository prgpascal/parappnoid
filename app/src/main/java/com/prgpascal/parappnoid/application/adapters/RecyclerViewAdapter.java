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

import java.util.ArrayList;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.activities.UsersListActivity;
import com.prgpascal.parappnoid.model.AssociatedUser;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.*;

/**
 * Adapter for the RecyclerView, used for showing the associated users.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<AssociatedUser> users;    // Dataset used for populate the RecyclerView
    private UsersListActivity activity;         // Activity reference


    /** Provide a reference to the views for each data item */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout layout;
        
        public ViewHolder(RelativeLayout v) {
            super(v);
            layout = v;
        }  
    }
   

    /** Constructor */
    public RecyclerViewAdapter(ArrayList<AssociatedUser> users, Context context) {
        this.users = users;
        activity = (UsersListActivity) context;
    }

    
    
    /** Create new views */
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new item
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.users_list_item, parent, false);

        return new ViewHolder(v);
    }
    

    
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
    	// Edit the contents of the views with the right AssociatedUser information.
        AssociatedUser user = users.get(position);

        ((ImageView)holder.layout.findViewById(R.id.avatar)).setImageResource(user.getAvatar());
    	((TextView)holder.layout.findViewById(R.id.username)).setText(user.getUsername());
        ((TextView)holder.layout.findViewById(R.id.keysAvailable1)).setText(
                activity.getResources().getString(R.string.encryption_keys) +
                user.getEncryptionKeys().size());
        ((TextView)holder.layout.findViewById(R.id.keysAvailable2)).setText(
                activity.getResources().getString(R.string.decryption_keys) +
                user.getDecryptionKeys().size());

        // Hide some elements if not necessary for the Activity purpose.
        // Handle onClick events related to the Activity purpose.
        if (activity.activityRequestType.equals(PICK_USER)) {
            (holder.layout.findViewById(R.id.keysAvailable2)).setVisibility(View.GONE);
            (holder.layout.findViewById(R.id.edit)).setVisibility(View.GONE);

            // Handle the clicks on this list item.
            (holder.layout.findViewById(R.id.container)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User picked
                    activity.userSelected(users.get(position));
                }
            });

        } else {
            // Handle the clicks on the Edit button of this list item.
            (holder.layout.findViewById(R.id.edit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // User to be edited selected
                    activity.userSelected(users.get(position));
                }
            });
        }
    }


    /** Return the size of the dataset (invoked by the layout manager) */
    @Override
    public int getItemCount() {
        return users.size();
    }
}