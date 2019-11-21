package com.example.gamegui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Player {

    private Card card1;
    private Card card2;
    private ArrayList<Card> cartastot;
    private int Xposition;
    private int Yposition;
    private String nome;
    private boolean horizontal;
    private int money;
    private long puntuacion;

    public Player(int xpos, int ypos, boolean horizontal, String nome, int money){
        this.Xposition = xpos;
        this.Yposition = ypos;
        this.horizontal=horizontal;
        this.nome=nome;
        this.money=money;
        cartastot= new ArrayList<>();
    }

    public void setcards(Card c1, Card c2){
        this.card1 = c1;
        this.card2 = c2;
        this.cartastot.add(c1);
        this.cartastot.add(c2);
        this.horizontal = horizontal;
    }

    //Pasase asi para saber nun futuro a IA de onde proveñen as cartas, se tes poker pero está na mesa hai menos prob de ganar
    public static String hasPair(ArrayList<Card> cards) {

        //Se tiveesemos duas parexas esto sería "doble parexa" e non estariamos aqui
        for(int contador=0;contador<cards.size();contador++){
            Card tocompare =cards.get(contador);
            int i=0;
            for(i=0;i<cards.size();i++){
                   Card comparable = cards.get(i);
                   if(comparable.getId().equals(tocompare.getId()))continue;
                   else if(comparable.getRank().equals(tocompare.getRank())){
                       System.out.println("ENCONTRADA PAREXA");
                       return comparable.getRank();
                   }
            }
        }
        return "false"; //TODO
    }

    public void newCarta(Card carta){
        this.cartastot.add(carta);
    }
    public void clearCartaMesa(){
        this.cartastot = new ArrayList<>();
    }

    public Card getcard1(){
        return this.card1;
    }

    public Card getcard2(){
        return  this.card2;
    }

    public String getname(){
        return this.nome;
    }

    public void calcularpuntuacion(){
        System.out.println(hasPair(this.cartastot));
    }

}
