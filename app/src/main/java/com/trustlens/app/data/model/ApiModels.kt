package com.trustlens.app.data.model

import com.google.gson.annotations.SerializedName


// /extract  (Member 3)


data class ExtractResponse(
    @SerializedName("success")       val success: Boolean,
    @SerializedName("document_id")   val documentId: String,
    @SerializedName("issuer")        val issuer: String,
    @SerializedName("title")         val title: String,
    @SerializedName("text")          val extractedText: String,
    @SerializedName("metadata")      val metadata: DocumentMetadata
)

data class DocumentMetadata(
    @SerializedName("file_type")     val fileType: String,
    @SerializedName("page_count")    val pageCount: Int,
    @SerializedName("created_date")  val createdDate: String,
    @SerializedName("language")      val language: String
)


// /source-discovery  (Member 3)


data class SourceDiscoveryRequest(
    @SerializedName("document_id")   val documentId: String,
    @SerializedName("issuer")        val issuer: String
)

data class SourceDiscoveryResponse(
    @SerializedName("success")         val success: Boolean,
    @SerializedName("document_id")     val documentId: String,
    @SerializedName("official_url")    val officialUrl: String,
    @SerializedName("source_found")    val sourceFound: Boolean,
    @SerializedName("source_content")  val sourceContent: String
)


// /verify  (Member 4)


data class VerifyRequest(
    @SerializedName("document_id")    val documentId: String,
    @SerializedName("extracted_text") val extractedText: String,
    @SerializedName("source_content") val sourceContent: String,
    @SerializedName("issuer")         val issuer: String
)

data class VerifyApiResponse(
    @SerializedName("success")       val success: Boolean,
    @SerializedName("document_id")   val documentId: String,
    @SerializedName("trust_score")   val trustScore: Int,
    @SerializedName("risk")          val risk: String,       // "Low" / "Medium" / "High"
    @SerializedName("summary")       val summary: String,
    @SerializedName("differences")   val differences: List<String>,
    @SerializedName("verdict")       val verdict: String
)


// UI State — used by ViewModel & Screens


sealed class UploadUiState {
    object Idle : UploadUiState()
    data class Uploading(
        val progress: Float,
        val statusMessage: String
    ) : UploadUiState()
    data class Success(val result: VerifyApiResponse) : UploadUiState()
    data class Error(val message: String) : UploadUiState()
}