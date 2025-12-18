package com.thesourceofcode.gita.model

sealed class VerseItem {
    data class ChapterSummaryItem(val chapter: Chapter) : VerseItem()
    data class VerseContentItem(val verse: Verse) : VerseItem()
}
