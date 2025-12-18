package com.thesourceofcode.gita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thesourceofcode.gita.R
import com.thesourceofcode.gita.model.Chapter
import com.thesourceofcode.gita.model.Speaker
import com.thesourceofcode.gita.model.Verse
import com.thesourceofcode.gita.model.VerseItem
import com.thesourceofcode.gita.utils.GitaJsonParser

class VerseAdapter(private val items: List<VerseItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CHAPTER_SUMMARY = 0
        private const val VIEW_TYPE_VERSE = 1
    }

    inner class ChapterSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapterSummaryContainer: View = itemView.findViewById(R.id.chapterSummaryContainer)
        val chapterName: TextView = itemView.findViewById(R.id.chapterName)
        val chapterNameTranslation: TextView = itemView.findViewById(R.id.chapterNameTranslation)
        val chapterMeaning: TextView = itemView.findViewById(R.id.chapterMeaning)
        val versesCount: TextView = itemView.findViewById(R.id.versesCount)
        val summaryHindi: TextView = itemView.findViewById(R.id.summaryHindi)
        val summaryEnglish: TextView = itemView.findViewById(R.id.summaryEnglish)
    }

    inner class VerseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val verseContainer: View = itemView.findViewById(R.id.verseContainer)
        val speakerName: TextView = itemView.findViewById(R.id.speakerName)
        val verseTitle: TextView = itemView.findViewById(R.id.verseTitle)
        val speakerPrefix: TextView = itemView.findViewById(R.id.speakerPrefix)
        val sanskritText: TextView = itemView.findViewById(R.id.sanskritText)
        val hindiTranslation: TextView = itemView.findViewById(R.id.hindiTranslation)
        val transliteration: TextView = itemView.findViewById(R.id.transliteration)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is VerseItem.ChapterSummaryItem -> VIEW_TYPE_CHAPTER_SUMMARY
            is VerseItem.VerseContentItem -> VIEW_TYPE_VERSE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CHAPTER_SUMMARY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chapter_summary, parent, false)
                ChapterSummaryViewHolder(view)
            }
            VIEW_TYPE_VERSE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_verse, parent, false)
                VerseViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is VerseItem.ChapterSummaryItem -> bindChapterSummary(holder as ChapterSummaryViewHolder, item.chapter)
            is VerseItem.VerseContentItem -> bindVerse(holder as VerseViewHolder, item.verse)
        }
    }

    private fun bindChapterSummary(holder: ChapterSummaryViewHolder, chapter: Chapter) {
        holder.chapterName.text = chapter.name
        holder.chapterNameTranslation.text = chapter.nameTransliterated
        holder.chapterMeaning.text = chapter.nameMeaning
        holder.versesCount.text = "${chapter.versesCount} verses"
        holder.summaryHindi.text = chapter.chapterSummaryHindi
        holder.summaryEnglish.text = chapter.chapterSummary
    }

    private fun bindVerse(holder: VerseViewHolder, verse: Verse) {
        val context = holder.itemView.context
        
        // Set speaker name
        holder.speakerName.text = verse.speakerString
        
        // Set verse title
        holder.verseTitle.text = "Chapter ${verse.chapterNumber}, Verse ${verse.verseNumber}"
        
        // Set speaker prefix if present (smaller font)
        if (verse.hasSpeakerPrefix && verse.speakerPrefixLine != null) {
            holder.speakerPrefix.visibility = View.VISIBLE
            holder.speakerPrefix.text = verse.speakerPrefixLine
        } else {
            holder.speakerPrefix.visibility = View.GONE
        }
        
        // Set main shloka text (without speaker prefix)
        holder.sanskritText.text = verse.shlokaText
        
        // Get Hindi translation from translation.json
        val hindiTranslation = GitaJsonParser.getHindiTranslation(context, verse.id)
        holder.hindiTranslation.text = hindiTranslation?.description ?: verse.transliteration
        
        // Set transliteration
        holder.transliteration.text = verse.transliteration
        
        // Apply speaker-based background color
        val backgroundColor = when (verse.speaker) {
            Speaker.KRISHNA -> ContextCompat.getColor(context, R.color.speaker_krishna_bg)
            Speaker.ARJUNA -> ContextCompat.getColor(context, R.color.speaker_arjuna_bg)
            Speaker.SANJAYA -> ContextCompat.getColor(context, R.color.speaker_sanjaya_bg)
            Speaker.DHRITARASHTRA -> ContextCompat.getColor(context, R.color.speaker_dhritarashtra_bg)
        }
        holder.verseContainer.setBackgroundColor(backgroundColor)
        
        // Apply speaker-based text color
        val textColor = when (verse.speaker) {
            Speaker.KRISHNA -> ContextCompat.getColor(context, R.color.speaker_krishna_text)
            Speaker.ARJUNA -> ContextCompat.getColor(context, R.color.speaker_arjuna_text)
            Speaker.SANJAYA -> ContextCompat.getColor(context, R.color.speaker_sanjaya_text)
            Speaker.DHRITARASHTRA -> ContextCompat.getColor(context, R.color.speaker_dhritarashtra_text)
        }
        holder.speakerName.setTextColor(textColor)
        holder.verseTitle.setTextColor(textColor)
        holder.speakerPrefix.setTextColor(textColor)
        holder.sanskritText.setTextColor(textColor)
        holder.hindiTranslation.setTextColor(textColor)
        holder.transliteration.setTextColor(textColor)
    }

    override fun getItemCount(): Int = items.size
}
