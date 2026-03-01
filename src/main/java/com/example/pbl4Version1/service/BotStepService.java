package com.example.pbl4Version1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pbl4Version1.chessEngine.ai.MiniMax;
import com.example.pbl4Version1.chessEngine.board.Board;
import com.example.pbl4Version1.chessEngine.board.BoardUtils;
import com.example.pbl4Version1.chessEngine.board.Move;
import com.example.pbl4Version1.chessEngine.player.MoveTransition;
import com.example.pbl4Version1.dto.request.StepToBotRequest;
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
public class BotStepService {
    private final MatchWithBotRepository matchRepository;
    private final StepRepisitory stepRepository;
    private final BotStatsService botStatsService;

    public BotStepService(MatchWithBotRepository matchRepository,
                          StepRepisitory stepRepository,
                          BotStatsService botStatsService) {
        this.matchRepository = matchRepository;
        this.stepRepository = stepRepository;
        this.botStatsService = botStatsService;
    }

    /**
     * Contract:
     * - Input fen is the board AFTER the human moved.
     * - Server will pick a bot move for side-to-move encoded in FEN (normally black).
     * - Returns bot move (from/to/name) + resulting fen after bot move.
     * - Cập nhật gameStatus / winner khi game kết thúc.
     */
    @Transactional
    public StepResponse applyHumanStepAndReplyBotMove(StepToBotRequest request) {
        MatchWithBot match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));

        // Đếm số nước đã đi để xác định stepNumber
        int currentStepCount = stepRepository.countByMatch_Id(match.getId());

        // === 1) Lưu nước đi của người chơi (WHITE) ===
        Step humanStep = new Step();
        humanStep.setMatch(match);
        humanStep.setFrom(request.getFrom());
        humanStep.setTo(request.getTo());
        humanStep.setName(request.getName());
        humanStep.setBoardState(request.getFen());
        humanStep.setStepNumber(currentStepCount + 1);
        humanStep.setPlayerType(PlayerType.WHITE);
        stepRepository.save(humanStep);

        // Cập nhật lượt đi trong match
        match.setTurn(PlayerType.BLACK);

        // === 2) Kiểm tra trạng thái sau nước đi của người (bot có bị chiếu hết / hết nước?) ===
        final Board boardAfterHuman = Board.createByFEN(request.getFen());

        if (boardAfterHuman.getCurrentPlayer().isInCheckMate()) {
            // Bot (BLACK) bị chiếu hết → Người chơi (WHITE) thắng
            match.setGameStatus(GameStatus.CHECK_MATE);
            match.setWinner(PlayerType.WHITE);
            matchRepository.save(match);
            botStatsService.recordResult(PlayerType.WHITE);
            return new StepResponse(request.getMatchId(), request.getFen(),
                    null, null, null, GameStatus.CHECK_MATE.name(), PlayerType.WHITE.name());
        }

        if (boardAfterHuman.getCurrentPlayer().isInStaleMate()) {
            // Bot hết nước đi hợp lệ → Hòa
            match.setGameStatus(GameStatus.STALE_MATE);
            match.setWinner(null);
            matchRepository.save(match);
            botStatsService.recordResult(null);
            return new StepResponse(request.getMatchId(), request.getFen(),
                    null, null, null, GameStatus.STALE_MATE.name(), PlayerType.DRAW.name());
        }

        // === 3) Bot tìm nước đi ===
        final Move botMove = new MiniMax(2).execute(boardAfterHuman);
        if (botMove == null) {
            // Không tìm được nước → hòa
            match.setGameStatus(GameStatus.DRAW);
            match.setWinner(null);
            matchRepository.save(match);
            botStatsService.recordResult(null);
            return new StepResponse(request.getMatchId(), request.getFen(),
                    null, null, null, GameStatus.DRAW.name(), PlayerType.DRAW.name());
        }

        final MoveTransition transition = boardAfterHuman.getCurrentPlayer().makeMove(botMove);
        if (!transition.getMoveStatus().isDone()) {
            // Nước đi không hợp lệ → trả về trạng thái hiện tại
            return new StepResponse(request.getMatchId(), request.getFen(),
                    null, null, null, match.getGameStatus().name(), null);
        }

        // === 4) Lưu nước đi của bot (BLACK) ===
        final Board boardAfterBot = transition.getTransitionBoard();
        final String nextFen = boardAfterBot.generateFen();
        final String from = BoardUtils.getPositionAtCoordinate(botMove.getCurrentCoordinate());
        final String to = BoardUtils.getPositionAtCoordinate(botMove.getDestinationCoordinate());

        Step botStep = new Step();
        botStep.setMatch(match);
        botStep.setFrom(from);
        botStep.setTo(to);
        botStep.setName(from + to);
        botStep.setBoardState(nextFen);
        botStep.setStepNumber(currentStepCount + 2);
        botStep.setPlayerType(PlayerType.BLACK);
        stepRepository.save(botStep);

        // Cập nhật lượt đi
        match.setTurn(PlayerType.WHITE);

        // === 5) Kiểm tra trạng thái sau nước đi của bot ===
        String gameStatusStr = GameStatus.ONGOING.name();
        String winnerStr = null;

        if (boardAfterBot.getCurrentPlayer().isInCheckMate()) {
            // Người chơi (WHITE) bị chiếu hết → Bot (BLACK) thắng
            match.setGameStatus(GameStatus.CHECK_MATE);
            match.setWinner(PlayerType.BLACK);
            gameStatusStr = GameStatus.CHECK_MATE.name();
            winnerStr = PlayerType.BLACK.name();
            botStatsService.recordResult(PlayerType.BLACK);
        } else if (boardAfterBot.getCurrentPlayer().isInStaleMate()) {
            // Người chơi hết nước → Hòa
            match.setGameStatus(GameStatus.STALE_MATE);
            match.setWinner(null);
            gameStatusStr = GameStatus.STALE_MATE.name();
            winnerStr = PlayerType.DRAW.name();
            botStatsService.recordResult(null);
        }

        matchRepository.save(match);

        return new StepResponse(request.getMatchId(), nextFen, from, to, botStep.getName(),
                gameStatusStr, winnerStr);
    }
}

