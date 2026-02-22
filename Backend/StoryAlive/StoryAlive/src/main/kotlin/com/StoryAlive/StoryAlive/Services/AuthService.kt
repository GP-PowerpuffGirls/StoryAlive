package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.TokenPair
import com.StoryAlive.StoryAlive.DTOs.UserLoginRequest
import com.StoryAlive.StoryAlive.DTOs.UserSignupRequest
import com.StoryAlive.StoryAlive.Enums.Tags
import com.StoryAlive.StoryAlive.Models.RefreshTokens
import com.StoryAlive.StoryAlive.Models.User
import com.StoryAlive.StoryAlive.Repositories.RefreshTokenRepo
import com.StoryAlive.StoryAlive.Repositories.UserRepo
import com.StoryAlive.StoryAlive.Security.HashEncoder
import com.StoryAlive.StoryAlive.Security.JwtService
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import kotlin.String

@Service
class AuthService (
    private val jwtService: JwtService,
    private val userRepo: UserRepo,
    private val hashEncoder : HashEncoder,
    private val refreshTokenRepo: RefreshTokenRepo
    ){

    fun registerUser(user : UserSignupRequest) {

        if (userRepo.findByEmail(user.email) != null)
            throw IllegalArgumentException("User with this email already exists")
        else {

            val hashedPassword = hashEncoder.encode(user.password) ?: throw IllegalStateException("Failed to hash password")
            userRepo.save(
                User(
                    userId = ObjectId(),
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    password = hashedPassword,
                    age = user.age,
                    userPreferencesTags = user.preferencesTags,

                    favouriteStories = emptyList(),
                    favouriteVoiceActors = emptyList(),
                    totalSearchCount = 0,
                    totalPublishedStoriesCount = 0,
                    totalVoiceActorsCount = 0
                )
            )

        }

    }

    fun login(userData: UserLoginRequest) : TokenPair {
        val user = userRepo.findByEmail(userData.email) ?: throw IllegalArgumentException("This Email Does Not Exist")

        if (!hashEncoder.matches(userData.password, user.password)) {
            throw IllegalArgumentException("Invalid password")
        }

        val newAccessToken = jwtService.generateAccessToken(user.userId)
        val newRefreshToken = jwtService.generateRefereshToken(user.userId)

        storeRefreshToken(user.userId ,newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )

    }

    fun storeRefreshToken(userId: ObjectId, refreshToken: String): RefreshTokens? {
        val hashed = hashToken(refreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        return refreshTokenRepo.save(
            RefreshTokens(
                userId = userId,
                hashedToken = hashed,
                expiresAt = expiresAt
            )
        )

    }

    private fun hashToken(rawToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawToken.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair{
        if(!jwtService.validateRefreshToken(refreshToken)) throw IllegalArgumentException("Invalid refresh token")

        val userId = jwtService.extractUserId(refreshToken) ?: throw IllegalArgumentException("Invalid refresh token: missing user id")
        val user = userRepo.findById(ObjectId(userId))

        val hashed = hashToken(refreshToken)
        refreshTokenRepo.findByUserIdAndHashedToken(ObjectId(userId), hashed) ?: throw IllegalArgumentException("Refresh token not recognized")

        refreshTokenRepo.deleteByUserIdAndHashedToken(ObjectId(userId), hashed)
        val newAccessToken = jwtService.generateAccessToken(ObjectId(userId))
        val newRefreshToken = jwtService.generateRefereshToken(ObjectId(userId))

        storeRefreshToken(ObjectId(userId), newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }
}