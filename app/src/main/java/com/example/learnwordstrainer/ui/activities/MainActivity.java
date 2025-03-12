package com.example.learnwordstrainer.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.learnwordstrainer.BubbleService;
import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.databinding.ActivityMainBinding;
import com.example.learnwordstrainer.model.ThemeMode;
import com.example.learnwordstrainer.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getCurrentTheme().observe(this, this::applyTheme);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        setupClickListeners();
        setupObservers();

        mainBinding.fabTheme.setOnClickListener(v -> showThemeDialog());
        startBubbleService();
    }
    private void startBubbleService() {
        if (Settings.canDrawOverlays(this)) {
            startService(new Intent(this, BubbleService.class));
        } else {
            // Просимо дозвіл, якщо його немає
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startService(intent);
        }
    }

    private void setupObservers() {
        viewModel.getTotalWordsCount().observe(this, count -> {
            mainBinding.tvWordCount.setText(String.valueOf(count));
        });

        viewModel.getLearnedPercentage().observe(this, percentage -> {
            mainBinding.tvLearnedCount.setText(percentage + "%");
        });
    }

    private void setupClickListeners() {
        mainBinding.addWordInclude.btnAddWord.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        mainBinding.repetitionInclude.btnRepetition.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RepetitionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadStatistics();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    private void applyTheme(Integer themeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
}