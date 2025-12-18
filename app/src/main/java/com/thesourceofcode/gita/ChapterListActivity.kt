package com.thesourceofcode.gita

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.thesourceofcode.gita.adapter.ChapterListAdapter
import com.thesourceofcode.gita.model.Chapter
import com.thesourceofcode.gita.utils.GitaJsonParser

class ChapterListActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnThemeToggle: ImageButton
    private lateinit var chapters: List<Chapter>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load saved theme preference
        val savedTheme = getSharedPreferences("gita_prefs", MODE_PRIVATE)
            .getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)
        
        setContentView(R.layout.activity_chapter_list)
        
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.chapterRecyclerView)
        btnThemeToggle = findViewById(R.id.btnThemeToggle)
        
        // Setup theme toggle
        updateThemeIcon()
        btnThemeToggle.setOnClickListener {
            toggleTheme()
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Load chapters
        chapters = GitaJsonParser.loadChapters(this)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ChapterListAdapter(chapters) { chapter ->
            onChapterClicked(chapter)
        }
    }
    
    private fun onChapterClicked(chapter: Chapter) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("CHAPTER_NUMBER", chapter.chapterNumber)
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
