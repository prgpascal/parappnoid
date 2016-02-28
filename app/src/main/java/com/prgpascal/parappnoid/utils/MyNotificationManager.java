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

package com.prgpascal.parappnoid.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import com.prgpascal.parappnoid.R;
import com.prgpascal.parappnoid.WriteMessageActivity;

import static com.prgpascal.parappnoid.utils.Constants.PREFERENCES;
import static com.prgpascal.parappnoid.utils.Constants.SHOW_NOTIFICATION;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.ACTIVITY_REQUEST_TYPE;
import static com.prgpascal.parappnoid.utils.Constants.UserManagerConstants.NEW_MESSAGE;

/**
 * Class used to show/hide a notification on the Notification bar.
 */
public class MyNotificationManager {
    private Context context;                            // Activity context.
    private NotificationManager notificationManager;    // Notification manager used.
    private int NOTIFICATION_ID = 7656;                 // ID used for this notification.


    /** Constructor */
    public MyNotificationManager(Context context){
        this.context = context;
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }



    /**
     * Shows or hide a notification in Notification Bar.
     * Is the same as showNotification(boolean show) except that this method will check the
     * SharedPreferences first.
     * @param show true if the notification must be showed.
     */
    public void showNotificationIfRequested(boolean show){
        // Check if the user WANTS the notifications
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
        if (prefs.getBoolean(SHOW_NOTIFICATION, true)){
            showNotification(show);
        }
    }



    /**
     * Shows or hide a notification in Notification Bar.
     * @param show true if the notification must be showed.
     */
     public void showNotification(boolean show){
        if (show){
            // Show Notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setOngoing(false)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(context.getResources().getString(R.string.app_name))
                            .setContentText(context.getResources().getString(R.string.notification_content));


            // Intent: if user clicks on the Notification ==> Open the specified Activity
            //    PS: Please note the definition of "android:launchMode="singleTop" in Manifest file
            Intent resultIntent = new Intent(context, WriteMessageActivity.class);
            resultIntent.putExtra(ACTIVITY_REQUEST_TYPE, NEW_MESSAGE);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            // Show the Notification
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        } else {
            // Stop showing the Notification.
            try {
                notificationManager.cancel(NOTIFICATION_ID);
            } catch(NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}
