package com.animeapp.controllers;

import com.animeapp.model.Anime;
import com.animeapp.model.requests.UserAnimeWatchedRequest;
import com.animeapp.service.AnimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnimeControllerTest {

    @Mock
    private AnimeService animeService;

    @InjectMocks
    private AnimeController animeController;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAnimeByTitleReturnsOkWhenFound() {
        List<Anime> anime = List.of(new Anime());
        when(animeService.getAnimeByTitle("Naruto")).thenReturn(anime);

        ResponseEntity<?> response = animeController.getAnimeByTitle("Naruto");

        assertEquals(200, response.getStatusCode().value());
        assertSame(anime, response.getBody());
        verify(animeService).getAnimeByTitle("Naruto");
    }

    @Test
    void getAnimeByTitleReturns404WhenMissing() {
        when(animeService.getAnimeByTitle("Bleach")).thenReturn(List.of());

        ResponseEntity<?> response = animeController.getAnimeByTitle("Bleach");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void updateAnimeWatchStatusDelegatesToService() {
        UserAnimeWatchedRequest request = new UserAnimeWatchedRequest();
        when(animeService.updateAnimeWatchStatus(request)).thenReturn(null);

        ResponseEntity<?> response = animeController.updateAnimeWatchStatus(request);

        assertEquals(200, response.getStatusCode().value());
        verify(animeService).updateAnimeWatchStatus(request);
    }

    @Test
    void getAllAnimeReturnsOk() {
        java.util.List<Anime> animeList = java.util.List.of(new Anime(), new Anime());
        when(animeService.getAllAnime()).thenReturn(animeList);

        ResponseEntity<?> response = animeController.getAllAnime();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(animeList, response.getBody());
        verify(animeService).getAllAnime();
    }

    @Test
    void getAnimeByTitleReturnsAnime() {
        Anime anime = new Anime();
        anime.setTitle("One Piece");
        when(animeService.getAnimeByTitle("One Piece")).thenReturn(List.of(anime));

        ResponseEntity<?> response = animeController.getAnimeByTitle("One Piece");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(anime, response.getBody());
    }

    @Test
    void updateAnimeWatchStatusWithResult() {
        UserAnimeWatchedRequest request = new UserAnimeWatchedRequest();
        request.setUserId(1);
        request.setAnimeId(5);
        request.setWatched(true);

        com.animeapp.model.UserAnimeWatched watched = new com.animeapp.model.UserAnimeWatched(1, 5, true);
        when(animeService.updateAnimeWatchStatus(request)).thenReturn(watched);

        ResponseEntity<?> response = animeController.updateAnimeWatchStatus(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(watched, response.getBody());
    }
}
