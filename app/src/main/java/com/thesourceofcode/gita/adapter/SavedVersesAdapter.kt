package com.thesourceofcode.gita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thesourceofcode.gita.R
import com.thesourceofcode.gita.model.BookmarkedVerse
import com.thesourceofcode.gita.model.Verse

class SavedVersesAdapter(
    private val bookmarkedVerses: List<BookmarkedVerse>,
    private val verses: List<Verse>,
    private val onVerseClick: (BookmarkedVerse) -> Unit
) : RecyclerView.Adapter<SavedVersesAdapter.SavedVerseViewHolder>() {

    inner class SavedVerseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val verseReference: TextView = itemView.findViewById(R.id.verseReference)
        val versePreview: TextView = itemView.findViewById(R.id.versePreview)
        val translationPreview: TextView = itemView.findViewById(R.id.translationPreview)
        
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onVerseClick(bookmarkedVerses[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedVerseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_verse, parent, false)
        return SavedVerseViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedVerseViewHolder, position: Int) {
        val bookmarkedVerse = bookmarkedVerses[position]
        val verse = verses.find { it.id == bookmarkedVerse.verseId }
        
        holder.verseReference.text = "Chapter ${bookmarkedVerse.chapterNumber}, Verse ${bookmarkedVerse.verseNumber}"
        
        if (verse != null) {
            // Show preview of Sanskrit text (without speaker prefix)
            holder.versePreview.text = verse.shlokaText
            
            // Show preview of transliteration
            holder.translationPreview.text = verse.transliteration
        } else {
            holder.versePreview.text = "Verse not found"
            holder.translationPreview.text = ""
        }
    }

    override fun getItemCount(): Int = bookmarkedVerses.size
}
