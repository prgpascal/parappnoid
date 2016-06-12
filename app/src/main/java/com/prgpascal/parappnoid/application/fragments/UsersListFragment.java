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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.application.adapters.UsersListAdapter;
import com.prgpascal.parappnoid.model.AssociatedUser;

import java.util.ArrayList;

/**
 * Fragment that contains the list of associated users.
 */
public class UsersListFragment extends Fragment {
	private ListView mUsersListView;
	private UsersListAdapter mAdapter;

	public static UsersListFragment newInstance() {
		return new UsersListFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      
		if (container == null) {
			return null;
		}

		// Inflate the layout
		View rootView = inflater.inflate(R.layout.fragment_users_list, container, false);
		mUsersListView = (ListView) rootView.findViewById(R.id.parent);

        return rootView; 
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mAdapter = new UsersListAdapter(new ArrayList<AssociatedUser>(), getActivity());
		mUsersListView.setAdapter(mAdapter);
	}

	public void updateLayout(ArrayList<AssociatedUser> users){
		mAdapter.setData(users);
	}

}