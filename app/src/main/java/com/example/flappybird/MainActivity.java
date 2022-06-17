package com.example.flappybird;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //properties
    private Button btnInstructions;             //button to go to instructions activity
    private Button btnLevel;                    //button to dialog of choosing level
    private Button btnStart;                    //button to start the game
    private int speedGame = 0;                  //the speed of game - relate to level of game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get ID
        btnInstructions = findViewById(R.id.instructions);
        btnLevel = findViewById(R.id.btnLevel);
        btnStart = findViewById(R.id.btnStart);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)          //ask permission to broadcast receiver - calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
    }

    //on Click of buttons
    public void onClick(View view) {
        if (view == btnInstructions)
        {
            Intent intent = new Intent(this, Instructions.class);   //if click on buttons to go to instructions screen
            startActivity(intent);
        }
        if(view == btnLevel)                                                     //if click on buttons to choose level of game - will open the dialog
        {
            Dialog dialog = chooseLevel();
            dialog.show();                                                       //show the "choose level" dialog
        }
        if(view == btnStart)                                                     //if click on buttons to start the game
        {
            if(speedGame != 0)                                                   //if chose level to game
            {
                Intent i = new Intent(this, GamePlay.class);        //move to GamePlay class
                i.putExtra("speed", speedGame);
                startActivity(i);
            }
            else {
                Toast.makeText(this, "You must choose level to your game!", Toast.LENGTH_SHORT).show();         //show text on the screen
            }
        }
    }

    //The dialog of choosing level
    private Dialog chooseLevel()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // נגדיר בנאי לתיבה
        builder.setTitle("Choose level of game:"); // נקבע את כותרת התיבה
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() //נגדיר מימוש לממשק
        {

            @Override
            public void onClick(DialogInterface dialog, int which) // נממש את הפעולה onclick
            {
                switch (which)
                {
                    case 0:
                        speedGame = 6;       //level 1
                        break;
                    case 1:
                        speedGame = 8;       //level 2
                        break;
                    case 2:
                        speedGame = 10;      //level 3
                        break;
                    case 3:
                        speedGame = 12;      //level 4
                        break;
                    case 4:
                        speedGame = 15;      //level 5
                        break;
                }
                dialog.dismiss();
            }
        };

        CharSequence[] colors_items = {"one" , "two" , "three", "four", "five"};
        builder.setSingleChoiceItems(colors_items, -1, listener); // נקבע את רשימת האיברים שיוצגו ברשימה ונבצע רישום למאזין
        builder.setCancelable(false); // נבטל את אפשרות ביטול התיבה
        builder.setIcon(android.R.drawable.ic_menu_help); // נקבע את צלמית התיבה
        return builder.create(); // ניצור ונחזיר את הדיאלוג שהכנו בבנאי התיבות
    }
}