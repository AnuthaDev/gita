package com.thesourceofcode.gita.model

import com.google.gson.annotations.SerializedName

data class Chapter(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("chapter_number")
    val chapterNumber: Int,
    
    @SerializedName("name")
    val name: String, // Sanskrit name in Devanagari
    
    @SerializedName("name_transliterated")
    val nameTransliterated: String,
    
    @SerializedName("name_translation")
    val nameTranslation: String,
    
    @SerializedName("name_meaning")
    val nameMeaning: String, // English meaning
    
    @SerializedName("chapter_summary")
    val chapterSummary: String, // English summary
    
    @SerializedName("chapter_summary_hindi")
    val chapterSummaryHindi: String, // Hindi summary
    
    @SerializedName("verses_count")
    val versesCount: Int,
    
    @SerializedName("image_name")
    val imageName: String?
)
