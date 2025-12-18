package com.thesourceofcode.gita.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thesourceofcode.gita.model.Chapter
import com.thesourceofcode.gita.model.Translation
import com.thesourceofcode.gita.model.Verse
import java.io.InputStreamReader

object GitaJsonParser {
    
    private var verses: List<Verse>? = null
    private var chapters: List<Chapter>? = null
    private var translations: List<Translation>? = null
    
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
    
    fun loadChapters(context: Context): List<Chapter> {
        if (chapters != null) {
            return chapters!!
        }
        
        val inputStream = context.assets.open("chapters.json")
        val reader = InputStreamReader(inputStream)
        val chapterListType = object : TypeToken<List<Chapter>>() {}.type
        chapters = Gson().fromJson(reader, chapterListType)
        reader.close()
        
        return chapters!!
    }
    
    fun loadTranslations(context: Context): List<Translation> {
        if (translations != null) {
            return translations!!
        }
        
        val inputStream = context.assets.open("translation.json")
        val reader = InputStreamReader(inputStream)
        val translationListType = object : TypeToken<List<Translation>>() {}.type
        translations = Gson().fromJson(reader, translationListType)
        reader.close()
        
        return translations!!
    }
    
    fun getChapterVerses(context: Context, chapterNumber: Int): List<Verse> {
        val allVerses = loadVerses(context)
        return allVerses.filter { it.chapterNumber == chapterNumber }
    }
    
    fun getVerse(context: Context, verseOrder: Int): Verse? {
        val allVerses = loadVerses(context)
        return allVerses.find { it.verseOrder == verseOrder }
    }
    
    fun getChapter(context: Context, chapterNumber: Int): Chapter? {
        val allChapters = loadChapters(context)
        return allChapters.find { it.chapterNumber == chapterNumber }
    }
    
    fun getVerseTranslations(context: Context, verseId: Int, language: String = "hindi"): List<Translation> {
        val allTranslations = loadTranslations(context)
        return allTranslations.filter { it.verseId == verseId && it.lang == language }
    }
    
    fun getHindiTranslation(context: Context, verseId: Int, authorName: String = "Swami Ramsukhdas"): Translation? {
        val allTranslations = loadTranslations(context)
        return allTranslations.find { 
            it.verseId == verseId && it.lang == "hindi" && it.authorName == authorName 
        }
    }
    
    fun getTotalChapters(): Int = 18
    
    fun getTotalVerses(context: Context): Int {
        return loadVerses(context).size
    }
}
