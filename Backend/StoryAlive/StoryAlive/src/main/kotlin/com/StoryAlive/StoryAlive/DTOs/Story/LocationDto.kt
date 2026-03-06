package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.LocationName

data class LocationDto(
    val locationName: LocationName,
    val path: String
)
