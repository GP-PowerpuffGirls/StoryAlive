package com.StoryAlive.StoryAlive.DTOs

import com.StoryAlive.StoryAlive.Enums.Gender
import com.StoryAlive.StoryAlive.Enums.PreferredRole
import com.StoryAlive.StoryAlive.Models.Audio
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class VoiceActorRequest (
    @field:NotBlank(message = "Invalid actor name")
    val actorName: String,
    @field:NotNull
    val gender: Gender,
    @field:NotNull
    val isAdult: Boolean,
    @field:NotNull
    val isPrivate: Boolean,
    val audios: @NotEmpty List<Audio>,
    var preferredRole: PreferredRole = PreferredRole.NONE

)