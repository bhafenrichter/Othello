package othelloai;

import java.util.ArrayList;
import java.util.Arrays;

public class OthelloBoard {

    ArrayList<OthelloBoard> potentialBoards;
    OthelloBoard parent;
    char[][] board;
    int score;
    int x;
    int y;
    int depth;
    int sumScore;
    char colorMoved;
    char opponentColor;

    public OthelloBoard(char[][] board) {
        this.board = board;
        potentialBoards = new ArrayList<OthelloBoard>();
        score = 0;
        depth = 0;
    }
}
