package org.example.app.service;

import org.example.app.model.User;
import org.example.app.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public UserService(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "user.exist",
                    null,
                    LocaleContextHolder.getLocale()));
        }
        save(user);
    }

    public UserDetailsService userDetailsService() {
        return this;
    }

    public User getCurrentUser() {
        String username = Objects
                .requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                .getName();
        return (User) loadUserByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage(
                        "user.not.found",
                        new Object[]{username},
                        LocaleContextHolder.getLocale()))
                );
    }

}
