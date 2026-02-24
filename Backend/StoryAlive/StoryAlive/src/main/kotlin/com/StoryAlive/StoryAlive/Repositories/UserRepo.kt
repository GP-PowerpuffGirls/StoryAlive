package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Models.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository


interface UserRepo: MongoRepository<User, ObjectId> {

    fun findByEmail(email: String): User?

}