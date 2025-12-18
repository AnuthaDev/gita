package com.thesourceofcode.gita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thesourceofcode.gita.R
import com.thesourceofcode.gita.model.Speaker
import com.thesourceofcode.gita.model.Verse

class VerseAdapter(private val verses: List<Verse>) : RecyclerView.Adapter<VerseAdapter.VerseViewHolder>() {

    inner class VerseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val verseContainer: View = itemView.findViewById(R.id.verseContainer)
        val speakerName: TextView = itemView.findViewById(R.id.speakerName)
        val verseTitle: TextView = itemView.findViewById(R.id.verseTitle)
        val speakerPrefix: TextView = itemView.findViewById(R.id.speakerPrefix)
        val sanskritText: TextView = itemView.findViewById(R.id.sanskritText)
        val hindiTranslation: TextView = itemView.findViewById(R.id.hindiTranslation)
        val transliteration: TextView = itemView.findViewById(R.id.transliteration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_verse, parent, false)
        return VerseViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        val verse = verses[position]
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
        
        // For now, using transliteration as Hindi translation placeholder
        // For now, using transliteration as Hindi translation placeholder
        // You'll need to add actual Hindi translation to the JSON or use word_meanings
        holder.hindiTranslation.text = verse.transliteration
        
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

    override fun getItemCount(): Int = verses.size
}
