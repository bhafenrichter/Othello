package othelloai;

import java.util.Arrays;

public class OthelloBoard {

    OthelloBoard next;
    char[][] board;
    int[][] scores;
    int level;
    int highestScore;

    public OthelloBoard(char[][] board, int level) {
        this.board = board;
        scores = new int[board.length][board[0].length];
        int highestScore = 0;
    }
}
