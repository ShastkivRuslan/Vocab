package com.example.learnwordstrainer.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.learnwordstrainer.repository.BubbleRepository;
import com.example.learnwordstrainer.service.BubbleService;
import com.example.learnwordstrainer.service.NotificationService;
import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.databinding.ActivityMainBinding;
import com.example.learnwordstrainer.utils.NotificationHelper;
import com.example.learnwordstrainer.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_OVERLAY_PERMISSION = 1234;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1235;

    private ActivityMainBinding mainBinding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ViewModel for this activity
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set up view binding to replace findViewById calls
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        // Initialize click listeners for buttons
        setupClickListeners();
        // Set up LiveData observers for UI updates
        setupObservers();

        // Start services
        startBubbleService();
        startNotificationService();
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

    // Handle notification permission request
    private final ActivityResultLauncher<String> notificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startNotificationServiceWithPermission();
                    Toast.makeText(this, "Сповіщення увімкнено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Сповіщення вимкнено. Ви можете увімкнути їх у налаштуваннях", Toast.LENGTH_LONG).show();
                    // Still start the service, but notifications won't be shown
                    startNotificationServiceWithPermission();
                }
            });

    /**
     * Start the notification service that listens for screen unlock events
     */
    private void startNotificationService() {
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request notification permission
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                startNotificationServiceWithPermission();
            }
        } else {
            startNotificationServiceWithPermission();
        }
    }

    /**
     * Start the notification service after permissions are granted
     */
    private void startNotificationServiceWithPermission() {
        // Create notification channel
        NotificationHelper.createNotificationChannel(this);

        // Start the notification service
        Intent notificationServiceIntent = new Intent(this, NotificationService.class);
        startForegroundService(notificationServiceIntent);
    }

    /**
     * Start the floating bubble service if permission is available,
     * or request the overlay permission if needed
     */
    private void startBubbleService() {
        BubbleRepository bubbleRepository = new BubbleRepository(this);
        if (!bubbleRepository.isBubbleEnabled()) {
            return;
        }
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
        startForegroundService(intent);
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
        mainBinding.allWordsInclude.allWords.setOnClickListener(view -> {
            NotificationHelper.showLearningReminder(this);
            Toast.makeText(this, "Тестове сповіщення надіслано", Toast.LENGTH_SHORT).show();
        });
        mainBinding.fabSettings.setOnClickListener(view-> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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
}