package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.BGMusicDto
import com.StoryAlive.StoryAlive.Models.BackgroundMusic
import com.StoryAlive.StoryAlive.Services.BGMusicService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bg-music")
class BGMusicController(private val bgMusicService: BGMusicService) {
    @PostMapping
    fun uploadBGMusic(@RequestBody bgMusic: BGMusicDto):BackgroundMusic {
        return bgMusicService.uploadBGMusic(bgMusic)
    }

    @PostMapping("/list")
    fun uploadBGMusicList(@RequestBody bgMusic: List<BGMusicDto>):List<BGMusicDto>  {
        return bgMusicService.uploadBGMusicList(bgMusic)
    }
}