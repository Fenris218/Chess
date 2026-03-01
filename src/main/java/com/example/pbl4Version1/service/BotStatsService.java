package com.example.pbl4Version1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pbl4Version1.entity.BotStats;
import com.example.pbl4Version1.enums.PlayerType;
import com.example.pbl4Version1.repository.BotStatsRepository;

@Service
public class BotStatsService {
    private final BotStatsRepository repo;

    public BotStatsService(BotStatsRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public BotStats getOrCreate() {
        return repo.findById(1L).orElseGet(() -> repo.save(new BotStats()));
    }

    @Transactional
    public BotStats recordResult(PlayerType winner) {
        BotStats stats = getOrCreate();
        if (winner == null) {
            stats.setDraws(stats.getDraws() + 1);
        } else if (winner == PlayerType.WHITE) {
            // assume player is WHITE in bot mode
            stats.setWins(stats.getWins() + 1);
        } else {
            stats.setLosses(stats.getLosses() + 1);
        }
        return repo.save(stats);
    }
}

