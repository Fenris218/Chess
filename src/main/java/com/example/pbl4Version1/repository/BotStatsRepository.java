package com.example.pbl4Version1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pbl4Version1.entity.BotStats;

public interface BotStatsRepository extends JpaRepository<BotStats, Long> {
}

