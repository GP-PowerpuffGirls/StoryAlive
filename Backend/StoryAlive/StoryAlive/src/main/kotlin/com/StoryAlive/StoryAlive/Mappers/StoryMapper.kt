package com.StoryAlive.StoryAlive.mapper

import Story
import com.StoryAlive.StoryAlive.DTOs.StoryResponseDTO

public fun Story.toResponse(): StoryResponseDTO {
    return StoryResponseDTO(
        storyId = this.storyId.toString(),
        creatorId = this.creatorId.toString(),
        voiceActors = this.voiceActors.mapKeys { it.key.toString() },
        title = this.title,
        description = this.description,
        tags = this.tags,
        genre = this.genre,
        duration = this.duration,
        isPrivate = this.isPrivate,
        hasSfx = this.hasSfx,
        hasBackgroundMusic = this.hasBackgroundMusic,
        finalAudioPath = this.finalAudioPath,
        jsonPath = this.jsonPath,
        pdfPath = this.pdfPath,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
        minimumAge = this.minimumAge,
        numberOfViews = this.numberOfViews
    )
}