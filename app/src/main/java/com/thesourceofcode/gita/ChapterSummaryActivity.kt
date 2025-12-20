package com.thesourceofcode.gita

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.thesourceofcode.gita.utils.GitaJsonParser

class ChapterSummaryActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_summary)
        
        val chapterNumber = intent.getIntExtra("CHAPTER_NUMBER", 1)
        val fromReading = intent.getBooleanExtra("FROM_READING", false)
        
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = "Chapter $chapterNumber"
        toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Load chapter data
        val chapter = GitaJsonParser.getChapter(this, chapterNumber)
        
        if (chapter != null) {
            findViewById<TextView>(R.id.chapterName).text = chapter.name
            findViewById<TextView>(R.id.chapterNameTranslation).text = chapter.nameTransliterated
            findViewById<TextView>(R.id.chapterMeaning).text = chapter.nameMeaning
            findViewById<TextView>(R.id.versesCount).text = "${chapter.versesCount} verses"
            findViewById<TextView>(R.id.summaryHindi).text = chapter.chapterSummaryHindi
            findViewById<TextView>(R.id.summaryEnglish).text = chapter.chapterSummary
        }
    }
}
