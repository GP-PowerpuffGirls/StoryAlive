package com.StoryAlive.StoryAlive.Controllers

import Story
import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.Story.StoryRequestDTO
import com.StoryAlive.StoryAlive.Services.StoryService
import org.springframework.data.domain.Page
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


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
    @PostMapping(consumes = ["multipart/form-data"])
    fun CreateNewStory(@RequestPart("file") file: MultipartFile,
                       @RequestPart("data") storyRequestDTO: StoryRequestDTO): Story {
        return storyService.createNewStory(file, storyRequestDTO);
    }

}