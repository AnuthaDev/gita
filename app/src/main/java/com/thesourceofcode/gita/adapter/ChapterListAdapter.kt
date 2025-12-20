package com.thesourceofcode.gita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thesourceofcode.gita.R
import com.thesourceofcode.gita.model.Chapter

class ChapterListAdapter(
    private val chapters: List<Chapter>,
    private val onChapterClick: (Chapter) -> Unit,
    private val onInfoClick: (Chapter) -> Unit
) : RecyclerView.Adapter<ChapterListAdapter.ChapterViewHolder>() {

    inner class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapterNumber: TextView = itemView.findViewById(R.id.chapterNumber)
        val chapterName: TextView = itemView.findViewById(R.id.chapterName)
        val chapterTranslation: TextView = itemView.findViewById(R.id.chapterTranslation)
        val chapterMeaning: TextView = itemView.findViewById(R.id.chapterMeaning)
        val versesCount: TextView = itemView.findViewById(R.id.versesCount)
        val btnInfo: ImageButton = itemView.findViewById(R.id.btnChapterInfo)
        
        init {
            itemView.setOnClickListener {
                onChapterClick(chapters[adapterPosition])
            }
            btnInfo.setOnClickListener {
                onInfoClick(chapters[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ChapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapters[position]
        
        holder.chapterNumber.text = chapter.chapterNumber.toString()
        holder.chapterName.text = chapter.name
        holder.chapterTranslation.text = chapter.nameTransliterated
        holder.chapterMeaning.text = chapter.nameMeaning
        holder.versesCount.text = "${chapter.versesCount} verses"
    }

    override fun getItemCount(): Int = chapters.size
}
