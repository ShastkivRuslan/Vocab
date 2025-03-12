package com.example.learnwordstrainer.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.databinding.ActivityAddWordBinding;
import com.example.learnwordstrainer.viewmodels.AddWordViewModel;

import java.util.Objects;

public class AddWordActivity extends AppCompatActivity {

    private ActivityAddWordBinding binding;
    private AddWordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AddWordViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Додати нове слово");
        }

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.clearMessage();
            }
        });

        viewModel.getWordAdded().observe(this, added -> {
            if (added) {
                binding.etEnglish.setText("");
                binding.etTranslation.setText("");
                clearFocus();
                viewModel.resetWordAdded();
            }
        });
    }

    private void setupListeners() {
        binding.btnAdd.setOnClickListener(view -> {
            String english = Objects.requireNonNull(binding.etEnglish.getText()).toString().trim().toLowerCase();
            String translation = Objects.requireNonNull(binding.etTranslation.getText()).toString().trim().toLowerCase();

            viewModel.addWord(english, translation);
        });
    }

    private void clearFocus() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}