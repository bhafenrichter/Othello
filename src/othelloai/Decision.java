/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package othelloai;

/**
 *
 * @author Brandon
 */
public class Decision {
    String move;
    int row;
    int col;
    
    public Decision(int row, int col, String move){
        this.row = row;
        this.col = col;
        this.move = move;
    }
}
