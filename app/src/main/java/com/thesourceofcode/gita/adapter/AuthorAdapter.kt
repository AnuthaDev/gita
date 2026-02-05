package com.thesourceofcode.gita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thesourceofcode.gita.R

class AuthorAdapter(
    private val authors: List<String>,
    private val onAuthorClick: (String) -> Unit
) : RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder>() {

    inner class AuthorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.authorName)
        
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onAuthorClick(authors[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_author, parent, false)
        return AuthorViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        holder.authorName.text = authors[position]
    }

    override fun getItemCount(): Int = authors.size
}
