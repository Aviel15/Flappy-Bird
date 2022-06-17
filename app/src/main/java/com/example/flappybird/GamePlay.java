package com.example.flappybird;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GamePlay extends AppCompatActivity {
    //properties
    private FrameLayout frameLayout;            //frameLayout
    private MySurfaceView mySurfaceView;        //mySurfaceView

    private int speed;                          //the speed of bird

    private TextView tvScore;                   //textView of score in current game
    private TextView tvBestScore;               //textView of best score
    private int score = 0;                      //count of score
    private int bestScore = 0;                  //the best score
    private SharedPreferences sp;               //shared preferences, in charge of to save the best score

    private Handler myHandler;                  //in charge of to send message between classes

    private Button btnPlay;                     //button to play again - when game over
    private Button btnGoBack;                   //button to go back to menu - when game over

    //short sounds
    private SoundPool soundPool;                //sound pool
    private int voiceGetScore;                  //voice when get score
    private int voiceGameOver;                  //voice when game over

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        //get ID
        frameLayout = findViewById(R.id.frameLayout);
        tvBestScore = findViewById(R.id.tvBestScore);
        tvScore = findViewById(R.id.tvScore);
        btnGoBack = findViewById(R.id.button6);
        btnPlay = findViewById(R.id.button);

        btnGoBack.setVisibility(View.GONE);         //hide the buttons
        btnPlay.setVisibility(View.GONE);
        btnPlay.setEnabled(false);                  //does not detect click
        btnGoBack.setEnabled(false);

        myHandler = new MyHandler();                //create handler

        sp = getSharedPreferences("details", MODE_PRIVATE);       //shared preference, set also the defaults
        bestScore = sp.getInt("record", 0);                        //get the value from "record", there save the record of score

        tvScore.setText("Score: 0");                                    //Initializing the score
        tvBestScore.setText("Best Score: " + bestScore);                //update the best score to variable

        Intent intent = getIntent();                                    //get intent
        Bundle data = intent.getExtras();
        if (data != null) {
            speed = data.getInt("speed");                           //update to speed the value in key "speed"
        }


        //check which sdk the user has and create the right version of SoundPool
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)  // max num of items is 10 simultaneously
                    .setAudioAttributes(aa)
                    .build();
        }
        else
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,1);     //set up to 10 audio stream simultaneously, the audio quality is 1

        this.voiceGameOver = soundPool.load(this,R.raw.gameover,1);              //load the voices
        this.voiceGetScore = soundPool.load(this,R.raw.getscore,1);
    }



    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mySurfaceView==null){
            int speedPole = speed/2;                                                             //the speed of pole
            mySurfaceView = new MySurfaceView(this, speed, speedPole, myHandler);         //create object of mySurfaceView
            frameLayout.addView(mySurfaceView);                                                  //addView to frameLayout
        }
    }

    //Enter - view
    //Exit  - none
    //on Click of buttons
    public void onClick(View view) {
        if(view == btnPlay)                         //play again and hide again the buttons
        {
            mySurfaceView.setScore(0);              //update the score to 0
            tvScore.setText("Score: 0" );
            mySurfaceView.setSaveScore(0);
            mySurfaceView.btnStart();               //call to method btnStart
            btnGoBack.setVisibility(View.GONE);     //hide the buttons and can't to detect click
            btnPlay.setVisibility(View.GONE);
            btnPlay.setEnabled(false);
            btnGoBack.setEnabled(false);
        }
        else {                                      //go back to menu
            Intent i = new Intent(this, MainActivity.class);      //Intent to go back to MainActivity class
            startActivity(i);
        }
    }

    @Override
    //Option menu
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        Intent i = new Intent(this, MainActivity.class);        //intent to MainActivity
        switch (id) {
            case R.id.goback:                                                //options menu, if click on him will go back to menu
                mySurfaceView.stopGame();
                startActivity(i);
                break;
        }
        return true;
    }

    //inner class
    public class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            score = msg.getData().getInt("score");                                      //set the score value that exist in "score" message
            boolean dontResetScore = msg.getData().getBoolean("soundGameOver");         //help that when game over, the score of current game will not reset
            if(!dontResetScore)
                tvScore.setText("Score: " + score);                                         //update on the screen

            if(score > bestScore)                                                           //if the record is broken
            {
                //edit the file, add some key-value data to save
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("record",score);                //update the new best score
                editor.commit();                                 //only when committed the file will be saved.

                bestScore = score;                               //update the new record
                tvBestScore.setText("Best Score: " + score);     //also update on the screen
            }

            boolean soundScore = msg.getData().getBoolean("soundScore");                        //the sound of score
            boolean soundGameOver = msg.getData().getBoolean("soundGameOver");                  //the sound of game over
            if(soundScore)                                                                          //if true will make the voice of score
            {
                soundScore = false;
                soundPool.play(voiceGetScore, 1, 1, 0, 0, 1);   //play the sound pool
            }
            if(soundGameOver)                                                                       //if true will make the voice of game over
            {
                soundGameOver = false;
                soundPool.play(voiceGameOver, 1, 1, 0, 0, 1);   //play the sound pool
                btnPlay.setVisibility(View.VISIBLE);                                                //visibility of buttons are true
                btnGoBack.setVisibility(View.VISIBLE);
                btnPlay.setEnabled(true);                                                           //can detect the click
                btnGoBack.setEnabled(true);
            }
        }
    }

}