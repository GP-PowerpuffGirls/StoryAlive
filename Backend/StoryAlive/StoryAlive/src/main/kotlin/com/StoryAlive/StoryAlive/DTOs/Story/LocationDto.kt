package com.StoryAlive.StoryAlive.DTOs.Story

import com.StoryAlive.StoryAlive.Enums.LocationName

public data class LocationDto(
    val locationName: LocationName,
    var path: String
)
