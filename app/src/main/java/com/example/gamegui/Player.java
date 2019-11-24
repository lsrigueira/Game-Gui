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
    static final int pair = 1;
    static final int doublepair = 2;
    static final int trio = 3;
    static final int straight = 4;
    static final int flush = 5;
    static final int full_house = 6;
    static final int poker = 7;
    static final int straight_flush = 8;
    static final int royal_flush = 9;


    static final int castToBestPlay = 1000000;
    static final int castToBestCardInPlay = 10000;
    static final int castToWorstCardInPlay = 100;
    private Card card1;
    private Card card2;
    private ArrayList<Card> cartastot;
    private String nome;
    private boolean horizontal;
    private int money;
    private int moneybet=100;
    private long puntuacion;

    public Player(String nome, int money){
        this.nome=nome;
        this.money=money;
        cartastot= new ArrayList<>();
    }

    public int getMoney(){
        return this.money;
    }

    public int getBet(){
        return this.moneybet;
    }

    public void win(int money){
        this.money +=money;
    }

    public void loose(int money){
        this.money -= money;
    }

    public void setcards(Card c1, Card c2){
        this.card1 = c1;
        this.card2 = c2;
        this.cartastot.add(c1);
        this.cartastot.add(c2);
        this.horizontal = horizontal;
    }

    //Pasase asi para saber nun futuro a IA de onde proveñen as cartas, se tes poker pero está na mesa hai menos prob de ganar
    public int hasPair() {
        int puntos = 0;
        //Se tiveesemos duas parexas esto sería "doble parexa" e non estariamos aqui
        for(int contador=0;contador<cartastot.size();contador++){
            Card tocompare =cartastot.get(contador);
            int i=0;
            for(i=0;i<cartastot.size();i++){
                Card comparable = cartastot.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getRank().equals(tocompare.getRank())){
                    puntos=pair*castToBestPlay+comparable.getRankValue()*castToBestCardInPlay;;
                    System.out.println(this.getname()+":ENCONTRADA PAREXA-->"+puntos);
                    return puntos;
                }
            }
        }
        return 0; //TODO
    }


    public int hasDoblePair() {
        int firstpairnumber = 0;
        int puntos = 0;
        //Se tivesemos duas parexas esto sería "doble parexa" e non estariamos aqui
        for(int contador=0;contador<cartastot.size();contador++){
            Card tocompare =cartastot.get(contador);
            int i=0;
            for(i=0;i<cartastot.size();i++){
                Card comparable = cartastot.get(i);//Cambiamnos ese if para evitar falsas dobles parexas
                if(comparable.getId().equals(tocompare.getId()) || comparable.getRankValue()== firstpairnumber )  continue;
                else if(comparable.getRank().equals(tocompare.getRank())){
                    if(firstpairnumber == 0){
                        firstpairnumber=comparable.getRankValue();
                    }else {
                        puntos = doublepair*castToBestPlay;
                        switch (((Integer) comparable.getRankValue()).compareTo(firstpairnumber) ){
                            case -1://firstpair>comparable
                                puntos+=firstpairnumber*castToBestCardInPlay+comparable.getRankValue()*castToWorstCardInPlay;
                                break;
                            case 0://firstpai=comparabñe
                                puntos+=firstpairnumber*castToBestCardInPlay+firstpairnumber*castToWorstCardInPlay;
                                break;
                            case 1://comparable>firstpairnumber
                                puntos +=comparable.getRankValue()*castToBestCardInPlay+firstpairnumber*castToWorstCardInPlay;
                                break;
                            default:
                                System.out.println("ALGO RARO PASA NO DOUBLE PAIR COMPARANDO");
                        }
                        System.out.println(this.getname()+":ENCONTRADA DOBLE PAREXA-->"+puntos);
                        return puntos;
                    }
                }
            }
        }
        return 0; //TODO
    }

    public int hastrio(){
        boolean foundbefore;
        int puntos =0;
        for(int contador=0;contador<cartastot.size();contador++){
            foundbefore = false;
            Card tocompare =cartastot.get(contador);
            int i=0;
            for(i=0;i<cartastot.size();i++){
                Card comparable = cartastot.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getRank().equals(tocompare.getRank())){
                    if(foundbefore){
                        puntos+=trio*castToBestPlay+comparable.getRankValue()*castToBestCardInPlay;
                        System.out.println(this.getname()+":ENCONTRADO TRIO-->"+puntos);
                        return puntos;}
                    else foundbefore=true;
                }
            }
        }
        return 0;
    }

    public int haspoker(){
        int foundbefore;
        int puntos =0;
        for(int contador=0;contador<cartastot.size();contador++){
            foundbefore = 0;
            Card tocompare =cartastot.get(contador);
            int i=0;
            for(i=0;i<cartastot.size();i++){
                Card comparable = cartastot.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getRank().equals(tocompare.getRank())){

                    if(foundbefore == 2){
                        puntos+=poker*castToBestPlay+comparable.getRankValue()*castToBestCardInPlay;
                        System.out.println(this.getname()+":ENCONTRADO POKER-->"+puntos);
                        return puntos;
                    }else foundbefore++;
                }
            }
        }
        return 0;
    }

    public int hasflush(){
        int foundbefore=0;
        int puntos =0;
        for(int contador=0;contador<cartastot.size();contador++){
            foundbefore =0;
            Card tocompare =cartastot.get(contador);
            int i=0;
            for(i=0;i<cartastot.size();i++){
                Card comparable = cartastot.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getSuit().equals(tocompare.getSuit())){
                    if(foundbefore == 3){
                        puntos+=flush*castToBestPlay;
                        System.out.println(this.getname()+":ENCONTRADO FLUSH-->"+puntos);
                        return puntos;}
                    else foundbefore++;
                }
            }
        }
        return 0;
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


    /*A puntuacion sera un int de formato xx-yy-yy-zz onde as letras solo indican o numero de dixistos e a orixe do calculo.Explicacion:
        XX: Indica se o xogar ten parexa,doble-parexa,trio...
        YY: Indica as cartas que usou para chegar ahí, non é o mesmo unha parexa de ases que de douses(hai 2 para distinguir as doble-parexas)
        ZZ: Indica a carta mais alta que non se empregou para o cálculo de XX
     */

    public int calcularpuntuacion(){
        int puntos =hasflush();
        if(puntos!=0)return puntos;
        puntos=haspoker();
        if(puntos!=0)return puntos;
        puntos=hastrio();
        if(puntos!=0)return puntos;
        puntos=hasDoblePair();
        if(puntos !=0)return puntos;
        puntos=hasPair();
        if(puntos!=0)return puntos;
        return 0;
    }

}
