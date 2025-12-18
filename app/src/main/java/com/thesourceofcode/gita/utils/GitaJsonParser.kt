package com.thesourceofcode.gita.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thesourceofcode.gita.model.Verse
import java.io.InputStreamReader

object GitaJsonParser {
    
    private var verses: List<Verse>? = null
    
    fun loadVerses(context: Context): List<Verse> {
        if (verses != null) {
            return verses!!
        }
        
        val inputStream = context.assets.open("verse.json")
        val reader = InputStreamReader(inputStream)
        val verseListType = object : TypeToken<List<Verse>>() {}.type
        verses = Gson().fromJson(reader, verseListType)
        reader.close()
        
        return verses!!
    }
    
    fun getChapterVerses(context: Context, chapterNumber: Int): List<Verse> {
        val allVerses = loadVerses(context)
        return allVerses.filter { it.chapterNumber == chapterNumber }
    }
    
    fun getVerse(context: Context, verseOrder: Int): Verse? {
        val allVerses = loadVerses(context)
        return allVerses.find { it.verseOrder == verseOrder }
    }
    
    fun getTotalChapters(): Int = 18
    
    fun getTotalVerses(context: Context): Int {
        return loadVerses(context).size
    }
}
