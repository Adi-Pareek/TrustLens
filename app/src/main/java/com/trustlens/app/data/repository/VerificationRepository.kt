package com.trustlens.app.data.repository

import android.content.Context
import android.net.Uri
import com.trustlens.app.data.model.ExtractResponse
import com.trustlens.app.data.model.DocumentMetadata
import com.trustlens.app.data.model.SourceDiscoveryRequest
import com.trustlens.app.data.model.SourceDiscoveryResponse
import com.trustlens.app.data.model.UploadUiState
import com.trustlens.app.data.model.VerifyApiResponse
import com.trustlens.app.data.model.VerifyRequest
import com.trustlens.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class VerificationRepository(private val context: Context) {


    // MAIN FUNCTION — called from ViewModel
    // Chains: Extract → Source Discovery → Verify

    fun verifyDocument(uri: Uri): Flow<UploadUiState> = flow {

        // Step 0 — Tell UI we are starting
        emit(UploadUiState.Uploading(0.1f, "📤 Uploading document..."))

        try {


            // REAL API CALLS
            // Remove the "if (USE_MOCK)" block below
            // when Members 3 & 4 give you working APIs

            if (USE_MOCK) {
                // ── MOCK PATH (active right now) ──
                emit(UploadUiState.Uploading(0.3f, "🔎 Running OCR scan..."))
                kotlinx.coroutines.delay(1000)

                emit(UploadUiState.Uploading(0.5f, "🌐 Discovering official source..."))
                kotlinx.coroutines.delay(1000)

                emit(UploadUiState.Uploading(0.7f, "🤖 AI analysis in progress..."))
                kotlinx.coroutines.delay(1000)

                emit(UploadUiState.Uploading(0.9f, "📊 Generating trust score..."))
                kotlinx.coroutines.delay(1000)

                emit(UploadUiState.Success(mockVerifyResponse()))

            } else {
                // ── REAL API PATH (uncomment when backend is ready) ──

                // STEP 1 — Read file from device
                emit(UploadUiState.Uploading(0.2f, "📤 Uploading document..."))
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Cannot read file from device")
                val bytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", "document", requestBody)

                // STEP 2 — Call /extract (Member 3)
                emit(UploadUiState.Uploading(0.4f, "🔎 Running OCR scan..."))
                val extractResponse = RetrofitClient.apiService.extractDocument(filePart)
                if (!extractResponse.isSuccessful || extractResponse.body() == null) {
                    emit(UploadUiState.Error("OCR extraction failed: ${extractResponse.code()}"))
                    return@flow
                }
                val extractResult = extractResponse.body()!!

                // STEP 3 — Call /source-discovery (Member 3)
                emit(UploadUiState.Uploading(0.6f, "🌐 Discovering official source..."))
                val sourceRequest = SourceDiscoveryRequest(
                    documentId = extractResult.documentId,
                    issuer = extractResult.issuer
                )
                val sourceResponse = RetrofitClient.apiService.discoverSource(sourceRequest)
                if (!sourceResponse.isSuccessful || sourceResponse.body() == null) {
                    emit(UploadUiState.Error("Source discovery failed: ${sourceResponse.code()}"))
                    return@flow
                }
                val sourceResult = sourceResponse.body()!!

                // STEP 4 — Call /verify (Member 4)
                emit(UploadUiState.Uploading(0.8f, "🤖 AI analysis in progress..."))
                val verifyRequest = VerifyRequest(
                    documentId = extractResult.documentId,
                    extractedText = extractResult.extractedText,
                    sourceContent = sourceResult.sourceContent,
                    issuer = extractResult.issuer
                )
                val verifyResponse = RetrofitClient.apiService.verifyDocument(verifyRequest)
                if (!verifyResponse.isSuccessful || verifyResponse.body() == null) {
                    emit(UploadUiState.Error("Verification failed: ${verifyResponse.code()}"))
                    return@flow
                }

                // STEP 5 — Done!
                emit(UploadUiState.Uploading(1.0f, "📊 Generating trust score..."))
                emit(UploadUiState.Success(verifyResponse.body()!!))
            }

        } catch (e: Exception) {
            emit(UploadUiState.Error(e.message ?: "Something went wrong. Please try again."))
        }
    }


    // MOCK DATA
    // Simulates a real backend response
    // Delete this whole block when backend is ready

    private fun mockVerifyResponse() = VerifyApiResponse(
        success = true,
        documentId = "DOC-${System.currentTimeMillis()}",
        trustScore = 87,
        risk = "Low",
        summary = "The document shows no signs of digital manipulation or content tampering. " +
                "Text layers are consistent, metadata aligns with the document creation date, " +
                "and formatting patterns match authentic templates.",
        differences = emptyList(),
        verdict = "AUTHENTIC"
    )

    companion object {
        // ⚠️ Set this to false when Members 3 & 4 APIs are ready
        private const val USE_MOCK = true
    }
}