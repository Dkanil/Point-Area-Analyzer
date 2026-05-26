package org.example.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.app.service.AuthService;
import org.example.app.service.UserService;
import org.example.app.service.JwtCore;
import org.example.app.dto.AuthRequest;
import org.example.app.dto.JwtResponse;
import org.example.app.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtCore jwtCore;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthService authService;

    private AuthRequest makeRequest(String username) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword("password");
        return authRequest;
    }

    @Test
    void testSignUp() {
        AuthRequest authRequest = makeRequest("username");
        when(passwordEncoder.encode(authRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtCore.generateToken(any())).thenReturn("jwtToken");
        JwtResponse response = authService.signUp(makeRequest("newUsername"));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).create(userCaptor.capture());
        User user = userCaptor.getValue();

        assertEquals("jwtToken", response.getToken());
        assertEquals("newUsername", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
    }
    @Test
    void testSignIn() {
        AuthRequest authRequest = makeRequest("existedUsername");
        User user = new User();
        user.setUsername("existedUsername");
        when(userService.loadUserByUsername("existedUsername")).thenReturn(user);
        when(jwtCore.generateToken(any())).thenReturn("jwtToken");

        JwtResponse response = authService.signIn(authRequest);
        assertEquals("jwtToken", response.getToken());
    }
}