package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.LocationDto
import com.StoryAlive.StoryAlive.Models.Location
import com.StoryAlive.StoryAlive.Services.LocationService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/location")
class LocationController (private val locationService: LocationService) {

    @GetMapping
    fun getLocation(@RequestParam(defaultValue = "0") pageNumber:Int,
                         @RequestParam(defaultValue = "10") pageSize:Int)
    : List<Location>{
        return locationService.getAllLocations(pageNumber, pageSize).content
    }

    @GetMapping("/{locationId}")
    fun getLocation( @PathVariable locationId: String ) : LocationDto {
        return locationService.getLocationById(ObjectId(locationId))
    }


//!  Make sure In the Content-Type column click it and type: audio/wav for wav (or audio/mpeg for MP3s).
//! and for request -> application/json
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveLocation( @RequestPart("location") locationDto: LocationDto, @RequestPart("file") file: MultipartFile ): ResponseEntity<LocationDto> {

        val location = locationService.saveLocation(request = locationDto, file = file)
        return ResponseEntity.status(HttpStatus.CREATED).body(location)

    }

    @PostMapping("list",consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveLocationList(@RequestPart("locations") locationDtoList: List<LocationDto>, @RequestPart("files") files: List<MultipartFile>) : ResponseEntity<List<LocationDto>> {

        val locations = locationService.saveLocationList(locationDtoList, files)
        return ResponseEntity.status(HttpStatus.CREATED).body(locations)

    }
    @PostMapping("list-DB")
    fun saveLocationListToDB( @RequestBody locationDtoList: List<LocationDto>): ResponseEntity<List<LocationDto>> {

        val location = locationService.saveLocationListToDB(requests = locationDtoList)
        return ResponseEntity.status(HttpStatus.CREATED).body(location)

    }
}