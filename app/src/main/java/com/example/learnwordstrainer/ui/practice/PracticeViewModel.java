package com.example.learnwordstrainer.ui.practice;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.learnwordstrainer.data.remote.client.OpenAIClient;
import com.example.learnwordstrainer.data.remote.dto.ChatCompletionResponse;
import com.example.learnwordstrainer.domain.model.Example;
import com.example.learnwordstrainer.domain.model.ExampleData;
import com.example.learnwordstrainer.domain.model.Word;
import com.example.learnwordstrainer.domain.model.WordInfoResponse;
import com.example.learnwordstrainer.data.repository.WordRepository;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeViewModel extends AndroidViewModel {
    private static final String TAG = "PracticeViewModel";

    private final WordRepository wordRepository;
    private final OpenAIClient aiClient;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<ExampleData>> examplesList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");

    public PracticeViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
        aiClient = new OpenAIClient();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<ExampleData>> getExamplesList() {
        return examplesList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Отримує приклади використання слова від AI
     * @param word слово, для якого потрібні приклади
     */
    public void getExamplesFromAI(String word) {
        if (TextUtils.isEmpty(word)) {
            errorMessage.setValue("Відсутнє слово для запиту до AI");
            return;
        }

        isLoading.setValue(true);

        aiClient.fetchExamplesFromGPT(word, new Callback<ChatCompletionResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatCompletionResponse> call,
                                   @NonNull Response<ChatCompletionResponse> response) {
                isLoading.postValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ChatCompletionResponse result = response.body();
                        if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                            String content = result.getChoices().get(0).getMessage().getContent();
                            WordInfoResponse wordResponse = parseWordInfo(content);
                            List<ExampleData> examples = convertToExampleData(wordResponse.getExamples());
                            examplesList.postValue(examples);
                        } else {
                            errorMessage.postValue("Отримано порожню відповідь від сервера");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing API response", e);
                        errorMessage.postValue("Помилка обробки відповіді: " + e.getMessage());
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatCompletionResponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Помилка мережі: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }

    private void handleErrorResponse(Response<ChatCompletionResponse> response) {
        String errorBody = "";
        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error body", e);
        }

        errorMessage.postValue("Помилка API: " + response.code() + " " + errorBody);
    }

    public WordInfoResponse parseWordInfo(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return createEmptyWordInfoResponse();
        }

        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, WordInfoResponse.class);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing JSON", e);
            return createEmptyWordInfoResponse();
        }
    }

    private WordInfoResponse createEmptyWordInfoResponse() {
        WordInfoResponse response = new WordInfoResponse();
        response.setTranslation("");
        response.setTranscription("");
        response.setPartOfSpeech("");
        response.setExamples(Collections.emptyList());
        return response;
    }

    private List<ExampleData> convertToExampleData(List<Example> examples) {
        List<ExampleData> result = new ArrayList<>();
        if (examples == null || examples.isEmpty()) {
            // Повертаємо хоча б один пустий приклад, щоб уникнути помилок відображення
            result.add(new ExampleData("No examples available", "Приклади відсутні"));
            return result;
        }

        for (Example example : examples) {
            String sentence = example.getSentence();
            String translation = example.getTranslation();

            if (!TextUtils.isEmpty(sentence)) {
                result.add(new ExampleData(sentence, translation != null ? translation : ""));
            }
        }

        return result;
    }

    public String getNextWord() {
        Word randomWord = wordRepository.getRandomWord();
        return randomWord != null ? randomWord.getEnglishWord() : "";
    }
}