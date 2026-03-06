package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.Gender

data class CastDto(private val name: String,
                   private val gender: Gender,
                   private val isAdult: Boolean,
                   private var voiceReference: String)
