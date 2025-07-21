package com.example.learnwordstrainer.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.example.learnwordstrainer.repository.BubbleRepository;
import com.example.learnwordstrainer.service.BubbleService;

/**
 * BroadcastReceiver that listens for system boot events and app updates
 * to automatically start the BubbleService
 */
public class BubbleAutoStartReceiver extends BroadcastReceiver {
    private static final String TAG = "BubbleAutoStartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        Log.d(TAG, "Received action: " + action);

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_MY_PACKAGE_REPLACED:
            case Intent.ACTION_PACKAGE_REPLACED:
                // Запускаємо після завантаження системи або оновлення додатку
                startBubbleServiceIfAllowed(context, "System boot or app update");
                break;

            case Intent.ACTION_USER_PRESENT:
            case Intent.ACTION_SCREEN_ON:
                // Запускаємо коли користувач розблокував екран або увімкнув дисплей
                startBubbleServiceIfAllowed(context, "Screen on or user present");
                break;

            case Intent.ACTION_USER_UNLOCKED:
                // Запускаємо після розблокування користувача (Android 7.0+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startBubbleServiceIfAllowed(context, "User unlocked");
                }
                break;
        }
    }

    private void startBubbleServiceIfAllowed(Context context, String reason) {
        BubbleRepository bubbleRepository = new BubbleRepository(context);
        if (!bubbleRepository.isBubbleEnabled()) {
            return;
        }

        // Перевіряємо дозвіл на показ поверх інших додатків
        if (!Settings.canDrawOverlays(context)) {
            Log.w(TAG, "Cannot start bubble service: overlay permission not granted");
            return;
        }

        // Перевіряємо чи сервіс вже запущений
        if (isBubbleServiceRunning(context)) {
            Log.d(TAG, "Bubble service already running");
            return;
        }

        try {
            Intent serviceIntent = new Intent(context, BubbleService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }

            Log.i(TAG, "Started bubble service. Reason: " + reason);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start bubble service", e);
        }
    }

    /**
     * Перевіряє чи вже запущений BubbleService
     */
    private boolean isBubbleServiceRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (BubbleService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}