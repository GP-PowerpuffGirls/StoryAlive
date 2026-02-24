package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Services.VoiceActorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class VoiceActorController(private val voiceActorService: VoiceActorService){

    @GetMapping("/voice-actors")
    fun getAllPublicVoiceActors(
        @RequestParam(defaultValue = "0") pageNumber:Int,
        @RequestParam(defaultValue = "10") pageSize:Int): List<VoiceActor>
    {
        return voiceActorService.getAllPublicVoiceActors(pageNumber, pageSize).content
    }
    //TODO: Implement the API for getting all private voice actors of a single user
    //TODO: Implement the API for creating a private voice actor of a single user


}
