package com.example.flappybird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

public class Bird extends Thread{
    //properties
    private Rect rect;                      //rect, use for check if happened a touch with pole or floor
    private boolean killed = false;         //if game over - killed = true, else killed = false

    private Bitmap[] bitmaps;               //array of bitmap use when draw the bird
    private int bmp_index = 1;              //use for know which bitmap need, 1/2

    private int x = 300, y = 900;           //the place on the screen of bird
    private int width, height;              //the size of the bird
    private int dy;                         //the move that the bird make every run

    //constructor
    public Bird(Bitmap[] bitmaps, int speed)
    {
        rect = new Rect();                      //create the rect
        this.dy = speed;                        //get the speed in accordance to level of game

        this.bitmaps = bitmaps;                 //update the bitmap

        //update the size of bird
        width = bitmaps[0].getWidth();
        height = bitmaps[0]. getHeight();
        rect.set(x,y,width+x,height+y);
    }

    @Override
    //Enter - none
    //Exit - none
    //His purpose is move the bird while the bird alive
    public void run(){
        while(!killed)                                               //run while killed is false
        {
            bmp_index = (bmp_index+1) % 2;                           //change bird's bitmap to make animation

            y += this.dy;                                            //add dy to place on the screen
            rect.set(x, y, width + x, height + y);       //update the rect every run
            if(y > 1700)                                             //if come too much low to floor - game over
                killed = true;
            SystemClock.sleep(30);                               //wait for 30 milliseconds, also give to other threads run
        }
    }

    //Enter - none
    //Exit - none
    //His purpose is make a jump, keep him on limits of screen
    public void jump() {
        if(y > 120)                             //if he lower than ceiling of screen - width 105 + 15 pixels for see the space
            for (int i = 0; i < 5; i++)         //for feel good and prolonged jump
            {
                y -= this.dy * 3;               //jump up
                SystemClock.sleep(30);      //wait for 30 milliseconds, also give to other threads run
            }
    }

    //Enter - get canvas
    //Exit  - none
    //Drawing the bird on screen
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(getBitmaps()[bmp_index], x,y, null);
    }

    //get the bitmap, return the bitmap of bird, when call to this method need call with bmp_index
    public Bitmap[] getBitmaps()
    {
        return this.bitmaps;
    }

    //get place
    public int getPlace()
    {
        return this.y;
    }

    //get rect
    public Rect getRect()
    {
        return rect;
    }

    //get x position
    public int getX()
    {
        return this.x;
    }

    //set killed
    public void setKilled()
    {
        this.killed = true;
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