package com.example.learnwordstrainer;

import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnwordstrainer.databinding.ActivityRepetitionBinding;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RepetitionActivity extends AppCompatActivity {

    private ActivityRepetitionBinding repetitionBinding;
    private DatabaseHelper dbHelper;
    private int currentWordId;
    private int correctAnswerIndex;
    private String currentWord;
    private String currentTranslation;
    private int currentCorrectAnswerCount;
    private int currentWrongAnswerCount;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repetitionBinding = ActivityRepetitionBinding.inflate(getLayoutInflater());
        setContentView(repetitionBinding.getRoot());
        dbHelper = new DatabaseHelper(this);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        repetitionBinding.btnListen.setOnClickListener(v -> speak(currentWord));
        repetitionBinding.layoutAnswer3.setOnClickListener(v ->
                Toast.makeText(this, "Натиснуто Варіант 3", Toast.LENGTH_SHORT).show());

        loadRandomWord();
        loadWrongAnswers();
    }

    private void loadWrongAnswers() {
        List<String> answers = dbHelper.getRandomTranslations(currentWordId);
        correctAnswerIndex = new Random().nextInt(4);
        answers.add(correctAnswerIndex, currentTranslation);

        repetitionBinding.tvVariantValue1.setText(answers.get(0));
        repetitionBinding.tvVariantValue2.setText(answers.get(1));
        repetitionBinding.tvVariantValue3.setText(answers.get(2));
        repetitionBinding.tvVariantValue4.setText(answers.get(3));

    }


    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }


    private void loadRandomWord() {
        Cursor cursor = dbHelper.getRandomWord();
        if (cursor != null && cursor.moveToFirst()) {
            currentWord = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ENGLISH));
            currentWordId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            currentCorrectAnswerCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CORRECT_ANSWER_COUNT));
            currentWrongAnswerCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WRONG_ANSWER_COUNT));
            currentTranslation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSLATION));
            repetitionBinding.tvWord.setText(currentWord);
            repetitionBinding.tvCorrectCount.setText(String.valueOf(currentCorrectAnswerCount));
            repetitionBinding.tvWrongCount.setText(String.valueOf(currentWrongAnswerCount));
        }
        if(cursor != null){
            cursor.close();
        }
    }
    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
