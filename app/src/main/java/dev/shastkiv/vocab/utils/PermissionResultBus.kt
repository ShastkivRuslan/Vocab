package dev.shastkiv.vocab.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PermissionResultBus {
    private val _results = MutableSharedFlow<PermissionResult>(extraBufferCapacity = 1)
    val results = _results.asSharedFlow()

    fun send(result: PermissionResult) {
        _results.tryEmit(result)
    }
}

data class PermissionResult(
    val permission: String,
    val granted: Boolean
)