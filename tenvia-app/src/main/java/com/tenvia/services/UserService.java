package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.dto.UserDTO;
import com.tenvia.entities.UserEntity;
import com.tenvia.exception.UserIdNotFoundException;
import com.tenvia.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity login(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    UserEntity user = new UserEntity(username);
                    return userRepository.save(user);
                });
    }

    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserIdNotFoundException(id));
    }

    public UserDTO getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        return UserDTO.from(user);
    }

    @Transactional
    public UserDTO addItem(Long userId, PowerUpType type, int quantity) {
        UserEntity userEntity = findUserById(userId);
        userEntity.addPowerUp(type, quantity);
        return UserDTO.from(userEntity);
    }

    @Transactional
    public UserDTO useItem(Long userId, PowerUpType type) {
        UserEntity userEntity = findUserById(userId);
        userEntity.consumePowerUp(type);
        return UserDTO.from(userEntity);
    }
}
