package com.example.learnwordstrainer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learnwordstrainer.repository.WordRepository;

public class AddWordActivity extends AppCompatActivity {

    private EditText etEnglish, etTranslation;
    private Button btnAdd;
    private WordRepository wordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Додати нове слово");
        }

        etEnglish = findViewById(R.id.etEnglish);
        etTranslation = findViewById(R.id.etTranslation);
        btnAdd = findViewById(R.id.btnAdd);

        wordRepository = new WordRepository(getApplication());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWord();
            }
        });
    }

    private void addWord() {
        String english = etEnglish.getText().toString().trim().toLowerCase();
        String translation = etTranslation.getText().toString().trim().toLowerCase();

        if (english.isEmpty() || translation.isEmpty()) {
            Toast.makeText(this, "Заповніть усі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!english.matches("[a-zA-Z ]+") || !translation.matches("[а-яА-ЯіІїЇєЄґҐ' ]+")) {
            Toast.makeText(this, "Використовуйте тільки літери", Toast.LENGTH_SHORT).show();
            return;
        }

        if (wordRepository.wordExists(english)) {
            Toast.makeText(this, "Це слово вже існує в словнику", Toast.LENGTH_SHORT).show();
            return;
        }

        wordRepository.addWord(english, translation);
        Toast.makeText(this, "Слово додано", Toast.LENGTH_SHORT).show();

        etEnglish.setText("");
        etTranslation.setText("");
        clearFocus();
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
