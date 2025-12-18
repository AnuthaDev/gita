package com.thesourceofcode.gita.model

import com.google.gson.annotations.SerializedName

data class Translation(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("verse_id")
    val verseId: Int,
    
    @SerializedName("verseNumber")
    val verseNumber: Int,
    
    @SerializedName("description")
    val description: String, // The actual translation text
    
    @SerializedName("authorName")
    val authorName: String,
    
    @SerializedName("author_id")
    val authorId: Int,
    
    @SerializedName("lang")
    val lang: String, // "hindi" or "english"
    
    @SerializedName("language_id")
    val languageId: Int
)
