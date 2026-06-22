package com.trustlens.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trustlens.app.data.model.VerificationResponse
import com.trustlens.app.data.model.VerificationState
import com.trustlens.app.data.repository.VerificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VerificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VerificationRepository(application)

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    private val _statusMessage = MutableStateFlow("Preparing...")
    val statusMessage: StateFlow<String> = _statusMessage

    private val _result = MutableStateFlow<VerificationResponse?>(null)
    val result: StateFlow<VerificationResponse?> = _result

    fun verifyDocument(uri: Uri) {
        viewModelScope.launch {
            // Simulate progress messages while backend processes
            launch { simulateProgress() }

            repository.verifyDocument(uri).collect { state ->
                _verificationState.value = state
                if (state is VerificationState.Success) {
                    _result.value = state.result
                    _uploadProgress.value = 1f
                }
            }
        }
    }

    private suspend fun simulateProgress() {
        val steps = listOf(
            0.2f to "📤 Uploading document...",
            0.4f to "🔎 Running OCR scan...",
            0.65f to "🤖 AI analysis in progress...",
            0.85f to "🌐 Cross-checking sources...",
            0.95f to "📊 Generating trust score..."
        )
        for ((progress, message) in steps) {
            kotlinx.coroutines.delay(500)
            _uploadProgress.value = progress
            _statusMessage.value = message
        }
    }

    fun reset() {
        _verificationState.value = VerificationState.Idle
        _uploadProgress.value = 0f
        _statusMessage.value = "Preparing..."
        _result.value = null
    }
}