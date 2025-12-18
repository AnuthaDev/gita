package com.thesourceofcode.gita.model

import com.google.gson.annotations.SerializedName

data class Verse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("chapter_id")
    val chapterId: Int,

    @SerializedName("chapter_number")
    val chapterNumber: Int,

    @SerializedName("verse_number")
    val verseNumber: Int,

    @SerializedName("verse_order")
    val verseOrder: Int,

    @SerializedName("text")
    val text: String,

    @SerializedName("transliteration")
    val transliteration: String,

    @SerializedName("word_meanings")
    val wordMeanings: String,

    @SerializedName("speaker")
    val speakerString: String,

    @SerializedName("title")
    val title: String
) {
    val speaker: Speaker
        get() = Speaker.fromString(speakerString)

    // Check if text has speaker prefix
    val hasSpeakerPrefix: Boolean
        get() = text.trim().lines().firstOrNull()?.contains("उवाच") == true || text.trim().lines()
            .firstOrNull()?.contains("भगवानुवाच") == true

    // Get the speaker prefix line (if exists)
    val speakerPrefixLine: String?
        get() = if (hasSpeakerPrefix) text.trim().lines().firstOrNull() else null

    // Get the main shloka text (excluding speaker prefix if present)
    val shlokaText: String
        get() {
            val lines = text.trim().lines()
            return if (hasSpeakerPrefix && lines.isNotEmpty()) {
                lines.drop(1).joinToString("\n").trim()
            } else {
                text.trim()
            }
        }
}
