package com.example.learnwordstrainer.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeRepository {
    private final SharedPreferences preferences;
    private static final String THEME_PREF = "theme_preference";

    public ThemeRepository(Application application) {
        preferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    public int getThemeMode() {
        return preferences.getInt(THEME_PREF, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void saveThemeMode(int themeMode) {
        preferences.edit().putInt(THEME_PREF, themeMode).apply();
    }
}