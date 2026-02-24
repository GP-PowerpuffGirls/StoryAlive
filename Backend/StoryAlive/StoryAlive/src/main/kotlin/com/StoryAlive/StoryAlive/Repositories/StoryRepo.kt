package com.StoryAlive.StoryAlive.Repositories

import Story
import com.StoryAlive.StoryAlive.Models.VoiceActor
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface StoryRepo: MongoRepository<Story, ObjectId> {
    fun findAllByIsPrivateFalse(pageable: Pageable): Page<Story>
    fun findAllByCreatorIdAndIsPrivateTrue(creatorId: ObjectId, pageable: Pageable): Page<Story>
}