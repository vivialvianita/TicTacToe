/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #1
 * 1 - 5026221161 - Ryan Adi Putra Pratama
 * 2 - 5026231185 - Jannati Urfa Muhayat
 * 3 - 5026231226 - Vivi Alvianita
 */

import java.util.ArrayList;
import java.util.List;

/**
 * The AIPlayer class represents an AI player that can play Tic-Tac-Toe
 * against a human player. The AI uses the Minimax algorithm to make
 * optimal moves.
 */
public class AIPlayer {

    private Seed aiSeed;       // AI's seed (either CROSS or NOUGHT)
    private Seed opponentSeed; // Opponent's seed (either NOUGHT or CROSS)

    /**
     * Constructor to initialize AI with its seed and opponent's seed.
     *
     * @param aiSeed The AI's seed.
     */
    public AIPlayer(Seed aiSeed) {
        this.aiSeed = aiSeed;
        this.opponentSeed = (aiSeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    /**
     * Get the best move for the AI using the Minimax algorithm.
     *
     * @param board The current game board.
     * @return An array of two integers {row, col} representing the AI's move.
     */
    public int[] getBestMove(Board board) {
        // Cari langkah terbaik (sederhana: pilih langkah pertama yang kosong)
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                if (board.cells[row][col].content == Seed.NO_SEED) {
                    return new int[] {row, col}; // Langkah valid pertama
                }
            }
        }
        return null; // Tidak ada langkah yang tersedia
    }

    /**
     * The Minimax algorithm with alpha-beta pruning.
     *
     * @param board The game board.
     * @param depth The depth of recursion.
     * @param isMaximizing True if it's the AI's turn to maximize the score.
     * @return The score of the current board state.
     */
    private int minimax(Board board, int depth, boolean isMaximizing) {
        State currentState = evaluateBoard(board);

        // Terminal conditions
        if (currentState == State.CROSS_WON) {
            return (aiSeed == Seed.CROSS) ? 10 - depth : depth - 10;
        } else if (currentState == State.NOUGHT_WON) {
            return (aiSeed == Seed.NOUGHT) ? 10 - depth : depth - 10;
        } else if (currentState == State.DRAW) {
            return 0;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int row = 0; row < Board.ROWS; ++row) {
                for (int col = 0; col < Board.COLS; ++col) {
                    if (board.cells[row][col].content == Seed.NO_SEED) {
                        board.cells[row][col].content = aiSeed;
                        int eval = minimax(board, depth + 1, false);
                        board.cells[row][col].content = Seed.NO_SEED; // Undo move
                        maxEval = Math.max(maxEval, eval);
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int row = 0; row < Board.ROWS; ++row) {
                for (int col = 0; col < Board.COLS; ++col) {
                    if (board.cells[row][col].content == Seed.NO_SEED) {
                        board.cells[row][col].content = opponentSeed;
                        int eval = minimax(board, depth + 1, true);
                        board.cells[row][col].content = Seed.NO_SEED; // Undo move
                        minEval = Math.min(minEval, eval);
                    }
                }
            }
            return minEval;
        }
    }

    /**
     * Evaluate the board and return its current state.
     *
     * @param board The game board.
     * @return The current game state (PLAYING, DRAW, CROSS_WON, or NOUGHT_WON).
     */
    private State evaluateBoard(Board board) {
        // Check if CROSS or NOUGHT has won
        for (int row = 0; row < Board.ROWS; ++row) {
            if (board.cells[row][0].content != Seed.NO_SEED &&
                    board.cells[row][0].content == board.cells[row][1].content &&
                    board.cells[row][1].content == board.cells[row][2].content) {
                return (board.cells[row][0].content == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
            }
        }

        for (int col = 0; col < Board.COLS; ++col) {
            if (board.cells[0][col].content != Seed.NO_SEED &&
                    board.cells[0][col].content == board.cells[1][col].content &&
                    board.cells[1][col].content == board.cells[2][col].content) {
                return (board.cells[0][col].content == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
            }
        }

        if (board.cells[0][0].content != Seed.NO_SEED &&
                board.cells[0][0].content == board.cells[1][1].content &&
                board.cells[1][1].content == board.cells[2][2].content) {
            return (board.cells[0][0].content == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }

        if (board.cells[0][2].content != Seed.NO_SEED &&
                board.cells[0][2].content == board.cells[1][1].content &&
                board.cells[1][1].content == board.cells[2][0].content) {
            return (board.cells[0][2].content == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }

        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                if (board.cells[row][col].content == Seed.NO_SEED) {
                    return State.PLAYING; // Still playing
                }
            }
        }
        return State.DRAW; // No moves left, it's a draw
    }
}
