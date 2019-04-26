package com.example.snake;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Random rand = new Random();
    ImageView timerImageView;

    // Gamespace Size info
    int widthGameSpace;
    int heightGameSpace;
    TextView textViewScore;
    Bitmap gameSpace;
    int gameScale;
    int numSquaresHorizontal;
    int numSquaresVertical;
    int[] squares;
    Drawable GameMap;

    snakeBots botSnakes;

    // Snake parts ----------------------------
    int tail = 4;
    Point head = new Point(0,3);
    int snakeColor = Color.GREEN;
    int snakeFoodId = 1000000;
    boolean turningLeft = false;
    boolean turningRight = false;
    int speedx = 1;
    int speedY = 0;
    int[] dir = {-1,-1,1,1};
    int direction = 2;
    // End od snake parts ----------------------

    void movesnake(){
        if (!(turningLeft&&turningRight)){
            if (turningRight){ // if the snake is turning left
                direction++;
                if (direction>3)direction=0;
                if(direction%2>0){
                    speedx=0;
                    speedY=dir[direction];
                }else{speedY=0;speedx=dir[direction];}
                }
            else{
                if (turningLeft){
                direction--;
                if (direction<0)
                    direction=3;
                if (direction%2>0){speedx=0;speedY=dir[direction];}else{speedY=0;speedx=dir[direction];}
                }
            }
        }
        turningLeft = false;
        turningRight = false;
        head = new Point(head.x+speedx,head.y+speedY);

        if (head.x>numSquaresHorizontal-1) {
            head.x = 0;
        }if (head.x<0)
            head.x = numSquaresHorizontal-1;
        if (head.y>numSquaresVertical-1)
            head.y =0;
        if (head.y<0)
            head.y = numSquaresVertical-1;
    }
int xYToOne(int x, int y,int SquaresHorizontal){
        return (y*SquaresHorizontal)+x;
}

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();



    boolean iFoundFood =false;
    void detectCollision(int headLocation, int [] map,int food){
        int headHitA=map[headLocation];

            if (headHitA>0){
                if (headHitA==food) {
                    growSnake();
                    iFoundFood=true;
                }else {
                    reStart();
                }
            }
        map[headLocation] = tail;
    }

    void reStart(){
        Toast.makeText(this, "You died, Score: "+score,
                Toast.LENGTH_LONG).show();
        for (int i=0;i<squares.length;i++){
            squares[i]=0;
        }
        speedIndex=1;
        timerLonger();
        subtractFromScore(0);
        tail=4;
        head.x=0;
        head.y=3;
        speedx=1;
        speedY=0;
        direction = 2;
        botSnakes.startOver();
    }

    int score =0;
    void updateScore(){
        score= score+(speedIndex*botSnakes.getBotSnakeCount());
        textViewScore.setText(" "+score);
    }
    void subtractFromScore(int amount){
        score= score-amount;
        textViewScore.setText(" "+score);
    }

    void splitSnakeOff(int lengthOfNewSNake, int [] map){
        if (lengthOfNewSNake>3){
        boolean  validNewSnake=false;
        int newSnakeLocation =-1;
        for (int i =0;i<map.length;i++){
            if (map[i]==lengthOfNewSNake){
                newSnakeLocation = i;
                validNewSnake=true;
            }
            if(map[i]>lengthOfNewSNake){
                map[i]-=lengthOfNewSNake;
            }
        }
        if (validNewSnake)
        {
            tail = tail- lengthOfNewSNake;
            botSnakes.newBotSnake(newSnakeLocation,lengthOfNewSNake);
        }
        }
    }
    void growSnake(){
        if (botSnakes.getBotSnakeCount()<1&&tail>7){
            splitSnakeOff(tail/2,squares);
        }
        tail++;

        updateScore();
        speedIncrease();
        newSnakeFood(squares);
    }
    void speedIncrease(){
        timerShorter();
    }
    int howManyTimesHasPlayerMissedTheFood =0;
    void newSnakeFood(int [] map){
        if (!iFoundFood){


            howManyTimesHasPlayerMissedTheFood++;
            if(howManyTimesHasPlayerMissedTheFood>100){
                howManyTimesHasPlayerMissedTheFood=0;
                subtractFromScore(10);
                timerLonger();
            }
        }
        iFoundFood=false;
        int x = rand.nextInt(map.length);
        for (int i=0;i <map.length;i++){
            if (x>=map.length)x=0;
            if (map[x]==0){

                map[x]=snakeFoodId;
                foodIsAt=x;
                botSnakes.tellBotsWhereTheFoodIs(x);
                break;
            }
            x++;

        }

    }
    int speedIndex=0;
    int [] snakeSpeeds = new int[]{350,280,220,165,140,120,105,100,90,85,80,75,70,60,50,30,20,};
    int timerLength=350;
    void timerShorter(){
        if (speedIndex<snakeSpeeds.length-1){
            speedIndex++;
            timerLength = snakeSpeeds[speedIndex];
        }
    }
    void timerLonger(){
        if (speedIndex>=0){
            speedIndex--;
            timerLength = snakeSpeeds[speedIndex];
        }
    }
    int foodIsAt =0;
    int [] magenta;
    void updateTiles(){


        if (squares[foodIsAt]!=snakeFoodId){
            newSnakeFood(squares);
            botSnakes.tellBotsWhereTheFoodIs(foodIsAt);
        }
        for (int i=0;i<magenta.length;i++)
        {
            if (squares[i]>0){
                if (squares[i]<snakeFoodId) {
                    magenta[i] = Color.GREEN;
                    squares[i]-=1;
                }else {
                    if (squares[i]==snakeFoodId){
                        magenta[i]= Color.MAGENTA;
                    }
                }
            }
            if (squares[i]==0)
                magenta[i]= Color.BLACK;

        }

        gameSpace.setPixels(magenta,0,numSquaresHorizontal,0,0,numSquaresHorizontal,numSquaresVertical);
    }
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {


            movesnake();
            detectCollision(xYToOne(head.x,head.y,numSquaresHorizontal), squares, snakeFoodId);
            botSnakes.moveBotSnakes();
            updateTiles();
            timerImageView.invalidate();

            timerHandler.postDelayed(this, timerLength);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE| View.SYSTEM_UI_FLAG_FULLSCREEN);
        textViewScore = (TextView) findViewById(R.id.textViewScore);
        textViewScore.setTextColor(Color.YELLOW);
        setGameDisplay();
        botSnakes = new snakeBots(squares,numSquaresHorizontal,numSquaresVertical,-1);
        newSnakeFood(squares);
        magenta= new int[squares.length];

        //mag.setPixels(magenta,0,numSquaresHorizontal,0,0,numSquaresHorizontal,numSquaresVertical);
        GameMap = new BitmapDrawable(getResources(), gameSpace);
        GameMap.setFilterBitmap(false);
        timerImageView.setImageDrawable(GameMap);
        timerHandler.postDelayed(timerRunnable, 0);


    }

    void setGameDisplay(){
        timerImageView = findViewById(R.id.imageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthGameSpace = displayMetrics.widthPixels;
        heightGameSpace = displayMetrics.heightPixels;

        determineNumberOfGameSquares(displayMetrics.densityDpi);
        gameSpace = Bitmap.createBitmap(numSquaresHorizontal,numSquaresVertical, Bitmap.Config.ARGB_8888);
        timerImageView.setImageBitmap(gameSpace);


    }

    void determineNumberOfGameSquares(int pixlesPerInch){

        int tilesPerInch = 8;
        int onThisDisplayWeWantThisManyTiles;


        if (widthGameSpace >heightGameSpace){ onThisDisplayWeWantThisManyTiles = widthGameSpace /(pixlesPerInch/tilesPerInch);
            gameScale = widthGameSpace /onThisDisplayWeWantThisManyTiles ;
        }
        else{ onThisDisplayWeWantThisManyTiles = heightGameSpace/(pixlesPerInch/tilesPerInch);
            gameScale = heightGameSpace/onThisDisplayWeWantThisManyTiles;}

        numSquaresVertical = heightGameSpace/gameScale;
        numSquaresHorizontal = widthGameSpace /gameScale;
        squares = new int[numSquaresHorizontal*numSquaresVertical];

    }
    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }
    public void rightPressed(View view){
        turningRight = true;
    }
    public void leftPressed(View view){
        turningLeft = true;
    }
}