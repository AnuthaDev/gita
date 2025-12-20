package com.thesourceofcode.gita.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thesourceofcode.gita.R
import com.thesourceofcode.gita.model.Speaker
import com.thesourceofcode.gita.model.Verse
import com.thesourceofcode.gita.utils.GitaJsonParser

class VerseAdapter(private val verses: List<Verse>) : RecyclerView.Adapter<VerseAdapter.VerseViewHolder>() {

    inner class VerseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val verseContainer: View = itemView.findViewById(R.id.verseContainer)
        val chapterIndicator: TextView = itemView.findViewById(R.id.chapterIndicator)
        val speakerName: TextView = itemView.findViewById(R.id.speakerName)
        val verseTitle: TextView = itemView.findViewById(R.id.verseTitle)
        val speakerPrefix: TextView = itemView.findViewById(R.id.speakerPrefix)
        val sanskritText: TextView = itemView.findViewById(R.id.sanskritText)
        val wordMeaningsHeader: View = itemView.findViewById(R.id.wordMeaningsHeader)
        val wordMeaningsToggle: TextView = itemView.findViewById(R.id.wordMeaningsToggle)
        val wordMeanings: TextView = itemView.findViewById(R.id.wordMeanings)
        val hindiTranslation: TextView = itemView.findViewById(R.id.hindiTranslation)
        val transliteration: TextView = itemView.findViewById(R.id.transliteration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verse, parent, false)
        return VerseViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        bindVerse(holder, verses[position], position)
    }

    private fun bindVerse(holder: VerseViewHolder, verse: Verse, position: Int) {
        val context = holder.itemView.context
        
        // Load custom fonts
        val jainiFont = Typeface.createFromAsset(context.assets, "Jaini-Regular.ttf")
        val hindRegular = Typeface.createFromAsset(context.assets, "Hind/Hind-Regular.ttf")
        
        // Show chapter indicator for first verse of each chapter
        if (verse.verseNumber == 1) {
            val chapter = GitaJsonParser.getChapter(context, verse.chapterNumber)
            holder.chapterIndicator.visibility = View.VISIBLE
            if (chapter != null) {
                val chapterText = "Chapter ${verse.chapterNumber}\n${chapter.name}\n${chapter.nameMeaning}"
                holder.chapterIndicator.text = chapterText
            } else {
                holder.chapterIndicator.text = "Chapter ${verse.chapterNumber}"
            }
        } else {
            holder.chapterIndicator.visibility = View.GONE
        }
        
        // Set speaker name
        holder.speakerName.text = verse.speakerString
        
        // Set verse title
        holder.verseTitle.text = "Chapter ${verse.chapterNumber}, Verse ${verse.verseNumber}"
        
        // Set speaker prefix if present (smaller font)
        if (verse.hasSpeakerPrefix && verse.speakerPrefixLine != null) {
            holder.speakerPrefix.visibility = View.VISIBLE
            holder.speakerPrefix.text = verse.speakerPrefixLine
            holder.speakerPrefix.typeface = jainiFont
        } else {
            holder.speakerPrefix.visibility = View.GONE
        }
        
        // Set main shloka text (without speaker prefix)
        holder.sanskritText.text = verse.shlokaText
        holder.sanskritText.typeface = jainiFont
        
        // Set word meanings and toggle functionality
        holder.wordMeanings.text = formatWordMeanings(verse.wordMeanings)
        holder.wordMeanings.visibility = View.GONE
        holder.wordMeaningsToggle.text = " ▼"
        
        holder.wordMeaningsHeader.setOnClickListener {
            if (holder.wordMeanings.visibility == View.GONE) {
                holder.wordMeanings.visibility = View.VISIBLE
                holder.wordMeaningsToggle.text = " ▲"
            } else {
                holder.wordMeanings.visibility = View.GONE
                holder.wordMeaningsToggle.text = " ▼"
            }
        }
        
        // Get Hindi translation from translation.json
        val hindiTranslation = GitaJsonParser.getHindiTranslation(context, verse.id)
        holder.hindiTranslation.text = hindiTranslation?.description ?: verse.transliteration
        holder.hindiTranslation.typeface = hindRegular
        
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
        holder.wordMeaningsToggle.setTextColor(textColor)
        holder.wordMeanings.setTextColor(textColor)
        holder.hindiTranslation.setTextColor(textColor)
        holder.transliteration.setTextColor(textColor)
    }
    
    private fun formatWordMeanings(wordMeanings: String): String {
        // Split by semicolon to get individual word-meaning pairs
        val pairs = wordMeanings.split(";")
        
        return pairs.mapIndexed { index, pair ->
            val trimmedPair = pair.trim()
            if (trimmedPair.isEmpty()) return@mapIndexed ""
            
            // Split by em dash (—) to separate word from meaning
            val parts = trimmedPair.split("—", "-")
            if (parts.size >= 2) {
                val word = parts[0].trim()
                val meaning = parts.subList(1, parts.size).joinToString("—").trim()
                "• $word — $meaning"
            } else {
                "• $trimmedPair"
            }
        }.filter { it.isNotEmpty() }.joinToString("\n")
    }

    override fun getItemCount(): Int = verses.size
}
