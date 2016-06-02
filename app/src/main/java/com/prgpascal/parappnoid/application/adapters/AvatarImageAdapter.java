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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * ImageAdapter for the avatar selection GridView.
 */
public class AvatarImageAdapter extends BaseAdapter {
    private Context context;                            // Activity context
    private Integer[] imagesRes;                        // Resource identifiers for the images
    private static final int AVATARS_AVAILABLE = 47;    // Number of available avatars


    /** Constructor */
    public AvatarImageAdapter(Context context) {
        this.context = context;

        // Instantiate and populate the images resource identifiers
        imagesRes = new Integer[AVATARS_AVAILABLE];
        for (int i=0; i<AVATARS_AVAILABLE; i++){
            imagesRes[i] = context.getResources().getIdentifier(
                    "avatar" + i,
                    "drawable",
                    context.getPackageName());
        }
    }

    public int getCount() {
        return imagesRes.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public int getImage(int position){
        return imagesRes[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(imagesRes[position]);

        return imageView;
    }
}