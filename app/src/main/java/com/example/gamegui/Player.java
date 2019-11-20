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
    private ArrayList<Card> cartasmesa;
    private int Xposition;
    private int Yposition;
    private String nome;
    private boolean horizontal;
    private int money;

    public Player(int xpos, int ypos, boolean horizontal, String nome, int money){
        this.Xposition = xpos;
        this.Yposition = ypos;
        this.horizontal=horizontal;
        this.nome=nome;
        this.money=money;
        cartasmesa= new ArrayList<>();
    }

    public void setcards(Card c1, Card c2){
        this.card1 = c1;
        this.card2 = c2;
        this.horizontal = horizontal;
    }

    public void newCartaMesa(Card carta){
        this.cartasmesa.add(carta);
    }
    public void clearCartaMesa(){
        this.cartasmesa = new ArrayList<>();
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

}
