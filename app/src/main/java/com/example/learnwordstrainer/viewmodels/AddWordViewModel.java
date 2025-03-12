package com.example.learnwordstrainer.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.learnwordstrainer.repository.WordRepository;

public class AddWordViewModel extends AndroidViewModel {
    private final WordRepository wordRepository;

    private final MutableLiveData<String> message = new MutableLiveData<>();

    private final MutableLiveData<Boolean> wordAdded = new MutableLiveData<>(false);

    public AddWordViewModel(Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<Boolean> getWordAdded() {
        return wordAdded;
    }

    public void clearMessage() {
        message.setValue("");
    }

    public void resetWordAdded() {
        wordAdded.setValue(false);
    }

    public void addWord(String english, String translation) {
        if (english.isEmpty() || translation.isEmpty()) {
            message.setValue("Заповніть усі поля");
            return;
        }

        if (!english.matches("[a-zA-Z ]+")) {
            message.setValue("Англійське слово повинно містити тільки латинські літери");
            return;
        }

        if (!translation.matches("[а-яА-ЯіІїЇєЄґҐ' ]+")) {
            message.setValue("Переклад повинен містити тільки українські літери");
            return;
        }

        if (wordRepository.wordExists(english)) {
            message.setValue("Це слово вже існує в словнику");
            return;
        }

        wordRepository.addWord(english, translation);
        message.setValue("Слово додано");
        wordAdded.setValue(true);
    }
}