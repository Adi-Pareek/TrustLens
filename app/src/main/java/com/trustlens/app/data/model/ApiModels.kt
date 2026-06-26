package com.trustlens.app.data.model

import com.google.gson.annotations.SerializedName

// /extract (Member 3)

data class ExtractResponse(
    @SerializedName("success") val success: Boolean? = true,
    @SerializedName("document_id") val documentId: String? = null,
    @SerializedName("issuer") val issuer: String,
    @SerializedName("title") val title: String,
    @SerializedName("text") val extractedText: String,
    @SerializedName("metadata") val metadata: DocumentMetadata? = null
)

data class DocumentMetadata(
    @SerializedName("file_type") val fileType: String? = null,
    @SerializedName("page_count") val pageCount: Int? = null,
    @SerializedName("created_date") val createdDate: String? = null,
    @SerializedName("language") val language: String? = null
)


// /source-discovery (Member 3)

data class SourceDiscoveryRequest(
    @SerializedName("document_id") val documentId: String,
    @SerializedName("issuer") val issuer: String
)

data class SourceDiscoveryResponse(
    @SerializedName("success") val success: Boolean? = true,
    @SerializedName("document_id") val documentId: String? = null,
    @SerializedName("official_url") val officialUrl: String? = null,
    @SerializedName("source_found") val sourceFound: Boolean? = false,
    @SerializedName("source_content") val sourceContent: String
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