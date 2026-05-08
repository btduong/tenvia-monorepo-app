package com.tenvia.components;

import com.tenvia.dto.RewardDTO;
import com.tenvia.dto.RewardType;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RewardEngine {

    private final Random random = new Random();

    public RewardDTO generatedBounty() {
        int chance = random.nextInt(100);
        if (chance < 10) {
            return new RewardDTO(RewardType.POWER_UP, 1);
        } else if (chance < 30) {
            int goldAmount = 10;
            return new RewardDTO(RewardType.GOLD, goldAmount);
        }
        return null;
    }
}
