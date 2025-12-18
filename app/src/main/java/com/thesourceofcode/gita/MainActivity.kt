package com.thesourceofcode.gita

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
import com.thesourceofcode.gita.model.Verse
import com.thesourceofcode.gita.utils.GitaJsonParser

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var verseCounter: TextView
    private lateinit var toolbar: MaterialToolbar
    
    private lateinit var verses: List<Verse>
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
        
        // Load verses from JSON
        verses = GitaJsonParser.loadVerses(this)
        
        // Setup ViewPager
        adapter = VerseAdapter(verses)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        
        // Setup navigation listeners
        setupNavigation()
        
        // Update initial counter
        updateVerseCounter(0)
    }
    
    private fun setupNavigation() {
        // ViewPager page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateVerseCounter(position)
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
            if (currentPosition < verses.size - 1) {
                viewPager.currentItem = currentPosition + 1
            }
        }
        
        // Initial button state
        updateNavigationButtons(0)
    }
    
    private fun updateVerseCounter(position: Int) {
        val verse = verses[position]
        verseCounter.text = "${position + 1} / ${verses.size}"
        toolbar.title = "Chapter ${verse.chapterNumber}"
    }
    
    private fun updateNavigationButtons(position: Int) {
        btnPrevious.isEnabled = position > 0
        btnNext.isEnabled = position < verses.size - 1
    }
}