package dev.shastkiv.vocab.ui.settings.bubble

sealed class BubbleEvent {
    object OpenOverlaySettings : BubbleEvent()
    object PermissionGrantedSuccess : BubbleEvent()
}