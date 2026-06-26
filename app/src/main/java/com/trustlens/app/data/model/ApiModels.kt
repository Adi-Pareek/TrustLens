package com.trustlens.app.data.model

import com.google.gson.annotations.SerializedName

// /extract (Member 3)

data class ExtractResponse(
    @SerializedName("text") val extractedText: String?,
    @SerializedName("issuer") val issuer: String?,
    @SerializedName("title") val title: String?
)


// /source-discovery (Member 3)

data class SourceDiscoveryRequest(
    @SerializedName("document_id") val documentId: String,
    @SerializedName("issuer") val issuer: String?
)

data class SourceDiscoveryResponse(
    @SerializedName("issuer") val issuer: String? = null,
    @SerializedName("official_source") val officialSource: String? = null,
    @SerializedName("confidence") val confidence: Double? = 0.0
)


// /verify (Member 4)

data class VerifyRequest(
    @SerializedName("document_id") val documentId: String,
    @SerializedName("extracted_text") val extractedText: String,
    @SerializedName("source_content") val sourceContent: String? = null,
    @SerializedName("issuer") val issuer: String
)

data class VerifyApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("document_id") val documentId: String,
    @SerializedName("trust_score") val trustScore: Int,
    @SerializedName("risk") val risk: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("differences") val differences: List<String>,
    @SerializedName("verdict") val verdict: String
)


// UI State

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