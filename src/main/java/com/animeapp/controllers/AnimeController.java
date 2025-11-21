package com.animeapp.controllers;

import com.animeapp.model.Anime;
import com.animeapp.service.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/anime")
public class AnimeController {

    private final AnimeService animeService;

    @Autowired
    public AnimeController(AnimeService animeService) {
        this.animeService = animeService;
    }

    @GetMapping("/getAnime")
    public ResponseEntity<?> getAnimeByName(@RequestParam("title") String title){
        Anime anime = animeService.getAnimeByTitle(title);

        if(Objects.isNull(anime)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(anime);
    }


}
