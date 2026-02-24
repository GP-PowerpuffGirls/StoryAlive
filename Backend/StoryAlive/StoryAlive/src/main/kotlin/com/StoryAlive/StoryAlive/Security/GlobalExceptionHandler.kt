package com.StoryAlive.StoryAlive.Security

import com.StoryAlive.StoryAlive.DTOs.ApiError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    // Handles ResponseStatusException (temporary if you're still using it)
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

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {

        val fieldErrors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        val error = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed",
            path = request.requestURI,
            timestamp = Instant.now().toString(),
            errors = fieldErrors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {

        val error = ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "Something went wrong",
            path = request.requestURI,
            timestamp = Instant.now().toString()
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}