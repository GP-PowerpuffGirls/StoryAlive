package com.StoryAlive.StoryAlive.Services

import Story
import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.Story.StoryRequestDTO
import com.StoryAlive.StoryAlive.Repositories.StoryRepo
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class StoryService(private val storyRepo: StoryRepo) {

    fun getAllStories(pageNumber: Int, pageSize: Int): Page<Story> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);
        return storyRepo.findAllByIsPrivateFalse(pageable);
    }
    fun getAllPrivateStories(pageNumber: Int, pageSize: Int): Page<Story> {
        val pageable = PageRequest.of(pageNumber, pageSize)

        val currentUser = (SecurityContextHolder
            .getContext()
            .authentication
            ?.principal) as CurrentUserDetails

        val creatorId: ObjectId = currentUser.getUserId()

        return storyRepo.findAllByCreatorIdAndIsPrivateTrue(creatorId, pageable)
    }
    fun createNewStory(storyRequest: StoryRequestDTO):Story {
        val currentUser = (SecurityContextHolder
            .getContext()
            .authentication
            ?.principal) as CurrentUserDetails
    }

}