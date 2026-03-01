package com.example.pbl4Version1.dto.request;

public class EndGameRequest {
    private Long matchId;
    private String gameStatus;  // CHECK_MATE, STALE_MATE, DRAW
    private String winner;      // WHITE, BLACK, DRAW (null nếu hòa)

    public EndGameRequest() {
    }

    public EndGameRequest(Long matchId, String gameStatus, String winner) {
        this.matchId = matchId;
        this.gameStatus = gameStatus;
        this.winner = winner;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
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

