package com.trustlens.app.data.remote

import com.trustlens.app.data.model.SourceDiscoveryRequest
import com.trustlens.app.data.model.VerifyApiResponse
import com.trustlens.app.data.model.VerifyRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.trustlens.app.data.model.ExtractResponse
import com.trustlens.app.data.model.SourceDiscoveryResponse
interface ApiService {

    // Member 3 — /extract
    @Multipart
    @POST("extract/")
    suspend fun extractDocument(
        @Part file: MultipartBody.Part
    ): Response<ExtractResponse>

    // Member 3 — /source-discovery
    @POST("source-discovery/")
    suspend fun discoverSource(
        @Body request: SourceDiscoveryRequest
    ): Response<SourceDiscoveryResponse>

    // Member 4 — /verify
    @POST("verify")
    suspend fun verifyDocument(
        @Body request: VerifyRequest
    ): Response<VerifyApiResponse>
}