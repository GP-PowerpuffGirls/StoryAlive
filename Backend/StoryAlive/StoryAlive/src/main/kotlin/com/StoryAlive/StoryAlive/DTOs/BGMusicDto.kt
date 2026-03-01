package com.StoryAlive.StoryAlive.DTOs

import com.StoryAlive.StoryAlive.Enums.BGMusicEmotion
import lombok.AllArgsConstructor
import lombok.Data
import lombok.Getter


data class BGMusicDto( val musicPath: String, val emotion: BGMusicEmotion, val forKids: Boolean ) {

}