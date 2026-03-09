package com.StoryAlive.StoryAlive.Repositories

import com.StoryAlive.StoryAlive.Models.Location
import com.StoryAlive.StoryAlive.Models.VoiceActor
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface LocationRepo: MongoRepository<Location, ObjectId> {

    override fun findAll(pageable: Pageable): Page<Location>

}