
package othelloai;

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
        this.color = Character.toUpperCase(color);
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

    private boolean isValidMove(int col, int row, char[][] board) {
        //check to see if there is anything already in that spot
        if(board[row][col] != ' '){
            return false;
        }
        if(row + 1 >= rowCount || row - 1 < 0 || col + 1 >= colCount || col - 1 < 0){
            return false;
        }
        //check to see if there is an opposing disk adjacent to target
        if((board[row - 1][col - 1] != opponentColor) &&          //top-left
                (board[row][col - 1] != opponentColor) &&                        //top
                (board[row + 1][col - 1] != opponentColor) &&     //top-right
                (board[row - 1][col] != opponentColor) &&         //left
                (board[row + 1][col] != opponentColor) &&         //right
                (board[row - 1][col + 1] != opponentColor) &&     //bottom-left
                (board[row][col - 1] != opponentColor) &&         //bottom
                (board[row + 1][col + 1] != opponentColor)){      //bottom-right
            return false;
        }
        
        //finally check to see if there is a chip on a horizontal, vertical, or diagonal line
        //horizontal lines
       
        boolean isHorizontalRight = false;
        boolean isHorizontalLeft = false;
        boolean isVerticalUp = false;
        boolean isVerticalDown = false;
        int pointer = 1;
        
        //go all the way to the right until you hit the end to see if we get any squares
        while(col + pointer <= colCount){
            if(board[row][col + pointer] != ' ' && board[row][col + pointer] != color){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1){
                    isHorizontalRight = true;
                }
                break;
            }
        }
        pointer = 1;
        while(col - pointer >= 0){
            if(board[row][col - pointer] != ' ' && board[row][col - pointer] != color){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1){
                    isHorizontalLeft = true;
                }
                break;
            }
        }
        pointer = 1;
        while(row + pointer >= 0){
            if(board[row + pointer][col] != ' ' && board[row + pointer][col] != color){
                pointer++;
            }else{
                //we've traversed some of the opponents land
                if(pointer > 1){
                    isVerticalDown = true;
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
                if(pointer > 1){
                    isVerticalUp = true;
                }
                break;
            }
        }
        
        if((isHorizontalLeft || isHorizontalRight || isVerticalUp || isVerticalDown) == false){
            return false;
        }
        
        
        return true;
    }
}

