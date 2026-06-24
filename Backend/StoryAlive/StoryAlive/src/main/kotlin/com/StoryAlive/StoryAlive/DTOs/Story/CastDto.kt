package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Gender
import com.StoryAlive.StoryAlive.Enums.PreferredRole

 data class CastDto(val name: String,
                   var gender: Gender,
                   val isAdult: Boolean,
                   var voiceReference: String,
                   var preferredRole: PreferredRole = PreferredRole.NONE
)
