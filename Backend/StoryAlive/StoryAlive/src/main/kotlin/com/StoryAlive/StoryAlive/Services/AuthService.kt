package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.TokenPair
import com.StoryAlive.StoryAlive.DTOs.UserLoginRequest
import com.StoryAlive.StoryAlive.DTOs.UserSignupRequest
import com.StoryAlive.StoryAlive.Models.RefreshTokens
import com.StoryAlive.StoryAlive.Models.User
import com.StoryAlive.StoryAlive.Repositories.RefreshTokenRepo
import com.StoryAlive.StoryAlive.Repositories.UserRepo
import com.StoryAlive.StoryAlive.Security.HashEncoder
import com.StoryAlive.StoryAlive.Security.JwtService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
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

    fun registerUser(user : UserSignupRequest) : TokenPair {

        if (userRepo.findByEmail(user.email) != null)
            throw ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists")
        else {

            val hashedPassword = hashEncoder.encode(user.password) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to hash password")
            val generatedUserId = ObjectId()
            userRepo.save(
                User(
                    userId = generatedUserId,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    password = hashedPassword,
                    age = user.age,

                    favouriteStories = emptyList(),
                    favouriteVoiceActors = emptyList(),
                    totalPublishedStoriesCount = 0,
                    totalVoiceActorsCount = 0
                )
            )

            return getTokens(generatedUserId);

        }

    }

    fun login(userData: UserLoginRequest) : TokenPair {
        val user = userRepo.findByEmail(userData.email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found")

        if (!hashEncoder.matches(userData.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password")
        }

        return getTokens(user.userId)

    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair {
        if(!jwtService.validateRefreshToken(refreshToken)) throw ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid refresh token")

        val userId = ObjectId(jwtService.extractUserId(refreshToken)) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing user ID")

        val hashed = hashToken(refreshToken)
        refreshTokenRepo.findByUserIdAndHashedToken(userId, hashed) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not recognized")
        refreshTokenRepo.deleteByUserIdAndHashedToken(userId, hashed)

        return getTokens(userId)

    }

    fun getTokens(userId: ObjectId): TokenPair {

//        refreshTokenRepo.findByUserId(userId) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not recognized")
//        refreshTokenRepo.deleteByUserId(userId)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefereshToken(userId)

        storeRefreshToken(userId ,newRefreshToken)

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

    fun logout(refreshToken: String) {

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")
        }

        val userId = jwtService.extractUserId(refreshToken)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing user ID")

        val hashedToken = hashToken(refreshToken)

        val deletedCount = refreshTokenRepo.deleteByUserIdAndHashedToken(ObjectId(userId), hashedToken)


        if (deletedCount == 0L) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found")
        }
    }

}