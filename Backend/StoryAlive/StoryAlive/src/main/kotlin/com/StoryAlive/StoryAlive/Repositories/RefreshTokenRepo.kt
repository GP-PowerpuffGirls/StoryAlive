package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Models.RefreshTokens
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepo: MongoRepository<RefreshTokens, ObjectId> {

    fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshTokens?
    fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)

}