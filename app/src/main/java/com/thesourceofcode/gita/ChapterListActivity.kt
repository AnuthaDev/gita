package com.thesourceofcode.gita

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thesourceofcode.gita.adapter.ChapterListAdapter
import com.thesourceofcode.gita.adapter.SavedVersesAdapter
import com.thesourceofcode.gita.model.Chapter
import com.thesourceofcode.gita.model.Verse
import com.thesourceofcode.gita.utils.BookmarkManager
import com.thesourceofcode.gita.utils.GitaJsonParser

class ChapterListActivity : AppCompatActivity() {

    private lateinit var chapterRecyclerView: RecyclerView
    private lateinit var savedVersesRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnThemeToggle: ImageButton
    private lateinit var chapters: List<Chapter>
    private lateinit var verses: List<Verse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load saved theme preference
        val savedTheme = getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        enableEdgeToEdge()
        setContentView(R.layout.activity_chapter_list)

        toolbar = findViewById(R.id.toolbar)
        chapterRecyclerView = findViewById(R.id.chapterRecyclerView)
        savedVersesRecyclerView = findViewById(R.id.savedVersesRecyclerView)
        emptyState = findViewById(R.id.emptyState)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        btnThemeToggle = findViewById(R.id.btnThemeToggle)

        // Setup theme toggle
        updateThemeIcon()
        btnThemeToggle.setOnClickListener {
            toggleTheme()
        }

        // Setup bottom navigation
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    showChapters()
                    true
                }
                R.id.navigation_saved -> {
                    showSavedVerses()
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load chapters and verses
        chapters = GitaJsonParser.loadChapters(this)
        verses = GitaJsonParser.loadVerses(this)

        // Setup Chapters RecyclerView
        chapterRecyclerView.layoutManager = LinearLayoutManager(this)
        chapterRecyclerView.adapter = ChapterListAdapter(
            chapters,
            onChapterClick = { chapter -> onChapterClicked(chapter) },
            onInfoClick = { chapter -> onInfoClicked(chapter) }
        )
        
        // Setup Saved Verses RecyclerView
        savedVersesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showChapters() {
        toolbar.title = "Bhagavad Gita - Chapters"
        chapterRecyclerView.visibility = View.VISIBLE
        savedVersesRecyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    private fun showSavedVerses() {
        toolbar.title = "Saved Verses"
        chapterRecyclerView.visibility = View.GONE
        
        val bookmarkedVerses = BookmarkManager.getBookmarks(this)
        
        if (bookmarkedVerses.isEmpty()) {
            savedVersesRecyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            savedVersesRecyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            
            savedVersesRecyclerView.adapter = SavedVersesAdapter(
                bookmarkedVerses,
                verses
            ) { bookmarkedVerse ->
                // Open MainActivity at the bookmarked verse
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("CHAPTER_NUMBER", bookmarkedVerse.chapterNumber)
                intent.putExtra("VERSE_ID", bookmarkedVerse.verseId)
                startActivity(intent)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh saved verses when returning to this activity
        if (bottomNavigation.selectedItemId == R.id.navigation_saved) {
            showSavedVerses()
        }
    }

    private fun onChapterClicked(chapter: Chapter) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("CHAPTER_NUMBER", chapter.chapterNumber)
        startActivity(intent)
    }

    private fun onInfoClicked(chapter: Chapter) {
        val intent = Intent(this, ChapterSummaryActivity::class.java)
        intent.putExtra("CHAPTER_NUMBER", chapter.chapterNumber)
        intent.putExtra("FROM_READING", false)
        startActivity(intent)
    }

    private fun toggleTheme() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        // Save preference
        getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .edit()
            .putInt("theme_mode", newMode)
            .apply()

        AppCompatDelegate.setDefaultNightMode(newMode)
    }

    private fun updateThemeIcon() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val iconRes = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            R.drawable.ic_light_mode
        } else {
            R.drawable.ic_dark_mode
        }
        btnThemeToggle.setImageDrawable(ContextCompat.getDrawable(this, iconRes))
    }
}
