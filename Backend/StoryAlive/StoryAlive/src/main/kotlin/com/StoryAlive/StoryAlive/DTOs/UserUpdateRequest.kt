package com.StoryAlive.StoryAlive.DTOs


data class UserUpdateRequest(

    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val age: Int?,

    val currentPassword: String?,
    val newPassword: String?,

)
