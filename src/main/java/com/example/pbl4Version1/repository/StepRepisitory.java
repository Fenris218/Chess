package com.example.pbl4Version1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pbl4Version1.entity.Step;

@Repository
public interface StepRepisitory extends JpaRepository<Step, Long> {
    List<Step> findByMatch_IdOrderByStepNumberAsc(Long matchId);

    int countByMatch_Id(Long matchId);
}
