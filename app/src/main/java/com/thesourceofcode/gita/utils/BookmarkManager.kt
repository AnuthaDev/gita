package com.thesourceofcode.gita.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thesourceofcode.gita.model.BookmarkedVerse

object BookmarkManager {
    private const val PREFS_NAME = "gita_bookmarks"
    private const val KEY_BOOKMARKS = "bookmarked_verses"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getBookmarks(context: Context): List<BookmarkedVerse> {
        val prefs = getPrefs(context)
        val json = prefs.getString(KEY_BOOKMARKS, null) ?: return emptyList()
        
        val type = object : TypeToken<List<BookmarkedVerse>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
    
    fun isBookmarked(context: Context, verseId: Int): Boolean {
        return getBookmarks(context).any { it.verseId == verseId }
    }
    
    fun addBookmark(context: Context, verseId: Int, chapterNumber: Int, verseNumber: Int) {
        val bookmarks = getBookmarks(context).toMutableList()
        
        // Don't add if already bookmarked
        if (bookmarks.any { it.verseId == verseId }) {
            return
        }
        
        bookmarks.add(BookmarkedVerse(verseId, chapterNumber, verseNumber))
        saveBookmarks(context, bookmarks)
    }
    
    fun removeBookmark(context: Context, verseId: Int) {
        val bookmarks = getBookmarks(context).toMutableList()
        bookmarks.removeAll { it.verseId == verseId }
        saveBookmarks(context, bookmarks)
    }
    
    fun toggleBookmark(context: Context, verseId: Int, chapterNumber: Int, verseNumber: Int) {
        if (isBookmarked(context, verseId)) {
            removeBookmark(context, verseId)
        } else {
            addBookmark(context, verseId, chapterNumber, verseNumber)
        }
    }
    
    private fun saveBookmarks(context: Context, bookmarks: List<BookmarkedVerse>) {
        val prefs = getPrefs(context)
        val json = Gson().toJson(bookmarks)
        prefs.edit().putString(KEY_BOOKMARKS, json).apply()
    }
}
