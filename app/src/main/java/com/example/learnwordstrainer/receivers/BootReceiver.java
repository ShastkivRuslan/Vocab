package com.example.learnwordstrainer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.learnwordstrainer.service.NotificationService;

/**
 * BroadcastReceiver that starts the notification service after device boot
 * or app update
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(action) ||
                Intent.ACTION_PACKAGE_REPLACED.equals(action)) {

            Log.d(TAG, "Starting NotificationService after boot/update");
            startNotificationService(context);
        }
    }

    /**
     * Start the notification service
     */
    private void startNotificationService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, NotificationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            Log.d(TAG, "NotificationService started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start NotificationService", e);
        }
    }
}