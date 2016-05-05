//Program:      Hafenrichter4.java
//Course:       COSC 430
//Description:  Implementation of an artificial intelligence known as "Hafthello" that plays the board game
//              Othello in a smart or random way
//Author:       Brandon Hafenrichter
//Revised       5/4/16
//Language:     Java
//IDE:          Netbeans
//**************************************************************************************************************
//**************************************************************************************************************
//Class: Hafthello.java
//Description: Hold the core methods used to make a decision for the board


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Hafthello {
    //insert parameters for AI
    int rowCount;
    int colCount;
    int maxTime;
    public char color;
    char opponentColor;
    String mode;

    //helps keeping track of recursive calls
    Decision bestDecision = new Decision(0, 0, "");
    int bestDecisionScore = 0;
    
    //depth at which we traverse
    public int defaultDepth = 5;
    
    public Hafthello(int rowCount, int colCount, int maxTime, char color){
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.maxTime = maxTime;
        
        //default to random
        mode = "random";
    }
    
    //**************************************************************************************************************
    //Method:           setColors
    //Description:      Sets both the user and the opponent colors
    //Parameters:       char userColor  the color of the user
    //Returns:          nothing
    
    public void setColors(char userColor){
        color = userColor;
        if(color == 'B'){
            opponentColor = 'W';
        }else{
            opponentColor = 'B';
        }
    }
    //**************************************************************************************************************
    //Method:           selectMode
    //Description:      prompts the user to select which intelligence they would like to test
    //Parameters:       Nothing
    //Returns:          String      returns the selection the user made
    public String selectMode(){
        KeyboardInputClass input = new KeyboardInputClass();
        System.out.println("1. User");
        System.out.println("2. Random");
        System.out.println("3. Hafthello 3.0");
        String str = input.getKeyboardInput("Select Intelligence: (Default: Hafthello 3.0)");
        
        if(str.equals("1")){
            mode = "user";
        }else if(str.equals("2")){
            mode = "random";
        }else if(str.equals("3")){
            mode = "hafthello";
        }else{
            mode = "hafthello";
        }
        return mode;
    }
    //**************************************************************************************************************
    //Method:           makeMove     
    //Description:      Based on what the user selected of the intelligence, it makes the move.  This is the first method    
    //                  to be called whenever the ai is making a decision    
    //Parameters:       char[][] board  the board is going to evaluate and make a move 
    //Returns:          Decision        the decision on which move it is going to make
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
    
    //**************************************************************************************************************
    //Method:           makeUserMove   
    //Description:      this method makes a move based on what the user enters
    //Parameters:       char[][] board  this is the board is makes a move on
    //Returns:          Decision        the decision on what move is made
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
    
    //**************************************************************************************************************
    //Method:           makeSmartMove       
    //Description:      traverses the game tree up to the specified depth and makes a move based on a heuristic
    //                  that evaluates all the boards that lead up to a certain move
    //Parameters:       OthelloBoard board  a board containing more information about the current board and is also
    //                                      needed for recursion
    //Returns:          Decision            makes a decision based on the recursive search
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
        if (!isCornerOpen(board)) {
            //end the search and see how far we've come
            if (board.depth == defaultDepth) {
                int heuristic = calculateHeuristic(board);
                if (heuristic > bestDecisionScore) {

                    OthelloBoard presentBoard = board;
                    for (int i = 0; i < defaultDepth - 1; i++) {
                        presentBoard = presentBoard.parent;
                    }
                    bestDecision = new Decision(presentBoard.y, presentBoard.x, "" + presentBoard.x + presentBoard.y);
                    bestDecisionScore = heuristic;
                }
            } else {
                //iterate through the entire board and get the valid boards
                for (int i = 0; i < board.board.length; i++) {
                    for (int j = 0; j < board.board[0].length; j++) {
                        if (isValidMove(i, j, board.board, currentColor, currentOpponentColor)) {
                            //draw the board and add it to the tree, use the parent to traverse back up to needed position
                            OthelloBoard child = new OthelloBoard(drawMove(copyOf(board.board), i, j, currentColor));
                            child.x = (short) i;
                            child.y = (short) j;
                            child.score = (short) evaluateBoard(child.board, currentColor);
                            child.parent = board;
                            child.depth = (short) (board.depth + 1);
                            child.colorMoved = currentColor;
                            child.opponentColor = currentOpponentColor;
                            board.potentialBoards.add(child);
                        }
                    }
                }

                //iterate through and get the values of the rest of the boards in the tree
                for (int i = 0; i < board.potentialBoards.size(); i++) {
                    if(!board.potentialBoards.get(i).isPruned){
                        makeSmartMove(board.potentialBoards.get(i));
                    }
                }

            }
        }else{
            return;
        }
    }
    
    //**************************************************************************************************************
    //Method:           isCornerOpen            
    //Description:      checks to see whether any of the corners are open to take, this is the best move we can make                  
    //Parameters:       OthelloBoard board      the board we evaluate
    //Returns:          boolean                 if any of the corners are open and it also sets the bestDecision to that
    //                                          immiediately
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
    
    //**************************************************************************************************************
    //Method:           makeRandomMove           
    //Description:      makes a random, yet valid move on the board
    //Parameters:       char[][] board      the board to make a move upon
    //Returns:          Decision            the move that is made
    private Decision makeRandomMove(char[][] board){
        //try to make a move and if you can't make a move after a certain amount of tries, concede turn
        int tryCount = 0;
        while(!isBoardFull(board)){
            if(tryCount == 10000){
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
    
    //**************************************************************************************************************
    //Method:           drawMove        
    //Description:      draws the move on the board with the specified color
    //Parameters:       char[][] board      the board to be manipulated
    //                  int row             row that the move was made on
    //                  int col             column that the move was made on
    //                  char color          the color that made the move
    //Returns:          char[][]            the updated board with the move made
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
    
    //**************************************************************************************************************
    //Method:           isValidMove  
    //Description:      checks to see if the move at the row and column is a valid move that can be made
    //Parameters:       int col         the column to be checked
    //                  int row         the row to be checked
    //                  char[][] board  the context of the column and row
    //                  char curColor   the currentColor being checked with
    //                  char opColor    the color of the current opponent
    //Returns:          boolean         returns whether or not the move is valid
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
    
    //**************************************************************************************************************
    //Method:           getValidComboDirections           
    //Description:      Checks to see what directions the move made can return a higher score at
    //Parameters:       char[][] board  the board to be checked
    //                  int row         the row the move was made on
    //                  int col         the col the move was made on
    //                  char color      the color of the move made
    //                  char opColor    the color of the opponent
    //Returns:          AL<String>      all of the directions the move can be drawn
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
    
    //**************************************************************************************************************
    //Method:           isBoardFull
    //Description:      Checks to see if the board is full
    //Parameters:       char[][] board  the board to be checked
    //Returns:          boolean         if the board if full or not
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
    
    //**************************************************************************************************************
    //Method:           evaluateBoard           
    //Description:      Checks to see how many tiles the current color scores with the current state of the board
    //Parameters:       char[][] board      board to be checked
    //                  char color          the color that is to be checked
    //Returns:          int                 number of tiles 
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
    
    //**************************************************************************************************************
    //Method:           copyOf     
    //Description:      returns a copy of the char[][] array, hack around the pass by reference-like functionality of
    //                  Java
    //Parameters:       char[][] original       the array to be cloned
    //Returns:          char[][]                the cloned array
    public static char[][] copyOf(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original.length);
        }
        return copy;
    }

    //**************************************************************************************************************
    //Method:           calculateHeuristic
    //Description:      Calculates a heuristic for the path of the boards based on how many tiles the user has
    //                  compared to the number of tiles the opponent scores of the traversal of the parents
    //Parameters:       OthelloBoard board      the board to be scored
    //Returns:          int                     the heuristic score of the chain of boards
    private int calculateHeuristic(OthelloBoard board) {
        int sumScore = 0;
            OthelloBoard cur = board;
            boolean isWeak = false;
            for (int i = 1; i <= defaultDepth; i++) {
                //add when its your board, subtract when its not
                if(cur.colorMoved == color){
                    sumScore += evaluateBoard(cur.board, board.colorMoved) * i;
                    if(isWeakMove(board) && board.depth == 0){
                        //its a weak move for me
                        isWeak = true;
                    }
                }else{
                    sumScore -= evaluateBoard(cur.board, board.opponentColor) * i;
                }
                
                cur = cur.parent;
            }
            if(isWeak){
                //set all of the parents to pruned so we don't go down that route
                cur = board;
                for(int i = 1; i <= defaultDepth; i++){
                    cur.isPruned = true;
                    cur = cur.parent;
                }
                return -1000;
            }else{
                return sumScore;
            }
    }
    
    //**************************************************************************************************************
    //Method:           isWeakMove
    //Description:      Checks to see if the current move is going to be a bad move
    //Parameters:       OthelloBoard b      the board to be checked
    //Returns:          boolean             whether the board move is weak or not
    private boolean isWeakMove(OthelloBoard b){
        int row = b.y;
        int col = b.x;
        
        if(row == 1 && col == 1){
            return true;
        }else if(row == 1 && col == 0){
            return true;
        }else if(row == 0 && col == 1){
            return true;
        }else if(row == rowCount - 2 && col == 0){
            return true;
        }else if(row == rowCount - 2 && col == 1){
            return true;
        }else if(row == rowCount - 1 && col == 1){
            return true;
        }else if(col == colCount - 2 && row == 0){
            return true;
        }else if(col == colCount - 2 && row == 1){
            return true;
        }else if(col == colCount - 1 && row == 1){
            return true;
        }else if(col == colCount - 1 && row == rowCount - 2){
            return true;
        }else if(col == colCount - 2 && row == rowCount - 2){
            return true;
        }else if(col == colCount - 2 && row == rowCount - 1){
            return true;
        }else{
            return false;
        }
    }
}

