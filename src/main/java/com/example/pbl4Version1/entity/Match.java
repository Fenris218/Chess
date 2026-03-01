package com.example.pbl4Version1.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.example.pbl4Version1.enums.GameStatus;
import com.example.pbl4Version1.enums.PlayerType;

@Entity(name = "game_match")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus gameStatus = GameStatus.ONGOING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerType turn = PlayerType.WHITE;

    @Enumerated(EnumType.STRING)
    private PlayerType winner;

    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER)
    private Set<Step> steps;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Match() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public PlayerType getTurn() {
        return turn;
    }

    public void setTurn(PlayerType turn) {
        this.turn = turn;
    }

    public PlayerType getWinner() {
        return winner;
    }

    public void setWinner(PlayerType winner) {
        this.winner = winner;
    }

    public Set<Step> getSteps() {
        return steps;
    }

    public void setSteps(Set<Step> steps) {
        this.steps = steps;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
