package com.StoryAlive.StoryAlive.Models

import com.StoryAlive.StoryAlive.Enums.LocationName
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="locations")
data class Location(
    @Id val locationId: ObjectId = ObjectId(),
    val locationName: LocationName,
    val sfxPath: String
)
