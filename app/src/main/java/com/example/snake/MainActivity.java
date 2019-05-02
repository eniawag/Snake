package com.example.snake;

import android.content.Intent;
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


public class MainActivity extends AppCompatActivity {

    Handler timerHandler = new Handler();
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
    snakeBots botSnakes;
    int [] colourMappingArray;
    int snakeFoodId = 1000000;
    int foodIsAt =0;
    Random rand = new Random();

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            botSnakes.moveBotSnakes();
            if (botSnakes.getBotSnakeCount()<4){
                botSnakes.newBotSnake(rand.nextInt(squares.length-1),4+rand.nextInt(6));
            }
            if (squares[foodIsAt]!=snakeFoodId){
                newSnakeFood(squares);
                botSnakes.tellBotsWhereTheFoodIs(foodIsAt);

            }


            for (int i = 0; i< colourMappingArray.length; i++)
            {

                if (squares[i]%squares.length==0){
                    squares[i]=0;
                }
                if (squares[i]>0){
                    if (squares[i]<snakeFoodId) {

                        if (squares[i]>squares.length){
                            colourMappingArray[i]= Color.RED;
                            if (squares[i]/squares.length==3){
                                colourMappingArray[i]= Color.BLUE;
                            }
                            else{
                                if  (squares[i]/squares.length==4){
                                    colourMappingArray[i]= Color.GREEN;
                                }else{
                                    if  (squares[i]/squares.length==5)
                                        colourMappingArray[i]= Color.YELLOW;
                                }
                            }
                        }else {
                            colourMappingArray[i] = Color.GREEN;
                        }
                        squares[i]-=1;
                    }else {
                        if (squares[i]==snakeFoodId){
                            colourMappingArray[i]= Color.WHITE;
                        }
                    }
                }
                if (squares[i]==0)
                    colourMappingArray[i]= Color.BLACK;
            }
            gameDisplayBitmap.setPixels(colourMappingArray,0,numSquaresHorizontal,0,0,numSquaresHorizontal,numSquaresVertical);
            timerImageView.invalidate();

            timerHandler.postDelayed(this, 50);
        }
    };
    void newSnakeFood(int [] map){
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE| View.SYSTEM_UI_FLAG_FULLSCREEN);
        setGameDisplay();
        colourMappingArray = new int[squares.length];
        GameMap = new BitmapDrawable(getResources(), gameDisplayBitmap);
        GameMap.setFilterBitmap(false);
        timerImageView.setImageDrawable(GameMap);
        botSnakes = new snakeBots(squares,numSquaresHorizontal,numSquaresVertical,-1);
        timerHandler.postDelayed(timerRunnable, 200);
    }
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
    public void launchClassicSnake(View view) {
        Intent intent = new Intent(this,classicSnake.class);

        startActivity(intent);
    }
    public void launchShadowSnake(View view) {
        Intent intent = new Intent(this,shadowSnake.class);

        startActivity(intent);
    }
    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }
    @Override
    public void onResume(){
        super.onResume();

        timerHandler.postDelayed(timerRunnable, 200);
    }
}