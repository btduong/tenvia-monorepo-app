package com.tenvia.components;

import com.tenvia.dto.RewardDTO;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RewardEngine {

    private final Random random = new Random();

    public RewardDTO generatedBounty() {
        int chance = random.nextInt(100);
        if (chance < 10) {
            return new RewardDTO("POWER_UP", 1);
        } else if (chance < 30) {
            int goldAmount = 10;
            return new RewardDTO("GOLD", goldAmount);
        }
        return null;
    }
}
