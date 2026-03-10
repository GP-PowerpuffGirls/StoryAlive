package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.UserDTO
import com.StoryAlive.StoryAlive.DTOs.UserUpdateRequest
import com.StoryAlive.StoryAlive.Models.User
import com.StoryAlive.StoryAlive.Repositories.UserRepo
import com.StoryAlive.StoryAlive.Security.HashEncoder
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Optional
import kotlin.collections.List

@Service
class UserService ( private val userRepo: UserRepo, private val hashEncoder : HashEncoder) {

    public fun getUserData() : UserDTO{

        val currentUser = (SecurityContextHolder
            .getContext()
            .authentication
            ?.principal) as CurrentUserDetails

        val userId : ObjectId = currentUser.getUserId()
        val user: Optional<User> = userRepo.findById(userId) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token")

        return UserDTO(
            firstName = user.get().firstName,
            lastName = user.get().lastName,
            email = user.get().lastName,
            accountCreationDate = user.get().accountCreationDate,
            favouriteVoiceActors =  user.get().favouriteVoiceActors,
            totalPublishedStoriesCount = user.get().totalPublishedStoriesCount,
            totalVoiceActorsCount = user.get().totalVoiceActorsCount,
            totalStoriesCount = user.get().totalStoriesCount,
        )

    }

    public fun editUserData( updatedData : UserUpdateRequest) : UserDTO{

        val currentUser = (SecurityContextHolder
            .getContext()
            .authentication
            ?.principal) as CurrentUserDetails

        val userId : ObjectId = currentUser.getUserId()
        val user: Optional<User> = userRepo.findById(userId) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token")

        var hashedPassword="";
        if (updatedData.newPassword != "" && updatedData.currentPassword != "" && updatedData.newPassword == updatedData.currentPassword){
            hashedPassword = hashEncoder.encode(updatedData.newPassword) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to hash password")
        }
        else if(updatedData.newPassword == updatedData.currentPassword){
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Password")
        }

        val updatedUser = userRepo.save(
            User(
                userId = user.get().userId,

                firstName = updatedData.firstName ?: user.get().firstName,
                lastName = updatedData.lastName ?: user.get().lastName,
                email = updatedData.email ?: user.get().email,
                password = if(hashedPassword != "") hashedPassword else user.get().password ,
                age = updatedData.age ?: user.get().age,

            )
        )

        return UserDTO(
            firstName = updatedUser.firstName,
            lastName = updatedUser.lastName,
            email = updatedUser.email,
            accountCreationDate = updatedUser.accountCreationDate,
            favouriteVoiceActors = updatedUser.favouriteVoiceActors,
            totalPublishedStoriesCount = updatedUser.totalStoriesCount,
            totalStoriesCount = updatedUser.totalStoriesCount
        )
    }
    fun getCurrrenctUser(): CurrentUserDetails {
        val auth = SecurityContextHolder.getContext().authentication
        require(auth != null && auth.principal is CurrentUserDetails)
        return auth.principal as CurrentUserDetails
    }


}