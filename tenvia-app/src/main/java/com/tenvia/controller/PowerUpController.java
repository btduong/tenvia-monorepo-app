package com.tenvia.controller;

import com.tenvia.PowerUpType;
import com.tenvia.services.PowerUpResponse;
import com.tenvia.services.PowerUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/powerups/")
public class PowerUpController {

    @Autowired
    private PowerUpService powerUpService;

    @PostMapping("/use")
    public ResponseEntity<PowerUpResponse> usePowerUp(@RequestParam PowerUpType type, @RequestParam Long userId, @RequestParam UUID sessionId) {
        PowerUpResponse powerUpResponse = powerUpService.applyPowerUp(userId, sessionId, type);
        return ResponseEntity.ok(powerUpResponse);
    }


}
