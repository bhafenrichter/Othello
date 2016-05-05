//**************************************************************************************************************
//Class: Decision.java
//Description:  this is used to make the move and store all of the vital information 
public class Decision {
    String move;
    int row;
    int col;
    
    public Decision(int row, int col, String move){
        this.row = row;
        this.col = col;
        this.move = move;
    }
    
    //**************************************************************************************************************
    //Method:           formatDecision   
    //Description:      formats the string so it looks nice for the end user
    //Parameters:       None
    //Returns:          DNone 
    public void formatDecision(){
        move = "";
        move += (char) (col + 97);
        move += " ";
        move += (char) (row + 97);
    }
}