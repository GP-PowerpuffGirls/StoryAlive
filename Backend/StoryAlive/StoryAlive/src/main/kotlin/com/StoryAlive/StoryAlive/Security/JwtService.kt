package com.StoryAlive.StoryAlive.Security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.io.encoding.Base64

@Service
class JwtService ( @Value($$"${jwt.secret:}") private val jwtSecrete : String ) {

//    ! Change access token to be 15-30 min
    val accessTokenValidityMs = 120L * 60L * 1000L // 2 hours
    val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000L // 30 days

    private val secretKey = Keys.hmacShaKeyFor(
        if (jwtSecrete.isBlank()) {
                    Base64.decode("9mHznJMNt8kR2pLqX7nY5mZ6aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ5aB6cD7eF8g")
                } else {
                    Base64.decode(jwtSecrete)
                }
    )

    fun generateToken( userId: ObjectId, type: String, expiry: Long ): String? {

        val now = Date()
        val expiryDate = Date(now.time + expiry)

        return Jwts.builder()
                    .subject(userId.toString())
                    .claim("type", type)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(secretKey, Jwts.SIG.HS256)
                    .compact()

    }
    fun generateAccessToken(userId: ObjectId) : String = generateToken(userId, "access", accessTokenValidityMs) ?: throw IllegalStateException ("Failed to generate access token")
    fun generateRefereshToken(userId: ObjectId) : String = generateToken(userId, "refresh", refreshTokenValidityMs) ?: throw IllegalStateException ("Failed to generate refresh token")

    fun parseAllClaims(token: String): Claims? {

        val rawToken = if(token.startsWith("Bearer")) token.removePrefix("Bearer ") else token

        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(rawToken)
            .payload

    }
    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims.get("type") as String
        return tokenType == "access"
    }
    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims.get("type") as String
        return tokenType == "refresh"
    }

    fun extractUserId(token: String): String {
        val claims = parseAllClaims(token) ?: return ""
        return claims.subject ?: ""
    }
    fun isTokenExpired(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return true
        val expiration = claims.expiration ?: return true
        return expiration.before(Date())
    }

}