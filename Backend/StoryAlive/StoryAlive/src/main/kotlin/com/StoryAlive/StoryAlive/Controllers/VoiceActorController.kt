package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.VoiceActorRequest
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Services.VoiceActorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

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

//!  Make sure In the Content-Type column click it and type: audio/wav for wav (or audio/mpeg for MP3s).
//! and for request -> application/json
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createVoiceActor(
        @Valid @RequestPart request: VoiceActorRequest,
        @RequestPart files: List<MultipartFile>)
    : ResponseEntity<VoiceActorRequest> {
        val createdVoiceActor = voiceActorService.saveVoiceActor(request, files)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVoiceActor)
    }

    @PostMapping("/list", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createVoiceActorList(@Valid @RequestPart("request")  requests: List<VoiceActorRequest>, @RequestPart("files") files: List<MultipartFile>): ResponseEntity<List<VoiceActorRequest>> {
        val createdActors = voiceActorService.saveListVoiceActor(requests, files)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActors)
    }
}
