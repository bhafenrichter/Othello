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


    public OthelloBoard(char[][] board) {
        this.board = board;
        potentialBoards = new ArrayList<OthelloBoard>();
        int highestScore = 0;
    }
}
