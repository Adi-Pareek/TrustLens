package com.trustlens.app.data.model

data class VerificationRequest(
    val fileName: String,
    val fileType: String,
    val fileSize: Long
)

data class VerificationResponse(
    val success: Boolean,
    val documentId: String,
    val trustScore: Int,
    val riskLevel: String,          // "LOW", "MEDIUM", "HIGH"
    val aiSummary: String,
    val verificationChecklist: List<ChecklistItemModel>,
    val sourceVerifications: List<SourceVerificationModel>,
    val detectedIssues: List<String>,
    val scanTimeSeconds: Int,
    val verifiedBy: String
)

data class ChecklistItemModel(
    val label: String,
    val passed: Boolean,
    val detail: String
)

data class SourceVerificationModel(
    val source: String,
    val status: String,
    val verified: Boolean
)

// UI State wrapper
sealed class VerificationState {
    object Idle : VerificationState()
    object Loading : VerificationState()
    data class Success(val result: VerificationResponse) : VerificationState()
    data class Error(val message: String) : VerificationState()
}

// Upload progress state
sealed class UploadState {
    object Idle : UploadState()
    data class Uploading(val progress: Float, val statusMessage: String) : UploadState()
    object Done : UploadState()
    data class Error(val message: String) : UploadState()
}