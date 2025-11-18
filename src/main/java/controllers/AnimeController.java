package controllers;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.AnimeService;
import service.UserService;

import java.util.Objects;

@RestController
public class AnimeController {
    private UserService userService;
    private AnimeService animeService;

    @Autowired
    public AnimeController(AnimeService animeService, UserService userService) {
        this.animeService = animeService;
        this.userService = userService;

    }

    @PostMapping("/register")
    ResponseEntity<User> registerUser(@RequestBody User request){
        if(userService.doesUsernameExist(request.getUsername())){
            return ResponseEntity.status(409).body(request);
        }
        User registeredUser = userService.registerUser(request.getUsername(),request.getPassword());
        if(Objects.isNull(registeredUser)){
            return ResponseEntity.status(400).body(request);
        }
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
        ResponseEntity<User> login(@RequestBody User request){

            User loggedInAccount = userService.loginUser(request);

            if(Objects.isNull(loggedInAccount)){
                return ResponseEntity.status(401).body(null);
            }

            return ResponseEntity.ok(loggedInAccount);
        }


}
