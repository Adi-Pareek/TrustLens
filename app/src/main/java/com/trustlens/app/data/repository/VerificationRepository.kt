package com.trustlens.app.data.repository

import android.content.Context
import android.net.Uri
import com.trustlens.app.data.model.SourceDiscoveryRequest
import com.trustlens.app.data.model.UploadUiState
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
                issuer = extractResult.issuer
            )

            val sourceResponse =
                RetrofitClient.apiService.discoverSource(sourceRequest)

            if (!sourceResponse.isSuccessful || sourceResponse.body() == null) {
                emit(UploadUiState.Error("Source discovery failed"))
                return@flow
            }

            val sourceResult = sourceResponse.body()!!

            // STEP 3 - Compare
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
                    extractResult.issuer,
                    similarity,
                    differences
                )

            // STEP 5 - Final verification API (send FILE, not JSON)
            val verifyResponse =
                RetrofitClient.apiService.verifyDocument(filePart)

            if (!verifyResponse.isSuccessful || verifyResponse.body() == null) {
                emit(
                    UploadUiState.Error(
                        "Final verification failed: ${verifyResponse.errorBody()?.string()}"
                    )
                )
                return@flow
            }

            val finalResult = verifyResponse.body()!!

            emit(
                UploadUiState.Success(
                    finalResult.copy(
                        documentId = finalResult.documentId ?: "DOC-${System.currentTimeMillis()}",
                        trustScore = similarity.coerceIn(0, 100),
                        risk = finalResult.risk ?: "Unverified",
                        summary = aiSummary.ifBlank { finalResult.summary ?: "Analysis completed" },
                        differences = if (differences.isNotEmpty()) differences else finalResult.differences ?: emptyList(),
                        verdict = finalResult.verdict ?: "Pending Review",
                        issuer = sourceResult.issuer,
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