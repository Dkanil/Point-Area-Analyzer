package org.example.app;

import org.example.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.example.app.model.User;
import org.example.app.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserService userService;

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        return user;
    }

    @Test
    public void testFoundUserByUsername() {
        User user = createUser("existingUser");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    public void testNotFoundUserByUsername() {
        User user = createUser("notExistingUser");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.empty());
        when(messageSource.getMessage(
                eq("user.not.found"),
                any(),
                any())).thenReturn("User 'notExistingUser' is not found");
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(user.getUsername()));
        assertEquals("User 'notExistingUser' is not found", exception.getMessage());
    }

    @Test
    public void testCreateUser() {
        User user = createUser("notExistingUser");
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        userService.create(user);
        verify(userRepository).save(user);
    }

    @Test
    public void testCreateUserWithExistingUsername() {
        User user = createUser("existingUser");
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        when(messageSource.getMessage(
                eq("user.exist"),
                any(),
                any())).thenReturn("User already exists");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.create(user));
        assertEquals("User already exists", exception.getMessage());
    }
}
