import java.util.*;
import java.lang.*;
import javax.script.*;

class Cell{

    public boolean on = false;

    public boolean bufferOn; // I will use a buffer to prevent sequential biases

    public static Rule cellRules;

    public int xcoord;
    public int ycoord;

    public Cell[] neighbors;

    ArrayList<Cell> neighbors(ArrayList<ArrayList<Cell>> Cellg){
    
        ArrayList<Cell> n = new ArrayList<Cell>();

        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if( !(i == 0 && j == 0) && ( (0 <= (xcoord + i)) && ((xcoord + i) < Cells.SIZE)) && ( (0 <= (ycoord + j)) && ((ycoord + j) < Cells.SIZE))){
                    //System.out.println("xcoord : " + xcoord + " | i : " + i + " | ycoord : " + ycoord + " | j : " + j );
                    n.add(Cellg.get(xcoord + i).get(ycoord + j));
                }
            }
        }

        return n;
    }

    Cell(int x, int y){
        this(x, y, new Rule() );
    }

    Cell(int x, int y, Rule CRule){

        xcoord = x;
        ycoord = y;
        cellRules = CRule;

    }

    public void loop(ArrayList<ArrayList<Cell>> Cellg) throws ScriptException{

        Cell.cellRules.run(neighbors(Cellg), this);
        
    
    }

}