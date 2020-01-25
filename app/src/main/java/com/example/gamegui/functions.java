package com.example.gamegui;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


public class functions {

    public static MediaPlayer mp;
    public static int permitirmusica;

    static int DEBUGLEVEL = 1;

    public static ArrayList<Card> nueva_baraja() {
        ArrayList<Card> cartasenbaraja = new ArrayList<>();
        String[] letras = {"S", "H", "C", "D"};
        String[] numeros = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (int contador1 = 0; contador1 < letras.length; contador1++)
            for (int contador2 = 0; contador2 < numeros.length; contador2++)
                cartasenbaraja.add(new Card(String.format("%s", numeros[contador2] + letras[contador1])));

        return cartasenbaraja;
    }

    public static void enseñar_carta(ImageView image, String carta) {

        switch (carta) {
            case "reverso":
                image.setImageResource(R.drawable.reverso);
                break;
            case "invisible":
                image.setVisibility(View.INVISIBLE);
                break;
            case "AS":
                image.setImageResource(R.drawable.sa);
                break;
            case "2S":
                image.setImageResource(R.drawable.s2);
                break;
            case "3S":
                image.setImageResource(R.drawable.s3);
                break;
            case "4S":
                image.setImageResource(R.drawable.s4);
                break;
            case "5S":
                image.setImageResource(R.drawable.s5);
                break;
            case "6S":
                image.setImageResource(R.drawable.s6);
                break;
            case "7S":
                image.setImageResource(R.drawable.s7);
                break;
            case "8S":
                image.setImageResource(R.drawable.s8);
                break;
            case "9S":
                image.setImageResource(R.drawable.s9);
                break;
            case "10S":
                image.setImageResource(R.drawable.s10);
                break;
            case "JS":
                image.setImageResource(R.drawable.sj);
                break;
            case "QS":
                image.setImageResource(R.drawable.sq);
                break;
            case "KS":
                image.setImageResource(R.drawable.sk);
                break;
            case "AH":
                image.setImageResource(R.drawable.ha);
                break;
            case "2H":
                image.setImageResource(R.drawable.h2);
                break;
            case "3H":
                image.setImageResource(R.drawable.h3);
                break;
            case "4H":
                image.setImageResource(R.drawable.h4);
                break;
            case "5H":
                image.setImageResource(R.drawable.h5);
                break;
            case "6H":
                image.setImageResource(R.drawable.h6);
                break;
            case "7H":
                image.setImageResource(R.drawable.h7);
                break;
            case "8H":
                image.setImageResource(R.drawable.h8);
                break;
            case "9H":
                image.setImageResource(R.drawable.h9);
                break;
            case "10H":
                image.setImageResource(R.drawable.h10);
                break;
            case "JH":
                image.setImageResource(R.drawable.hj);
                break;
            case "QH":
                image.setImageResource(R.drawable.hq);
                break;
            case "KH":
                image.setImageResource(R.drawable.hk);
                break;
            case "AC":
                image.setImageResource(R.drawable.ca);
                break;
            case "2C":
                image.setImageResource(R.drawable.c2);
                break;
            case "3C":
                image.setImageResource(R.drawable.c3);
                break;
            case "4C":
                image.setImageResource(R.drawable.c4);
                break;
            case "5C":
                image.setImageResource(R.drawable.c5);
                break;
            case "6C":
                image.setImageResource(R.drawable.c6);
                break;
            case "7C":
                image.setImageResource(R.drawable.c7);
                break;
            case "8C":
                image.setImageResource(R.drawable.c8);
                break;
            case "9C":
                image.setImageResource(R.drawable.c9);
                break;
            case "10C":
                image.setImageResource(R.drawable.c10);
                break;
            case "JC":
                image.setImageResource(R.drawable.cj);
                break;
            case "QC":
                image.setImageResource(R.drawable.cq);
                break;
            case "KC":
                image.setImageResource(R.drawable.ck);
                break;
            case "AD":
                image.setImageResource(R.drawable.da);
                break;
            case "2D":
                image.setImageResource(R.drawable.d2);
                break;
            case "3D":
                image.setImageResource(R.drawable.d3);
                break;
            case "4D":
                image.setImageResource(R.drawable.d4);
                break;
            case "5D":
                image.setImageResource(R.drawable.d5);
                break;
            case "6D":
                image.setImageResource(R.drawable.d6);
                break;
            case "7D":
                image.setImageResource(R.drawable.d7);
                break;
            case "8D":
                image.setImageResource(R.drawable.d8);
                break;
            case "9D":
                image.setImageResource(R.drawable.d9);
                break;
            case "10D":
                image.setImageResource(R.drawable.d10);
                break;
            case "JD":
                image.setImageResource(R.drawable.dj);
                break;
            case "QD":
                image.setImageResource(R.drawable.dq);
                break;
            case "KD":
                image.setImageResource(R.drawable.dk);
                break;

            default:
                System.out.println("NON SE ATOPOU A CARTA");
        }
        image.setVisibility(View.VISIBLE);

    }


    public static int maximo(Integer[] array) {
        int maximo = Integer.MIN_VALUE;
        for (int contador = 0; contador < array.length; contador++) {
            if (array[contador] > maximo) {
                maximo = array[contador];
            }
        }
        return maximo;
    }

    public static void imprimirdebug(String paraimprimir, int nivel) {

        if (nivel <= DEBUGLEVEL) {
            System.out.println(paraimprimir);
        }
    }

    public static void cashflow(ArrayList<Player> jugadores, int indexganador) {
        int totalmoneybet = 0;
        int monebet = 0;
        for (int contador = 0; contador < jugadores.size(); contador++) {
            if (contador == indexganador) continue;
            else {
                monebet = jugadores.get(contador).getBet();
                jugadores.get(contador).loose(monebet);
                totalmoneybet += monebet;
            }
        }
        jugadores.get(indexganador).win(totalmoneybet);
    }


    public static void play(Context a, String cancion){
        try{
            mp.stop();
        }catch (Exception e){System.out.println("Non habia musca inicializada");}
        if(functions.permitirmusica == 1);
        switch (cancion){
            case "eye_of_the_tiger":
                mp = MediaPlayer.create(a,R.raw.eye_of_the_tiger);
                mp.start();
                break;
            case "perdiste":
                System.out.println("AQUIAQUI");
                mp = MediaPlayer.create(a,R.raw.perdiste);
                mp.start();;
                break;
             default:
                System.out.println("AQUIAQUI");
                mp = MediaPlayer.create(a,R.raw.perdiste);
                mp.start();;
                break;

        }

    }

    public static void enseñar_carta(Player x) {
        /*switch (x.getname()){
            case "player1":
                functions.enseñar_carta((ImageView)findViewById(R.id.player1card1),"invisible");
                functions.enseñar_carta((ImageView)findViewById(R.id.player1card2),"invisible");
                break;
            case "player2":
                functions.enseñar_carta((ImageView)findViewById(R.id.player1card1),"invisible");
                functions.enseñar_carta((ImageView)findViewById(R.id.player1card2),"invisible");
                break;
            case "player3":
                functions.enseñar_carta((ImageView)findViewById(R.id.player1card1),"invisible");
                functions.enseñar_carta((ImageView)findViewById(R.id.player1card2),"invisible");
                break;
        }*/
    }

}