package com.example.gamegui;

import android.media.Image;

import java.util.TreeMap;

public class Card {

    private String id;
    private String name;
    private String suit;
    private String rank;

    public Card(String id){

        this.id = id;

        if(id.length() == 2) {
            this.suit = String.valueOf(id.charAt(1));
            this.rank = String.valueOf(id.charAt(0));
        }
        else if(id.length() == 3) {
            this.suit = String.valueOf(id.charAt(2));
            this.rank = id.substring(0,1);
        }

        String rank_name = "";
        String suit_name = "";
        switch(this.suit){
            case "C":
                suit_name = "clubs";
                break;
            case "D":
                suit_name = "diamonds";
                break;
            case "H":
                suit_name = "hearts";
                break;
            case "S":
                suit_name = "spades";
                break;
        }

        switch (this.rank){
            case"A":
                suit_name = "Ace";
                break;
            case"2":
                suit_name = "Two";
                break;
            case"3":
                suit_name = "Three";
                break;
            case"4":
                suit_name = "Four";
                break;
            case"5":
                suit_name = "Five";
                break;
            case"6":
                suit_name = "Six";
                break;
            case"7":
                suit_name = "Seven";
                break;
            case"8":
                suit_name = "Eight";
                break;
            case"9":
                suit_name = "Nine";
                break;
            case"10":
                suit_name = "Ten";
                break;
            case"J":
                suit_name = "Jack";
                break;
            case"Q":
                suit_name = "Queen";
                break;
            case"K":
                suit_name = "King";
                break;
        }

        this.name = suit_name + " of " + rank_name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }
}
