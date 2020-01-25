package com.example.gamegui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.ExecutionException;

public class Options extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        final Button musicButton = (Button) findViewById(R.id.musicbutton);

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicButton.getText().equals("MUSIC OFF")){
                    try{
                        functions.mp.stop();
                    }catch (Exception e){
                        System.out.println("Non habia musica reproducindose");
                    }
                    functions.permitirmusica = 0;
                }else{
                    functions.permitirmusica = 1;
                }
            }
        });

    }


}