package com.example.commentapp.services;

import com.example.commentapp.entity.Users;
import com.example.commentapp.enums.UserRole;
import com.example.commentapp.exception.CustomConflictException;
import com.example.commentapp.repository.UserRepository;
import com.example.commentapp.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    private static CustomUserDetailsService customerUserDetailsService;

    private static UserRepository userRepository;

    private static Users userMock;

    @BeforeAll
    public static void initialize() {
        customerUserDetailsService = new CustomUserDetailsService();
        userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(customerUserDetailsService, "userRepository", userRepository);
        userMock = new Users("user1", "user1", Set.of(UserRole.ROLE_GUEST.toString()));
    }

    @Test
    public void loadUserByUsername_success() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userMock));
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername("user1");
        assertNotNull(userDetails);
        assertEquals(userDetails.getUsername(), "user1");
    }

    @Test
    public void loadUserByUsername_failed() {
        when(userRepository.findByUsername(any())).thenReturn(null);
        assertThrows(NullPointerException.class, () -> customerUserDetailsService.loadUserByUsername("user1"));
    }
}
