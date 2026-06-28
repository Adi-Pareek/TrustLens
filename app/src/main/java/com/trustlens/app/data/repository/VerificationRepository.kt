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

    fun verifyDocument(uri: Uri): Flow<UploadUiState> = flow<UploadUiState> {

        emit(UploadUiState.Uploading(0.1f, "Uploading document..."))

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot read file")

            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestBody =
                bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())

            val filePart =
                MultipartBody.Part.createFormData(
                    "file",
                    "document.pdf",
                    requestBody
                )

            // STEP 1 - OCR
            emit(UploadUiState.Uploading(0.3f, "Extracting text..."))

            val extractResponse =
                RetrofitClient.apiService.extractDocument(filePart)

            if (!extractResponse.isSuccessful || extractResponse.body() == null) {
                emit(UploadUiState.Error("OCR extraction failed"))
                return@flow
            }

            val extractResult = extractResponse.body()!!

            // STEP 2 - Source Discovery
            emit(UploadUiState.Uploading(0.5f, "Finding official source..."))

            val sourceRequest = SourceDiscoveryRequest(
                documentId = "DOC-${System.currentTimeMillis()}",
                issuer = extractResult.issuer ?: "Unknown"
            )

            val sourceResponse =
                RetrofitClient.apiService.discoverSource(sourceRequest)

            if (!sourceResponse.isSuccessful || sourceResponse.body() == null) {
                emit(UploadUiState.Error("Source discovery failed"))
                return@flow
            }

            val sourceResult = sourceResponse.body()!!

            // STEP 3 - Compare documents
            emit(UploadUiState.Uploading(0.7f, "Comparing documents..."))

            val extractedText = extractResult.extractedText
            val sourceText = sourceResult.sourceContent ?: ""

            val (similarity, differences) =
                CompareEngine.compareDocuments(
                    extractedText,
                    sourceText
                )

            // STEP 4 - AI Analysis
            emit(UploadUiState.Uploading(0.9f, "Analyzing with AI..."))

            val aiSummary =
                GeminiAnalyzer.analyzeDocument(
                    extractedText,
                    sourceText,
                    extractResult.issuer ?: "Unknown",
                    similarity,
                    differences
                )

            // Risk + Verdict Logic
            val risk: String
            val verdict: String

            when {
                similarity >= 85 -> {
                    risk = "Low"
                    verdict = "AUTHENTIC"
                }

                similarity >= 60 -> {
                    risk = "Medium"
                    verdict = "SUSPICIOUS"
                }

                similarity > 0 -> {
                    risk = "High"
                    verdict = "FAKE"
                }

                else -> {
                    risk = "Unverified"
                    verdict = "UNVERIFIED"
                }
            }

            emit(
                UploadUiState.Success(
                    VerifyApiResponse(
                        success = true,
                        documentId = "DOC-${System.currentTimeMillis()}",
                        trustScore = similarity.coerceIn(0, 100),
                        risk = risk,
                        summary = aiSummary,
                        differences = differences,
                        verdict = verdict,
                        issuer = extractResult.issuer,
                        confidence = sourceResult.confidence,
                        officialSource = sourceResult.officialSource
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