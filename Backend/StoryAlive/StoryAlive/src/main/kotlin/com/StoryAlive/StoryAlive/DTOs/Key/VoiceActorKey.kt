package com.StoryAlive.StoryAlive.DTOs.Key

import com.StoryAlive.StoryAlive.Enums.Gender
import com.StoryAlive.StoryAlive.Enums.PreferredRole

data class VoiceActorKey(val isAdult: Boolean, val gender: Gender, val preferredRole: PreferredRole)
