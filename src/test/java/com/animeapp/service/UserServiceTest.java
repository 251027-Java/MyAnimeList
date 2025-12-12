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
}
