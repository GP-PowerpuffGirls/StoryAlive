package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.VoiceActorRequest
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Repositories.VoiceActorRepo
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class VoiceActorService(val voiceActorRepo: VoiceActorRepo) {

    fun getAllPublicVoiceActors(pageNumber:Int, pageSize:Int): Page<VoiceActor> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);
        return voiceActorRepo.findAllByIsPrivateFalse(pageable);
    }

    fun getAllPrivateVoiceActorsOfUser(pageNumber: Int, pageSize: Int): Page<VoiceActor> {

        val currentUser = (SecurityContextHolder
            .getContext()
            .authentication
            ?.principal) as CurrentUserDetails

        val userId: ObjectId = currentUser.getUserId()

        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);

        return voiceActorRepo.findAllByUserIdAndIsPrivateTrue(userId, pageable)

    }

    fun createVoiceActor(voiceActor: VoiceActorRequest) {

        val currentUser = SecurityContextHolder
            .getContext()
            .authentication
            ?.principal as CurrentUserDetails
        val userId = currentUser.getUserId()

        // 1️⃣ Check private actor for this user
        val existingPrivate = voiceActorRepo.findByUserIdAndActorNameAndIsPrivateTrue(userId, voiceActor.actorName)

        if (existingPrivate != null) {
            existingPrivate.audios = existingPrivate.audios + voiceActor.audios
            voiceActorRepo.save(existingPrivate)
            return
        }

        // 2️⃣ Check public actor
        val existingPublic = voiceActorRepo.findByActorNameAndIsPrivateFalse(voiceActor.actorName)

        if (existingPublic != null) {

            if (voiceActor.isPrivate) {
                // create new private actor
                voiceActorRepo.save(
                    VoiceActor(
                        voiceActorId = ObjectId(),
                        userId = userId,
                        actorName = voiceActor.actorName,
                        gender = voiceActor.gender,
                        isAdult = voiceActor.isAdult,
                        isPrivate = true,
                        audios = voiceActor.audios
                    )
                )
            }
            else {
                existingPublic.audios += voiceActor.audios
                voiceActorRepo.save(existingPublic)
            }

            return
        }

        // 3️⃣ Create new actor
        voiceActorRepo.save(
            VoiceActor(
                voiceActorId = ObjectId(),
                userId = userId,
                actorName = voiceActor.actorName,
                gender = voiceActor.gender,
                isAdult = voiceActor.isAdult,
                isPrivate = voiceActor.isPrivate,
                audios = voiceActor.audios
            )
        )
    }

}

