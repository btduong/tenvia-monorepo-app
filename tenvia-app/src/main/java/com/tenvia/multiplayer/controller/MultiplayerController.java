package com.tenvia.multiplayer.controller;

import com.tenvia.multiplayer.model.Lobby;
import com.tenvia.multiplayer.model.Player;
import com.tenvia.multiplayer.service.MultiplayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RestController
@RequestMapping("/api/multiplayer")
public class MultiplayerController {

    private final MultiplayerService multiplayerService;

    public MultiplayerController(MultiplayerService multiplayerService) {
        this.multiplayerService = multiplayerService;
    }

    // REST endpoint to create a lobby and get the join code
    @PostMapping("/lobby")
    public ResponseEntity<Lobby> createLobby(@AuthenticationPrincipal String userIdString,
                                             @RequestParam(defaultValue = "10") int limit) {
        Long hostId = Long.valueOf(userIdString);
        Lobby lobby = multiplayerService.createLobby(hostId, limit);
        return ResponseEntity.ok(lobby);
    }

    // WebSocket endpoints

    @MessageMapping("/lobby/{lobbyId}/join")
    public void joinLobby(@DestinationVariable String lobbyId, @Payload Map<String, String> payload, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        String username = payload.getOrDefault("username", "Player " + userId);
        Player player = new Player(userId, username);
        multiplayerService.joinLobby(lobbyId, player);
    }

    @MessageMapping("/lobby/{lobbyId}/leave")
    public void leaveLobby(@DestinationVariable String lobbyId, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        multiplayerService.leaveLobby(lobbyId, userId);
    }

    @MessageMapping("/lobby/{lobbyId}/start")
    public void startGame(@DestinationVariable String lobbyId, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        multiplayerService.startGame(lobbyId, userId);
    }

    @MessageMapping("/lobby/{lobbyId}/answer")
    public void submitAnswer(@DestinationVariable String lobbyId, @Payload Map<String, Boolean> payload, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Boolean isCorrect = payload.getOrDefault("isCorrect", false);
        multiplayerService.submitAnswer(lobbyId, userId, isCorrect);
    }
}
