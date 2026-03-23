package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.VoiceActorRequest
import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity
import com.StoryAlive.StoryAlive.Models.Audio
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Repositories.VoiceActorRepo
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Optional

@Service
class VoiceActorService(val voiceActorRepo: VoiceActorRepo, val supabaseStorageService: SupabaseStorageService, val userService: UserService) {

    fun getAudio(actorId: ObjectId, emotion: Emotion, intensity: Intensity): Audio {
        val actor = voiceActorRepo.findAudioByEmotionAndIntensity(actorId, emotion, intensity) ?:
        throw RuntimeException("Audio not found")

        return actor.audios.firstOrNull()
            ?: throw RuntimeException("Audio not found")
    }

    fun getAllPublicVoiceActors(pageNumber:Int, pageSize:Int): Page<VoiceActor> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);
        return voiceActorRepo.findAllByIsPrivateFalse(pageable);
    }

    fun getAllPublicVoiceActors(): List<VoiceActor> {
        return voiceActorRepo.findAllByIsPrivateFalse();
    }

    fun getAudioByActorId(actorId: ObjectId): Optional<VoiceActor> {
        return voiceActorRepo.findById(actorId)
    }

    fun getAllPrivateVoiceActorsOfUser(pageNumber: Int, pageSize: Int): Page<VoiceActor> {

        val userId = userService.getCurrrenctUser().getUserId()

        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);

        return voiceActorRepo.findAllByUserIdAndIsPrivateTrue(userId, pageable)

    }

    fun saveVoiceActor(request: VoiceActorRequest, files: List<MultipartFile>): VoiceActorRequest {

        if (files.size != request.audios.size) throw IllegalArgumentException("Files count must match audio metadata count")
        val userId = userService.getCurrrenctUser().getUserId()

        val savedActor: VoiceActor
        val actorName = request.actorName.trim().lowercase()
        val voiceActorId = ObjectId();
        val audios = saveAudios(request, files, voiceActorId);

        if (request.isPrivate) {

            // PRIVATE FLOW
            val existingPrivate = voiceActorRepo.findByUserIdAndActorNameAndIsPrivateTrue(userId, actorName)

            savedActor = if (existingPrivate != null) {
                existingPrivate.audios += audios
                voiceActorRepo.save(existingPrivate)
            } else {
                voiceActorRepo.save(
                    VoiceActor(
                        voiceActorId = voiceActorId,
                        userId = userId,
                        actorName = actorName,
                        gender = request.gender,
                        isAdult = request.isAdult,
                        isPrivate = true,
                        audios = audios,
                        preferredRole = request.preferredRole
                    )
                )
            }

        }
        else {

            // PUBLIC FLOW
            val existingPublic =
                voiceActorRepo.findByActorNameAndIsPrivateFalse(actorName)

            savedActor = if (existingPublic != null) {
                existingPublic.audios += audios
                voiceActorRepo.save(existingPublic)
            } else {
                voiceActorRepo.save(
                    VoiceActor(
                        voiceActorId = ObjectId(),
                        userId = null,
                        actorName = actorName,
                        gender = request.gender,
                        isAdult = request.isAdult,
                        isPrivate = false,
                        audios = audios,
                        preferredRole = request.preferredRole
                    )
                )
            }
        }

        return VoiceActorRequest(
            actorName = savedActor.actorName,
            gender = savedActor.gender,
            isAdult = savedActor.isAdult,
            isPrivate = savedActor.isPrivate,
            audios = audios,
            preferredRole = savedActor.preferredRole
        )
    }

    fun saveListVoiceActor(requests: List<VoiceActorRequest>, files: List<MultipartFile>): List<VoiceActorRequest> {

        val expectedFiles = requests.sumOf { it.audios.size }
        if (expectedFiles != files.size) throw IllegalArgumentException("Total files must match total audio metadata count")

        val results = mutableListOf<VoiceActorRequest>()
        var fileIndex = 0

        for (request in requests) {

            val audioCount = request.audios.size

            val actorFiles = files.subList(fileIndex, fileIndex + audioCount)

            val saved = saveVoiceActor(request, actorFiles)

            results.add(saved)

            fileIndex += audioCount
        }

        return results
    }

    private fun saveAudios(request: VoiceActorRequest, files: List<MultipartFile>, userId: ObjectId): List<Audio>{

        val audioUrls = files.map { file ->
            supabaseStorageService.saveAudioToCloud(file, userId, "voice-actor-files")
        }

        return audioUrls.mapIndexed { index, url ->
            val meta = request.audios[index]

            Audio(
                filepath = url,
                emotion = meta.emotion,
                intensity = meta.intensity
            )
        }

    }
}

