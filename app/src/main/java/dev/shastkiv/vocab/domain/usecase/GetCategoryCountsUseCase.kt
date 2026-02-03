package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.CategoryCounts
import dev.shastkiv.vocab.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCategoryCountsUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val getLanguageSettingsUseCase: GetLanguageSettingsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
){
    operator fun invoke() : Flow<CategoryCounts> = flow {
        val languageSettings = getLanguageSettingsUseCase()
        val langCode = languageSettings.sourceLanguage.code

        val countsFlow = wordRepository.getCategoryCounts(langCode)

        emitAll(countsFlow)
    }.flowOn(ioDispatcher)
}