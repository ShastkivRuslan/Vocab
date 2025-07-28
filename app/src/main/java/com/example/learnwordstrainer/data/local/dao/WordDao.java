package com.example.learnwordstrainer.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.learnwordstrainer.domain.model.Word;
import java.util.List;

@Dao
public interface WordDao {
    @Insert
    void addWord(Word word);

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    Word getRandomWord();

    @Query("SELECT translation FROM words WHERE _id != :excludeId ORDER BY RANDOM() LIMIT 3")
    List<String> getRandomTranslations(int excludeId);

    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE english_word = :englishWord)")
    boolean wordExists(String englishWord);

    @Query("SELECT COUNT(*) FROM words")
    int getWordCount();

    @Query("SELECT COUNT(*) FROM words WHERE correct_count - wrong_count >= 10")
    int getLearnedWordsCount();

    @Query("UPDATE words SET correct_count = :correctCount, wrong_count = :wrongCount WHERE _id = :id")
    void updateScore(int id, int correctCount, int wrongCount);
}
