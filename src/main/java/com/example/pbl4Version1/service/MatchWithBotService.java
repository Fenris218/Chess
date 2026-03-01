package com.example.pbl4Version1.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pbl4Version1.dto.request.EndGameRequest;
import com.example.pbl4Version1.dto.response.MatchWithBotResponse;
import com.example.pbl4Version1.dto.response.StepResponse;
import com.example.pbl4Version1.entity.MatchWithBot;
import com.example.pbl4Version1.entity.Step;
import com.example.pbl4Version1.enums.GameStatus;
import com.example.pbl4Version1.enums.PlayerType;
import com.example.pbl4Version1.exception.AppException;
import com.example.pbl4Version1.exception.ErrorCode;
import com.example.pbl4Version1.repository.MatchWithBotRepository;
import com.example.pbl4Version1.repository.StepRepisitory;

@Service
public class MatchWithBotService {
    private final MatchWithBotRepository matchRepository;
    private final StepRepisitory stepRepisitory;
    private final BotStatsService botStatsService;

    public MatchWithBotService(MatchWithBotRepository matchRepository,
                               StepRepisitory stepRepisitory,
                               BotStatsService botStatsService) {
        this.matchRepository = matchRepository;
        this.stepRepisitory = stepRepisitory;
        this.botStatsService = botStatsService;
    }


    public MatchWithBotResponse getMatch(Long id) {
        MatchWithBot match =
                matchRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));
        match.setSteps(new HashSet<>(stepRepisitory.findByMatch_IdOrderByStepNumberAsc(match.getId())));
        return toResponse(match);
    }

    public MatchWithBotResponse create() {
        MatchWithBot match = new MatchWithBot();
        match = matchRepository.save(match);
        return toResponse(match);
    }

    /**
     * Cập nhật kết quả ván cờ khi frontend phát hiện game kết thúc.
     * Chỉ cập nhật nếu ván cờ đang ONGOING (tránh ghi đè).
     */
    @Transactional
    public MatchWithBotResponse endGame(EndGameRequest request) {
        MatchWithBot match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));

        // Chỉ cập nhật nếu game đang ONGOING
        if (match.getGameStatus() == GameStatus.ONGOING) {
            GameStatus status = GameStatus.valueOf(request.getGameStatus());
            match.setGameStatus(status);

            PlayerType winner = null;
            if (request.getWinner() != null && !request.getWinner().equals("DRAW")) {
                winner = PlayerType.valueOf(request.getWinner());
            }
            match.setWinner(winner);
            matchRepository.save(match);

            // Ghi nhận thống kê
            botStatsService.recordResult(winner);
        }

        match.setSteps(new HashSet<>(stepRepisitory.findByMatch_IdOrderByStepNumberAsc(match.getId())));
        return toResponse(match);
    }


    private MatchWithBotResponse toResponse(MatchWithBot match) {
        List<StepResponse> steps = match.getSteps() == null
                ? List.of()
                : match.getSteps().stream()
                    .sorted((a, b) -> Integer.compare(a.getStepNumber(), b.getStepNumber()))
                    .map(this::toStepResponse)
                    .toList();
        String winner = match.getWinner() == null ? null : match.getWinner().toString();
        String gameStatus = match.getGameStatus() == null ? null : match.getGameStatus().toString();
        return new MatchWithBotResponse(match.getId(), gameStatus, winner, steps);
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
