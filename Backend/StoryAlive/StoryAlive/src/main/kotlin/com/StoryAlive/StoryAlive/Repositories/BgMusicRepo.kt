package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Models.BackgroundMusic
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface BgMusicRepo: MongoRepository<BackgroundMusic, ObjectId> {
    fun findAllByForKids(forKids: Boolean): List<BackgroundMusic>
}