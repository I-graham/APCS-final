import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.*;
import java.awt.Frame;
import java.util.Scanner;
import javax.script.*;


class Cells{

    public static int SIZE = 10;
    public static final char FULL  = '\u2588'; //corresponds to a graphic character which fills a full square.
    public static final char EMPTY = ' ';

    public static float Speed = 1;

    public static ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");

    public static ArrayList<ArrayList<Cell>> Cellg;

    public static char ByteToChar(byte b){
        return (char) (b & 0xFF);
    }

    public static void main(String[] args)  throws ScriptException, IOException, InterruptedException {

        StringBuilder output = new StringBuilder(); //printing to console was causing issues (flickering), so I'm using this as a buffer.

        Cellg = new ArrayList<ArrayList<Cell>>(SIZE);
        
        
        for(int x = 0; x < SIZE; x++){
            Cellg.add(new ArrayList<Cell>());
            for(int y = 0; y < SIZE; y++){
                Cellg.get(x).add(new Cell(x, y, new Rule('m')));
            }
        }

        Scanner s = new Scanner(System.in);
        String input = s.nextLine();
        
        while(read(input, output)){

            input = s.nextLine();
        }
        
        s.close();

    }

    public static boolean read(String command, StringBuilder output)  throws ScriptException, IOException, InterruptedException {

        Scanner reading = new Scanner(command);

        if (!reading.hasNext()){
            System.out.println("Please input a command.");
            return true;
        }

        switch (reading.next()){

            case "run":
                if (!reading.hasNextInt()){
                    System.out.println("Invalid input, try a number instead.");
                    return true;
                }

                run(reading.nextInt(), output);
                
                break;

            case "resize":
                {

                    if (!reading.hasNextInt())
                    {
                        System.out.println("Invalid input, try an integer instead.");
                        return true;
                    }

                    SIZE = reading.nextInt();

                    Cellg = new ArrayList<ArrayList<Cell>>(SIZE);

                    for(int x = 0; x < SIZE; x++){
                        Cellg.add(new ArrayList<Cell>());
                        for(int y = 0; y < SIZE; y++){
                            Cellg.get(x).add(new Cell(x, y, new Rule('m')));
                        }
                    }

                    read("show", output);

                }
                break;

            case "exit":
                return false;
                
            case "rule":

                {
                    if (!reading.hasNext())
                    {
                        System.out.println("Invalid input, add a mode.");
                        return true;
                    }

                    char mode = reading.next().charAt(0);

                    Cell.cellRules = new Rule(mode);

                }
                break;

            case "flipall":

                for(int x = 0; x < SIZE; x++){
                    for(int y = 0; y < SIZE; y++){
                        Cellg.get(x).get(y).on = !Cellg.get(x).get(y).on;
                    }
                }

                break;

            case "flip":
                {
                    if (!reading.hasNextInt())
                    {
                        System.out.println("Invalid input, try an integer instead.");
                        return true;
                    }

                    int a = reading.nextInt();

                    if (!reading.hasNextInt())
                    {
                        System.out.println("Invalid input, try an integer instead.");
                        return true;
                    }

                    int b = reading.nextInt();

                    if ( (0 <= a && a < SIZE) && (0 <= b && b < SIZE) )
                    {
                        Cellg.get(b).get(a).on = !Cellg.get(b).get(a).on;
                    }
                    else
                    {
                        System.out.println("Input out of range.");
                    }

                }

            case "show":
                {

                    
                    output = new StringBuilder();
                    out(output);
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); 
                    
                    System.out.print(output);
                    
                    Thread.sleep(100);//wait until time for next frame

                }
                break;

            case "speed":

                if (!reading.hasNextFloat())
                {
                    System.out.println("Invalid input, try a float instead.");
                    return true;
                }

                Cells.Speed = reading.nextFloat();

                break;

            case "clear":

                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); 

                break;

            case "GameOfLife":

            Cell.cellRules = new GOF();

            break;

            case "Func":
                {

                    if (!reading.hasNext())
                    {
                        System.out.println("Invalid input, add a mode.");
                        return true;
                    }

                    char mode = reading.next().charAt(0);

                    Cell.cellRules = new Func(mode, reading.nextLine());
                }
                break;

            case "Switcher":

            {
                
                ArrayList<Boolean> list = new ArrayList<Boolean>();

                for(int i = 0; i < 9; i++){

                    if (reading.hasNextBoolean()){

                        list.add( (reading.nextBoolean() ? Boolean.TRUE : Boolean.FALSE) );

                    } else {

                        System.out.println("Not a boolean.");
                        break;
                    
                    }

                }

                Cell.cellRules = list;

            }

            break;

            case "help":

            System.out.println(
            
            "
            show                    - displays world.\n
            resize [n]              - takes an integer n and makes the world's size n x n.\n
            clear                   - clears screen. \n
            speed [n]               - sets the speed of simulations to 100n ms per frame. \n
            flip [x y]              - toggles the on state of the square at (x, y), counting from the top left corner. \n
            flipall                 - toggles the on state of all squares. \n
            help                    - displays help menu. \n
            run [n]                 - runs the set rule n times, showing each Frame. \n
            GameOfLife              - sets the rule to those of Conway's Game of Life. \n
            exit                    - closes program. \n
            rule [m]                - sets rule to default rule (always true) with mode m (\"Rule /\" will create a rule that flips all tiles). \n
            Func [m expr]           - m (can be '>', '<', or '=') is the mode through which 0 is compared to expr (expr is an expression which can contain x and y as variables, which represent the coordinates of tiles). \n
            Switcher [n0 n1 ... n9] - allows the user to set a rule whether tiles should be on or off if they have specific numbers of neighbors. Inputs Booleans. \n
            "
            );

            break;

            default:
                System.out.println("Invalid command");
                Thread.sleep( (int) ( 100 * Speed ) );
                return true;
        }

        return true;

    }

    public static void run(int times,  StringBuilder output) throws ScriptException, IOException, InterruptedException {

            for(int i = 0; i < times; i++){
                for(int x = 0; x < SIZE; x++){
                    for(int y = 0; y < SIZE; y++){
                        Cellg.get(x).get(y).loop(Cellg);
                    }
                }

                for(int x = 0; x < SIZE; x++){
                    for(int y = 0; y < SIZE; y++){
                        Cellg.get(x).get(y).on = Cellg.get(x).get(y).bufferOn;
                    }
                }

                //clear output stream (this makes output appear jaggy at times, it will be used only for debugging/until I create a GUI)
                output = new StringBuilder();
                out(output);
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); 
                
                System.out.print(output);
                
                Thread.sleep((long)(Cells.Speed * 100));//wait until time for next frame
                

            }
        }

    //outputs grid to screen with adequate spacing
    public static void out(StringBuilder output){

        output.append("\n");

        for(int i = 0; i < SIZE; i++)
            output.append("--");

        for(int i = 0; i < Cellg.size(); i++){
            output.append("\n");
            for(int j = 0; j < Cellg.get(i).size(); j++){
                //prints it twice so that it appears to be a square shape in terminal instead of a rectangle
                output.append("" + (Cellg.get(i).get(j).on ? FULL : EMPTY) + (Cellg.get(i).get(j).on ? FULL : EMPTY));
            }
        }

        output.append("\n");

        for(int i = 0; i < SIZE; i++)
            output.append("--");
        
        output.append("\n");
    }

}