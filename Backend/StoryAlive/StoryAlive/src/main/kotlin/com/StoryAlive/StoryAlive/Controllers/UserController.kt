package com.StoryAlive.StoryAlive.Controllers

import com.StoryAlive.StoryAlive.DTOs.UserDTO
import com.StoryAlive.StoryAlive.DTOs.UserUpdateRequest
import com.StoryAlive.StoryAlive.Services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("user")
class UserController (private val userService: UserService){

    @GetMapping
    public fun getUser() : ResponseEntity<UserDTO>  {
        val user = userService.getUserData();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/edit")
    public fun editUser( @RequestBody updatedData : UserUpdateRequest ) : ResponseEntity<UserDTO>  {
        val user = userService.editUserData(updatedData);
        return ResponseEntity.ok(user);
    }

}