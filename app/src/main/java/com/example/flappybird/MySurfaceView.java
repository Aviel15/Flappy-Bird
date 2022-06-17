package com.example.flappybird;

import static android.graphics.Rect.intersects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView {
    private SurfaceHolder surfaceHolder;                                    //holder
    private DrawingThread drawingThread;                                    //drawing thread
    private Handler myHandler;                                              //in charge of to send message between classes

    private Bitmap[] bitmapsPoles, bitmapsBird;
    private int[] bitmapBird = {R.drawable.bird, R.drawable.bird2};         //bitmaps of bird
    private int[] bitmapPoles = {R.drawable.pole1, R.drawable.pole2, R.drawable.pole3, R.drawable.pole4, R.drawable.pole5};         //bitmaps of pole

    private Bird birdThread;                                                //thread of bird
    private Pole poleThread;                                                //thread of pole

    private boolean running = true;                                         //running, until this true the game will run, else the game over
    private Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);               //in charge of to paint the bitmaps of bird and pole

    private int speedPole, speed;                                           //the speed of pole and bird

    private int score = 0, saveScore = 0;                                   //score at current game, need variable saveScore because after increase bt one the score I stop the increasing
    private int bestScore = 0;                                              //the best score

    //constructor
    public MySurfaceView(Context context, int speed, int speedPole, Handler myHandler) {
        super(context);
        this.speedPole = speedPole;                                     //the speed of pole
        this.speed = speed;                                             //the speed of bird

        this.myHandler = myHandler;                                     //myHandler

        surfaceHolder = getHolder();                                    //get Holder

        //update the bitmaps
        this.bitmapsBird = new Bitmap[2];                               //create arrays of type Bitmap, them length is 2
        Bitmap[] bmpBird = new Bitmap[2];

        //bitmaps for bird
        for (int i = 0; i < bitmapsBird.length; i++) {
            bmpBird[i] = BitmapFactory.decodeResource(this.getResources(), bitmapBird[i]);
            bitmapsBird[i] = Bitmap.createScaledBitmap(bmpBird[i], 105, 80, true);
        }

        //update the bitmaps to poles
        this.bitmapsPoles = new Bitmap[5];
        Bitmap[] bmpPoles = new Bitmap[5];

        for (int i = 0; i < bitmapsPoles.length; i++) {
            bmpPoles[i] = BitmapFactory.decodeResource(this.getResources(), bitmapPoles[i]);
            bitmapsPoles[i] = Bitmap.createScaledBitmap(bmpPoles[i], 150, 2000, true);
        }

        //create object of type Pole
        poleThread = new Pole(bitmapsPoles, speedPole);
        poleThread.start();

        //create object of type Bird
        birdThread = new Bird(bitmapsBird, speed);
        birdThread.start();

        //run the drawing thread:
        drawingThread = new DrawingThread();
        drawingThread.start();
    }

    //Enter - MotionEvent event
    //Exit  - return true everytime
    //check touch with screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            birdThread.jump();
        return false;
    }

    //Enter - get the objects of bird and pole
    //Exit  - return true if happened a touch, else return false
    //check if happened a touch between bird to pole, check if game over
    private boolean checkTouch(Bird bird, Pole pole) {
        boolean res;
        res = intersects(bird.getRect(), pole.getRect());
        if (res)
            res = check(bird, pole);
        return res;
    }

    //Enter - get bird and pole
    //Exit  - return true if there is touch between bird and pole, using method isFilled
    private boolean check(Bird bird, Pole pole) {
        //lion’s & strawberry’s position at intersection:
        int birdX= bird.getX();
        int birdY= bird.getY();
        int poleX = pole.getX();
        int poleY = pole.getY();

        //build intersect rectangle:
        int left = Math.max(birdX, poleX); //find left coordinate

        int right = Math.min(birdX+birdThread.getWidth(), poleX+ poleThread.getWidth());//find right coordinate
        int top = Math.max(birdY, poleY);//find top coordinate
        int bottom = Math.min(birdY+birdThread.getHeight(), poleY+poleThread.getHeight());//find bottom coordinate    //go over all pixels in the intersect rectangle and check if not transparent in both bitmaps:
        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                if (bird.isFilled(x-birdX,y-birdY) && pole.isFilled(x-poleX,y-poleY)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Enter - get the score
    //Exit  - none
    //send message with using handler, the messages is score, best score, and sound of score
    private void updateScore()
    {
        //send message
        Message msg = myHandler.obtainMessage();
        msg.getData().putInt("score",score);
        msg.getData().putInt("bestscore", bestScore);
        msg.getData().putBoolean("soundScore", true);            //sent message that game over and then will sound of game over
        myHandler.sendMessage(msg);
    }

    //Enter - none
    //Exit  - none
    //When exit from the game, all characters on game is stop
    public void stopGame()
    {
        birdThread.setKilled();
        poleThread.setStop();
    }

    //Enter - none
    //Exit  - none
    //button to start again the game
    public void btnStart()
    {
        poleThread = new Pole(bitmapsPoles, speedPole);
        poleThread.start();

        birdThread = new Bird(bitmapsBird, speed);
        birdThread.start();

        //run the drawing thread:
        drawingThread = new MySurfaceView.DrawingThread();
        running = true;
        drawingThread.start();
    }

    //set score
    public void setScore(int score)
    {
        this.score = score;
    }

    //set save score
    public void setSaveScore(int score)
    {
        this.saveScore = score;
    }


    //inner class- DrawingThread
    private class DrawingThread extends Thread {
        public void run() {           //method run
            while (running) {         //run while running is true
                if(birdThread.getPlace() > 1700 || checkTouch(birdThread, poleThread))              //if come too much low or there is a touch with pole
                {
                    running = false;                        //game over
                    drawSurfaceViewGameOver();
                }

                else {                                      //if the game is continued
                    drawSurfaceView();
                    if(poleThread.getX() + 50 < birdThread.getX() && score <= saveScore)            //if the bird moved the pole will add a one score
                    {
                        score++;
                        updateScore();
                    }
                    if(poleThread.getX() < -212)            //if pole come to end of screen he will go back to start position, the right side, 212 - the width of pole
                    {
                        poleThread.setFirstPosition();      //go to start position
                        saveScore++;
                    }
                }
                SystemClock.sleep(30);                 //wait for 30 milliseconds
            }
        }

        //Enter - none
        //Exit  - none
        //draw bird and poles on canvas
        private void drawSurfaceView()
        {
            if (surfaceHolder.getSurface().isValid())                                                   //check if can lock
            {
                Canvas canvas = surfaceHolder.lockCanvas();                                             //lock canvas
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);       //bitmap of background
                Rect r = new Rect(0,0,1100,2050);                                  //the size
                canvas.drawBitmap(bmp,null,r,myPaint);
                birdThread.draw(canvas);                                                                //draw bird
                poleThread.draw(canvas);                                                                //draw pole
                surfaceHolder.unlockCanvasAndPost(canvas);                                              //unlock canvas
            }
        }

        //Enter - none
        //Exit  - none
        //draw bitmap of game over
        private void drawSurfaceViewGameOver()
        {
            if (surfaceHolder.getSurface().isValid())
            {                                                 //check if can lock
                Canvas canvas = surfaceHolder.lockCanvas();                                             //lock canvas
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gameend);          //bitmap of game over
                Rect r = new Rect(250,400,800,600);                                //the size
                canvas.drawBitmap(bmp,null,r,myPaint);
                gameOver();                                                                             //send message that game over
                surfaceHolder.unlockCanvasAndPost(canvas);                                              //unlock canvas
            }
        }

        //Enter - none
        //Exit  - none
        //send message that can voice the sound and visible the buttons, when game over
        private void gameOver()
        {
            Message msg1 = myHandler.obtainMessage();
            msg1.getData().putBoolean("soundGameOver", true);            //sent message that game over and then will sound of game over
            msg1.getData().putString("visibleBtn", "visibleBtn");        //sent message that game over and then will visible the buttons
            myHandler.sendMessage(msg1);                                 //send message
        }
    }
}



