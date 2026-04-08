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

@Service
class UserService ( private val userRepo: UserRepo, private val hashEncoder : HashEncoder) {

     fun getUserData() : UserDTO{

        val userId : ObjectId = getCurrentUser().getUserId()
        val user: Optional<User> = userRepo.findById(userId)

        return UserDTO(
            firstName = user.get().firstName,
            lastName = user.get().lastName,
            email = user.get().lastName,
            accountCreationDate = user.get().accountCreationDate,
            favouriteVoiceActors =  user.get().favouriteVoiceActors,
            totalPublishedStoriesCount = user.get().totalPublishedStoriesCount,
            totalVoiceActorsCount = user.get().totalVoiceActorsCount,
            totalStoriesCount = user.get().totalStoriesCount,
            age = user.get().age
        )

    }
     fun saveUser(user: User){
        userRepo.save(user)
    }

     fun getUserFavouriteStories() : List<ObjectId>{

        val userId : ObjectId = getCurrentUser().getUserId()
        val user: Optional<User> = userRepo.findById(userId)

        return user.get().favouriteStories
    }

     fun editUserData( updatedData : UserUpdateRequest) : UserDTO{

        val userId : ObjectId = getCurrentUser().getUserId()
        val user: Optional<User> = userRepo.findById(userId)

        var hashedPassword=""
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
            totalStoriesCount = updatedUser.totalStoriesCount,
            age = updatedData.age?: user.get().age
        )
    }
    fun getUser(): User {
        return userRepo.findByUserId(getCurrentUser().getUserId())
    }

    fun getCurrentUser(): CurrentUserDetails {
        val auth = SecurityContextHolder.getContext().authentication
        require(auth != null && auth.principal is CurrentUserDetails)
        return auth.principal as CurrentUserDetails
    }

    fun addStoryToHistory(storyId: ObjectId) {

        val userId : ObjectId = getCurrentUser().getUserId()
        val user: Optional<User> = userRepo.findById(userId)

        val newHistory = user.get().historyStories + storyId

        userRepo.save(
            User(
                historyStories = newHistory,

                userId = userId,
                firstName = user.get().firstName,
                lastName = user.get().lastName,
                email = user.get().email,
                password = user.get().password ,
                age = user.get().age,
                )
        )

    }

    fun getHistoryStories() : List<ObjectId>{
        val userId : ObjectId = getCurrentUser().getUserId()
        val user: Optional<User> = userRepo.findById(userId)

        return user.get().historyStories
    }
}