package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags
import com.StoryAlive.StoryAlive.Models.VoiceActor
import org.bson.types.ObjectId

data class StoryRequestDTO(
    val title: String,
    val description: String,
    val voiceActors: Map<ObjectId, String>? = emptyMap(),
    val genre: Genre,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val pdfPath: String,
    val tags: List<Tags> = emptyList(),
    val minimumAge: Int

    ){
}
