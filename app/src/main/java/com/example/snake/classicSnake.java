package com.example.snake;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class classicSnake extends AppCompatActivity {

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Random rand = new Random();
    ImageView timerImageView;

    int widthGameSpace;
    int heightGameSpace;
    TextView textViewScore;
    Bitmap gameDisplayBitmap;
    int gameScale;
    int numSquaresHorizontal;
    int numSquaresVertical;
    int[] squares;
    Drawable GameMap;
    playersSnake player;
    int snakeFoodId = 1000000;
    int foodIsAt =0;
    int [] colourMappingArray;

    int speedIndex=0;
    int timerLength=315;
    int [] snakeSpeeds = new int[]{255,230,205,185,165,150,135,120,105,100,90,85,80,75,70,60,50,45,40};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_snake);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE| View.SYSTEM_UI_FLAG_FULLSCREEN);
        textViewScore = (TextView) findViewById(R.id.textViewScore);
        textViewScore.setTextColor(Color.YELLOW);
        setGameDisplay();

        player = new playersSnake(squares,numSquaresHorizontal,numSquaresVertical);
        newSnakeFood(squares);
        colourMappingArray = new int[squares.length];
        GameMap = new BitmapDrawable(getResources(), gameDisplayBitmap);
        GameMap.setFilterBitmap(false);
        timerImageView.setImageDrawable(GameMap);

        timerHandler.postDelayed(timerSCRunnable, 1000);
    }

    int score=0;
    int foodcount=1;
    void foodWasConsumed(){
        score= score+speedIndex+1;
        textViewScore.setText(""+score);
        foodcount++;
        if (foodcount>speedIndex){
            timerShorter();
            foodcount=0;
        }

    }

    void newSnakeFood(int [] map){
        int x = rand.nextInt(map.length);
        for (int i=0;i <map.length;i++){
            if (x>=map.length)x=0;
            if (map[x]==0){
                map[x]=snakeFoodId;
                foodIsAt=x;
                break;
            }
            x++;
        }
    }

    void timerShorter(){
        if (speedIndex<snakeSpeeds.length-1){
            speedIndex++;
            timerLength = snakeSpeeds[speedIndex];
        }
    }

    void updateTiles(){
        if (squares[foodIsAt]!=snakeFoodId){
            foodWasConsumed();
            newSnakeFood(squares);
            player.thereIsNewFoodToFind();
        }
        for (int i = 0; i< colourMappingArray.length; i++)
        {
            if (squares[i]%squares.length==0){
                squares[i]=0;
            }
            if (squares[i]>0){
                if (squares[i]<snakeFoodId) {
                   colourMappingArray[i] = Color.GREEN;
                   squares[i]-=1;
                }else {
                    if (squares[i]==snakeFoodId){
                        colourMappingArray[i]= Color.MAGENTA;
                    }
                }
            }
            if (squares[i]==0)
                colourMappingArray[i]= Color.BLACK;
        }
        gameDisplayBitmap.setPixels(colourMappingArray,0,numSquaresHorizontal,0,0,numSquaresHorizontal,numSquaresVertical);
    }
    Runnable timerSCRunnable = new Runnable() {

        @Override
        public void run() {
            player.movesnake();
            player.detectCollision(squares, snakeFoodId);
            updateTiles();
            timerImageView.invalidate();

            timerHandler.postDelayed(this, timerLength);
        }
    };



    void setGameDisplay(){
        timerImageView = findViewById(R.id.imageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthGameSpace = displayMetrics.widthPixels;
        heightGameSpace = displayMetrics.heightPixels;
        determineNumberOfGameSquares(displayMetrics.densityDpi);
        gameDisplayBitmap = Bitmap.createBitmap(numSquaresHorizontal,numSquaresVertical, Bitmap.Config.ARGB_8888);
        timerImageView.setImageBitmap(gameDisplayBitmap);


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
    public void onResume(){
        super.onResume();

        //timerHandler.postDelayed(timerSCRunnable, 200);
    }
    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerSCRunnable);
    }
    public void rightPressed(View view){
        player.rightPressed();;
    }
    public void leftPressed(View view){
        player.leftPressed();
    }
}
