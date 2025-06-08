package com.example.learnwordstrainer.ui.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.learnwordstrainer.databinding.ActivitySettingsBinding;
import com.example.learnwordstrainer.model.ThemeMode;
import com.example.learnwordstrainer.viewmodels.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.fabBack.setOnClickListener(v -> finish());
        binding.switchBubble.setOnClickListener(v -> viewModel.toggleBubbleSwitch());

        binding.ThemeSettings.setOnClickListener(v -> showThemeDialog());
        viewModel.getIsBubbleEnabled().observe(this, isEnabled -> {
            binding.switchBubble.setChecked(isEnabled);
        });
    }

    private void showThemeDialog() {
        String[] themes = {"Системна", "Світла", "Темна"};
        int currentThemeValue = viewModel.getCurrentThemeValue();
        int selectedTheme = 0;

        switch (currentThemeValue) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                selectedTheme = 1;
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                selectedTheme = 2;
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle("Виберіть тему")
                .setSingleChoiceItems(themes, selectedTheme, (dialog, which) -> {
                    ThemeMode selectedThemeMode;
                    switch (which) {
                        case 1:
                            selectedThemeMode = ThemeMode.LIGHT;
                            break;
                        case 2:
                            selectedThemeMode = ThemeMode.DARK;
                            break;
                        default:
                            selectedThemeMode = ThemeMode.SYSTEM;
                    }
                    viewModel.setTheme(selectedThemeMode);
                    dialog.dismiss();
                })
                .show();
    }
}
