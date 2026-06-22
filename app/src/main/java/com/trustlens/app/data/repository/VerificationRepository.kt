package com.trustlens.app.data.repository

import android.content.Context
import android.net.Uri
import com.trustlens.app.data.model.ChecklistItemModel
import com.trustlens.app.data.model.SourceVerificationModel
import com.trustlens.app.data.model.VerificationResponse
import com.trustlens.app.data.model.VerificationState
import com.trustlens.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class VerificationRepository(private val context: Context) {

    fun verifyDocument(uri: Uri): Flow<VerificationState> = flow {
        emit(VerificationState.Loading)

        try {
            // Real API call — uncomment when backend is ready
            /*
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: throw Exception("Cannot read file")
            val requestBody = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", "document", requestBody)
            val response = RetrofitClient.apiService.verifyDocument(filePart)

            if (response.isSuccessful && response.body() != null) {
                emit(VerificationState.Success(response.body()!!))
            } else {
                emit(VerificationState.Error("Verification failed: ${response.code()}"))
            }
            */

            // MOCK response — remove when backend is ready
            kotlinx.coroutines.delay(3000)
            emit(VerificationState.Success(mockVerificationResponse()))

        } catch (e: Exception) {
            emit(VerificationState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    // Mock data — teammate replaces with real API
    private fun mockVerificationResponse() = VerificationResponse(
        success = true,
        documentId = "DOC-${System.currentTimeMillis()}",
        trustScore = 87,
        riskLevel = "LOW",
        aiSummary = "The document shows no signs of digital manipulation or content tampering. Text layers are consistent, metadata aligns with the document creation date, and formatting patterns match authentic templates.",
        verificationChecklist = listOf(
            ChecklistItemModel("OCR Text Extraction", true, "All text successfully extracted"),
            ChecklistItemModel("Metadata Integrity", true, "Timestamps match document age"),
            ChecklistItemModel("Digital Signature", true, "Signature block detected"),
            ChecklistItemModel("Source Cross-check", true, "Matched in 3 trusted databases"),
            ChecklistItemModel("Tampering Detection", true, "No edits detected"),
            ChecklistItemModel("Format Validation", true, "Structure matches original template")
        ),
        sourceVerifications = listOf(
            SourceVerificationModel("Government Database", "Matched", true),
            SourceVerificationModel("University Registry", "Matched", true),
            SourceVerificationModel("Issuer Portal", "Verified", true)
        ),
        detectedIssues = emptyList(),
        scanTimeSeconds = 18,
        verifiedBy = "AI + OCR"
    )
}