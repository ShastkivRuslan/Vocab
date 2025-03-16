package com.example.learnwordstrainer.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.learnwordstrainer.repository.WordRepository;

public class AddWordFloatingViewModel extends AndroidViewModel {

    private final WordRepository wordRepository;
    public MutableLiveData<Boolean> wordAdded = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AddWordFloatingViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    public void addWord(String word, String translation) {
        if (word.isEmpty() || translation.isEmpty()) {
            errorMessage.setValue("Заповніть всі поля");
        } else {
            wordRepository.addWord(word, translation);
            wordAdded.setValue(true);
        }
    }
}

