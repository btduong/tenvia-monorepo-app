package com.tenvia.services;

import com.tenvia.entities.UserEntity;
import com.tenvia.exception.UserIdNotFoundException;
import com.tenvia.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void loginUser_expectExistingUser_whenUserAlreadyExists() {
        String username = "abc";
        UserEntity user = createTestUser(1L, username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserEntity userEntity = userService.login(username);

        assertEquals(username, userEntity.getUsername());
        verify(userRepository, never()).save(any());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loginUser_expectNewUserCreated() {
        String username = "abc";
        UserEntity user = createTestUser(1L, username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity userEntity = userService.login(username);

        assertEquals(username, userEntity.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void findExistingUser_whenNotFound_expectException() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(UserIdNotFoundException.class, () -> userService.findUserById(123L));
    }

    private static UserEntity createTestUser(Long id, String username) {
        UserEntity user = new UserEntity(username);
        return user;
    }
}