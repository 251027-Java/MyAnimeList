package com.animeapp.controllers;

import com.animeapp.model.Anime;
import com.animeapp.model.requests.UserAnimeWatchedRequest;
import com.animeapp.service.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping()
    public ResponseEntity<?> getAnimeByTitle(@RequestParam("title") String title) {
        Anime anime = animeService.getAnimeByTitle(title);

        if (Objects.isNull(anime)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(anime);
    }

    // List all anime titles.
    @GetMapping("/all")
    public ResponseEntity<?> getAllAnime() {
        return ResponseEntity.ok(animeService.getAllAnime());
    }

    @PostMapping("/watch")
    public ResponseEntity<?> updateAnimeWatchStatus(@RequestBody UserAnimeWatchedRequest request) {
        return ResponseEntity.ok(animeService.updateAnimeWatchStatus(request));
    }

}
