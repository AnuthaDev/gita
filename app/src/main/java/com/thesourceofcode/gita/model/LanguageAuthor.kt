package com.thesourceofcode.gita.model

data class LanguageAuthor(
    val language: String,
    val languageDisplayName: String,
    val authors: List<String>
)

object LanguageAuthorData {
    fun getAvailableLanguages(): List<LanguageAuthor> {
        return listOf(
            LanguageAuthor(
                language = "hindi",
                languageDisplayName = "हिन्दी",
                authors = listOf(
                    "Swami Ramsukhdas",
                    "Swami Tejomayananda"
                )
            ),
            LanguageAuthor(
                language = "english",
                languageDisplayName = "English",
                authors = listOf(
                    "Swami Adidevananda",
                    "Swami Gambirananda",
                    "Swami Sivananda",
                    "Dr. S. Sankaranarayan",
                    "Shri Purohit Swami"
                )
            )
        )
    }
}
