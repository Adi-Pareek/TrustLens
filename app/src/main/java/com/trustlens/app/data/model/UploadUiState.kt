package com.trustlens.app.data.model

sealed class UploadUiState {

    object Idle : UploadUiState()

    data class Uploading(
        val progress: Float,
        val statusMessage: String
    ) : UploadUiState()

    data class Success(
        val result: VerifyApiResponse
    ) : UploadUiState()

    data class Error(
        val message: String
    ) : UploadUiState()
}