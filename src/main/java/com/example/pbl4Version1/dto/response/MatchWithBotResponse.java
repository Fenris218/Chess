package com.example.pbl4Version1.dto.response;

import java.util.List;

public class MatchWithBotResponse {
    private Long id;
    private String gameStatus;
    private String winner;
    private List<StepResponse> steps;

    public MatchWithBotResponse() {
    }

    public MatchWithBotResponse(Long id, String gameStatus, String winner, List<StepResponse> steps) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.winner = winner;
        this.steps = steps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<StepResponse> getSteps() {
        return steps;
    }

    public void setSteps(List<StepResponse> steps) {
        this.steps = steps;
    }
}
