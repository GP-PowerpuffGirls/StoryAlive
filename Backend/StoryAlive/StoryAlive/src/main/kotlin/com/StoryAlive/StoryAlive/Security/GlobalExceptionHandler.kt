package com.StoryAlive.StoryAlive.Security

import com.StoryAlive.StoryAlive.DTOs.ApiError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {

        val error = ApiError(
            status = ex.statusCode.value(),
            message = ex.reason ?: "Unexpected error",
            path = request.requestURI,
            timestamp = Instant.now().toString()
        )

        return ResponseEntity.status(ex.statusCode).body(error)
    }

}