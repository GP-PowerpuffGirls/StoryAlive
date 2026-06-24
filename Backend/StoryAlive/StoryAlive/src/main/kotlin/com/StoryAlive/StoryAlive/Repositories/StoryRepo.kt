package com.StoryAlive.StoryAlive.Repositories

import Story
import com.StoryAlive.StoryAlive.Models.VoiceActor
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface StoryRepo: MongoRepository<Story, ObjectId> {
    fun findByStoryIdIn(storyIds: List<ObjectId>, pageable: Pageable): Page<Story>
    fun findAllByIsPrivateFalse(pageable: Pageable): Page<Story>
    fun findAllByCreatorIdAndIsPrivateTrue(creatorId: ObjectId, pageable: Pageable): Page<Story>
}