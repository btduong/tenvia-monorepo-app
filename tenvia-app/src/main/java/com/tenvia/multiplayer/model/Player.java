package com.tenvia.multiplayer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {
    private Long id;
    private String username;
    private int score;
    private boolean answeredCurrentQuestion;

    public Player(Long id, String username) {
        this.id = id;
        this.username = username;
        this.score = 0;
        this.answeredCurrentQuestion = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void addScore(int points) { this.score += points; }
    
    @JsonProperty("answeredCurrentQuestion")
    public boolean hasAnsweredCurrentQuestion() { return answeredCurrentQuestion; }
    
    @JsonProperty("answeredCurrentQuestion")
    public void setAnsweredCurrentQuestion(boolean answered) { this.answeredCurrentQuestion = answered; }
}
