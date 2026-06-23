package com.StoryAlive.StoryAlive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class StoryAliveApplication

fun main(args: Array<String>) {
	runApplication<StoryAliveApplication>(*args)
}
