package com.example.storyalive.model

import com.google.gson.annotations.SerializedName

data class AudioRequest(
    val emotion: String,
    val intensity: String,
    val filepath: String
)
enum class Gender {
    @SerializedName("MALE") MALE,
    @SerializedName("FEMALE") FEMALE,
    @SerializedName("OTHER") OTHER
}
data class VoiceActorRequest(
    val actorName: String,
    val gender: Gender,
    val adult: Boolean,
    val private: Boolean,
    val audios: List<AudioRequest>,
    val preferredRole: String
)
data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)