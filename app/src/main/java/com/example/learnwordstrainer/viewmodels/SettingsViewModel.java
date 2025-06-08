package com.example.learnwordstrainer.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.learnwordstrainer.model.ThemeMode;
import com.example.learnwordstrainer.repository.BubbleRepository;
import com.example.learnwordstrainer.repository.ThemeRepository;
import com.example.learnwordstrainer.service.BubbleService;

public class SettingsViewModel extends AndroidViewModel {
    private final ThemeRepository themeRepository;
    private final BubbleRepository bubbleRepository;

    private final MutableLiveData<Integer> currentTheme = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isBubbleEnabled = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);

        themeRepository = new ThemeRepository(application);
        bubbleRepository = new BubbleRepository(application);
        currentTheme.setValue(themeRepository.getThemeMode());
        isBubbleEnabled.setValue(bubbleRepository.isBubbleEnabled());
    }

    public LiveData<Boolean> getIsBubbleEnabled() {
        return isBubbleEnabled;
    }

    public void setTheme(ThemeMode themeMode) {
        int mode;
        switch (themeMode) {
            case LIGHT:
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case DARK:
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case SYSTEM:
            default:
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }

        themeRepository.saveThemeMode(mode);
        currentTheme.setValue(mode);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public int getCurrentThemeValue() {
        return currentTheme.getValue() != null ? currentTheme.getValue() : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    public void toggleBubbleSwitch() {
        boolean newState = bubbleRepository.toggleBubble();
        isBubbleEnabled.setValue(newState);

        // Керуємо сервісом залежно від стану
        if (newState) {
            startBubbleService();
        } else {
            stopBubbleService();
        }
    }

    private void startBubbleService() {
        try {
            Intent serviceIntent = new Intent(getApplication(), BubbleService.class);
            getApplication().startForegroundService(serviceIntent);
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Failed to start bubble service", e);
        }
    }

    private void stopBubbleService() {
        Intent serviceIntent = new Intent(getApplication(), BubbleService.class);
        getApplication().stopService(serviceIntent);
    }
}
