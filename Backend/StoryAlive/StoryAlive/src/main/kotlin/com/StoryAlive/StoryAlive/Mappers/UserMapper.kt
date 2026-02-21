package com.StoryAlive.StoryAlive.Mappers

import com.StoryAlive.StoryAlive.DTOs.UserSignupRequest
import com.StoryAlive.StoryAlive.Models.User
import org.bson.types.ObjectId

fun UserSignupRequest.toUser(): User {
    return User(
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password, //assigned password should be hashed ya menna
        age = age,
        userPreferencesTags = preferencesTags
    )
}