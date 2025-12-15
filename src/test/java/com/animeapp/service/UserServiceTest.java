package com.animeapp.service;

import com.animeapp.exceptions.UserException;
import com.animeapp.model.User;
import com.animeapp.repository.UserRepository;
import com.animeapp.repository.UserAnimeWatchlistRepository;
import com.animeapp.repository.UserRatingRepository;
import com.animeapp.repository.UserAnimeWatchedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserAnimeWatchlistRepository userAnimeWatchlistRepository;
    @Mock
    private UserRatingRepository userRatingRepository;
    @Mock
    private UserAnimeWatchedRepository userAnimeWatchedRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userAnimeWatchlistRepository, userRatingRepository,
                userAnimeWatchedRepository);
    }

    @Test
    void registerUserPersistsValidUser() throws UserException {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser("newUser", "strongPass");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("newUser", saved.getUsername());
        assertEquals("strongPass", saved.getPassword());
        assertSame(saved, result);
    }

    @Test
    void registerUserThrowsWhenInvalidInput() {
        assertThrows(UserException.class, () -> userService.registerUser("", "short"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void doesUsernameExistDelegatesToRepository() {
        when(userRepository.existsUserByUsername("naruto")).thenReturn(true);

        boolean exists = userService.doesUsernameExist("naruto");

        assertTrue(exists);
        verify(userRepository).existsUserByUsername("naruto");
    }

    @Test
    void loginUserReturnsRepositoryResult() throws UserException {
        User user = new User("sasuke", "uchihaClan");
        when(userRepository.findByUsernameAndPassword("sasuke", "uchihaClan")).thenReturn(user);

        User result = userService.loginUser(user);

        assertSame(user, result);
        verify(userRepository).findByUsernameAndPassword("sasuke", "uchihaClan");
    }

    @Test
    void addToWatchlistCreatesNewEntry() {
        when(userAnimeWatchlistRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.empty());

        userService.addToWatchlist(1, 10);

        verify(userAnimeWatchlistRepository).save(any(com.animeapp.model.UserAnimeWatchlist.class));
    }

    @Test
    void addToWatchlistDoesNotDuplicateExisting() {
        when(userAnimeWatchlistRepository.findByUserIdAndAnimeId(1, 10))
                .thenReturn(java.util.Optional.of(new com.animeapp.model.UserAnimeWatchlist(1, 10)));

        userService.addToWatchlist(1, 10);

        verify(userAnimeWatchlistRepository, never()).save(any());
    }

    @Test
    void removeFromWatchlistDeletesEntry() {
        com.animeapp.model.UserAnimeWatchlist entry = new com.animeapp.model.UserAnimeWatchlist(1, 10);
        when(userAnimeWatchlistRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.of(entry));

        userService.removeFromWatchlist(1, 10);

        verify(userAnimeWatchlistRepository).delete(entry);
    }

    @Test
    void setRatingCreatesNewRating() {
        when(userRatingRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.empty());

        userService.setRating(1, 10, 9.0f);

        verify(userRatingRepository).save(any(com.animeapp.model.UserRating.class));
    }

    @Test
    void setRatingUpdatesExistingRating() {
        com.animeapp.model.UserRating existing = new com.animeapp.model.UserRating(1, 10, 7.0f);
        when(userRatingRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.of(existing));

        userService.setRating(1, 10, 9.0f);

        assertEquals(9.0f, existing.getRating());
        verify(userRatingRepository).save(existing);
    }

    @Test
    void setWatchedCreatesNewEntry() {
        when(userAnimeWatchedRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.empty());

        userService.setWatched(1, 10, true);

        verify(userAnimeWatchedRepository).save(any(com.animeapp.model.UserAnimeWatched.class));
    }

    @Test
    void isWatchedReturnsFalseWhenNotFound() {
        when(userAnimeWatchedRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.empty());

        boolean result = userService.isWatched(1, 10);

        assertFalse(result);
    }

    @Test
    void getMostWatchedAnimeFiltersOneUserAnime() {
        when(userAnimeWatchedRepository.findMostWatchedAnime()).thenReturn(
                java.util.List.of(
                        new Object[] { 1, 5L },
                        new Object[] { 2, 1L },
                        new Object[] { 3, 4L },
                        new Object[] { 4, 3L },
                        new Object[] { 5, 2L }));

        var result = userService.getMostWatchedAnimeWithCount();

        // With 4 anime >= 2 users, limit = (4+1)/2 = 2
        assertEquals(2, result.size());
        verify(userAnimeWatchedRepository).findMostWatchedAnime();
    }

    @Test
    void getTopRatedAnimeReturnsResults() {
        java.util.List<Object[]> mockResults = java.util.List.of(
                new Object[] { 1, 9.5 },
                new Object[] { 2, 9.5 },
                new Object[] { 3, 8.7 },
                new Object[] { 4, 8.5 },
                new Object[] { 5, 8.5 },
                new Object[] { 6, 8.0 },
                new Object[] { 7, 7.8 },
                new Object[] { 8, 7.5 },
                new Object[] { 9, 7.2 },
                new Object[] { 10, 7.0 });
        when(userRatingRepository.findTopRatedAnime()).thenReturn(mockResults);

        var result = userService.getTopRatedAnime();

        // With 10+ results, limit = 5
        assertEquals(5, result.size());
        verify(userRatingRepository).findTopRatedAnime();
    }

    @Test
    void getWatchlistReturnsUserWatchlist() {
        java.util.List<com.animeapp.model.UserAnimeWatchlist> watchlist = java.util.List.of(
                new com.animeapp.model.UserAnimeWatchlist(1, 5),
                new com.animeapp.model.UserAnimeWatchlist(1, 10));
        when(userAnimeWatchlistRepository.findByUserId(1)).thenReturn(watchlist);

        var result = userService.getWatchlist(1);

        assertEquals(2, result.size());
        verify(userAnimeWatchlistRepository).findByUserId(1);
    }

    @Test
    void getWatchedAnimeReturnsUserWatched() {
        java.util.List<com.animeapp.model.UserAnimeWatched> watched = java.util.List.of(
                new com.animeapp.model.UserAnimeWatched(1, 5, true));
        when(userAnimeWatchedRepository.findByUserId(1)).thenReturn(watched);

        var result = userService.getWatchedAnime(1);

        assertEquals(1, result.size());
        verify(userAnimeWatchedRepository).findByUserId(1);
    }

    @Test
    void getRatingReturnsExistingRating() {
        com.animeapp.model.UserRating rating = new com.animeapp.model.UserRating(1, 10, 8.5f);
        when(userRatingRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.of(rating));

        var result = userService.getRating(1, 10);

        assertTrue(result.isPresent());
        assertEquals(8.5f, result.get().getRating());
    }

    @Test
    void setWatchedUpdatesExistingEntry() {
        com.animeapp.model.UserAnimeWatched existing = new com.animeapp.model.UserAnimeWatched(1, 10, false);
        when(userAnimeWatchedRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.of(existing));

        userService.setWatched(1, 10, true);

        assertTrue(existing.getWatched());
        verify(userAnimeWatchedRepository).save(existing);
    }

    @Test
    void isWatchedReturnsTrueWhenWatched() {
        com.animeapp.model.UserAnimeWatched watched = new com.animeapp.model.UserAnimeWatched(1, 10, true);
        when(userAnimeWatchedRepository.findByUserIdAndAnimeId(1, 10)).thenReturn(java.util.Optional.of(watched));

        boolean result = userService.isWatched(1, 10);

        assertTrue(result);
    }

    @Test
    void getLeastWatchedAnimeReturnsResults() {
        Object[] row1 = new Object[] { 1, 10L };
        Object[] row2 = new Object[] { 2, 2L };
        Object[] row3 = new Object[] { 3, 2L };
        java.util.List<Object[]> mostWatchedList = new java.util.ArrayList<>();
        mostWatchedList.add(row1);
        mostWatchedList.add(row2);
        mostWatchedList.add(row3);

        Object[] leastRow = new Object[] { 2, 2L };
        java.util.List<Object[]> leastWatchedList = new java.util.ArrayList<>();
        leastWatchedList.add(leastRow);

        when(userAnimeWatchedRepository.findMostWatchedAnime()).thenReturn(mostWatchedList);
        when(userAnimeWatchedRepository.findLeastWatchedAnimeExcluding(any())).thenReturn(leastWatchedList);

        var result = userService.getLeastWatchedAnime();

        assertFalse(result.isEmpty());
        verify(userAnimeWatchedRepository).findMostWatchedAnime();
    }

    @Test
    void getLeastRatedAnimeReturnsResults() {
        Object[] topRow = new Object[] { 1, 9.0 };
        java.util.List<Object[]> topRatedList = new java.util.ArrayList<>();
        topRatedList.add(topRow);

        Object[] leastRow = new Object[] { 2, 5.0 };
        java.util.List<Object[]> leastRatedList = new java.util.ArrayList<>();
        leastRatedList.add(leastRow);

        when(userRatingRepository.findTopRatedAnime()).thenReturn(topRatedList);
        when(userRatingRepository.findLeastRatedAnimeExcluding(any())).thenReturn(leastRatedList);

        var result = userService.getLeastRatedAnime();

        assertFalse(result.isEmpty());
        verify(userRatingRepository).findLeastRatedAnimeExcluding(any());
    }

    @Test
    void getAnimeWithMultipleRatingsReturnsResults() {
        java.util.List<Object[]> multipleRatingsList = java.util.List.of(
                new Object[] { 1, 8.5, 3L },
                new Object[] { 2, 7.8, 2L });
        when(userRatingRepository.findAnimeWithMultipleRatings()).thenReturn(multipleRatingsList);

        var result = userService.getAnimeWithMultipleRatings();

        assertEquals(2, result.size());
        verify(userRatingRepository).findAnimeWithMultipleRatings();
    }

    @Test
    void registerUserThrowsWhenPasswordTooShort() {
        assertThrows(UserException.class, () -> userService.registerUser("user", "short"));
    }

    @Test
    void registerUserThrowsWhenUsernameBlank() {
        assertThrows(UserException.class, () -> userService.registerUser("  ", "password123"));
    }
}
