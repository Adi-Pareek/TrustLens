package com.trustlens.app.domain

object CompareEngine {

    fun compareDocuments(
        extractedText: String,
        sourceContent: String
    ): Pair<Int, List<String>> {

        val differences = mutableListOf<String>()

        if (sourceContent.isBlank()) {
            differences.add("No official source found for verification")
            return Pair(0, differences)
        }

        val similarity = calculateSimilarity(
            extractedText.lowercase(),
            sourceContent.lowercase()
        )

        if (similarity < 60) {
            differences.add("Content mismatch detected")
        }

        if (similarity < 40) {
            differences.add("Possible modifications found")
        }

        return Pair(similarity, differences)
    }

    private fun calculateSimilarity(
        text1: String,
        text2: String
    ): Int {

        val words1 = text1.split(" ").toSet()
        val words2 = text2.split(" ").toSet()

        val commonWords = words1.intersect(words2).size
        val totalWords = maxOf(words1.size, words2.size)

        if (totalWords == 0) return 0

        return (commonWords * 100) / totalWords
    }
}