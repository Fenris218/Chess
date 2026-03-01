package com.example.pbl4Version1.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.pbl4Version1.dto.response.MatchWithBotResponse;
import com.example.pbl4Version1.dto.response.StepResponse;
import com.example.pbl4Version1.entity.MatchWithBot;
import com.example.pbl4Version1.entity.Step;
import com.example.pbl4Version1.exception.AppException;
import com.example.pbl4Version1.exception.ErrorCode;
import com.example.pbl4Version1.repository.MatchWithBotRepository;
import com.example.pbl4Version1.repository.StepRepisitory;

@Service
public class MatchWithBotService {
    private final MatchWithBotRepository matchRepository;
    private final StepRepisitory stepRepisitory;

    public MatchWithBotService(MatchWithBotRepository matchRepository, StepRepisitory stepRepisitory) {
        this.matchRepository = matchRepository;
        this.stepRepisitory = stepRepisitory;
    }


    public MatchWithBotResponse getMatch(Long id) {
        MatchWithBot match =
                matchRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));
        match.setSteps(new HashSet<>(stepRepisitory.findByMatch_Id(match.getId())));
        return toResponse(match);
    }

    public MatchWithBotResponse create() {
        MatchWithBot match = new MatchWithBot();
        match = matchRepository.save(match);
        return toResponse(match);
    }


    private MatchWithBotResponse toResponse(MatchWithBot match) {
        List<StepResponse> steps = match.getSteps() == null
                ? List.of()
                : match.getSteps().stream().map(this::toStepResponse).toList();
        String winner = match.getWinner() == null ? null : match.getWinner().toString();
        return new MatchWithBotResponse(match.getId(), winner, steps);
    }

    private StepResponse toStepResponse(Step step) {
        return new StepResponse(
                step.getMatch() == null ? null : step.getMatch().getId(),
                step.getBoardState(),
                step.getFrom(),
                step.getTo(),
                step.getName(),
                null,
                null);
    }
}
