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
import com.example.pbl4Version1.exception.AppException;
import com.example.pbl4Version1.exception.ErrorCode;
import com.example.pbl4Version1.repository.MatchWithBotRepository;
import com.example.pbl4Version1.repository.StepRepisitory;

@Service
public class BotStepService {
    private final MatchWithBotRepository matchRepository;
    private final StepRepisitory stepRepository;

    public BotStepService(MatchWithBotRepository matchRepository, StepRepisitory stepRepository) {
        this.matchRepository = matchRepository;
        this.stepRepository = stepRepository;
    }

    /**
     * Contract:
     * - Input fen is the board AFTER the human moved.
     * - Server will pick a bot move for side-to-move encoded in FEN (normally black).
     * - Returns bot move (from/to/name) + resulting fen after bot move.
     */
    @Transactional
    public StepResponse applyHumanStepAndReplyBotMove(StepToBotRequest request) {
        MatchWithBot match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_EXISTED));

        // Save human step (for history)
        Step human = new Step();
        human.setMatch(match);
        human.setFrom(request.getFrom());
        human.setTo(request.getTo());
        human.setName(request.getName());
        human.setBoardState(request.getFen());
        stepRepository.save(human);

        final Board board = Board.createByFEN(request.getFen());
        final Move botMove = new MiniMax(2).execute(board);
        if (botMove == null) {
            // No legal move -> return current state
            return new StepResponse(request.getMatchId(), request.getFen(), null, null, null, null, null);
        }

        final MoveTransition transition = board.getCurrentPlayer().makeMove(botMove);
        if (!transition.getMoveStatus().isDone()) {
            // Fallback: if illegal for any reason, don't crash the UI
            return new StepResponse(request.getMatchId(), request.getFen(), null, null, null, null, null);
        }

        final Board next = transition.getTransitionBoard();
        final String nextFen = next.generateFen();

        final String from = BoardUtils.getPositionAtCoordinate(botMove.getCurrentCoordinate());
        final String to = BoardUtils.getPositionAtCoordinate(botMove.getDestinationCoordinate());

        Step bot = new Step();
        bot.setMatch(match);
        bot.setFrom(from);
        bot.setTo(to);
        bot.setName(from + to);
        bot.setBoardState(nextFen);
        stepRepository.save(bot);

        return new StepResponse(request.getMatchId(), nextFen, from, to, bot.getName(), null, null);
    }
}

