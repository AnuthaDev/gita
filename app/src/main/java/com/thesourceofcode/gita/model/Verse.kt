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
    
    // Extract Sanskrit shloka from text (removes speaker prefix if present)
    val sanskritText: String
        get() {
            val lines = text.split("\n")
            // Skip first line if it contains "उवाच"
            return if (lines.isNotEmpty() && lines[0].contains("उवाच")) {
                lines.drop(1).joinToString("\n").trim()
            } else {
                text.trim()
            }
        }
}
