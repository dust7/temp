import java.util.Scanner;
import java.awt.Color;
import java.awt.Point;
import javax.swing.*;

class SnakeGame {
    private static Snake snake; //statics used by WindowListener methods in SnakeFrame
    private static Snake troll;
    private static Food food;
    private static Grid grid;
    private static int time=200; //time between moves

    public static Snake getSnake() {
        return new Snake(snake);
    }    
    
    public static Snake getTroll() {
        return new Snake(troll);
    }
    
    public static Food getFood() {
        return new Food(food);
    }
    
    public static Grid getGrid() {
        return new Grid(grid);
    }    
    
    private static void adjustTime() {    
        if(time>150) {
            time=time-4;        
        } else if(time<=150 && time>100) {
            time=time-2;
        } else if(time<=100 && time>20) {
            time=time-1;
        }    
    }
    
    private static boolean gameOver() {
        boolean bitestroll=false;
        for(int i=0; i<troll.getSize(); i++) {
            if(snake.getPartsElement(0).equals(troll.getPartsElement(i))) {
                bitestroll=true;
                break;
            }
        }
        return bitestroll || snake.bites() || snake.isInDeadzone(grid);
    }
    
    //methods for debugging (format grid locations, print info)
    private static int normXout(int x) {        
        x=x-Grid.SIDE-Grid.SIDE/2-Grid.OFFSET_X;
        x=x/Grid.SIDE;
        return x;
    }
    
    private static int normYout(int y) {        
        y=y-Grid.SIDE-Grid.SIDE/2-Grid.OFFSET_Y;
        y=y/Grid.SIDE;
        return y;
    }
    
    private static String normLocOut(Point pt) {
        int x=(int)pt.getX();
        int y=(int)pt.getY();
        x=normXout(x);
        y=normYout(y);
        return "("+x+","+y+")";
    }
    
    public static void inform() {
        System.out.println("snake size: "+snake.getSize());
        System.out.println("time: "+time+" ms");
        System.out.println("head at: "+normLocOut(snake.getPartsElement(0)));    
        System.out.println("food at: "+normLocOut(food.getFoodPosition())+"\n");
    }
    
    public static void main(String[] args) {
        //ask user for playzone dimensions
        AskFrame askuser=new AskFrame();
        while(!askuser.isInitialized()) {
            try {
                Thread.sleep(100);                
            } catch(InterruptedException ex){}
        }
        askuser.setVisible(false);
        askuser.dispose();
        //init grid, snake, food, frame                           
        grid=new Grid(askuser.getUserX(), askuser.getUserY());        
        snake=new Snake(grid);
        troll=new TrollSnake(grid);    
        food=new Food(grid);
        while(food.isOnSnake(snake) || food.isOnSnake(troll)) {
            food.jump(grid);
        }       
        SnakeFrame frame=new SnakeFrame(askuser.getUserX(), askuser.getUserY());
        ((SnakeFrame)frame).setBg(new Color(228, 228, 228));          
        //start game        
        inform();
        System.out.println("use arrow keys to move!");        
        while(!gameOver()) {            
            String command=frame.getLastListened();
            String lastMoved=snake.getLastMoved();
            if(command.equals("up") && !lastMoved.equals("down")) {
                snake.moveUp();                            
            } else if(command.equals("down") && !lastMoved.equals("up")){
                snake.moveDown();                
            } else if(command.equals("left") && !lastMoved.equals("right")) {
                snake.moveLeft();                   
            } else if(command.equals("right") && !lastMoved.equals("left")) {
                snake.moveRight();                    
            } else if(lastMoved.equals("up")) {                               
                snake.moveUp();
            } else if(lastMoved.equals("down")) {
                snake.moveDown();
            } else if(lastMoved.equals("left")) {
                snake.moveLeft();
            } else if(lastMoved.equals("right")) {
                snake.moveRight();
            }
            if(!snake.getLastMoved().equals("")) {
                ((TrollSnake)troll).randMove(grid);
            }        
            if(snake.eats(food) || troll.eats(food)) {
                adjustTime();
                troll.grow();
                snake.grow();
                SnakePainter.repaintWholeSnake(frame,troll);  
                SnakePainter.repaintSnake(frame,troll,food);                        
                SnakePainter.repaintSnake(frame,snake,food);                
                boolean win=(askuser.getUserX()*askuser.getUserY()-troll.getSize()
                             <=snake.getSize());    
                while(!win && (food.isOnSnake(snake) || food.isOnSnake(troll))) {
                    food.jump(grid);
                }
                SnakePainter.repaintFood(frame,food);    
                inform();
            } else {
                SnakePainter.repaintWholeSnake(frame,troll);
                SnakePainter.repaintSnake(frame,troll,food);
                SnakePainter.repaintSnake(frame,snake,food);
            }
            //limit gamespeed
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {}           
        }
        //game over
        snake.setHeadColor(Color.red);
        SnakePainter.repaintSnake(frame,snake,food);           
        ScoreBoard score=new ScoreBoard(frame, snake.getSize());            
    }
}