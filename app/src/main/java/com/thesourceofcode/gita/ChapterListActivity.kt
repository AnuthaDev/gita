package com.thesourceofcode.gita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var chapters: List<Chapter>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_list)
        
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.chapterRecyclerView)
        
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
}
