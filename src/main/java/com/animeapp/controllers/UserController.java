package com.animeapp.controllers;

import com.animeapp.exceptions.UserException;
import com.animeapp.model.ErrorResponse;
import com.animeapp.model.User;
import com.animeapp.service.AnimeService;
import com.animeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animeapp.model.requests.UserAnimeWatchedRequest;
import com.animeapp.model.requests.UserRatingRequest;
import com.animeapp.model.requests.UserWatchlistRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@RequestBody User request) {
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

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody User request) {
        try {
            User loggedInAccount = userService.loginUser(request);

            if (Objects.isNull(loggedInAccount)) {
                return ResponseEntity.status(401).body(null);
            }

            return ResponseEntity.ok(loggedInAccount);
        } catch (UserException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/watchlist/add")
    ResponseEntity<?> addToWatchlist(@RequestBody UserWatchlistRequest request) {
        userService.addToWatchlist(request.getUserId(), request.getAnimeId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/watchlist/remove")
    ResponseEntity<?> removeFromWatchlist(@RequestBody UserWatchlistRequest request) {
        userService.removeFromWatchlist(request.getUserId(), request.getAnimeId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/watchlist/{userId}")
    ResponseEntity<?> getWatchlist(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getWatchlist(userId));
    }

    @PostMapping("/rating")
    ResponseEntity<?> setRating(@RequestBody UserRatingRequest request) {
        userService.setRating(request.getUserId(), request.getAnimeId(), request.getRating());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rating/{userId}/{animeId}")
    ResponseEntity<?> getRating(@PathVariable Integer userId, @PathVariable Integer animeId) {
        return ResponseEntity.ok(userService.getRating(userId, animeId).orElse(null));
    }

    @PostMapping("/watched")
    ResponseEntity<?> setWatched(@RequestBody UserAnimeWatchedRequest request) {
        userService.setWatched(request.getUserId(), request.getAnimeId(), request.getWatched());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/watched/{userId}/{animeId}")
    ResponseEntity<?> isWatched(@PathVariable Integer userId, @PathVariable Integer animeId) {
        return ResponseEntity.ok(userService.isWatched(userId, animeId));
    }

}
