package com.example.audio

/**
 * Jungle soundscape presets that users or parents can select
 */
enum class JungleAudioTheme(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String
) {
    SERENE_RIVER(
        id = "serene_river",
        title = "Serene River & Breeze",
        emoji = "🌊",
        description = "Gentle water stream, warm jungle wind, and soothing pentatonic chimes"
    ),
    CANOPY_BIRDS(
        id = "canopy_birds",
        title = "Canopy Birds & Flutes",
        emoji = "🦜",
        description = "Soft bamboo flutes, tranquil breeze, and distant sweet songbirds"
    ),
    RAINFOREST_CALM(
        id = "rainforest_calm",
        title = "Rainforest Calm",
        emoji = "🌿",
        description = "Mellow nature frequencies, lush rainforest ambience, and peaceful notes"
    );

    companion object {
        fun fromId(id: String): JungleAudioTheme = entries.find { it.id == id } ?: SERENE_RIVER
    }
}
