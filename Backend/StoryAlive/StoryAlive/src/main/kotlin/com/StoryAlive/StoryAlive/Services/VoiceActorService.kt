package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.VoiceActorRequest
import com.StoryAlive.StoryAlive.Enums.Emotion
import com.StoryAlive.StoryAlive.Enums.Intensity
import com.StoryAlive.StoryAlive.Models.Audio
import com.StoryAlive.StoryAlive.Models.VoiceActor
import com.StoryAlive.StoryAlive.Repositories.VoiceActorRepo
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.Optional

@Service
class VoiceActorService(val voiceActorRepo: VoiceActorRepo, val supabaseStorageService: SupabaseStorageService, val userService: UserService) {

    fun getAudio(actorId: ObjectId, emotion: Emotion, intensity: Intensity): Audio {
        val actor = voiceActorRepo.findAudioByEmotionAndIntensity(actorId, emotion, intensity) ?:
        throw RuntimeException("Audio not found")

        return actor.audios.firstOrNull()
            ?: throw RuntimeException("Audio not found")
    }

    fun getAllPublicVoiceActors(pageNumber:Int, pageSize:Int): Page<VoiceActor> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)
        return voiceActorRepo.findAllByIsPrivateFalse(pageable)
    }

    fun getAllPublicVoiceActors(): List<VoiceActor> {
        return voiceActorRepo.findAllByIsPrivateFalse()
    }

    fun getAudioByActorId(actorId: ObjectId): Optional<VoiceActor> {
        return voiceActorRepo.findById(actorId)
    }

    fun getAllPrivateVoiceActorsOfUser(pageNumber: Int, pageSize: Int): Page<VoiceActor> {

        val userId = userService.getCurrentUser().getUserId()

        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)

        return voiceActorRepo.findAllByUserIdAndIsPrivateTrue(userId, pageable)

    }
    fun getAllAvailableVoiceActorsForUser(pageNumber: Int, pageSize: Int): Page<VoiceActor> {

        val userId = userService.getCurrentUser().getUserId()

        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)

        return voiceActorRepo.findAllPublicAndUserPrivate(userId, pageable)

    }

    fun saveVoiceActor(request: VoiceActorRequest, files: List<MultipartFile>): VoiceActorRequest {

        if (files.size != request.audios.size)
            throw IllegalArgumentException("Files count must match audio metadata count")

        val userId = userService.getCurrentUser().getUserId()
        val actorName = request.actorName.trim().lowercase()
        val newActorId = ObjectId()

        val existingActor = if (request.isPrivate) {
            voiceActorRepo.findByUserIdAndActorNameAndIsPrivateTrue(userId, actorName)
        } else {
            voiceActorRepo.findByActorNameAndIsPrivateFalse(actorName)
        }

        val savedActor = if (existingActor != null) {

            val audios = saveAudios(request, files, existingActor.voiceActorId)
            existingActor.audios += audios
            voiceActorRepo.save(existingActor)

        } else {

            val audios = saveAudios(request, files, newActorId)

            voiceActorRepo.save(
                VoiceActor(
                    voiceActorId = newActorId,
                    userId = if (request.isPrivate) userId else null,
                    actorName = actorName,
                    gender = request.gender,
                    isAdult = request.isAdult,
                    isPrivate = request.isPrivate,
                    audios = audios,
                    preferredRole = request.preferredRole
                )
            )
        }

        return VoiceActorRequest(
            actorName = savedActor.actorName,
            gender = savedActor.gender,
            isAdult = savedActor.isAdult,
            isPrivate = savedActor.isPrivate,
            audios = savedActor.audios,
            preferredRole = savedActor.preferredRole
        )
    }
    fun saveListVoiceActor(requests: List<VoiceActorRequest>, files: List<MultipartFile>): List<VoiceActorRequest> {

        val expectedFiles = requests.sumOf { it.audios.size }
        if (expectedFiles != files.size) throw IllegalArgumentException("Total files must match total audio metadata count")

        val results = mutableListOf<VoiceActorRequest>()
        var fileIndex = 0

        for (request in requests) {

            val audioCount = request.audios.size

            val actorFiles = files.subList(fileIndex, fileIndex + audioCount)

            val saved = saveVoiceActor(request, actorFiles)

            results.add(saved)

            fileIndex += audioCount
        }

        return results
    }

    private fun saveAudios(
        request: VoiceActorRequest,
        files: List<MultipartFile>,
        userId: ObjectId
    ): List<Audio> {

        val audioUrls: List<String> = files.map { file ->

            // convert to WAV
            val wavFile = convertToWav(file)

            // upload converted file

            val url = supabaseStorageService.saveAudioToCloud(
                audioBytes= wavFile.readBytes(), fileName= file.name, actorId= userId, usedBucket= "voice-actor-files"
            )

            // delete temp file
            wavFile.delete()

            url
        }

        return audioUrls.mapIndexed { index, url ->
            val meta = request.audios[index]

            Audio(
                filepath = url,
                emotion = meta.emotion,
                intensity = meta.intensity
            )
        }
    }
    //HELPER FUNCTIONS
private fun convertToWav(file: MultipartFile): File {

    val inputFile = File.createTempFile("input_", file.originalFilename)
    file.transferTo(inputFile)

    val outputFile = File.createTempFile("converted_", ".wav")

    val process = ProcessBuilder(
        "ffmpeg",
        "-y",
        "-i", inputFile.absolutePath,
        "-ar", "22000",
        "-ac", "1",
        "-acodec", "pcm_s16le",
        outputFile.absolutePath
    )
        .redirectErrorStream(true)
        .start()

    process.waitFor()

    if (!outputFile.exists()) {
        throw RuntimeException("Audio conversion failed")
    }

    inputFile.delete()

    return outputFile
}
    fun saveListVoiceActorToDB(voiceActors: List<VoiceActorRequest>): MutableList<VoiceActorRequest> {
        val results = mutableListOf<VoiceActorRequest>()

        for (voiceActor in voiceActors) {

            val saved = saveVoiceActorToDB(voiceActor)

            results.add(saved)
        }

        return results
    }
    fun saveVoiceActorToDB(request: VoiceActorRequest): VoiceActorRequest {

        val userId = userService.getCurrentUser().getUserId()

        val savedActor: VoiceActor
        val actorName = request.actorName.trim().lowercase()
        val voiceActorId = ObjectId()

        if (request.isPrivate) {

            // PRIVATE FLOW
            val existingPrivate = voiceActorRepo.findByUserIdAndActorNameAndIsPrivateTrue(userId, actorName)

            savedActor = if (existingPrivate != null) {
                existingPrivate.audios += request.audios
                voiceActorRepo.save(existingPrivate)
            } else {
                voiceActorRepo.save(
                    VoiceActor(
                        voiceActorId = voiceActorId,
                        userId = userId,
                        actorName = actorName,
                        gender = request.gender,
                        isAdult = request.isAdult,
                        isPrivate = true,
                        audios = request.audios,
                        preferredRole = request.preferredRole
                    )
                )
            }

        }
        else {

            // PUBLIC FLOW
            val existingPublic =
                voiceActorRepo.findByActorNameAndIsPrivateFalse(actorName)

            savedActor = if (existingPublic != null) {
                existingPublic.audios += request.audios
                voiceActorRepo.save(existingPublic)
            } else {
                voiceActorRepo.save(
                    VoiceActor(
                        voiceActorId = ObjectId(),
                        userId = null,
                        actorName = actorName,
                        gender = request.gender,
                        isAdult = request.isAdult,
                        isPrivate = false,
                        audios = request.audios,
                        preferredRole = request.preferredRole
                    )
                )
            }
        }

        return VoiceActorRequest(
            actorName = savedActor.actorName,
            gender = savedActor.gender,
            isAdult = savedActor.isAdult,
            isPrivate = savedActor.isPrivate,
            audios = request.audios,
            preferredRole = savedActor.preferredRole
        )
    }
}

