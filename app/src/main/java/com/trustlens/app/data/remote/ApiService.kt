package com.trustlens.app.data.remote

import com.trustlens.app.data.model.VerificationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("process-document")
    suspend fun verifyDocument(
        @Part file: MultipartBody.Part
    ): Response<VerificationResponse>
}