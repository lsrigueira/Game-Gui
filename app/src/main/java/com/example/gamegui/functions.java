package com.example.gamegui;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class functions {


    public static void enseñar_carta(ImageView image, String carta){

            switch (carta){
                case "reverso":
                    image.setImageResource(R.drawable.reverso);
                    break;
                case "corazones_10":
                image.setImageResource(R.drawable.corazones_10);
                    break;
                case "corazones_5":
                    image.setImageResource(R.drawable.corazones_5);
                    break;
                case "corazones_7":
                    image.setImageResource(R.drawable.corazones_7);
                    break;
                case "corazones_8":
                    image.setImageResource(R.drawable.corazones_8);
                    break;
                case "corazones_9":
                    image.setImageResource(R.drawable.corazones_9);
                    break;
                case "corazones_as":
                    image.setImageResource(R.drawable.corazones_as);
                    break;
                case "corazones_k":
                    image.setImageResource(R.drawable.corazones_k);
                    break;
                case "corazones_q":
                    image.setImageResource(R.drawable.corazones_q);
                    break;
                case "diamantes_10":
                    image.setImageResource(R.drawable.diamantes_10);
                    break;
                case "diamantes_5":
                    image.setImageResource(R.drawable.diamantes_5);
                    break;
                case "diamantes_7":
                    image.setImageResource(R.drawable.diamantes_7);
                    break;
                case "diamantes_8":
                    image.setImageResource(R.drawable.diamantes_8);
                    break;
                case "diamantes_9":
                    image.setImageResource(R.drawable.diamantes_9);
                    break;
                case "diamantes_as":
                    image.setImageResource(R.drawable.diamantes_as);
                    break;
                case "diamantes_k":
                    image.setImageResource(R.drawable.diamantes_k);
                    break;
                case "diamantes_q":
                    image.setImageResource(R.drawable.diamantes_q);
                    break;
                case "picas_10":
                    image.setImageResource(R.drawable.picas_10);
                    break;
                case "picas_7":
                    image.setImageResource(R.drawable.picas_7);
                    break;
                case "picas_8":
                    image.setImageResource(R.drawable.picas_8);
                    break;
                case "picas_9":
                    image.setImageResource(R.drawable.picas_9);
                    break;
                case "picas_as":
                    image.setImageResource(R.drawable.picas_as);
                    break;
                case "picas_k":
                    image.setImageResource(R.drawable.picas_k);
                    break;
                case "picas_q":
                    image.setImageResource(R.drawable.picas_q);
                    break;
                case "treboles_5":
                    image.setImageResource(R.drawable.treboles_5);
                    break;
                case "treboles_7":
                    image.setImageResource(R.drawable.treboles_7);
                    break;
                case "treboles_8":
                    image.setImageResource(R.drawable.treboles_8);
                    break;
                case "treboles_9":
                    image.setImageResource(R.drawable.treboles_9);
                    break;
                case "treboles_as":
                    image.setImageResource(R.drawable.treboles_as);
                    break;
                case "treboles_k":
                    image.setImageResource(R.drawable.treboles_k);
                    break;
                case "treboles_q":
                    image.setImageResource(R.drawable.treboles_q);
                    break;
            }
            image.setVisibility(View.VISIBLE);

    }

}
