package com.thesourceofcode.gita

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.thesourceofcode.gita.adapter.VerseAdapter
import com.thesourceofcode.gita.model.VerseItem
import com.thesourceofcode.gita.utils.GitaJsonParser

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var verseCounter: TextView
    private lateinit var toolbar: MaterialToolbar
    
    private lateinit var items: List<VerseItem>
    private lateinit var adapter: VerseAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Initialize views
        viewPager = findViewById(R.id.verseViewPager)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        verseCounter = findViewById(R.id.verseCounter)
        toolbar = findViewById(R.id.toolbar)
        
        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Build items list with chapter summaries
        items = buildItemsList()
        
        // Get starting chapter if provided
        val startChapter = intent.getIntExtra("CHAPTER_NUMBER", 1)
        val startPosition = items.indexOfFirst { item ->
            when (item) {
                is VerseItem.ChapterSummaryItem -> item.chapter.chapterNumber == startChapter
                is VerseItem.VerseContentItem -> item.verse.chapterNumber == startChapter && item.verse.verseNumber == 1
            }
        }
        
        // Setup ViewPager
        adapter = VerseAdapter(items)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        
        // Setup navigation listeners
        setupNavigation()
        
        // Set initial position
        if (startPosition >= 0) {
            viewPager.setCurrentItem(startPosition, false)
        } else {
            updateCounter(0)
        }
        
        // Toolbar navigation
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun buildItemsList(): List<VerseItem> {
        val verses = GitaJsonParser.loadVerses(this)
        val chapters = GitaJsonParser.loadChapters(this)
        val itemsList = mutableListOf<VerseItem>()
        
        // Group verses by chapter
        val versesByChapter = verses.groupBy { it.chapterNumber }
        
        // For each chapter, add summary first, then verses
        chapters.forEach { chapter ->
            itemsList.add(VerseItem.ChapterSummaryItem(chapter))
            versesByChapter[chapter.chapterNumber]?.forEach { verse ->
                itemsList.add(VerseItem.VerseContentItem(verse))
            }
        }
        
        return itemsList
    }
    
    private fun setupNavigation() {
        // ViewPager page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateCounter(position)
                updateNavigationButtons(position)
            }
        })
        
        // Previous button click
        btnPrevious.setOnClickListener {
            val currentPosition = viewPager.currentItem
            if (currentPosition > 0) {
                viewPager.currentItem = currentPosition - 1
            }
        }
        
        // Next button click
        btnNext.setOnClickListener {
            val currentPosition = viewPager.currentItem
            if (currentPosition < items.size - 1) {
                viewPager.currentItem = currentPosition + 1
            }
        }
        
        // Initial button state
        updateNavigationButtons(0)
    }
    
    private fun updateCounter(position: Int) {
        when (val item = items[position]) {
            is VerseItem.ChapterSummaryItem -> {
                toolbar.title = "Chapter ${item.chapter.chapterNumber}"
                verseCounter.text = "Summary"
            }
            is VerseItem.VerseContentItem -> {
                val verse = item.verse
                // Calculate actual verse position (excluding chapter summaries)
                val versePosition = items.take(position + 1).count { it is VerseItem.VerseContentItem }
                val totalVerses = items.count { it is VerseItem.VerseContentItem }
                
                toolbar.title = "Chapter ${verse.chapterNumber}"
                verseCounter.text = "$versePosition / $totalVerses"
            }
        }
    }
    
    private fun updateNavigationButtons(position: Int) {
        btnPrevious.isEnabled = position > 0
        btnNext.isEnabled = position < items.size - 1
    }
}