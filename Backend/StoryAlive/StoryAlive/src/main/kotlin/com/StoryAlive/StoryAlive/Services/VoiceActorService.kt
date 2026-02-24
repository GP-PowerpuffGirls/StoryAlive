package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Repositories.VoiceActorRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class VoiceActorService(val voiceActorRepo: VoiceActorRepo) {
    fun getAllPublicVoiceActors(pageNumber:Int, pageSize:Int): Page<VoiceActor> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);
        return voiceActorRepo.findAllByIsPrivateFalse(pageable);
    }
    /*TODO: Implement the function for getting all private voice actors of a single user the
    *  user id will be retrieved by retrieving the current user email and querying the id from
    *  the email and getting all voice actors by that id*/

    /*TODO: Implement the API for creating a private voice actor of a single user.
       we'll have to check that the name isn't repeated then that the audios isn't empty, then add the userId to the voice actor
       and save. the userId will be brought from the current user email too*/
}

