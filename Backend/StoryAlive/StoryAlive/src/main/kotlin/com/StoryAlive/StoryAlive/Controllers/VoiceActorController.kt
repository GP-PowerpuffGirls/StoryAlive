package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.VoiceActorRequest
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Services.VoiceActorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @GetMapping("/voice-actors/private")
    fun getAllPrivateVoiceActorsOfUser(
        @RequestParam(defaultValue = "0") pageNumber:Int,
        @RequestParam(defaultValue = "10") pageSize:Int
    ): List<VoiceActor>{
        return voiceActorService.getAllPrivateVoiceActorsOfUser(pageNumber, pageSize).content
    }

    fun createPrivateVoiceActor( @Valid @RequestBody voiceActor: VoiceActorRequest): ResponseEntity<String> {
        voiceActorService.createVoiceActor(voiceActor)
        return ResponseEntity.status(HttpStatus.CREATED).body("Voice actor created successfully")
    }

}
