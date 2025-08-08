package com.example.learnwordstrainer.data.repository

import com.example.learnwordstrainer.data.local.dao.WordDetailsCacheDao
import com.example.learnwordstrainer.data.remote.client.OpenAIClient
import com.example.learnwordstrainer.domain.model.WordData
import com.example.learnwordstrainer.domain.model.WordDetailsCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordDetailsCacheRepositoryImpl @Inject constructor(
    private val cacheDao: WordDetailsCacheDao,
    private val openAIClient: OpenAIClient
) : WordDetailsCacheRepository {

    override suspend fun getWordDetails(word: String): Result<WordData> {
        return withContext(Dispatchers.IO) {
            val cachedData = cacheDao.getCacheForWord(word)
            if (cachedData != null) {
                val wordData = WordData.fromJson(cachedData.jsonResponse)
                if (wordData != null) {
                    return@withContext Result.success(wordData)
                }
            }

            val networkResult = openAIClient.fetchWordInfo(word)
            networkResult.onSuccess { wordData ->
                val cacheEntry = WordDetailsCache(
                    englishWord = word,
                    jsonResponse = wordData.toJson()
                )
                cacheDao.insert(cacheEntry)
            }

            return@withContext networkResult
        }
    }
}