package com.StoryAlive.StoryAlive
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gemini")
class GeminiConfig {
    lateinit var apiKey: String
    lateinit var baseUrl: String
}