package com.animeapp.controllers;

import com.animeapp.exceptions.UserException;
import com.animeapp.model.ErrorResponse;
import com.animeapp.model.User;
import com.animeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignUpController {
    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    ResponseEntity<?> signUp(@RequestBody User request) {
        if (userService.doesUsernameExist(request.getUsername())) {
            return ResponseEntity.status(409).body(request);
        }
        try {
            User registeredUser = userService.registerUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(registeredUser);
        } catch (UserException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
