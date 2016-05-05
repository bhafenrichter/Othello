//**************************************************************************************************************
//Class: OthelloBoard.java
//Description:  this is the node class that generates a game tree.  Traversal up the game tree is held by the parent
//              the potentialBoards are the children spawned from the board 

import java.util.ArrayList;
import java.util.Arrays;

public class OthelloBoard {

    ArrayList<OthelloBoard> potentialBoards;
    OthelloBoard parent;
    char[][] board;
    short score;
    short x;
    short y;
    short depth;
    short sumScore;
    char colorMoved;
    char opponentColor;
    boolean isPruned;
    
    public OthelloBoard(char[][] board) {
        this.board = board;
        potentialBoards = new ArrayList<OthelloBoard>();
        score = 0;
        depth = 0;
        isPruned = false;
    }
}