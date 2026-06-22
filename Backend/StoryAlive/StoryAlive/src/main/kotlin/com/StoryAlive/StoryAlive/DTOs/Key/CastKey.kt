package com.StoryAlive.StoryAlive.DTOs.Key

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity
import org.bson.types.ObjectId

public data class CastKey(val actorId: ObjectId, val castName: String, val emotion: Emotion, val intensity: Intensity)