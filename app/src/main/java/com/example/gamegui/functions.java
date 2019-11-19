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
                case "AS":
                    image.setImageResource(R.drawable.a);
                    break;
                case "KS":
                    image.setImageResource(R.drawable.sk);
                    break;
                case "QS":
                    image.setImageResource(R.drawable.sq);
                    break;
                case "JS":
                    image.setImageResource(R.drawable.sj);
                    break;
                case "10S":
                    image.setImageResource(R.drawable.s10);
                    break;
                case "9S":
                    image.setImageResource(R.drawable.s9);
                    break;
                case "8S":
                    image.setImageResource(R.drawable.s8);
                    break;
                case "7S":
                    image.setImageResource(R.drawable.s7);
                    break;
                case "6S":
                    image.setImageResource(R.drawable.s6);
                    break;
                case "5S":
                    image.setImageResource(R.drawable.s5);
                    break;
                case "4S":
                    image.setImageResource(R.drawable.s4);
                    break;
                case "3s":
                    image.setImageResource(R.drawable.s3);
                    break;
                case "2S":
                    image.setImageResource(R.drawable.s2);
                    break;
                case "AC":
                    image.setImageResource(R.drawable.ca);
                    break;
                case "CS":
                    image.setImageResource(R.drawable.ck);
                    break;
                case "QC":
                    image.setImageResource(R.drawable.cq);
                    break;
                case "JC":
                    image.setImageResource(R.drawable.cj);
                    break;
                case "10C":
                    image.setImageResource(R.drawable.c10);
                    break;
                case "9C":
                    image.setImageResource(R.drawable.c9);
                    break;
                case "8C":
                    image.setImageResource(R.drawable.c8);
                    break;
                case "7C":
                    image.setImageResource(R.drawable.c7);
                    break;
                case "6C":
                    image.setImageResource(R.drawable.c6);
                    break;
                case "5C":
                    image.setImageResource(R.drawable.c5);
                    break;
                case "4C":
                    image.setImageResource(R.drawable.c4);
                    break;
                case "3C":
                    image.setImageResource(R.drawable.c3);
                    break;
                case "2C":
                    image.setImageResource(R.drawable.c2);
                    break;


                case "AD":
                    image.setImageResource(R.drawable.da);
                    break;
                case "CD":
                    image.setImageResource(R.drawable.dk);
                    break;
                case "QD":
                    image.setImageResource(R.drawable.dq);
                    break;
                case "JD":
                    image.setImageResource(R.drawable.dj);
                    break;
                case "10":
                    image.setImageResource(R.drawable.d10);
                    break;
                case "9D":
                    image.setImageResource(R.drawable.d9);
                    break;
                case "8D":
                    image.setImageResource(R.drawable.d8);
                    break;
                case "7D":
                    image.setImageResource(R.drawable.d7);
                    break;
                case "6D":
                    image.setImageResource(R.drawable.d6);
                    break;
                case "5D":
                    image.setImageResource(R.drawable.d5);
                    break;
                case "4D":
                    image.setImageResource(R.drawable.d4);
                    break;
                case "3D":
                    image.setImageResource(R.drawable.d3);
                    break;
                case "2D":
                    image.setImageResource(R.drawable.d2);
                    break;


                case "AH":
                    image.setImageResource(R.drawable.ha);
                    break;
                case "CH":
                    image.setImageResource(R.drawable.hk);
                    break;
                case "QH":
                    image.setImageResource(R.drawable.hq);
                    break;
                case "JH":
                    image.setImageResource(R.drawable.hj);
                    break;
                case "10H":
                    image.setImageResource(R.drawable.h10);
                    break;
                case "9H":
                    image.setImageResource(R.drawable.h9);
                    break;
                case "8H":
                    image.setImageResource(R.drawable.h8);
                    break;
                case "7H":
                    image.setImageResource(R.drawable.h7);
                    break;
                case "6H":
                    image.setImageResource(R.drawable.h6);
                    break;
                case "5H":
                    image.setImageResource(R.drawable.h5);
                    break;
                case "4H":
                    image.setImageResource(R.drawable.h4);
                    break;
                case "3H":
                    image.setImageResource(R.drawable.h3);
                    break;
                case "2H":
                    image.setImageResource(R.drawable.d2);
                    break;


            }
            image.setVisibility(View.VISIBLE);

    }

}
