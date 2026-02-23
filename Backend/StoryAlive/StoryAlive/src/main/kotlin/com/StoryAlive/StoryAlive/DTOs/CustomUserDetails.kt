package com.StoryAlive.StoryAlive.DTOs

import org.bson.types.ObjectId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails (private val userId: String) :  UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String? = null

    override fun getUsername(): String = userId

    fun getUserId(): ObjectId = ObjectId(userId)
}