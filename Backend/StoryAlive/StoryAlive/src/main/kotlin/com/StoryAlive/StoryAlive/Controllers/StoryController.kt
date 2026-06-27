package com.StoryAlive.StoryAlive.Controllers

import Story
import com.StoryAlive.StoryAlive.DTOs.Story.RequestStoryUpdateDTO
import com.StoryAlive.StoryAlive.DTOs.Story.StoryRequestDTO
import com.StoryAlive.StoryAlive.DTOs.StoryResponseDTO
import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Services.StoryService
import io.ktor.util.StatelessHmacNonceManager
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("stories")
class StoryController(private val storyService: StoryService) {

    @GetMapping("/{storyId}")
    fun getStory(
        @PathVariable storyId: String,
    ): StoryResponseDTO {
        return storyService.getStoryById(ObjectId(storyId))
    }

    @GetMapping
    fun getAllStories(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): Page<StoryResponseDTO> {
        return storyService.getAllStories(pageNumber, pageSize)
    }

    @GetMapping("favourites")
    fun getAllFavouriteStories(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): Page<StoryResponseDTO> {
        return storyService.getAllFavouriteStories(pageNumber, pageSize)
    }

    @GetMapping("history")
    fun getAllHistoryStories(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): Page<StoryResponseDTO> {
        return storyService.getHistory(pageNumber, pageSize)
    }

    @GetMapping("/private")
    fun getAllPrivateStories(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): Page<StoryResponseDTO> {
        return storyService.getAllPrivateStories(pageNumber, pageSize)
    }

    @PostMapping("/create-story", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createStory(
        @RequestPart storyRequestDTO: StoryRequestDTO,
        @RequestPart("file") file: MultipartFile
    ): StoryResponseDTO{
        return storyService.createStory(storyRequestDTO, file)
    }

    @PutMapping("/{storyId}/sentences/{sentenceId}")
    fun updateStory(
        @PathVariable storyId: String,
        @PathVariable sentenceId: String,
        @RequestBody requestStoryUpdateDTO: RequestStoryUpdateDTO)
            : StoryResponseDTO {
        println("Entered controller");
        return storyService.updateStory(ObjectId(storyId), sentenceId, requestStoryUpdateDTO)
    }
    @PostMapping("/{storyId}")
    fun uploadDummyStoryFile(
        @PathVariable storyId: String,
    ): StoryResponseDTO {
        return storyService.createStoryDummy(ObjectId(storyId))
    }

}