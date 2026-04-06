package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Genre
import com.StoryAlive.StoryAlive.Enums.Tags
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("enums")
class EnumsController {

    @GetMapping("/all")
    fun getAllEnums(): Map<String, List<String>> {
        return mapOf(
            "genre" to Genre.entries.map { it.name },
            "tags" to Tags.entries.map { it.name },
            "emotions" to Emotion.entries.map { it.name }
        )
    }

//    Response Example
//    {
//        "genre": [
//            "COMEDY",
//            "ACTION"
//        ],
//            "tags": [
//            "HORROR",
//        ]
//    }

}