package com.animeapp.controllers;

import com.animeapp.exceptions.UserException;
import com.animeapp.model.ErrorResponse;
import com.animeapp.model.User;
import com.animeapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUserReturnsConflictWhenUsernameExists() throws UserException {
        User request = new User("existing", "password123");
        when(userService.doesUsernameExist("existing")).thenReturn(true);

        ResponseEntity<?> response = userController.registerUser(request);

        assertEquals(409, response.getStatusCode().value());
        assertSame(request, response.getBody());
        verify(userService, never()).registerUser(anyString(), anyString());
    }

    @Test
    void registerUserReturnsBadRequestWhenServiceThrows() throws UserException {
        User request = new User("newUser", "password123");
        when(userService.doesUsernameExist("newUser")).thenReturn(false);
        when(userService.registerUser("newUser", "password123")).thenThrow(new UserException("bad"));

        ResponseEntity<?> response = userController.registerUser(request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void registerUserReturnsOkWhenSuccessful() throws UserException {
        User request = new User("newUser", "password123");
        User saved = new User("newUser", "password123");
        when(userService.doesUsernameExist("newUser")).thenReturn(false);
        when(userService.registerUser("newUser", "password123")).thenReturn(saved);

        ResponseEntity<?> response = userController.registerUser(request);

        assertEquals(200, response.getStatusCode().value());
        assertSame(saved, response.getBody());
    }

    @Test
    void loginReturnsUnauthorizedWhenNull() throws UserException {
        User request = new User("user", "pass");
        when(userService.loginUser(request)).thenReturn(null);

        ResponseEntity<?> response = userController.login(request);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void loginReturnsBadRequestWhenException() throws UserException {
        User request = new User("user", "pass");
        when(userService.loginUser(request)).thenThrow(new UserException("bad"));

        ResponseEntity<?> response = userController.login(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("bad", response.getBody());
    }

    @Test
    void loginReturnsOkWhenSuccessful() throws UserException {
        User request = new User("user", "pass");
        when(userService.loginUser(request)).thenReturn(request);

        ResponseEntity<?> response = userController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertSame(request, response.getBody());
    }

    @Test
    void addToWatchlistReturnsOk() {
        ResponseEntity<?> response = userController.addToWatchlist(
                new com.animeapp.model.requests.UserWatchlistRequest() {
                    {
                        setUserId(1);
                        setAnimeId(10);
                    }
                });

        assertEquals(200, response.getStatusCode().value());
        verify(userService).addToWatchlist(1, 10);
    }

    @Test
    void removeFromWatchlistReturnsOk() {
        ResponseEntity<?> response = userController.removeFromWatchlist(
                new com.animeapp.model.requests.UserWatchlistRequest() {
                    {
                        setUserId(1);
                        setAnimeId(10);
                    }
                });

        assertEquals(200, response.getStatusCode().value());
        verify(userService).removeFromWatchlist(1, 10);
    }

    @Test
    void getWatchlistReturnsOk() {
        when(userService.getWatchlist(1)).thenReturn(java.util.List.of());

        ResponseEntity<?> response = userController.getWatchlist(1);

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getWatchlist(1);
    }

    @Test
    void setRatingReturnsOk() {
        ResponseEntity<?> response = userController.setRating(
                new com.animeapp.model.requests.UserRatingRequest() {
                    {
                        setUserId(1);
                        setAnimeId(10);
                        setRating(8.5f);
                    }
                });

        assertEquals(200, response.getStatusCode().value());
        verify(userService).setRating(1, 10, 8.5f);
    }

    @Test
    void getRatingReturnsOk() {
        when(userService.getRating(1, 10)).thenReturn(java.util.Optional.empty());

        ResponseEntity<?> response = userController.getRating(1, 10);

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getRating(1, 10);
    }

    @Test
    void getMostWatchedAnimeReturnsOk() {
        when(userService.getMostWatchedAnimeWithCount()).thenReturn(java.util.List.of());

        ResponseEntity<?> response = userController.getMostWatchedAnime();

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getMostWatchedAnimeWithCount();
    }

    @Test
    void getTopRatedAnimeReturnsOk() {
        when(userService.getTopRatedAnime()).thenReturn(java.util.List.of());

        ResponseEntity<?> response = userController.getTopRatedAnime();

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getTopRatedAnime();
    }

    @Test
    void getLeastWatchedAnimeReturnsOk() {
        when(userService.getLeastWatchedAnime()).thenReturn(java.util.List.of());

        ResponseEntity<?> response = userController.getLeastWatchedAnime();

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getLeastWatchedAnime();
    }

    @Test
    void getLeastRatedAnimeReturnsOk() {
        when(userService.getLeastRatedAnime()).thenReturn(java.util.List.of());

        ResponseEntity<?> response = userController.getLeastRatedAnime();

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getLeastRatedAnime();
    }

    @Test
    void getAnimeWithMultipleRatingsReturnsOk() {
        when(userService.getAnimeWithMultipleRatings()).thenReturn(java.util.List.of());

        ResponseEntity<?> response = userController.getAnimeWithMultipleRatings();

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getAnimeWithMultipleRatings();
    }

    @Test
    void setWatchedReturnsOk() {
        ResponseEntity<?> response = userController.setWatched(
                new com.animeapp.model.requests.UserAnimeWatchedRequest() {
                    {
                        setUserId(1);
                        setAnimeId(10);
                        setWatched(true);
                    }
                });

        assertEquals(200, response.getStatusCode().value());
        verify(userService).setWatched(1, 10, true);
    }

    @Test
    void getWatchedAnimeReturnsOk() {
        when(userService.getWatchedAnime(1)).thenReturn(java.util.List.of());

        ResponseEntity<?> response = userController.getWatchedAnime(1);

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getWatchedAnime(1);
    }
}
