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

    fun savePdfToCloud(pdf: MultipartFile, userId: ObjectId):String {
        if (pdf.contentType != "application/pdf") {
            throw IllegalArgumentException("Only PDF files are allowed")
        }

        val safeName = pdf.originalFilename!!.replace("\\s+".toRegex(), "_")
        val path = "stories/${userId}/${UUID.randomUUID()}_${safeName}"

        val pdfUrl = supabaseStorageService.uploadFile(
            fileBytes = pdf.bytes,      // the actual PDF bytes
            path = path,             // where it will live in Supabase
            contentType = "application/pdf"  // MIME type for PDF
        )
        return pdfUrl
    }
    fun uploadJsonContent(jsonContent: String, userId: ObjectId): String {
        val bytes = jsonContent.toByteArray()
        val path = "stories/$userId/${UUID.randomUUID()}_story.json"
        return supabaseStorageService.uploadFile(bytes, path, "application/json")
    }
    fun createNewStory(pdf: MultipartFile, storyRequest: StoryRequestDTO):Story {

        val auth = SecurityContextHolder.getContext().authentication
        require(auth != null && auth.principal is CurrentUserDetails)
        val currentUser = auth.principal as CurrentUserDetails

        val pdfPath = savePdfToCloud(pdf, currentUser.getUserId())
        val jsonString= aiService.createStoryFromPdf(pdf.bytes)
        val jsonPath= uploadJsonContent(jsonString, currentUser.getUserId())

        //TODO: CALL THE TTS AND PASS THE JSON STRING TO IT
        val newStory = Story(
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
            pdfPath = pdfPath,
            minimumAge = storyRequest.minimumAge,
            storyId = ObjectId(),
            finalAudioPath = "", //from tts
            jsonPath = jsonPath, //from llm
            createdAt = java.time.Instant.now(),
            modifiedAt = java.time.Instant.now(),
            numberOfViews = 0
        )
        return storyRepo.save(newStory);
    }

}