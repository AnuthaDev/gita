package com.thesourceofcode.gita.model

data class BookmarkedVerse(
    val verseId: Int,
    val chapterNumber: Int,
    val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)
