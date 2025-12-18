package com.thesourceofcode.gita.model

enum class Speaker {
    KRISHNA,
    ARJUNA,
    SANJAYA,
    DHRITARASHTRA;

    companion object {
        fun fromString(value: String): Speaker {
            return when (value) {
                "Krishna" -> KRISHNA
                "Arjuna" -> ARJUNA
                "Sanjaya" -> SANJAYA
                "Dhritarashtra" -> DHRITARASHTRA
                else -> SANJAYA // Default fallback
            }
        }
    }
}
