package com.tenvia.user;

import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.exceptions.UserIdNotFoundException;
import com.tenvia.user.repositories.UserRepository;
import com.tenvia.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.tenvia.shop.PowerUpType.HAMMER;
import static org.assertj.core.api.Assertions.assertThat;
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

    private static final String username = "alice";
    private UserEntity user;

    @BeforeEach
    public void setUp() {
        user = new UserEntity(username);
    }

    @Test
    void loginUser_expectExistingUser_whenUserAlreadyExists() {

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserEntity userEntity = userService.login(username);

        assertEquals(username, userEntity.getUsername());
        verify(userRepository, never()).save(any());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loginUser_expectNewUserCreated() {

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity userEntity = userService.login(username);

        assertEquals(username, userEntity.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void expectException_whenFindExistingUser_returnEmpty() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(UserIdNotFoundException.class, () -> userService.findUserById(123L));
    }

    @Test
    void canAddItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.addItem(1L, HAMMER, 1);
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.inventory().size()).isEqualTo(1);
        assertThat(userDTO.inventory().get(HAMMER)).isEqualTo(1);

    }

    @Test
    void expectException_whenAddItem_withNegativeQuantity() {
        int invalidQuantity = -1;
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.addItem(1L, HAMMER, invalidQuantity));
        assertThat(exception.getMessage()).isEqualTo("Quantity must be > 0. Received: " + invalidQuantity);
    }

    @Test
    void canUseItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.addItem(1L, HAMMER, 5);

        UserDTO userDTO = userService.useItem(1L, HAMMER);
        assertThat(userDTO.inventory().get(HAMMER)).isEqualTo(4);
    }

    @Test
    void expectException_whenInventoryHaveInsufficientQuantity() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(IllegalStateException.class ,() -> userService.useItem(1L, HAMMER));
    }
}