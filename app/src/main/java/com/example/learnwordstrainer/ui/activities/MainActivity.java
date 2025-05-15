package com.example.learnwordstrainer.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.learnwordstrainer.service.BubbleService;
import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.databinding.ActivityMainBinding;
import com.example.learnwordstrainer.model.ThemeMode;
import com.example.learnwordstrainer.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_OVERLAY_PERMISSION = 1234;

    private ActivityMainBinding mainBinding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ViewModel for this activity
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // Set up an observer for theme changes
        viewModel.getCurrentTheme().observe(this, this::applyTheme);

        // Set up view binding to replace findViewById calls
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        // Initialize click listeners for buttons
        setupClickListeners();
        // Set up LiveData observers for UI updates
        setupObservers();

        // Set up the theme switch button
        mainBinding.fabTheme.setOnClickListener(v -> showThemeDialog());
        // Start the floating bubble service if it's not already running
        startBubbleService();
    }

    // Modern way to handle activity results using ActivityResultLauncher
    // This handles the result of the overlay permission request
    private final ActivityResultLauncher<Intent> overlayPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Settings.canDrawOverlays(this)) {
                    // If permission is granted, start the bubble service
                    startBubbleServiceWithPermission();
                } else {
                    Toast.makeText(this, "Потрібен дозвіл для показу бульбашки", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Start the floating bubble service if permission is available,
     * or request the overlay permission if needed
     */
    private void startBubbleService() {
        if (Settings.canDrawOverlays(this)) {
            // Permission already granted, start the service
            startBubbleServiceWithPermission();
        } else {
            // Request system overlay permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            overlayPermissionLauncher.launch(intent);
        }
    }

    /**
     * Starts the bubble service as a foreground service for Android O and above
     */
    private void startBubbleServiceWithPermission() {
        Intent intent = new Intent(this, BubbleService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    // Legacy way to handle activity results - kept for backward compatibility
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startBubbleServiceWithPermission();
            } else {
                Toast.makeText(this, "Потрібен дозвіл для показу бульбашки", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Set up LiveData observers to update UI when data changes
     */
    private void setupObservers() {
        // Update the word count display when it changes
        viewModel.getTotalWordsCount().observe(this, count
                -> mainBinding.tvWordCount.setText(String.valueOf(count)));

        // Update the learned percentage when it changes
        viewModel.getLearnedPercentage().observe(this, percentage
                -> mainBinding.tvLearnedCount.setText(getString(R.string.percentage_format, percentage)));
    }

    /**
     * Set up click listeners for all navigation buttons
     */
    private void setupClickListeners() {
        // Navigate to Add Word screen
        mainBinding.addWordInclude.btnAddWord.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Navigate to Word Repetition screen
        mainBinding.repetitionInclude.btnRepetition.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RepetitionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Navigate to Practice screen
        mainBinding.practiceInclude.btnPractice.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PracticeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh statistics when returning to this screen
        viewModel.loadStatistics();
    }

    @Override
    public void finish() {
        super.finish();
        // Apply exit animation when closing the activity
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Display a dialog for theme selection (System, Light, Dark)
     */
    private void showThemeDialog() {
        String[] themes = {"Системна", "Світла", "Темна"};
        int currentThemeValue = viewModel.getCurrentThemeValue();
        int selectedTheme = 0;

        // Determine which theme is currently active
        switch (currentThemeValue) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                break; // Default is already 0
            case AppCompatDelegate.MODE_NIGHT_NO:
                selectedTheme = 1; // Light theme
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                selectedTheme = 2; // Dark theme
                break;
        }

        // Show the theme selection dialog
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
                    // Apply the selected theme
                    viewModel.setTheme(selectedThemeMode);
                    dialog.dismiss();
                })
                .show();
    }

    /**
     * Apply the selected theme to the app
     *
     * @param themeMode The theme mode to apply (from AppCompatDelegate constants)
     */
    private void applyTheme(Integer themeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
}