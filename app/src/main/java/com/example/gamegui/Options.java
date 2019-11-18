package com.example.gamegui;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import java.io.IOException;

public class Options extends AppCompatActivity {

    MediaPlayer player = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        /*ToggleButton musica = (ToggleButton) findViewById(R.id.musicbutton);

        try{
            player.setDataSource("195.154.182.222:25223/live.mp3");

            /*player.setAudioAttributes (new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            player.prepareAsync();
        }catch(IOException e){
            e.printStackTrace();
        }

        musica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton musica, boolean isChecked) {
                if (musica.isChecked()) {
                    player.start();
                } else {
                    player.reset();
                }
            }
        });*/


    }



}
