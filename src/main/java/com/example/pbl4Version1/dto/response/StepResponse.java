package com.example.pbl4Version1.dto.response;

public class StepResponse {
    private Long matchId;
    private String fen;
    private String from;
    private String to;
    private String name;
    private String gameStatus;
    private String winner;

    public StepResponse() {
    }

    public StepResponse(Long matchId, String fen, String from, String to, String name, String gameStatus, String winner) {
        this.matchId = matchId;
        this.fen = fen;
        this.from = from;
        this.to = to;
        this.name = name;
        this.gameStatus = gameStatus;
        this.winner = winner;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
