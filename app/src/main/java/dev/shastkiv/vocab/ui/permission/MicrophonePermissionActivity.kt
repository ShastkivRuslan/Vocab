package dev.shastkiv.vocab.ui.permission

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import dev.shastkiv.vocab.utils.PermissionResult
import dev.shastkiv.vocab.utils.PermissionResultBus

/**
 * Прозора Activity виключно для запиту RECORD_AUDIO дозволу.
 * Використовується з overlay/сервісу де неможливо викликати ActivityResultLauncher напряму.
 * Після отримання відповіді надсилає результат через PermissionResultBus і закривається.
 */
class MicrophonePermissionActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        PermissionResultBus.send(
            PermissionResult(
                permission = Manifest.permission.RECORD_AUDIO,
                granted = granted
            )
        )
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}