package com.StoryAlive.StoryAlive.Models

import com.StoryAlive.StoryAlive.Enums.Gender
import jakarta.validation.constraints.NotEmpty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "voice_actors")
data class VoiceActor(
    @Id val voiceActorId: ObjectId = ObjectId(),
    val userId: ObjectId? = null,
    @Indexed(unique = true)
    val actorName: String,
    val gender: Gender,
    val isAdult: Boolean = true,
    val isPrivate: Boolean,
    val audios: @NotEmpty List<Audio>
){
    init {
        require(audios.isNotEmpty()) { "VoiceActor must have at least one audio" }
    }
}
