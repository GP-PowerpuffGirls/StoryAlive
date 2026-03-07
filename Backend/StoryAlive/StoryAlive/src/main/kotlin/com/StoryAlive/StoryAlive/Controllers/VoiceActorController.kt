package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.VoiceActorRequest
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Services.VoiceActorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/voice-actors")
class VoiceActorController(private val voiceActorService: VoiceActorService){

    @GetMapping
    fun getAllPublicVoiceActors(
        @RequestParam(defaultValue = "0") pageNumber:Int,
        @RequestParam(defaultValue = "10") pageSize:Int): List<VoiceActor>
    {
        return voiceActorService.getAllPublicVoiceActors(pageNumber, pageSize).content
    }

    @GetMapping("/private")
    fun getAllPrivateVoiceActorsOfUser(
        @RequestParam(defaultValue = "0") pageNumber:Int,
        @RequestParam(defaultValue = "10") pageSize:Int
    ): List<VoiceActor>{
        return voiceActorService.getAllPrivateVoiceActorsOfUser(pageNumber, pageSize).content
    }

    @PostMapping
    fun createPrivateVoiceActor( @Valid @RequestBody voiceActor: VoiceActorRequest): ResponseEntity<VoiceActorRequest> {
        val createdVoiceActor = voiceActorService.createVoiceActor(voiceActor)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVoiceActor)
    }

}
