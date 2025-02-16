package com.example.learnwordstrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Button btnAddWord, btnRepetition;
    private TextView tvWordCount, tvLearnedCount;
    private DatabaseHelper dbHelper;

    private SharedPreferences preferences;
    private static final String THEME_PREF = "theme_preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        applyTheme(preferences.getInt(THEME_PREF, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddWord = findViewById(R.id.btnAddWord);
        btnRepetition = findViewById(R.id.btnRepetition);
        tvWordCount = findViewById(R.id.tvWordCount);
        tvLearnedCount = findViewById(R.id.tvLearnedCount);

        dbHelper = new DatabaseHelper(this);

        setupClickListeners();
        updateStatistics();
        FloatingActionButton fabTheme = findViewById(R.id.fabTheme);
        fabTheme.setOnClickListener(v -> showThemeDialog());
    }

    private void setupClickListeners() {
        btnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnRepetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RepetitionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void updateStatistics() {
        int totalWords = dbHelper.getWordCount();
        int learnedWords = dbHelper.getLearnedWordsCount();

        tvWordCount.setText(String.valueOf(totalWords));

        int percentage = totalWords > 0 ? (learnedWords * 100) / totalWords : 0;
        tvLearnedCount.setText(percentage + "%");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void showThemeDialog() {
        String[] themes = {"Системна", "Світла", "Темна"};
        int currentTheme = preferences.getInt(THEME_PREF, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        int selectedTheme = 0;

        switch (currentTheme) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                selectedTheme = 0;
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
                    int themeMode;
                    switch (which) {
                        case 0:
                            themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                            break;
                        case 1:
                            themeMode = AppCompatDelegate.MODE_NIGHT_NO;
                            break;
                        case 2:
                            themeMode = AppCompatDelegate.MODE_NIGHT_YES;
                            break;
                        default:
                            themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    }
                    preferences.edit().putInt(THEME_PREF, themeMode).apply();
                    applyTheme(themeMode);
                    dialog.dismiss();
                })
                .show();
    }

    private void applyTheme(int themeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
}