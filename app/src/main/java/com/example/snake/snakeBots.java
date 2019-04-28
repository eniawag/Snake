package com.example.snake;

import java.util.HashSet;
import java.util.Set;

public class snakeBots {
    int [] map;
    int botSnakeCount=0;
    int foodIsAt;
    int botSnakesOffset=0;
    int numSquaresHorizontal;
    int numSquaresVertical;
    int [] botSnakes; // <--- The head location and size of other snakes that are in play will be stored in here
    public  snakeBots(int [] _map, int gameWidth,int gameHeight,int foodLocation){
        map =_map;
        numSquaresHorizontal = gameWidth;
        numSquaresVertical = gameHeight;
        foodIsAt =foodLocation;
        botSnakes = new int[_map.length];
        for (int i=0;i<botSnakes.length;i++)
            botSnakes[i] = -1;
        botSnakesOffset= botSnakes.length/3;
    }
    public void startOver(){
        botSnakes = new int[botSnakes.length];
        botSnakeCount=0;
        for (int i=0;i<botSnakes.length;i++)
            botSnakes[i] = -1;
    }

    public void tellBotsWhereTheFoodIs(int x){
        foodIsAt = x;
    }

    void newBotSnake(int location,int length){
        botSnakeCount++;
        int i =0;
        for (;i<botSnakesOffset;i++){
            if (botSnakes[i]==-1)
                break;;
        }
        botSnakes[i]=location;
        botSnakes[i+botSnakesOffset]= tailMod(length);
    }
    int tailMod(int length){
        return ((1+botSnakeCount)*map.length)+ length;
    }

    public void moveBotSnakes(){
        for (int i=0;i<botSnakesOffset&&botSnakes[i]>=0;i++){

            chooseBotMoveSnake(botSnakes[i],i);
        }

    }

    public int getBotSnakeCount(){
        return botSnakeCount;
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
        if (squ<map.length-numSquaresHorizontal){ return squ+numSquaresHorizontal;
        }return squ%numSquaresHorizontal;
    }

    int getSquareAbove(int squ){
        if (squ>=numSquaresHorizontal){ return squ-numSquaresHorizontal;
        }return map.length-numSquaresHorizontal+squ;
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
            if (map[getSquareAbove(location)]== 0){ placesToMove.add(getSquareAbove(location));}
            if (map[getSquareBelow(location)]==0){placesToMove.add(getSquareBelow(location));}
            if (map[getSquareLeft(location)]==0){placesToMove.add(getSquareLeft(location));}
            if (map[getSquareRight(location)]==0){placesToMove.add(getSquareRight(location));}
        }

    }

    int movePreferenceEuclideanDistance(int location, int movementType){

        int xLocation = (location%numSquaresHorizontal)+1;
        int foodX = (foodIsAt%numSquaresHorizontal)+1;
        int yLocation = (location/numSquaresHorizontal)+1;
        int foodY = (foodIsAt/numSquaresHorizontal)+1;

        boolean rightIsPreferableToLeft = false;
        boolean downIsPreferableToUp =false;
        int distanceToFoodXPlane;
        int distanceToFoodYPlane;

        if (xLocation<foodX){
            distanceToFoodXPlane = foodX-xLocation;
            rightIsPreferableToLeft=true;
        }else {
            distanceToFoodXPlane = xLocation-foodX;
        }
        if (yLocation<foodY){
            distanceToFoodYPlane = foodY-yLocation;
            downIsPreferableToUp=true;
        }else {
            distanceToFoodYPlane = yLocation-foodY;
        }
        if (!(movementType==2)){
            if (distanceToFoodXPlane>numSquaresHorizontal/2){
                if (rightIsPreferableToLeft){
                    rightIsPreferableToLeft= false;
                    distanceToFoodXPlane = numSquaresHorizontal-foodX+xLocation;
                }else{
                    rightIsPreferableToLeft=true;
                    distanceToFoodXPlane = numSquaresHorizontal-xLocation+foodX;
                }
            }

            if (distanceToFoodYPlane>numSquaresVertical/2){
                if (downIsPreferableToUp){
                    downIsPreferableToUp= false;
                    distanceToFoodYPlane = numSquaresVertical-foodY+yLocation;
                }else{
                    downIsPreferableToUp=true;
                    distanceToFoodYPlane = numSquaresVertical-yLocation+foodY;
                }
            }}

        if (movementType==0){
            if (distanceToFoodXPlane>0){
                if (rightIsPreferableToLeft){
                    return getSquareRight(location);
                }
                return getSquareLeft(location);
            }else{
                if (downIsPreferableToUp){
                    return getSquareBelow(location);
                }

                return getSquareAbove(location);
            }
        }else{
            if (distanceToFoodXPlane>=distanceToFoodYPlane){
                if (rightIsPreferableToLeft){
                    return getSquareRight(location);
                }
                return getSquareLeft(location);
            }else{
                if (downIsPreferableToUp){
                    return getSquareBelow(location);
                }

                return getSquareAbove(location);
            }
        }

    }

    int lookAheadAFewMoves(int location, int numberOfLayersStillToLookAt){
        Set<Integer> placesToMove = new HashSet<>();
        return lookAheadAFewMoves(location, numberOfLayersStillToLookAt,0,placesToMove);

    }
    int lookAheadAFewMoves(int location, int numberOfLayersStillToLookAt,int numberOfLayersLookedAt, Set<Integer> placesToMove){
        if (numberOfLayersStillToLookAt==0
                || map[location]>numberOfLayersLookedAt
                || placesToMove.contains(location))
        {
            return 0;
        }

        placesToMove.add(location);
        int tileCounter=1;

        tileCounter += lookAheadAFewMoves(getSquareAbove(location),numberOfLayersStillToLookAt-1,numberOfLayersLookedAt+1,placesToMove);
        tileCounter += lookAheadAFewMoves(getSquareBelow(location),numberOfLayersStillToLookAt-1,numberOfLayersLookedAt+1,placesToMove);
        tileCounter += lookAheadAFewMoves(getSquareLeft(location),numberOfLayersStillToLookAt-1,numberOfLayersLookedAt+1,placesToMove);
        tileCounter += lookAheadAFewMoves(getSquareRight(location),numberOfLayersStillToLookAt-1,numberOfLayersLookedAt+1,placesToMove);

        return tileCounter;// <--to do
    }

    void chooseBotMoveSnake(int location, int BSArrayPointer){

        Set<Integer> placesToMove = new HashSet<>();
        // if there is food nearby
        findMoves(location,2,placesToMove);
        // if there is a valid move
        if (placesToMove.size()>=1){
            if (placesToMove.size()==1){
                moveBotSnake(placesToMove.toArray(new Integer[1])[0],BSArrayPointer);
            }
            else
            {
                int aGoodMOve = movePreferenceEuclideanDistance(location,botSnakes[BSArrayPointer+botSnakesOffset]%2);

                if (placesToMove.contains(aGoodMOve)
                        && lookAheadAFewMoves(aGoodMOve,10)>20)
                {
                    moveBotSnake(aGoodMOve, BSArrayPointer);
                }
                else
                {

                    Integer [] moves = placesToMove.toArray(new Integer[placesToMove.size()]);
                    int lengthofMove =0;
                    for (int i: moves)
                    {
                        int temp = lookAheadAFewMoves(i,10);
                        if (temp>lengthofMove){
                            lengthofMove=temp;
                            aGoodMOve = i;
                        }
                    }

                    moveBotSnake(aGoodMOve, BSArrayPointer);
                }
            }
        }else { // else there is no valid move so kill snake


            killBotSnake(BSArrayPointer);
        }

    }
    void moveBotSnake(int newLocation,int BSArrayPointer){
        botSnakes[BSArrayPointer] =newLocation;
        map[newLocation]=botSnakes[BSArrayPointer+botSnakesOffset];
        if (newLocation==foodIsAt){
            botSnakes[BSArrayPointer+botSnakesOffset]++;
        }
    }
    void killBotSnake(int BSArrayPointer){
        botSnakeCount =0;
        int i =BSArrayPointer;
        for (;i<botSnakesOffset-1;i++){
            if (botSnakes[i+1]==-1)break;
        }
        botSnakes[BSArrayPointer] = botSnakes[i];
        botSnakes[BSArrayPointer+botSnakesOffset]= botSnakes[i+botSnakesOffset];
        botSnakes[i]=-1;

    }

}
