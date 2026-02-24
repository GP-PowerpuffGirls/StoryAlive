package com.StoryAlive.StoryAlive.DTOs

import com.StoryAlive.StoryAlive.Enums.Tags
import com.StoryAlive.StoryAlive.Models.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.bson.types.ObjectId

data class UserSignupRequest(
    @field:NotBlank(message = "First Name is Required")
    val firstName: String,
    @field:NotBlank(message = "Last Name is Required")
    val lastName: String,
    @field:Email(message = "Invalid Email")
    val email: String,
    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}\$", message = "Password must be at least 8 characters long and contain both letters and numbers")
    val password: String,
    @field:Min(value = 5, message = "You must be at least 5 years old")
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
        )
    }
}
