package com.trustlens.app.data.repository

import android.content.Context
import android.net.Uri
import com.trustlens.app.data.model.SourceDiscoveryRequest
import com.trustlens.app.data.model.UploadUiState
import com.trustlens.app.data.model.VerifyApiResponse
import com.trustlens.app.data.remote.RetrofitClient
import com.trustlens.app.domain.CompareEngine
import com.trustlens.app.domain.GeminiAnalyzer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class VerificationRepository(private val context: Context) {

    fun verifyDocument(uri: Uri): Flow<UploadUiState> = flow {

        emit(UploadUiState.Uploading(0.1f, "Uploading document..."))

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot read file")

            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestBody =
                bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())

            val filePart =
                MultipartBody.Part.createFormData("file", "document", requestBody)

            // STEP 1 - OCR
            emit(UploadUiState.Uploading(0.4f, "Running OCR..."))

            val extractResponse =
                RetrofitClient.apiService.extractDocument(filePart)

            if (!extractResponse.isSuccessful || extractResponse.body() == null) {
                emit(UploadUiState.Error("OCR extraction failed"))
                return@flow
            }

            val extractResult = extractResponse.body()!!

            // STEP 2 - Source Discovery
            emit(UploadUiState.Uploading(0.6f, "Finding official source..."))

            val sourceRequest = SourceDiscoveryRequest(
                documentId = extractResult.documentId ?: "DOC-${System.currentTimeMillis()}",
                issuer = extractResult.issuer ?: "Unknown"
            )

            val sourceResponse =
                RetrofitClient.apiService.discoverSource(sourceRequest)

            if (!sourceResponse.isSuccessful || sourceResponse.body() == null) {
                emit(UploadUiState.Error("Source discovery failed"))
                return@flow
            }

            val sourceResult = sourceResponse.body()!!

            // STEP 3 - Compare
            val (similarity, differences) =
                CompareEngine.compareDocuments(
                    extractResult.extractedText ?: "",
                    sourceResult.sourceContent ?: ""
                )

            // STEP 4 - Gemini AI
            val aiSummary =
                GeminiAnalyzer.analyzeDocument(
                    extractResult.extractedText ?: "",
                    sourceResult.sourceContent ?: "",
                    extractResult.issuer ?: "Unknown",
                    similarity,
                    differences
                )

            val risk = when {
                sourceResult.sourceContent.isNullOrBlank() -> "Unverified"
                similarity >= 85 -> "Low"
                similarity >= 60 -> "Medium"
                else -> "High"
            }

            val verdict = when {
                sourceResult.sourceContent.isNullOrBlank() -> "UNVERIFIED"
                similarity >= 85 -> "AUTHENTIC"
                similarity >= 60 -> "SUSPICIOUS"
                else -> "FAKE"
            }

            emit(
                UploadUiState.Success(
                    VerifyApiResponse(
                        success = true,
                        documentId = extractResult.documentId
                            ?: "DOC-${System.currentTimeMillis()}",
                        trustScore = similarity,
                        risk = risk,
                        summary = aiSummary,
                        differences = differences,
                        verdict = verdict
                    )
                )
            )

        } catch (e: Exception) {
            emit(
                UploadUiState.Error(
                    e.message ?: "Something went wrong"
                )
            )
        }
    }
}