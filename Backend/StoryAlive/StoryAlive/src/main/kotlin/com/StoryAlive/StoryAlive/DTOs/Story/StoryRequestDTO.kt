package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags
import com.StoryAlive.StoryAlive.Models.VoiceActor
import io.jsonwebtoken.lang.Maps
import org.bson.types.ObjectId

data class StoryRequestDTO(
    val title: String,
    val description: String,
    val voiceActors: MutableMap<String, Pair<String,String>>? = mutableMapOf(),
    val genre: Genre,
    val isPrivate: Boolean,
    val hasSfx: Boolean,
    val hasBackgroundMusic: Boolean,
    val tags: List<Tags> = emptyList(),
    val minimumAge: Int

    ){
}
