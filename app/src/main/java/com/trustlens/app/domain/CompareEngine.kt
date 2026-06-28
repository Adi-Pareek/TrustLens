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
            extractedText,
            sourceContent
        )

        if (similarity < 85) {
            differences.add("Content mismatch detected")
        }

        if (similarity < 60) {
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

        val words1 = text1.lowercase().split("\\s+".toRegex()).toSet()
        val words2 = text2.lowercase().split("\\s+".toRegex()).toSet()

        val commonWords = words1.intersect(words2).size
        val totalWords = maxOf(words1.size, words2.size)

        if (totalWords == 0) return 0

        return ((commonWords.toDouble() / totalWords.toDouble()) * 100).toInt()
    }
}