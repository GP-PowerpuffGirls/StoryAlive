package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity
import com.StoryAlive.StoryAlive.Models.VoiceActor
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface VoiceActorRepo: MongoRepository<VoiceActor, ObjectId> {
    @Query(
        value = "{ '_id': ?0 }",
        fields = "{ 'audios': { \$elemMatch: { 'emotion': ?1, 'intensity': ?2 } } }"
    )
    fun findAudioByEmotionAndIntensity(
        actorId: ObjectId,
        emotion: Emotion,
        intensity: Intensity
    ): VoiceActor?
    fun findAllByIsPrivateFalse(pageable: Pageable): Page<VoiceActor>
    fun findAllByUserIdAndIsPrivateTrue(userId: ObjectId, pageable: Pageable): Page<VoiceActor>
    fun findByUserIdAndActorNameAndIsPrivateTrue(userId: ObjectId, actorName: String): VoiceActor?
    fun findByActorNameAndIsPrivateFalse(actorName: String): VoiceActor?
    fun findAllByIsPrivateFalse(): List<VoiceActor>
    @Query(
        "{ \$or: [ " +
                "{ 'isPrivate': false }, " +
                "{ 'isPrivate': true, 'userId': ?0 } " +
                "] }"
    )
    fun findAllPublicAndUserPrivate(userId: ObjectId, pageable: Pageable): Page<VoiceActor>
}