package com.example.flappybird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.SystemClock;

import java.util.Random;

public class Pole extends Thread {
    //properties
    private Rect rect;                   //rect, use for check if happened a touch with bird
    private Random rnd;                  //random for bitmaps
    private int randomNum;

    private int x = 1000, y = 0;         //the place on the screen of pole
    private int dx;                      //the move that the pole make every run
    private Bitmap[] bitmaps;            //array of bitmap for all poles
    private int width, height;           //the size of pole
    private int bmp_index = 0;           //bmp_index for bitmaps, use when draw the pole

    private boolean stop = true;         //running while the player play the game

    //constructor
    public Pole(Bitmap[] bitmaps, int speedPole){
        rnd = new Random();                     //create random
        rect = new Rect();                      //create rect

        randomNum = rnd.nextInt(4);     //make a random, between 0-4

        this.dx = speedPole;                   //the speed that pole will move
        this.bitmaps = bitmaps;                //update the bitmap

        //resize the size of poles
        width = bitmaps[0].getWidth();
        height = bitmaps[0].getHeight();
        rect.set(x,y,width+x,height+y);
    }

    @Override
    //Enter - none
    //Exit - none
    //his purpose is move the pole while the bird alive
    public void run() {
        while(stop)
        {
            this.bmp_index = randomNum;                                 //update the index according to random number
            x -= dx;                                                    //move the pole dx pixels left
            rect.set(x, y, width + x, height + y);          //update the rect
            if (x < -212)                                               //the width of pole, if come to end of screen, on left side
                randomNum = rnd.nextInt(4);                       //make a random again
            SystemClock.sleep(30);                                  //wait for 30 milliseconds, also give to other threads run
        }
    }

    //Enter - get canvas
    //Exit  - none
    //Drawing the pole on screen
    public void draw (Canvas canvas)
    {
        canvas.drawBitmap(getBitmap(),x,y,null);
    }

    //get bitmap with current bitmap
    public Bitmap getBitmap()
    {
        return bitmaps[bmp_index];
    }

    //get x position
    public int getX()
    {
        return this.x;
    }

    //set stop to false
    public void setStop()
    {
        this.stop = false;
    }

    //Enter - none
    //Exit - none
    //go back to start position if come to end of screen
    public void setFirstPosition()
    {
        this.x = 1100;
    }

    //get rect
    public Rect getRect() {
        return rect;
    }

    //get y
    public int getY() {
        return this.y;
    }

    //get width
    public int getWidth() {
        return this.width;
    }

    //get height
    public int getHeight() {
        return this.height;
    }

    //Enter - get to integer numbers
    //Exit  - return true if there is touch between players
    public boolean isFilled(int row, int col)
    {
        return bitmaps[bmp_index].getPixel(row, col)!= Color.TRANSPARENT;
    }

}