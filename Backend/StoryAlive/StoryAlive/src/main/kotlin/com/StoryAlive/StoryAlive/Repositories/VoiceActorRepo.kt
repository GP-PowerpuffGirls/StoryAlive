package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Models.VoiceActor
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface VoiceActorRepo: MongoRepository<VoiceActor, ObjectId> {
}