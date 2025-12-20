package com.thesourceofcode.gita

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.thesourceofcode.gita.adapter.VerseAdapter
import com.thesourceofcode.gita.model.VerseItem
import com.thesourceofcode.gita.utils.GitaJsonParser

class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val KEY_CURRENT_POSITION = "viewpager_position"
        private const val KEY_SCROLL_POSITION = "scroll_position"
    }
    
    private lateinit var viewPager: ViewPager2
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var verseCounter: TextView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnThemeToggle: ImageButton
    private lateinit var btnInfo: ImageButton
    
    private lateinit var verses: List<com.thesourceofcode.gita.model.Verse>
    private lateinit var adapter: VerseAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load saved theme preference
        val savedTheme = getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Initialize views
        viewPager = findViewById(R.id.verseViewPager)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        verseCounter = findViewById(R.id.verseCounter)
        toolbar = findViewById(R.id.toolbar)
        btnThemeToggle = findViewById(R.id.btnThemeToggle)
        
        // Setup theme toggle
        updateThemeIcon()
        btnThemeToggle.setOnClickListener {
            toggleTheme()
        }
        
        // Setup info button
        btnInfo = toolbar.findViewById(R.id.btnInfo)
        btnInfo.setOnClickListener {
            showCurrentChapterSummary()
        }
        
        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Load verses
        verses = GitaJsonParser.loadVerses(this)
        
        // Setup ViewPager
        adapter = VerseAdapter(verses)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        
        // Setup navigation listeners
        setupNavigation()
        
        // Check for saved position from theme change
        val prefs = getSharedPreferences("gita_prefs", MODE_PRIVATE)
        val savedPosition = prefs.getInt(KEY_CURRENT_POSITION, -1)
        val savedScrollY = prefs.getInt(KEY_SCROLL_POSITION, -1)
        
        if (savedPosition >= 0) {
            // Restore from theme change
            viewPager.setCurrentItem(savedPosition, false)
            
            // Clear saved position
            prefs.edit()
                .remove(KEY_CURRENT_POSITION)
                .remove(KEY_SCROLL_POSITION)
                .apply()
            
            // Restore scroll position after layout is complete
            if (savedScrollY >= 0) {
                viewPager.post {
                    val recyclerView = viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView
                    recyclerView?.let { rv ->
                        val viewHolder = rv.findViewHolderForAdapterPosition(savedPosition)
                        viewHolder?.itemView?.findViewById<android.widget.ScrollView>(R.id.verseContainer)?.scrollTo(0, savedScrollY)
                    }
                }
            }
        } else {
            // Get starting chapter if provided
            val startChapter = intent.getIntExtra("CHAPTER_NUMBER", 1)
            val startPosition = verses.indexOfFirst { it.chapterNumber == startChapter }
            
            // Set initial position
            if (startPosition >= 0) {
                viewPager.setCurrentItem(startPosition, false)
            } else {
                updateCounter(0)
            }
        }
        
        // Toolbar navigation
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun showCurrentChapterSummary() {
        val currentPosition = viewPager.currentItem
        if (currentPosition >= 0 && currentPosition < verses.size) {
            val verse = verses[currentPosition]
            val intent = Intent(this, ChapterSummaryActivity::class.java)
            intent.putExtra("CHAPTER_NUMBER", verse.chapterNumber)
            intent.putExtra("FROM_READING", true)
            startActivity(intent)
        }
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
            if (currentPosition < verses.size - 1) {
                viewPager.currentItem = currentPosition + 1
            }
        }
        
        // Initial button state
        updateNavigationButtons(0)
    }
    
    private fun updateCounter(position: Int) {
        val verse = verses[position]
        toolbar.title = "Chapter ${verse.chapterNumber}"
        
        // Calculate verse position within current chapter
        val versesInChapter = verses.filter { it.chapterNumber == verse.chapterNumber }
        val versePositionInChapter = versesInChapter.indexOfFirst { it.id == verse.id } + 1
        val totalVersesInChapter = versesInChapter.size
        
        verseCounter.text = "${verse.chapterNumber} - $versePositionInChapter/$totalVersesInChapter"
    }
    
    private fun updateNavigationButtons(position: Int) {
        btnPrevious.isEnabled = position > 0
        btnNext.isEnabled = position < verses.size - 1
    }
    
    private fun toggleTheme() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        
        // Save current ViewPager position and ScrollView scroll position
        val currentPosition = viewPager.currentItem
        var scrollY = 0
        
        val recyclerView = viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView
        recyclerView?.let { rv ->
            val viewHolder = rv.findViewHolderForAdapterPosition(currentPosition)
            viewHolder?.itemView?.findViewById<android.widget.ScrollView>(R.id.verseContainer)?.let { scrollView ->
                scrollY = scrollView.scrollY
            }
        }
        
        // Save preference and positions
        getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .edit()
            .putInt("theme_mode", newMode)
            .putInt(KEY_CURRENT_POSITION, currentPosition)
            .putInt(KEY_SCROLL_POSITION, scrollY)
            .apply()
        
        // Apply theme with fade animation
        window.decorView.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                AppCompatDelegate.setDefaultNightMode(newMode)
                window.decorView.alpha = 0f
                window.decorView.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
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