package com.trustlens.app.data.model

import com.google.gson.annotations.SerializedName

data class ExtractResponse(
    @SerializedName("text") val extractedText: String,
    @SerializedName("issuer") val issuer: String?,
    @SerializedName("title") val title: String?
)

data class SourceDiscoveryRequest(
    @SerializedName("document_id") val documentId: String,
    @SerializedName("issuer") val issuer: String?
)

data class SourceDiscoveryResponse(
    @SerializedName("issuer") val issuer: String? = null,

    @SerializedName("official_source")
    val officialSource: String? = null,

    @SerializedName("source_content")
    val sourceContent: String? = null,

    @SerializedName("confidence")
    val confidence: Double? = 0.0
)

data class VerifyRequest(
    @SerializedName("document_id") val documentId: String,
    @SerializedName("extracted_text") val extractedText: String,
    @SerializedName("source_content") val sourceContent: String?,
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