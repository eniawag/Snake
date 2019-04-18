package com.example.snake;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    int [] botSnakes; // <--- The head location and size of other snakes that are in play will be stored in here
    int botSnakeColour = Color.RED;

    // Snake parts ----------------------------
    int tail = 30;
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
/*    void cutTail(){
        cutTail = tail.get(0);
        squares[xYToOne(cutTail.x,cutTail.y)]=0;
        tail.remove(0);
        drawSquareAdapter(gameSpace,cutTail.x,cutTail.y,(int)drawScaleX,(int)drawScaleY,Color.BLACK);

    }*/
int xYToOne(int x, int y){
        return (y*numSquaresHorizontal)+x;

}

    float drawScaleX;
    float drawScaleY;
    void drawSnake(){

        drawSquareAdapter(gameSpace,head.x,head.y,(int)drawScaleX,(int)drawScaleY,snakeColor);


    }
    void drawSquareAdapter(Bitmap b, int x, int y, int width, int height, int color){
        int xC =(int) (x*drawScaleX);
        int yC =(int)(y*drawScaleY);
        int wS = width;
        int wh = height;
        if (x<rx){
            xC = xC+x;
            wS = wS +1;
        }else{xC = xC + rx;}
        if (y<ry){
            yC = yC +y;
            wh = wh +1;
        }else{yC = yC + ry;}
        drawSquare(b,xC,yC,wS,wh,color);

    }
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();

    void drawSquare(Bitmap b, int x, int y, int width, int height, int color){
        for (int i =0;i<width;i++)
        {
            for (int k=0;k<height;k++){
                saferSetPixle(b,i+x,k+y,color);
            }
        }

    }
    void saferSetPixle(Bitmap b, int x, int y, int color){
        if (x<b.getWidth()&& x>=0&& y<b.getHeight()&& y>=0){
            b.setPixel(x,y,color);
        }
    }

    void detectCollision(){
        int headHitA=squares[xYToOne(head.x,head.y)];

            if (headHitA>0){
                if (headHitA==snakeFoodId) {
                    growSnake();
                }
            }
        squares[xYToOne(head.x,head.y)] = tail;
    }
    void endGame(){

    }

    int score =0;
    int bonus=1;
    void updateScore(){
        score= score+bonus;
        textViewScore.setText(" "+score);
    }
    void growSnake(){
        tail++;
        updateScore();
        speedIncrease();
        newSnakeFood();
    }
    void speedIncrease(){
        timerShorter();
    }
    void printArray(int [] array){
        String s = ")-";
        for (int i =0; i<array.length;i++)
            s = s +" "+array[i];
        System.out.println(s);
    }
    void newSnakeFood(){

        int x = rand.nextInt(squares.length);
        for (int i=0;i <squares.length;i++){
            if (x>=squares.length)x=0;
            if (squares[x]==0){

                squares[x]=snakeFoodId;
                updateSquare(x,Color.WHITE);
                foodIsAt=x;
                break;
            }
            x++;

        }

    }
    void updateSquare(int squ, int Colour){

        int x = squ% numSquaresHorizontal;
        int y = squ/numSquaresHorizontal;
        drawSquareAdapter(gameSpace,x,y,(int)drawScaleX,(int)drawScaleY,Colour);

    }
    int timerLength=100;
    void timerShorter(){
        if (timerLength>20){
            timerLength=timerLength - timerLength/5;
        }else timerLength=50;

    }

    int foodIsAt =0;
    int Colour= Color.MAGENTA;
    void updateTiles(){

        for (int i =0;i<squares.length;i++){

            if (squares[i]>0){
                if (squares[i]<snakeFoodId){
                    if (squares[i]==1){
                        updateSquare(i,Color.BLACK);
                    }
                    squares[i]-=1;
                }
            }
        }


        // Redraw Food:
       /* int x = foodIsAt% numSquaresHorizontal;
        int y = foodIsAt/numSquaresHorizontal;
        drawSquareAdapter(gameSpace,x,y,(int)drawScaleX,(int)drawScaleY,Colour);*/
    }
    int botSnakesOffset=0;
    void newBotSnake(int location,int length){
        int i =0;
        for (;i<botSnakesOffset;i++){
            if (botSnakes[i]==-1)
                break;;
        }
        botSnakes[i]=location;
        botSnakes[i+botSnakesOffset]= length;
    }

    void moveBotSnakes(){
        for (int i=0;i<botSnakesOffset&&botSnakes[i]>=0;i++){

            chooseBotMoveSnake(botSnakes[i],i);
        }

    }


    //region Methods for finding up Down left and right squares Tested and working As Expected
    int getSquareRight(int squ){
        if (squ%numSquaresHorizontal==numSquaresHorizontal-1){ return squ-numSquaresHorizontal+1;
        }return squ+1;
    }

    int getSquareLeft(int squ){
        if (squ%numSquaresHorizontal==0){ return squ+numSquaresHorizontal-1;
        }return squ-1;
    }

    int getSquareBelow(int squ){
        if (squ<squares.length-numSquaresHorizontal){ return squ+numSquaresHorizontal;
        }return squ%numSquaresHorizontal;
    }

    int getSquareAbove(int squ){
        if (squ>=numSquaresHorizontal){ return squ-numSquaresHorizontal;
        }return squares.length-numSquaresHorizontal+squ;
    }
    //endregion


    boolean findFood(int location, Set<Integer> placesToMove){
        if (getSquareAbove(location)== foodIsAt){ placesToMove.add(getSquareAbove(location));
            return true;}
        if (getSquareBelow(location)==foodIsAt){placesToMove.add(getSquareBelow(location));
            return true;}
        if (getSquareLeft(location)==foodIsAt){placesToMove.add(getSquareLeft(location));
            return true;}
        if (getSquareRight(location)==foodIsAt){placesToMove.add(getSquareRight(location));
            return true;}
        return false;
    }
    void findMoves(int location, int depth, Set<Integer> placesToMove){
        if (findFood( location, placesToMove)){

        } else{
            if (squares[getSquareAbove(location)]== 0){ placesToMove.add(getSquareAbove(location));}
            if (squares[getSquareBelow(location)]==0){placesToMove.add(getSquareBelow(location));}
            if (squares[getSquareLeft(location)]==0){placesToMove.add(getSquareLeft(location));}
            if (squares[getSquareRight(location)]==0){placesToMove.add(getSquareRight(location));}
        }

    }
    void chooseBotMoveSnake(int location, int BSArrayPointer){
        System.out.println("--- running : " +location);
        Set<Integer> placesToMove = new HashSet<>();
        // if there is food nearby
        findMoves(location,2,placesToMove);
        // if there is a valid move
        if (placesToMove.size()>=1){
            if (placesToMove.size()==1){
                moveBotSnake(placesToMove.toArray(new Integer[1])[0],BSArrayPointer);
            }else{
                moveBotSnake(placesToMove.toArray(new Integer[placesToMove.size()])[rand.nextInt(placesToMove.size())],BSArrayPointer);
            }
        }else { // else there is no valid move so kill snake
            System.out.println("--- Killed : " +placesToMove.size());

            killBotSnake(BSArrayPointer);
        }

    }
    void moveBotSnake(int newLocation,int BSArrayPointer){
        botSnakes[BSArrayPointer] =newLocation;
        squares[newLocation]=botSnakes[BSArrayPointer+botSnakesOffset];
        if (newLocation==foodIsAt){
            botSnakes[BSArrayPointer+botSnakesOffset]++;
            newSnakeFood();
        }
        updateSquare(newLocation,botSnakeColour);
    }
    void killBotSnake(int BSArrayPointer){
        int i =BSArrayPointer;
        for (;i<botSnakesOffset-1;i++){
            if (botSnakes[i+1]==-1)break;
        }
        botSnakes[BSArrayPointer] = botSnakes[i];
        botSnakes[BSArrayPointer+botSnakesOffset]= botSnakes[i+botSnakesOffset];
        botSnakes[i]=-1;

    }


    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            updateTiles();
            movesnake();
            detectCollision();
            drawSnake();
            moveBotSnakes();
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
        botSnakes = new int[squares.length];
        for (int i=0;i<botSnakes.length;i++)
            botSnakes[i] = -1;
        botSnakesOffset= botSnakes.length/2;

        newBotSnake(100,10);
        newBotSnake(200,6);
        newBotSnake(200,5);
        newBotSnake(200,4);
        newSnakeFood();


        timerHandler.postDelayed(timerRunnable, 0);


    }
    int rx;
    int ry;
    void setGameDisplay(){
        timerImageView = findViewById(R.id.imageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthGameSpace = displayMetrics.widthPixels;
        heightGameSpace = displayMetrics.heightPixels;
        gameSpace = Bitmap.createBitmap(widthGameSpace,heightGameSpace, Bitmap.Config.ARGB_8888);
        timerImageView.setImageBitmap(gameSpace);



        determineNumberOfGameSquares(displayMetrics.densityDpi);
        drawScaleX = widthGameSpace/numSquaresHorizontal;
        drawScaleY = heightGameSpace/numSquaresVertical;
        rx = widthGameSpace%(int)drawScaleX;
        ry = heightGameSpace%(int)drawScaleY;

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
