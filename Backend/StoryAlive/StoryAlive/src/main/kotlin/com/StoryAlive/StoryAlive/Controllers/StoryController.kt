package com.StoryAlive.StoryAlive.Controllers

import Story
import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.Story.StoryRequestDTO
import com.StoryAlive.StoryAlive.Services.StoryService
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import retrofit2.http.Multipart


@RestController
@RequestMapping("stories")
class StoryController(private val storyService: StoryService) {

    @GetMapping
    fun getAllStories(@RequestParam(defaultValue = "0") pageNumber:Int,
                      @RequestParam(defaultValue = "10") pageSize:Int): Page<Story> {
        return storyService.getAllStories(pageNumber, pageSize);
    }
    @GetMapping("/private")
    fun getAllPrivateStories(@RequestParam(defaultValue = "0") pageNumber:Int,
                      @RequestParam(defaultValue = "10") pageSize:Int): Page<Story> {
        return storyService.getAllPrivateStories(pageNumber, pageSize);
    }
//    @PostMapping(consumes = ["multipart/form-data"])
//    fun CreateNewStory(@RequestPart("file") file: MultipartFile,
//                       @RequestPart("data") storyRequestDTO: StoryRequestDTO): Story {
//        return storyService.createNewStory(file, storyRequestDTO);
//    }
    @PostMapping("/create-story")
fun createStoryMetaData(@RequestBody storyRequestDTO: StoryRequestDTO): String{
    return storyService.createStoryMetaData(storyRequestDTO).toString();
}
    @PostMapping("/{storyId}/upload")
    fun uploadStoryFile(
        @PathVariable storyId: String,
        @RequestParam("file") file: MultipartFile
    ):Story {
        return storyService.createStory(ObjectId( storyId), file)
    }
    @PostMapping("/{storyId}")
    fun uploadDummyStoryFile(
        @PathVariable storyId: String, ) {
        storyService.createStoryDummy(ObjectId( storyId))
    }

}