package com.tenvia.services;

import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import com.tenvia.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RewardService {
    private static final int BASE_REWARD = 5;

    @Autowired
    private UserRepository userRepository;

    int calculateGold(GameSessionEntity session) {
        return session.getScore() * 10 + BASE_REWARD;
    }

    /**
     * @return a list
     */
    List<Integer> easyReward(int size) {
        return new ArrayList<>(Collections.nCopies(size, 1 ) );
    }

    public int grantGold(Long userId, int amount) {
        if (amount <= 0) return 0;

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User id not found"));
        int newBalance = userEntity.getBalance() + amount;
        userEntity.setBalance(newBalance);
        userRepository.save(userEntity);

        return newBalance;
    }
}
