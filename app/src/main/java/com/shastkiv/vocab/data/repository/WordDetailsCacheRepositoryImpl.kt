package com.shastkiv.vocab.data.repository

import com.shastkiv.vocab.data.local.dao.WordDetailsCacheDao
import com.shastkiv.vocab.data.remote.client.OpenAIClient
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.WordData
import com.shastkiv.vocab.domain.model.WordDetailsCache
import com.shastkiv.vocab.domain.repository.WordDetailsCacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordDetailsCacheRepositoryImpl @Inject constructor(
    private val cacheDao: WordDetailsCacheDao,
    private val openAIClient: OpenAIClient
) : WordDetailsCacheRepository {

    override suspend fun getWordDetails(
        word: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): Result<WordData> {
        return withContext(Dispatchers.IO) {
            val cachedData = cacheDao.getCacheForWord(word, sourceLanguage.code, targetLanguage.code)
            if (cachedData != null) {
                val wordData = WordData.fromJson(cachedData.jsonResponse)
                if (wordData != null) {
                    return@withContext Result.success(wordData)
                }
            }

            val networkResult = openAIClient.fetchWordInfo(
                word,
                sourceLanguageName = sourceLanguage.name,
                targetLanguageName = targetLanguage.name
            )

            networkResult.onSuccess { wordData ->
                val cacheEntry = WordDetailsCache(
                    sourceWord = word,
                    sourceLanguageCode = sourceLanguage.code,
                    targetLanguageCode = targetLanguage.code,
                    jsonResponse = wordData.toJson()
                )
                cacheDao.insert(cacheEntry)
            }

            return@withContext networkResult
        }
    }
}