package com.StoryAlive.StoryAlive.Services

import com.StoryAlive.StoryAlive.DTOs.CurrentUserDetails
import com.StoryAlive.StoryAlive.DTOs.LocationDto
import com.StoryAlive.StoryAlive.Models.Location
import com.StoryAlive.StoryAlive.Repositories.LocationRepo
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class LocationService (val locationRepo: LocationRepo, val supabaseStorageService: SupabaseStorageService, val userService: UserService) {

    fun getAllLocations( pageNumber:Int, pageSize:Int ): Page<Location> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);
        return locationRepo.findAll(pageable)
    }
    fun getAllLocationsList(): List<Location> {
        return locationRepo.findAll().toList()
    }

    fun getLocationById(locationId: ObjectId): LocationDto {
        val location = locationRepo.findById(locationId)
        return LocationDto(
            locationName = location.get().locationName,
            sfxPath = location.get().sfxPath
        )
    }

    fun saveLocation(request: LocationDto, file: MultipartFile) : LocationDto {
        var location = Location(locationId = ObjectId(), locationName = request.locationName, sfxPath = "")
        var locationDto = saveLocationToCloud(request, file, location.locationId)
        location.sfxPath = locationDto.sfxPath
        locationRepo.save(location)
        return locationDto
    }

    private fun saveLocationToCloud(request: LocationDto, file: MultipartFile, locationId: ObjectId): LocationDto {

        val sfxUrl = supabaseStorageService.saveAudioToCloud(file, locationId, "location-audio-files")

        return LocationDto(
                locationName = request.locationName,
                sfxPath = sfxUrl
            )
        }

    fun saveLocationList(requests: List<LocationDto>, files: List<MultipartFile>) :List<LocationDto>{

        if(requests.size != files.size) throw IllegalArgumentException("Total files must match total audio metadata count")

        val locations = mutableListOf<LocationDto>()
        var index = 0;
        for (request in requests) {
            val savedLocation = saveLocation(request, files[index])
            locations.add(savedLocation)
            index++;
        }
        return locations
    }

}