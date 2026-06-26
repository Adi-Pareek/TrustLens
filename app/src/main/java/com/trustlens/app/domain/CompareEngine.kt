package com.trustlens.app.domain

object CompareEngine {

    fun compareDocuments(
        extractedText: String,
        sourceContent: String
    ): Pair<Int, List<String>> {

        val differences = mutableListOf<String>()

        // If no official source found
        if (sourceContent.isBlank()) {
            differences.add("No official source found for verification")
            return Pair(0, differences)
        }

        val similarity = calculateSimilarity(
            extractedText,
            sourceContent
        )

        if (similarity < 90) {
            differences.add("Content mismatch detected")
        }

        if (extractedText != sourceContent) {
            differences.add("Possible modifications found")
        }

        return Pair(similarity, differences)
    }

    private fun calculateSimilarity(
        text1: String,
        text2: String
    ): Int {

        if (text1.isBlank() || text2.isBlank()) {
            return 0
        }

        val commonLength = text1.commonPrefixWith(text2).length
        val maxLength = maxOf(text1.length, text2.length)

        return (commonLength * 100 / maxLength)
    }
}