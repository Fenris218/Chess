package com.example.pbl4Version1.chessEngine.ai;

import com.example.pbl4Version1.chessEngine.board.Move;

public class MoveEvaluation {
    private final Move move;
    private final int value;

    public MoveEvaluation(Move move, int value) {
        this.move = move;
        this.value = value;
    }

    public Move getMove() {
        return move;
    }

    public int getValue() {
        return value;
    }
}
