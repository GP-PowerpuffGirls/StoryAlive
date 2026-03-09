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
class LocationService (val locationRepo: LocationRepo, val supabaseStorageService: SupabaseStorageService) {

    fun getAllLocations( pageNumber:Int, pageSize:Int ): Page<Location> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize);
        return locationRepo.findAll(pageable)
    }

    fun getLocationById(locationId: ObjectId): LocationDto {
        val location = locationRepo.findById(locationId)
        return LocationDto(
            locationName = location.get().locationName,
            sfxPath = location.get().sfxPath
        )
    }

    fun saveLocation(request: LocationDto, file: MultipartFile) : LocationDto {
        val userId = getCurrentUserId()
        return saveLocationToCloud(request, file, userId)
    }

    private fun getCurrentUserId(): ObjectId {
        val user = SecurityContextHolder
            .getContext()
            .authentication
            ?.principal as CurrentUserDetails
        return user.getUserId()
    }

    private fun saveLocationToCloud(request: LocationDto, file: MultipartFile, userId: ObjectId): LocationDto {

        val sfxUrl = supabaseStorageService.saveAudioToCloud(file, userId)

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