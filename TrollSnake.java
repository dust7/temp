import java.util.Random;
import java.awt.Color;
import java.util.ArrayList;

class TrollSnake extends Snake {
    Random rand=new Random();
    
    public TrollSnake(Grid grid) {
        super(grid);
        int quartsqx=grid.getSQX()/4;
        int quartsqy=grid.getSQY()/4;
        for(int i=1; i<=quartsqy; i++) {
            moveLeft();
        }
        boolean rb=rand.nextBoolean();           
        if(rb) {
            for(int i=1; i<rand.nextInt(quartsqx); i++) {
                moveUp();
            }
        } else {
            for(int i=1; i<rand.nextInt(quartsqx); i++) {
                moveDown();
            }
        }
        this.setHeadColor(new Color(255,96,0));
        this.setBodyColor(new Color(255,215,0));
    }
    
    private boolean[] evalMove(Grid grid, int where) {
        Snake dummy=new Snake((Snake)this);
        if(where==0) {
            dummy.moveUp();            
        } else if(where==1) {
            dummy.moveDown();            
        } else if(where==2) {
            dummy.moveLeft();            
        } else if(where==3) {
            dummy.moveRight();            
        }        
        boolean[] eval=new boolean[2];
        eval[0]=dummy.bites();        
        eval[1]=dummy.isInDeadzone(grid);        
        return eval;
    }
    
    private void move(int i) {
        if(i==0) {
            moveUp();            
        } else if(i==1) {
            moveDown();            
        } else if(i==2) {
            moveLeft();            
        } else if(i==3) {
            moveRight();            
        }
    }
    
    public void randMove(Grid grid) {
        ArrayList<Integer> directions=new ArrayList<Integer>(4);
        ArrayList<Integer> forcedDir=new ArrayList<Integer>(4);
        for(int i=0; i<=3; i++) {
            directions.add(i);
        }        
        boolean moved=false;
        while(directions.size()>=1 && !moved) {
            int chosen=directions.get(rand.nextInt(directions.size()));            
            boolean[] evalChosen=evalMove(grid,chosen);
            if(!evalChosen[0] && !evalChosen[1]) {
                move(chosen);                
                moved=true;                
            } else {
                directions.remove(directions.indexOf(chosen));
                if(evalChosen[0] && !evalChosen[1]) {
                    forcedDir.add(chosen);
                }
            }            
        }
        if(!moved) {
            move(forcedDir.get(rand.nextInt(forcedDir.size())));            
        }
    }
}