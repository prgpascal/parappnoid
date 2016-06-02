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

package com.prgpascal.parappnoid.application.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.adapters.RecyclerViewAdapter;
import com.prgpascal.parappnoid.utils.AssociatedUser;
import com.prgpascal.parappnoid.utils.DBUtils;

import static com.prgpascal.parappnoid.utils.Constants.PASSPHRASE;

/**
 * Fragment that contains the list of associated users.
 */
public class UsersListFragment extends Fragment {
	private RecyclerView mRecyclerView;										// RecyclerView
    private RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> mAdapter;	// RecyclerView Adapter
    private RecyclerView.LayoutManager mLayoutManager;						// RecyclerView LayoutManager

	private char[] passphrase;                   							// Passphrase inserted by the user
	private DBUtils dbUtils;                        						// Object used for DB operations


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      
		if (container == null) {
			return null;
		}

		// Inflate the layout
		View rootView = inflater.inflate(R.layout.users_list_fragment, container, false);
		
		// RecyclerView (Defined here because of an Android SDK bug)
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.parent);
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView; 
	}
   


    /** Called when the Activity is created: define the Views here */
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		// Check and get all the bundle parameters.
		// If a parameter is missing, show an error and finish the activity.
		Bundle b = getArguments();
		if (b.containsKey(PASSPHRASE)){

			// Parameters OK, read them all!
			passphrase = b.getCharArray(PASSPHRASE);

			// Instantiate the DBUtils object
			dbUtils = DBUtils.getNewInstance(getActivity());

		} else {
			// One or more parameters are missing!
			// Show an error message and finish the activity
			Toast.makeText(getContext(), R.string.error_missing_params, Toast.LENGTH_SHORT).show();
			getActivity().finish();
		}
	}



	@Override
	public void onStart(){
		super.onStart();

		// Read the updated UsersList on each onStart() method call
		if (dbUtils != null) {
			dbUtils.loadAssociatedUsers(passphrase);
		}
	}


	public void updateLayout(ArrayList<AssociatedUser> users){
		// RecyclerView Adapter
		mAdapter = new RecyclerViewAdapter(users, getActivity());
		mRecyclerView.setAdapter(mAdapter);
	}

}