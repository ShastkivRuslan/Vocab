package com.example.learnwordstrainer.viewmodels;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull ;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.learnwordstrainer.client.OpenAIClient;
import com.example.learnwordstrainer.integration.ChatCompletionResponse;
import com.example.learnwordstrainer.model.Example;
import com.example.learnwordstrainer.model.WordInfoResponse;
import com.example.learnwordstrainer.repository.WordRepository;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWordFloatingViewModel extends AndroidViewModel {
    private static final String TAG = "AddWordViewModel";

    private final OpenAIClient aiClient;
    private final WordRepository wordRepository;
    private final MutableLiveData<String> currentWord = new MutableLiveData<>("");
    private final MutableLiveData<String> currentTranslation = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> wordAdded = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private final MutableLiveData<String> examples = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AddWordFloatingViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
        aiClient = new OpenAIClient();
    }

    public LiveData<String> getCurrentWord() {
        return currentWord;
    }

    public LiveData<String> getCurrentTranslation() {
        return currentTranslation;
    }

    public LiveData<Boolean> getWordAdded() {
        return wordAdded;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getExamples() {
        return examples;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void addWord() {
        String word = currentWord.getValue();
        String translation = currentTranslation.getValue();

        if (TextUtils.isEmpty(word) || TextUtils.isEmpty(translation)) {
            errorMessage.setValue("Помилка, відсутні необхідні дані!");
            return;
        }

        try {
            wordRepository.addWord(word, translation);
            wordAdded.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "Error adding word", e);
            errorMessage.setValue("Помилка додавання слова: " + e.getMessage());
        }
    }

    public void getAiWordInfo(String word) {
        if (TextUtils.isEmpty(word)) {
            errorMessage.setValue("Введіть слово для пошуку");
            return;
        }

        isLoading.setValue(true);
        currentWord.setValue(word);

        aiClient.fetchExamplesFromGPT(word, new Callback<ChatCompletionResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatCompletionResponse> call,
                                   @NonNull Response<ChatCompletionResponse> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ChatCompletionResponse result = response.body();
                        if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                            String content = result.getChoices().get(0).getMessage().getContent();
                            WordInfoResponse wordResponse = parseWordInfo(content);

                            currentTranslation.setValue(wordResponse.getTranslation());
                            examples.setValue(buildWordInfoMessage(wordResponse));
                        } else {
                            errorMessage.setValue("Отримано порожню відповідь від сервера");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing API response", e);
                        errorMessage.setValue("Помилка обробки відповіді: " + e.getMessage());
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatCompletionResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Помилка мережі: " + t.getMessage());
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

        errorMessage.setValue("Помилка API: " + response.code() + " " + errorBody);
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

    public String buildWordInfoMessage(WordInfoResponse wordInfo) {
        if (wordInfo == null) {
            return "Інформацію про слово не вдалося отримати";
        }

        StringBuilder message = new StringBuilder();

        String translation = wordInfo.getTranslation();
        if (!TextUtils.isEmpty(translation)) {
            message.append("Переклад: ").append(translation).append("\n\n");
        } else {
            message.append("Переклад: не знайдено\n\n");
        }

        String transcription = wordInfo.getTranscription();
        if (!TextUtils.isEmpty(transcription)) {
            message.append("Транскрипція: [").append(transcription).append("]\n\n");
        }

        String partOfSpeech = wordInfo.getPartOfSpeech();
        if (!TextUtils.isEmpty(partOfSpeech)) {
            message.append("Частина мови: ").append(partOfSpeech).append("\n\n");
        }

        List<Example> examples = wordInfo.getExamples();
        if (examples != null && !examples.isEmpty()) {
            message.append("Приклади:\n");

            for (int i = 0; i < examples.size(); i++) {
                Example example = examples.get(i);
                if (example != null) {
                    String sentence = example.getSentence();
                    String exampleTranslation = example.getTranslation();

                    if (!TextUtils.isEmpty(sentence)) {
                        message.append(i + 1).append(". ").append(sentence).append("\n");
                    }

                    if (!TextUtils.isEmpty(exampleTranslation)) {
                        message.append("   ").append(exampleTranslation).append("\n\n");
                    } else {
                        message.append("\n");
                    }
                }
            }
        }

        return message.toString();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}