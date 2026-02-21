package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Models.Location
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface LocationRepo: MongoRepository<Location, ObjectId> {
}