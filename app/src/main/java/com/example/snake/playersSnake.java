package com.example.snake;

import android.graphics.Point;

public class playersSnake {

    int [] map;
    // Snake parts ----------------------------
    int tail = 4;
    Point head = new Point(0,3);
    int numSquaresHorizontal;
    int numSquaresVertical;

    boolean turningLeft = false;
    boolean turningRight = false;
    int speedX = 1;
    int speedY = 0;
    int[] dir = {-1,-1,1,1};
    int direction = 2;
    boolean iFoundFood =false;
    // End od snake parts ----------------------

    public playersSnake(int [] _map, int gameWidth,int gameHeight){
        map = _map;
        numSquaresHorizontal = gameWidth;
        numSquaresVertical = gameHeight;
    }

    public void rightPressed(){
        turningRight = true;
    }
    public void leftPressed(){
        turningLeft = true;
    }

    public boolean haveYouFoundFood(){
        return iFoundFood;
    }

    public void thereIsNewFoodToFind(){
        iFoundFood=false;
    }

    public void movesnake(){
        if (!(turningLeft&&turningRight)){
            if (turningRight){ // if the snake is turning left
                direction++;
                if (direction>3)direction=0;
                if(direction%2>0){
                    speedX =0;
                    speedY=dir[direction];
                }else{speedY=0;
                    speedX =dir[direction];}
            }
            else{
                if (turningLeft){
                    direction--;
                    if (direction<0)
                        direction=3;
                    if (direction%2>0){
                        speedX =0;speedY=dir[direction];}else{speedY=0;
                        speedX =dir[direction];}
                }
            }
        }
        turningLeft = false;
        turningRight = false;
        head = new Point(head.x+ speedX,head.y+speedY);

        if (head.x>numSquaresHorizontal-1) {
            head.x = 0;
        }if (head.x<0)
            head.x = numSquaresHorizontal-1;
        if (head.y>numSquaresVertical-1)
            head.y =0;
        if (head.y<0)
            head.y = numSquaresVertical-1;
    }


   public void detectCollision( int [] map,int food){


       int headHitA=map[(head.y*numSquaresHorizontal)+head.x];
        if (headHitA>0){
            if (headHitA==food) {
                growSnake(1);
                iFoundFood=true;
            }else {
                reStart();
            }
        }
        map[(head.y*numSquaresHorizontal)+head.x] = tail;
    }

    public void growSnake(int num){
        tail+=num;
    }

    void reStart(){

    }
}
