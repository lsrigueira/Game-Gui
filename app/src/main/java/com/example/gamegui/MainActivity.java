package com.example.gamegui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button optionsbutton = (Button) findViewById(R.id.optionsbutton);
        optionsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOptions();
            }
        });

        Button playbutton = (Button) findViewById(R.id.playbutton);
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlay();
            }
        });

        setTitle("Poker Texas Holdem (PSI C)");
    }

    public void openOptions(){
        Intent intent = new Intent(this,Options.class);
        startActivity(intent);
    }

    public void openPlay(){
        Intent intent = new Intent(this,InGame.class);
        startActivity(intent);
    }

}
