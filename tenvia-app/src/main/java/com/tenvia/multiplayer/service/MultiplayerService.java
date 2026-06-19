package com.tenvia.multiplayer.service;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.multiplayer.model.Lobby;
import com.tenvia.multiplayer.model.Player;
import com.tenvia.question.service.QuestionService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MultiplayerService {

    private final ConcurrentHashMap<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final QuestionService questionService;

    public MultiplayerService(SimpMessagingTemplate messagingTemplate, QuestionService questionService) {
        this.messagingTemplate = messagingTemplate;
        this.questionService = questionService;
    }

    public Lobby createLobby(Long hostId) {
        String lobbyId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Lobby lobby = new Lobby(lobbyId, hostId);
        lobbies.put(lobbyId, lobby);
        return lobby;
    }

    public Lobby joinLobby(String lobbyId, Player player) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.addPlayer(player);
            broadcastLobbyState(lobbyId);
        }
        return lobby;
    }

    public void leaveLobby(String lobbyId, Long playerId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.removePlayer(playerId);
            broadcastLobbyState(lobbyId);
            
            // Clean up empty lobbies
            if (lobby.getPlayers().isEmpty()) {
                lobbies.remove(lobbyId);
            }
        }
    }

    public void startGame(String lobbyId, Long hostId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null && lobby.getHostId().equals(hostId)) {
            if (lobby.getGameState() == Lobby.GameState.RESULTS) {
                lobby.incrementQuestionIndex();
            }
            
            // Fetch a random question for the new round
            List<QuestionDTO> questions = questionService.fetchRandomQuestion(1);
            if (!questions.isEmpty()) {
                lobby.setCurrentQuestion(questions.get(0));
            }

            lobby.setGameState(Lobby.GameState.ACTIVE);
            lobby.resetAnswerStatus();
            broadcastLobbyState(lobbyId);
        }
    }

    public void submitAnswer(String lobbyId, Long playerId, boolean isCorrect) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null && lobby.getGameState() == Lobby.GameState.ACTIVE) {
            Optional<Player> playerOpt = lobby.getPlayer(playerId);
            playerOpt.ifPresent(player -> {
                player.setAnsweredCurrentQuestion(true);
                if (isCorrect) {
                    player.addScore(100); // Simple scoring
                }
            });

            // Check if all players answered
            if (lobby.allPlayersAnswered()) {
                lobby.setGameState(Lobby.GameState.RESULTS);
                broadcastLobbyState(lobbyId);
            }
        }
    }

    private void broadcastLobbyState(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobby);
        }
    }
}
