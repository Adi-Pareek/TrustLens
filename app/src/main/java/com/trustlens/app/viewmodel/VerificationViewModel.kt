package com.trustlens.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trustlens.app.data.model.UploadUiState
import com.trustlens.app.data.repository.VerificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerificationViewModel(application: Application) : AndroidViewModel(application) {

    // Repository handles all API calls
    private val repository = VerificationRepository(application)

    // UI watches this to know what to show
    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    // ─────────────────────────────────────────
    // Called when user picks a file and taps Verify
    // ─────────────────────────────────────────
    fun verifyDocument(uri: Uri) {
        viewModelScope.launch {
            repository.verifyDocument(uri).collect { state ->
                _uiState.value = state
            }
        }
    }

    // ─────────────────────────────────────────
    // Called when user taps "Try Again" on error
    // or "Verify Another" on success
    // ─────────────────────────────────────────
    fun reset() {
        _uiState.value = UploadUiState.Idle
    }
}