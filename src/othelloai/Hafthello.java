
package othelloai;

import java.util.ArrayList;
import java.util.Random;

public class Hafthello {
    //insert parameters for AI
    int rowCount;
    int colCount;
    int maxTime;
    char color;
    char opponentColor;
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
    }
    
    public Decision makeMove(char[][] board){
        
        return new Decision(0,0,"");
    }
    
    public Decision makeRandomMove(char[][] board){
        while(true){
            Random rand = new Random();
            int col = rand.nextInt(colCount);
            int row = rand.nextInt(rowCount); 
            if(isValidMove(col,row,board)){
                return new Decision(row,col, row + " " + col);
            }
        }
    }

    public char[][] drawMove(char[][] board, int row, int col, char color){
        //draw piece at first location
        board[row][col] = color;
        
        ArrayList<String> directions = getValidComboDirections(board, row, col);
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
    
    private boolean isValidMove(int col, int row, char[][] board) {
        //check to see if there is anything already in that spot
        if(board[row][col] != ' '){
            return false;
        }
        
        boolean hasAdjacentOpponent = false;
        try{
            if(board[row - 1][col - 1] == opponentColor){
                hasAdjacentOpponent = true;
            }   //top left
        }catch(Exception e){}
        try{
            if(board[row][col - 1] == opponentColor){
                hasAdjacentOpponent = true;
            }   //top
        }catch(Exception e){}
        try{
            if(board[row + 1][col - 1] == opponentColor){
                hasAdjacentOpponent = true;
            }   //top right
        }catch(Exception e){}
        try{
            if(board[row - 1][col] == opponentColor){
                hasAdjacentOpponent = true;
            }   //left
        }catch(Exception e){}
        try{
            if(board[row + 1][col] == opponentColor){
                hasAdjacentOpponent = true;
            }   //right
        }catch(Exception e){}
        try{
            if(board[row + 1][col - 1] == opponentColor){
                hasAdjacentOpponent = true;
            }   //bottom left
        }catch(Exception e){}
        try{
            if(board[row][col + 1] == opponentColor){
                hasAdjacentOpponent = true;
            }   //bottom
        }catch(Exception e){}
        try{
            if(board[row + 1][col + 1] == opponentColor){
                hasAdjacentOpponent = true;
            }   //bottom right
        }catch(Exception e){}
        
        
        if(hasAdjacentOpponent == false){
            return false;
        }
        
        //finally check to see if there is a chip on a horizontal, vertical, or diagonal line
        //horizontal lines
        ArrayList<String> directions = getValidComboDirections(board, row, col);
        
        //we can't chain a valid move
        if(directions.size() == 0){
            return false;
        }
        
        return true;
    }
    
    private ArrayList<String> getValidComboDirections(char[][] board, int row, int col){
        ArrayList<String> directions = new ArrayList<String>();
        
        //go all the way to the right until you hit the end to see if we get any squares
        int pointer = 1;
        while(col + pointer < colCount){
            if(board[row][col + pointer] != ' ' && board[row][col + pointer] != color){
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
            if(board[row][col - pointer] != ' ' && board[row][col - pointer] != color){
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
            if(board[row + pointer][col] != ' ' && board[row + pointer][col] != color){
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
            if(board[row - pointer][col] != ' ' && board[row - pointer][col] != color){
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
            if(board[row - pointer][col - pointer] != ' ' && board[row - pointer][col - pointer] != color){
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
            if(board[row - pointer][col + pointer] != ' ' && board[row - pointer][col + pointer] != color){
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
            if(board[row + pointer][col - pointer] != ' ' && board[row + pointer][col - pointer] != color){
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
            if(board[row + pointer][col + pointer] != ' ' && board[row + pointer][col + pointer] != color){
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
}

