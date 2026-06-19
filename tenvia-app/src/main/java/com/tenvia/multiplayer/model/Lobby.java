package com.tenvia.multiplayer.model;

import com.tenvia.common.dto.QuestionDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Lobby {
    public enum GameState {
        WAITING, ACTIVE, RESULTS, FINISHED
    }

    private String lobbyId;
    private Long hostId;
    private List<Player> players;
    private GameState gameState;
    private int currentQuestionIndex;
    private int totalQuestions;
    private QuestionDTO currentQuestion;

    public Lobby(String lobbyId, Long hostId, int totalQuestions) {
        this.lobbyId = lobbyId;
        this.hostId = hostId;
        this.players = new ArrayList<>();
        this.gameState = GameState.WAITING;
        this.currentQuestionIndex = 0;
        this.totalQuestions = totalQuestions;
    }

    public String getLobbyId() { return lobbyId; }
    public Long getHostId() { return hostId; }
    public List<Player> getPlayers() { return players; }
    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public int getTotalQuestions() { return totalQuestions; }
    public void incrementQuestionIndex() { this.currentQuestionIndex++; }
    public QuestionDTO getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(QuestionDTO currentQuestion) { this.currentQuestion = currentQuestion; }

    public void addPlayer(Player player) {
        if (players.stream().noneMatch(p -> p.getId().equals(player.getId()))) {
            players.add(player);
        }
    }

    public void removePlayer(Long playerId) {
        players.removeIf(p -> p.getId().equals(playerId));
    }

    public Optional<Player> getPlayer(Long playerId) {
        return players.stream().filter(p -> p.getId().equals(playerId)).findFirst();
    }
    
    public void resetAnswerStatus() {
        players.forEach(p -> p.setAnsweredCurrentQuestion(false));
    }
    
    public boolean allPlayersAnswered() {
        return !players.isEmpty() && players.stream().allMatch(Player::hasAnsweredCurrentQuestion);
    }
}
