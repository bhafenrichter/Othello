
package othelloai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Hafthello {
    //insert parameters for AI
    int rowCount;
    int colCount;
    int maxTime;
    char color;
    char opponentColor;
    String mode;
    
    //helps keeping track of recursive calls
    Decision bestDecision = new Decision(0, 0, "");
    int bestDecisionScore = 0;
    
    public static int defaultDepth = 5;
    
    public Hafthello(int rowCount, int colCount, int maxTime, char color){
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.maxTime = maxTime;
        //HARDCODED
        this.color = color;
        if(color == 'B'){
            opponentColor = 'W';
        }else{
            opponentColor = 'B';
        }
        //default to random
        mode = "random";
    }
    
    public void selectMode(){
        KeyboardInputClass input = new KeyboardInputClass();
        System.out.println("1. User");
        System.out.println("2. Random");
        System.out.println("3. Hafthello 1.0");
        String str = input.getKeyboardInput("Select Intelligence:");
        
        if(str.equals("1")){
            mode = "user";
        }else if(str.equals("2")){
            mode = "random";
        }else if(str.equals("3")){
            mode = "hafthello";
        }else{
            mode = "hafthello";
        }
    }

    public Decision makeMove(char[][] board) {
        if (mode.equals("random")) {
            return makeRandomMove(board);
        } else if (mode.equals("user")) {
            return makeUserMove(board);
        } else if (mode.equals("hafthello")) {
            bestDecision = new Decision(0,0,"");
            bestDecisionScore = 0;
            
            //generates best move in bestDecision
            makeSmartMove(new OthelloBoard(board));
            if(bestDecision.move.length() == 0){
                bestDecision = makeRandomMove(board);
            }
            return bestDecision;
        } else {
            return new Decision(0, 0, "");
        }

    }
    
    private Decision makeUserMove(char[][] board) {
        KeyboardInputClass input = new KeyboardInputClass();
        while (true) {
            String move = input.getKeyboardInput("Enter Move (Ex. 'AB' will place piece in row A at column B):");

            //97 is for the ascii table, 97 is the beginning of the alphabet
            char[] moves = move.toLowerCase().toCharArray();
            int row = (int) moves[0] - 97;
            int col = (int) moves[1] - 97;

            if (isValidMove(col, row, board, color, opponentColor)) {
                return new Decision(row, col, move);
            } else {
                System.out.println("Invalid Move, please try again.");
            }
        }
    }

    private void makeSmartMove(OthelloBoard board) {
        //printBoard(board.board, board.depth);
        char currentColor = ' ';
        char currentOpponentColor = ' ';
        if (board.depth % 2 == 0) {
            currentColor = color;
            currentOpponentColor = opponentColor;
        } else {
            currentColor = opponentColor;
            currentOpponentColor = color;
        }

        //will set the bestDecision to open corner
        if(!isCornerOpen(board)){
        //end the search and see how far we've come
        if (board.depth == defaultDepth) {
            int sumScore = 0;
            OthelloBoard cur = board;
            for (int i = 1; i <= defaultDepth; i++) {
                //add when its your board, subtract when its not
                if(currentColor == color){
                    sumScore += evaluateBoard(cur.board, currentColor);
                }else{
                    sumScore -= evaluateBoard(cur.board, currentColor);
                }
                
                cur = cur.parent;
            }
            //int currentScore = evaluateBoard(board.board);
            if (sumScore > bestDecisionScore) {

                OthelloBoard presentBoard = board;
                for (int i = 0; i < defaultDepth - 1; i++) {
                    presentBoard = presentBoard.parent;
                }
                bestDecision = new Decision(presentBoard.y, presentBoard.x, "" + presentBoard.x + presentBoard.y);
                bestDecisionScore = sumScore;
            }
        } else {
            //iterate through the entire board and get the valid boards
            for (int i = 0; i < board.board.length; i++) {
                for (int j = 0; j < board.board[0].length; j++) {
                    if (isValidMove(i, j, board.board, currentColor, currentOpponentColor)) {
                        //draw the board and add it to the tree, use the parent to traverse back up to needed position
                        OthelloBoard child = new OthelloBoard(drawMove(copyOf(board.board), i, j, currentColor));
                        child.x = i;
                        child.y = j;
                        child.score = evaluateBoard(child.board, currentColor);
                        child.parent = board;
                        child.depth = board.depth + 1;
                        board.potentialBoards.add(child);
                    }
                }
            }
            
            //iterate through and get the values of the rest of the boards in the tree
            for (int i = 0; i < board.potentialBoards.size(); i++) {
                makeSmartMove(board.potentialBoards.get(i));
            }
            
        }
        }
    }
    
    private boolean isCornerOpen(OthelloBoard board) {
        //we only deal with boards we can currently change
        if(board.depth != 0){
            return false;
        }
        if(isValidMove(0, 0, board.board, color, opponentColor)){
            bestDecision = new Decision(0,0, "00");
            return true;
        }else if(isValidMove(0, rowCount-1, board.board, color, opponentColor)){
            bestDecision = new Decision(rowCount-1,0, "0" + (rowCount-1));
            return true;
        }else if(isValidMove(colCount-1, 0, board.board, color, opponentColor)){
            bestDecision = new Decision(0,colCount-1, (colCount-1) + "0");
            return true;
        }else if(isValidMove(colCount-1, rowCount-1, board.board, color, opponentColor)){
            bestDecision = new Decision(rowCount-1, colCount-1, "" + (rowCount-1) + (colCount-1));
            return true;
        }
        return false;
    }
    
    private Decision makeRandomMove(char[][] board){
        //try to make a move and if you can't make a move after a certain amount of tries, concede turn
        int tryCount = 0;
        while(!isBoardFull(board)){
            if(tryCount == 1000){
                return new Decision(-1, -1, "");
            }
            
            Random rand = new Random();
            int col = rand.nextInt(colCount);
            int row = rand.nextInt(rowCount);
            if(isValidMove(col,row,board, color, opponentColor)){
                tryCount = 0;
                return new Decision(row,col, row + " " + col);
            }else{
                tryCount++;
            }
        }
        return new Decision(-1, -1, "");
    }

    public char[][] drawMove(char[][] board, int row, int col, char color){
        //draw piece at first location
        board[row][col] = color;
        
        char opColor = ' ';
        if(color == 'B'){opColor = 'W';}
        else{opColor = 'B';}
        
        ArrayList<String> directions = getValidComboDirections(board, row, col, color, opColor);
        for(int i = 0; i < directions.size(); i++){
            int pointer = 1;
            if(directions.get(i).equals("up")){
                while(board[row-pointer][col] != color){
                    board[row-pointer][col] = color;
                    pointer++;
                }
            }else if(directions.get(i).equals("down")){
                while(board[row+pointer][col] != color){
                    board[row+pointer][col] = color;
                    pointer++;
                }
            }else if(directions.get(i).equals("left")){
                while(board[row][col-pointer] != color){
                    board[row][col-pointer] = color;
                    pointer++;
                }
            }else if(directions.get(i).equals("right")){
                while(board[row][col+pointer] != color){
                    board[row][col+pointer] = color;
                    pointer++;
                }
            }else if(directions.get(i).equals("topleft")){
               while(board[row-pointer][col-pointer] != color){
                    board[row-pointer][col-pointer] = color;
                    pointer++;
                }
            }else if(directions.get(i).equals("topright")){
                while(board[row-pointer][col+pointer] != color){
                    board[row-pointer][col+pointer] = color;
                    pointer++;
                }
            }else if(directions.get(i).equals("bottomleft")){
                while(board[row+pointer][col-pointer] != color){
                    board[row+pointer][col-pointer] = color;
                    pointer++;
                }
            }else if(directions.get(i).equals("bottomright")){
                while(board[row+pointer][col+pointer] != color){
                    board[row+pointer][col+pointer] = color;
                    pointer++;
                }
            }
        }
        
        return board;
    }
    
    private boolean isValidMove(int col, int row, char[][] board, char currentColor, char currentOpponentColor) {
        //check to see if there is anything already in that spot
        if(board[row][col] != ' '){
            return false;
        }
        
        boolean hasAdjacentOpponent = false;
        try{
            if(board[row - 1][col - 1] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //top left
        }catch(Exception e){}
        try{
            if(board[row][col - 1] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //top
        }catch(Exception e){}
        try{
            if(board[row + 1][col - 1] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //top right
        }catch(Exception e){}
        try{
            if(board[row - 1][col] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //left
        }catch(Exception e){}
        try{
            if(board[row + 1][col] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //right
        }catch(Exception e){}
        try{
            if(board[row + 1][col - 1] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //bottom left
        }catch(Exception e){}
        try{
            if(board[row][col + 1] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //bottom
        }catch(Exception e){}
        try{
            if(board[row + 1][col + 1] == currentOpponentColor){
                hasAdjacentOpponent = true;
            }   //bottom right
        }catch(Exception e){}
        
        
        if(hasAdjacentOpponent == false){
            return false;
        }
        
        //finally check to see if there is a chip on a horizontal, vertical, or diagonal line
        //horizontal lines
        ArrayList<String> directions = getValidComboDirections(board, row, col, currentColor, currentOpponentColor);
        
        //we can't chain a valid move
        if(directions.size() == 0){
            return false;
        }
        
        //check to see if we actually flip any pieces
        int oldScore = evaluateBoard(board, currentColor);
        int newScore = evaluateBoard(drawMove(copyOf(board), row, col, currentColor), currentColor);
        if(oldScore == newScore){
            return false;
        }
        return true;
    }
    
    private ArrayList<String> getValidComboDirections(char[][] board, int row, int col, char color, char opColor){
        ArrayList<String> directions = new ArrayList<String>();
        
        //go all the way to the right until you hit the end to see if we get any squares
        int pointer = 1;
        while(col + pointer < colCount){
            if(board[row][col + pointer] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1 && board[row][col+pointer] == color){
                    directions.add("right");
                }
                break;
            }
        }
        pointer = 1;
        while(col - pointer >= 0){
            if(board[row][col - pointer] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land and we've ended on our own color
                if(pointer > 1 && board[row][col-pointer] == color){
                    directions.add("left");
                }
                break;
            }
        }
        pointer = 1;
        while(row + pointer < rowCount){
            if(board[row + pointer][col] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1 && board[row+pointer][col] == color){
                    directions.add("down");
                }
                break;
            }
        }
        pointer = 1;
        while(row - pointer >= 0){
            if(board[row - pointer][col] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1 && board[row-pointer][col] == color){
                    directions.add("up");
                }
                break;
            }
        }
        
        pointer = 1;
        while(row - pointer >= 0 && col-pointer >= 0){
            if(board[row - pointer][col - pointer] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1 && board[row-pointer][col-pointer] == color){
                    directions.add("topleft");
                }
                break;
            }
        }
        
        pointer = 1;
        while(row - pointer >= 0 && col + pointer < colCount){
            if(board[row - pointer][col + pointer] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1 && board[row-pointer][col+pointer] == color){
                    directions.add("topright");
                }
                break;
            }
        }
        
        pointer = 1;
        while(row + pointer < rowCount && col - pointer >= 0){
            if(board[row + pointer][col - pointer] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1 && board[row+pointer][col-pointer] == color){
                     directions.add("bottomleft");
                }
                break;
            }
        }
        
        pointer = 1;
        while(row + pointer < rowCount && col + pointer < colCount){
            if(board[row + pointer][col + pointer] == opColor){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1 && board[row+pointer][col+pointer] == color){
                     directions.add("bottomright");
                }
                break;
            }
        }
        return directions;
    }
    
    public boolean isBoardFull(char[][] board){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if(board[i][j] == ' '){
                    return false;
                }
            }
        }
        return true;
    }
    
    public int evaluateBoard(char[][] board, char color){
        int score = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if(board[i][j] == color){
                    score++;
                }
            }
        }
        return score;
    }
    
    public static char[][] copyOf(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original.length);
        }
        return copy;
    }

    private void printBoard(char[][] board, int depth, char currentColor) {
        System.out.println("Color: " + currentColor + "Depth: " + depth);
        for(int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++) {
                if(board[i][j] != ' '){
                    System.out.print(board[i][j]);
                }else{
                    System.out.print(" ");
                }
                
            }
            System.out.println("");
        }
        System.out.println("");
    }
}

