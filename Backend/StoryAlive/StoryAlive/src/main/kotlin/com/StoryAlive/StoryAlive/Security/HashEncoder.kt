package com.StoryAlive.StoryAlive.Security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {

    private val bcrypt = BCryptPasswordEncoder()

    // Encodes a raw password using BCrypt
    fun encode(rawPassword: String?): String? = bcrypt.encode(rawPassword)

    // Verifies a raw password against an encoded hash
    fun matches(rawPassword: String?, encodedPassword: String?): Boolean = bcrypt.matches(rawPassword, encodedPassword)

}