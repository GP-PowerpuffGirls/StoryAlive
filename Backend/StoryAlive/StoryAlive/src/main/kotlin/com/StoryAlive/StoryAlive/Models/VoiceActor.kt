package com.StoryAlive.StoryAlive.Models

import com.StoryAlive.StoryAlive.Enums.Gender
import jakarta.validation.constraints.NotEmpty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "voice_actors")
@CompoundIndex(
    name = "actor_uniqueness",
    def = "{'actorName':1, 'isPrivate':1, 'userId':1}",
    unique = true
)
data class VoiceActor(
    @Id val voiceActorId: ObjectId = ObjectId(),
    val userId: ObjectId? = null,
    val actorName: String,
    val gender: Gender,
    val isAdult: Boolean = true,
    val isPrivate: Boolean,
    var audios: @NotEmpty List<Audio>
){
    init {
        require(audios.isNotEmpty()) { "VoiceActor must have at least one audio" }
    }
}