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
        Anime anime = new Anime();
        when(animeService.getAnimeByTitle("Naruto")).thenReturn(anime);

        ResponseEntity<?> response = animeController.getAnimeByTitle("Naruto");

        assertEquals(200, response.getStatusCode().value());
        assertSame(anime, response.getBody());
        verify(animeService).getAnimeByTitle("Naruto");
    }

    @Test
    void getAnimeByTitleReturns404WhenMissing() {
        when(animeService.getAnimeByTitle("Bleach")).thenReturn(null);

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
}
