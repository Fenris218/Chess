package com.example.pbl4Version1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "bot_stats")
public class BotStats {
    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private long wins = 0;

    @Column(nullable = false)
    private long losses = 0;

    @Column(nullable = false)
    private long draws = 0;

    public BotStats() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getWins() {
        return wins;
    }

    public void setWins(long wins) {
        this.wins = wins;
    }

    public long getLosses() {
        return losses;
    }

    public void setLosses(long losses) {
        this.losses = losses;
    }

    public long getDraws() {
        return draws;
    }

    public void setDraws(long draws) {
        this.draws = draws;
    }
}

