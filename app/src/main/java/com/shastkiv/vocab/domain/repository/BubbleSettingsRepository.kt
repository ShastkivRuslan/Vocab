package com.shastkiv.vocab.domain.repository

import com.shastkiv.vocab.domain.model.BubblePosition
import kotlinx.coroutines.flow.Flow

interface BubbleSettingsRepository {

    val position: Flow<BubblePosition>
    val isBubbleEnabled: Flow<Boolean>
    val bubbleSize: Flow<Float>
    val bubbleTransparency: Flow<Float>
    val isVibrationEnabled: Flow<Boolean>
    val autoHideAppList: Flow<Set<String>>

    suspend fun savePosition(x: Int, y: Int)
    suspend fun setBubbleEnabled(isEnabled: Boolean)
    suspend fun setBubbleSize(size: Float)
    suspend fun setBubbleTransparency(transparency: Float)
    suspend fun setVibrationEnabled(isEnabled: Boolean)
    suspend fun addAppToAutoHideList(packageName: String)
    suspend fun removeAppFromAutoHideList(packageName: String)
}
