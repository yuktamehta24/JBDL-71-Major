package org.gfg.userservice.controller;

import jakarta.validation.Valid;
import org.gfg.userservice.dto.CreateUserRequest;
import org.gfg.userservice.model.User;
import org.gfg.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user")
    public User createUser(@RequestBody @Valid CreateUserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @GetMapping("/user")
    public User getUser(@RequestParam("phoneNo") String phoneNo) {
        return userService.getUserByPhoneNo(phoneNo);
    }
}
