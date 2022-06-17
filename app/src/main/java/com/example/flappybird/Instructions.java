package com.example.flappybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Instructions extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

    }

    //button click to go back to menu
    public void onClick(View view) {
        Intent i = new Intent(this, MainActivity.class);        //go back to menu screen
        startActivity(i);
    }
}