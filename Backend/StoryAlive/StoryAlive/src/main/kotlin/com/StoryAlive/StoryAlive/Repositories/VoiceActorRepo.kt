package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Models.VoiceActor
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface VoiceActorRepo: MongoRepository<VoiceActor, ObjectId> {
    fun findAllByIsPrivateFalse(pageable: Pageable): Page<VoiceActor>
    fun findAllByUserIdAndIsPrivateTrue(userId: ObjectId, pageable: Pageable): Page<VoiceActor>
    fun findByUserIdAndActorNameAndIsPrivateTrue(userId: ObjectId, actorName: String): VoiceActor?
    fun findByActorNameAndIsPrivateFalse(actorName: String): VoiceActor?
}