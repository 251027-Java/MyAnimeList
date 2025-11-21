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
    void registerUserReturnsConflictWhenUsernameExists() throws UserException{
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
}
