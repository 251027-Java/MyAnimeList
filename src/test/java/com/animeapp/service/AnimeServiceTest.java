package com.animeapp.service;

import com.animeapp.model.Anime;
import com.animeapp.model.UserAnimeWatched;
import com.animeapp.model.requests.UserAnimeWatchedRequest;
import com.animeapp.repository.AnimeRepository;
import com.animeapp.repository.UserAnimeWatchedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimeServiceTest {

    @Mock
    private AnimeRepository animeRepository;

    @Mock
    private UserAnimeWatchedRepository userAnimeWatchedRepository;

    private AnimeService animeService;

    @BeforeEach
    void setUp() {
        animeService = new AnimeService(animeRepository, userAnimeWatchedRepository);
    }

    @Test
    void getAllAnimeDelegatesToRepository() {
        List<Anime> animeList = List.of(new Anime());
        when(animeRepository.findAll()).thenReturn(animeList);

        List<Anime> result = animeService.getAllAnime();

        assertSame(animeList, result);
        verify(animeRepository).findAll();
    }

    @Test
    void getAnimeByTitleReturnsRepositoryResult() {
        Anime anime = new Anime();
        when(animeRepository.findByTitle("Naruto")).thenReturn(anime);

        Anime result = animeService.getAnimeByTitle("Naruto");

        assertSame(anime, result);
        verify(animeRepository).findByTitle("Naruto");
    }

    @Test
    void updateAnimeWatchStatusCreatesRecordWhenNotExisting() {
        UserAnimeWatchedRequest request = buildRequest(1, 2, true);
        UserAnimeWatched saved = new UserAnimeWatched(1, 2, true);
        when(userAnimeWatchedRepository.findByUserIdAndAnimeId(1, 2)).thenReturn(null);
        when(userAnimeWatchedRepository.save(any(UserAnimeWatched.class))).thenReturn(saved);

        UserAnimeWatched result = animeService.updateAnimeWatchStatus(request);

        assertSame(saved, result);
        ArgumentCaptor<UserAnimeWatched> captor = ArgumentCaptor.forClass(UserAnimeWatched.class);
        verify(userAnimeWatchedRepository).save(captor.capture());
        UserAnimeWatched persisted = captor.getValue();
        assertEquals(1, persisted.getUserId());
        assertEquals(2, persisted.getAnimeId());
        assertTrue(persisted.getWatched());
    }

    @Test
    void updateAnimeWatchStatusUpdatesExistingWhenStateChanges() {
        UserAnimeWatchedRequest request = buildRequest(3, 4, false);
        UserAnimeWatched existing = new UserAnimeWatched(3, 4, true);
        when(userAnimeWatchedRepository.findByUserIdAndAnimeId(3, 4)).thenReturn(existing);
        when(userAnimeWatchedRepository.save(existing)).thenReturn(existing);

        UserAnimeWatched result = animeService.updateAnimeWatchStatus(request);

        assertSame(existing, result);
        assertFalse(existing.getWatched());
        verify(userAnimeWatchedRepository).save(existing);
    }

    @Test
    void updateAnimeWatchStatusReturnsNullWhenStateSame() {
        UserAnimeWatchedRequest request = buildRequest(5, 6, true);
        UserAnimeWatched existing = new UserAnimeWatched(5, 6, true);
        when(userAnimeWatchedRepository.findByUserIdAndAnimeId(5, 6)).thenReturn(existing);

        UserAnimeWatched result = animeService.updateAnimeWatchStatus(request);

        assertNull(result);
        verify(userAnimeWatchedRepository, never()).save(any());
    }

    private UserAnimeWatchedRequest buildRequest(int userId, int animeId, boolean watched) {
        UserAnimeWatchedRequest request = new UserAnimeWatchedRequest();
        request.setUserId(userId);
        request.setAnimeId(animeId);
        request.setWatched(watched);
        return request;
    }
}
