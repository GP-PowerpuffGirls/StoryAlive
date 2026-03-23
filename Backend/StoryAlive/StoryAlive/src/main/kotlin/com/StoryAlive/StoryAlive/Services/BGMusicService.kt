package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.BGMusicDto
import com.StoryAlive.StoryAlive.Models.BackgroundMusic
import com.StoryAlive.StoryAlive.Repositories.BgMusicRepo
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class BGMusicService(private val bgMusicRepo: BgMusicRepo) {
    fun uploadBGMusic(bgMusicDTO: BGMusicDto): BackgroundMusic {
        var bgMusic: BackgroundMusic= BackgroundMusic(ObjectId(),bgMusicDTO.musicPath, bgMusicDTO.emotion, bgMusicDTO.forKids)
        return bgMusicRepo.save(bgMusic)
    }
    fun getAllBGMusicByForKids(forKids: Boolean): List<BackgroundMusic>{
        return bgMusicRepo.findAllByForKids(forKids)
    }
}