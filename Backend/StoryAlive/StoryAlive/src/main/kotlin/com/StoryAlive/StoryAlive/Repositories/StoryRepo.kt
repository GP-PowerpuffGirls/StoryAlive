package com.StoryAlive.StoryAlive.Repositories

import Story
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface StoryRepo: MongoRepository<Story, ObjectId> {
}