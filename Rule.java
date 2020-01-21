import java.nio.Buffer;
import java.util.*;

import javax.script.ScriptException;

//Rules by which cells operate
class Rule{

    char mode; // mode determines what kind of rule it is. 'm' = maintain (if rule is true bufferOn does not change, otherwise it changes), 'f' = flip (if rule is false bufferOn does not change, otherwise it changes), 'o' = on (if rule is true bufferOn is true), '/' = off (if rule is true bufferOn is false)

    public static char[] Modes = {'m', 'f', 'o', '/'};

    public static Rule[] to_arr(Rule r){
        return Arrays.asList( r ).stream().toArray(Rule[]::new);
    }

    public boolean modify(boolean rule, boolean On){

        switch(mode){

            case 'm':
                if (rule)
                    return On;
                
                return !On;
            
            case 'f':
                if (rule)
                    return !On;
        
                return On;

            case 'o':
                return rule;
                                
            case '/':
                return !rule;

            default:
                System.out.println("NoMode, default 'm' \n"); // should never happen

                return rule;

        }

    }

    public boolean run(ArrayList<Cell> neighbors, Cell thisCell) throws ScriptException{ 
        thisCell.bufferOn = modify(true, thisCell.on);
        return true;
    }

    Rule(char m){
        mode = m;
    }

    Rule(){
        mode = 'm';
    }

}

class Func extends Rule{


    String function;
    char mode;


    Func(char m, String func){

        function = func;
        mode = m;

    }

    Func(){}

    public boolean run(ArrayList<Cell> neighbors, Cell thisCell) throws ScriptException{

        Object result = Cells.engine.eval ( function.replace("y", String.valueOf(thisCell.xcoord) ).replace("x", String.valueOf(Cells.SIZE - thisCell.ycoord)) );

        float val;

        if(result instanceof Integer){
            val = ( (Integer) result).floatValue();
        }
        else if (result instanceof Float || result instanceof Double){
            val = (float) result;
        } else {
            System.out.println(result.getClass() + " | classError");
            return false;
        }

        switch(mode){
        
        case '=':
            thisCell.bufferOn = (val == 0);
            break;
        case '>':
            thisCell.bufferOn = (0 > val);
            break;

        case '<':
            thisCell.bufferOn = (0 < val);
            break;

        default:
            System.out.println(mode);
            return false;
        }

        return thisCell.bufferOn;

    }

}

class GOF extends Rule{

    public boolean run(ArrayList<Cell> neighbors, Cell thisCell) throws ScriptException{

        int count = 0;

        for (Cell neighbor : neighbors){
            if (neighbor.on){
                count++;
            }

        }

        if ( thisCell.on && (count == 2 || count == 3)){
            thisCell.bufferOn = true;
        } else if ( !thisCell.on && (count == 3) ){
            thisCell.bufferOn = true;
        } else {
            thisCell.bufferOn = false;
        }
        return true;

    }
}

class SwitcherN extends Rule{

    ArrayList<Boolean> cases;

    SwitcherN(ArrayList<Boolean> list){

        cases = list;

    }

    public boolean run(ArrayList<Cell> neighbors, Cell thisCell) throws ScriptException{

        int count = 0;

        for (Cell neighbor : neighbors){
            if (neighbor.on){
                count++;
            }

        }

        thisCell.on = (cases.get(count) == Boolean.TRUE) ? true : false;

        return thisCell.on;

    }

}
