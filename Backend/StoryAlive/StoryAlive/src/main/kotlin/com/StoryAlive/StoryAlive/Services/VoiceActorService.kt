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
import java.util.Optional

@Service
class VoiceActorService(val voiceActorRepo: VoiceActorRepo) {

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

        val userId = getCurrentUserId()

        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);

        return voiceActorRepo.findAllByUserIdAndIsPrivateTrue(userId, pageable)

    }

    fun createVoiceActor(request: VoiceActorRequest): VoiceActorRequest {

        val userId = getCurrentUserId()

        val savedActor: VoiceActor
        val actorName = request.actorName.trim().lowercase()

        if (request.isPrivate) {

            // PRIVATE FLOW
            val existingPrivate =
                voiceActorRepo.findByUserIdAndActorNameAndIsPrivateTrue(userId, actorName)

            savedActor = if (existingPrivate != null) {
                existingPrivate.audios += request.audios
                voiceActorRepo.save(existingPrivate)
            } else {
                voiceActorRepo.save(
                    VoiceActor(
                        voiceActorId = ObjectId(),
                        userId = userId,
                        actorName = actorName,
                        gender = request.gender,
                        isAdult = request.isAdult,
                        isPrivate = true,
                        audios = request.audios
                    )
                )
            }

        }
        else {

            // PUBLIC FLOW
            val existingPublic =
                voiceActorRepo.findByActorNameAndIsPrivateFalse(actorName)

            savedActor = if (existingPublic != null) {
                existingPublic.audios += request.audios
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
                        audios = request.audios
                    )
                )
            }
        }

        return VoiceActorRequest(
            actorName = savedActor.actorName,
            gender = savedActor.gender,
            isAdult = savedActor.isAdult,
            isPrivate = savedActor.isPrivate,
            audios = savedActor.audios
        )
    }

    private fun getCurrentUserId(): ObjectId {
        val user = SecurityContextHolder
            .getContext()
            .authentication
            ?.principal as CurrentUserDetails
        return user.getUserId()
    }
}

