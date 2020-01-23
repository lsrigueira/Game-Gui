package com.example.gamegui;


import androidx.annotation.NonNull;

public class Card {

    private String id;
    private String suit;
    private String rank;
    private String posicion;
    private boolean used;

    public Card(String id) {
        this.id = id;
        if (id.length() == 2) {
            this.suit = String.valueOf(id.charAt(1));
            this.rank = String.valueOf(id.charAt(0));
        } else if (id.length() == 3) {
            this.suit = String.valueOf(id.charAt(2));
            this.rank = id.substring(0, 2);
        }
    }

    public void setUsed(boolean x){
        this.used = x;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public boolean getUsed(){
        return this.used;
    }

    public String getPosicion(){
        return this.posicion;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return this.toString(); //Kept for backwards compatibility
    }

    public String getSuit() {

        return suit;
    }

    public String getRank() {

        return rank;
    }

    @NonNull
    @Override
    public String toString() {

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
                rank_name = "Ace";
                break;
            case"2":
                rank_name = "Two";
                break;
            case"3":
                rank_name = "Three";
                break;
            case"4":
                rank_name = "Four";
                break;
            case"5":
                rank_name = "Five";
                break;
            case"6":
                rank_name = "Six";
                break;
            case"7":
                rank_name = "Seven";
                break;
            case"8":
                rank_name = "Eight";
                break;
            case"9":
                rank_name = "Nine";
                break;
            case"10":
                rank_name = "Ten";
                break;
            case"J":
                rank_name = "Jack";
                break;
            case"Q":
                rank_name = "Queen";
                break;
            case"K":
                rank_name = "King";
                break;
        }

        return rank_name + " of " + suit_name;

    }
    
    public boolean equals(Card other) {
        return this.id.equals(other.id);
    }

    public boolean equalSuit(Card other) {
        return this.suit.equals(other.suit);
    }

    public int compareRank(Card other){
        return new Integer(this.getRankValue()).compareTo(new Integer(other.getRankValue())) ;
    }

    public int getRankValue() {
        try {
            return Integer.parseInt(this.rank);
        } catch (NumberFormatException nfe){
           switch (this.rank){
               case "J": return 11;
               case "Q": return 12;
               case "K": return 13;
               case "A": return 14;
               default: return 0;
           }
        }
    }
}
