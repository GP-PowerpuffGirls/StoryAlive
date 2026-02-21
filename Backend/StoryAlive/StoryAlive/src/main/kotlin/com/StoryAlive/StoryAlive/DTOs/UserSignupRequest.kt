package com.StoryAlive.StoryAlive.DTOs

import com.StoryAlive.StoryAlive.Enums.Tags
import com.StoryAlive.StoryAlive.Models.User
import org.bson.types.ObjectId

data class UserSignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val age: Int,
    val preferencesTags: List<Tags>
){
    fun UserSignupRequest.toUser(): User {
        return User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            age = age,
            userPreferencesTags = preferencesTags
        )
    }
}
