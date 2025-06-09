package com.example.learnwordstrainer.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.learnwordstrainer.databinding.ActivityBubbleSettingsBinding;
import com.example.learnwordstrainer.viewmodels.BubbleSettingsViewModel;
import com.example.learnwordstrainer.viewmodels.BubbleViewModel;

public class BubbleSettingsActivity extends AppCompatActivity {
    private ActivityBubbleSettingsBinding binding;
    private BubbleSettingsViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBubbleSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BubbleSettingsViewModel.class);

        binding.fabBack.setOnClickListener(v -> finish());


    }
}
