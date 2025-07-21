package com.example.learnwordstrainer.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.learnwordstrainer.databinding.ActivityBubbleSettingsBinding;
import com.example.learnwordstrainer.service.BubbleService;
import com.example.learnwordstrainer.viewmodels.BubbleSettingsViewModel;
import com.google.android.material.slider.Slider;

public class BubbleSettingsActivity extends AppCompatActivity {
    private ActivityBubbleSettingsBinding binding;
    private BubbleSettingsViewModel viewModel;

    private BubbleService bubbleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBubbleSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BubbleSettingsViewModel.class);

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        // Існуюча логіка
        binding.fabBack.setOnClickListener(v -> finish());
        binding.llBubbleSwitch.setOnClickListener(view -> viewModel.toggleBubbleSwitch());

        // Налаштування слайдеру розміру
        binding.sliderBubbleSize.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // Початок перетягування
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // Кінець перетягування - зберігаємо значення та перезапускаємо сервіс
                int newSize = (int) slider.getValue();
                viewModel.saveBubbleSize(newSize);
            }
        });
    }

    private void observeViewModel() {
        // Існуюча логіка
        viewModel.getIsBubbleEnabled().observe(this, isEnabled -> {
            binding.switchBubble.setChecked(isEnabled);
        });

        viewModel.getBubbleSize().observe(this, size -> {
            binding.sliderBubbleSize.setValue(size);
        });
    }
}