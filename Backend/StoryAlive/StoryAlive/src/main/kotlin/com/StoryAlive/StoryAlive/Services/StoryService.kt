package com.StoryAlive.StoryAlive.Services

import Story
import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.Story.StoryCreationDTO
import com.StoryAlive.StoryAlive.DTOs.Story.StoryRequestDTO
import com.StoryAlive.StoryAlive.Repositories.StoryRepo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class StoryService(private val storyRepo: StoryRepo, private val supabaseStorageService: SupabaseStorageService, private val aiService: AIService) {

    fun getAllStories(pageNumber: Int, pageSize: Int): Page<Story> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)
        return storyRepo.findAllByIsPrivateFalse(pageable)
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

    fun createStoryMetaData(storyRequest: StoryRequestDTO): ObjectId {
        val currentUser = getCurrrenctUser()

        val newStory = Story(
            storyId = ObjectId(),
            creatorId = currentUser.getUserId(),
            duration = 0.0, //from tts
            title = storyRequest.title,
            voiceActors = storyRequest.voiceActors ?: emptyMap(),
            description = storyRequest.description ?: "",
            tags = storyRequest.tags,
            genre = storyRequest.genre,
            isPrivate = storyRequest.isPrivate,
            hasSfx = storyRequest.hasSfx,
            hasBackgroundMusic = storyRequest.hasBackgroundMusic,
            pdfPath = "",
            minimumAge = storyRequest.minimumAge,
            finalAudioPath = "", //from tts
            jsonPath = "", //from llm
            createdAt = java.time.Instant.now(),
            modifiedAt = java.time.Instant.now(),
            numberOfViews = 0
        )
        storyRepo.save(newStory);
        return newStory.storyId;
    }

    fun createStory(storyId: ObjectId, pdf: MultipartFile){
        val currentUser = getCurrrenctUser()
        val jsonString= aiService.createStoryFromPdf(pdf.bytes)
        val storyDto: StoryCreationDTO = jacksonObjectMapper()
            .readValue(jsonString, StoryCreationDTO::class.java);
        println(storyDto)
//        val currentStory: Story = storyRepo.findById(storyId)
//            .orElseThrow { RuntimeException("Story not found with id $storyId") }
//        val pdfPath = supabaseStorageService.savePdfToCloud(pdf, currentUser.getUserId())
//        val jsonPath = supabaseStorageService.saveJsonToCloud(jsonString, currentUser.getUserId())
//        currentStory.pdfPath= pdfPath
//        currentStory.jsonPath= jsonPath
//        storyRepo.save(currentStory)
    }

    //HELPER FUNCTIONS

    fun getCurrrenctUser(): CurrentUserDetails{
        val auth = SecurityContextHolder.getContext().authentication
        require(auth != null && auth.principal is CurrentUserDetails)
        return auth.principal as CurrentUserDetails
    }

}