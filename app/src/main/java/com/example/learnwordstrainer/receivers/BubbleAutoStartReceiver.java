package com.example.learnwordstrainer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.example.learnwordstrainer.service.BubbleService;

/**
 * BroadcastReceiver that listens for system boot events and app updates
 * to automatically start the BubbleService
 */
public class BubbleAutoStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_MY_PACKAGE_REPLACED:
            case Intent.ACTION_USER_PRESENT:
                if (Settings.canDrawOverlays(context)) {
                    Intent serviceIntent = new Intent(context, BubbleService.class);
                    context.startForegroundService(serviceIntent);
                }
                break;
        }
    }
}
