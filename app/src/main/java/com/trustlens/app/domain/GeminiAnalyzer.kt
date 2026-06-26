package com.trustlens.app.domain

import com.google.ai.client.generativeai.GenerativeModel
import com.trustlens.app.BuildConfig
object GeminiAnalyzer {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",

        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun analyzeDocument(
        extractedText: String,
        sourceContent: String,
        issuer: String,
        similarity: Int,
        differences: List<String>
    ): String {

        val prompt = """
            Analyze this document verification.

            Issuer: $issuer

            User Document:
            $extractedText

            Official Source:
            $sourceContent

            Similarity Score:
            $similarity

            Differences:
            $differences

            Give:
            1. Trust Score
            2. Risk Level
            3. Summary
            4. Verdict
        """.trimIndent()

        val response = model.generateContent(prompt)

        return response.text ?: "No analysis generated"
    }

    suspend fun extractIssuer(ocrText: String): String {
        val prompt = """
        Extract the issuing organization/company/hospital name from this document text.
        Return only the issuer name.

        Document:
        $ocrText
    """.trimIndent()

        val response = model.generateContent(prompt)
        return response.text?.trim() ?: "Unknown"
    }
}