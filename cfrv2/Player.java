package com.example.gamegui;


import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    static final int PAIR = 1;
    static final int DOUBLEPAIR = 2;
    static final int TRIO = 3;
    static final int STRAIGHT = 4;
    static final int FLUSH = 5;
    static final int FULL_HOUSE = 6;
    static final int POKER = 7;
    static final int STRAIGHT_FLUSH = 8;
    static final int ROYAL_FLUSH = 9;


    static final int CASTTOBESTPLAY = 1000000;
    static final int CASTTOBESTCARDINPLAY = 10000;
    static final int CASTTOWORSTCARDINPLAY = 100;
    static final int CASTTOBESTCARDOUTPLAY = 1;
    private Card card1;
    private Card card2;
    private ImageView imagencard1;
    private ImageView imagencard2;
    private ArrayList<Card> cartastot;
    private ArrayList<Card> cartasmesa;
    private String nome;
    private boolean horizontal;
    private int money;
    private int moneybet = 100;
    private boolean playing = false;
	private long puntuacion;
	private long puntuacionmesa;

    public Player(String nome, int money, ImageView imagencarta1, ImageView imagencarta2) {
        this.nome = nome;
        this.money = money;
        this.imagencard1 = imagencarta1;
        this.imagencard2 = imagencarta2;
        cartastot = new ArrayList<>();
        cartasmesa = new ArrayList<>();
    }

    public long getPuntuacion() {
        return this.puntuacion;
    }

    public int getMoney() {
        return this.money;
    }

    public int getBet() {
        return this.moneybet;
    }

    public String getdecision(int nronda) {

        /*if(nronda==4&&calcularpuntuacion()==0){
            this.playing = false;
            return "fold";
        }*/
        return "bet";
    }

    public Card getcard1() {
        return this.card1;
    }

    public Card getcard2() {
        return this.card2;
    }

    public String getname() {
        return this.nome;
    }

    public ArrayList<Card> getcartastot() {
        return this.cartastot;
    }

    public ArrayList<Card> getcartasmesa() {
        return this.cartasmesa;
    }

//    public void en
//        functions.a(this.imagencard1, "reverso");
//        functions.ta(this.imagencard2, "reverso");
//    }
//
//    public void en {
//        functions._carta(this.imagencard1, this.card1.getId());
//        functions.carta(this.imagencard2, this.card2.getId());
//    }

    public void stop_playing() {
        this.playing = false;
    }

    public boolean is_playing() {
        return this.playing;
    }

    public void start_playing() {
        this.playing = true;
    }

//    public void cartas_visibles(boolean visibilidad) {
//        if (visibilidad) {
//            this.imagencard1.setVisibility(View.VISIBLE);
//            this.imagencard2.setVisibility(View.VISIBLE);
//        } else {
//            this.imagencard1.setVisibility(View.INVISIBLE);
//            this.imagencard2.setVisibility(View.INVISIBLE);
//        }
//    }

    public void win(int money) {
        this.money += money;
    }

    public void loose(int money) {
        this.money -= money;
    }

    public void setcards(Card c1, Card c2) {
        this.card1 = c1;
        this.card2 = c2;
        this.cartastot.add(c1);
        this.cartastot.add(c2);
        this.horizontal = horizontal;
    }

    public int bestcard(boolean completo) {
        ArrayList<Card> cartas = new ArrayList<>();
        int bestcard = 0;
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }
        for (int contador = 0; contador < cartas.size(); contador++) {
            if (!cartas.get(contador).getUsed()) {
                if (cartas.get(contador).getRankValue() > bestcard) {
                    bestcard = cartas.get(contador).getRankValue();
                }
            }
        }
        return bestcard;
    }


    public void newCarta(Card carta) {
        this.cartastot.add(carta);
        if (carta.getPosicion().equals("Mesa")) {
            this.cartasmesa.add(carta);
        }
        for (int i = 0; i < cartastot.size(); i++) {
            cartastot.get(i).setUsed(false);
        }
        for (int i = 0; i < cartasmesa.size(); i++) {
            cartasmesa.get(i).setUsed(false);
        }
    }

    public void clearCartaMesa() {
        this.playing = true;
        this.cartastot = new ArrayList<>();
        this.cartasmesa = new ArrayList<>();
    }


    //DE AQUI PARA ABAJO ES TODO NUEVO
    //---------------------------------------------------------------------------------
    private int hayPareja() {

        for (int nCartaA = 0; nCartaA < cartastot.size(); nCartaA++) {
            Card CartaA = cartastot.get(nCartaA);

            for (int nCartaB = 0; nCartaB < cartastot.size(); nCartaB++) {
                Card CartaB = cartastot.get(nCartaB);
                if (CartaA.getRank().equals(CartaB.getRank()) && !CartaA.getId().equals(CartaB.getId())
                        && !CartaA.getUsed() && !CartaB.getUsed()) {

                    //functions.imprimirdebug("PAREJA CON LAS CARTAS: " + " " + CartaA.toString() + " " + CartaB.toString(), 1);

                    CartaA.setUsed(true);
                    CartaB.setUsed(true);
                    return Math.max(CartaA.getRankValue(), CartaB.getRankValue());
                }
            }
        }
        return 0;
    }

    private int hayPokerOTrio() {

        //Cojo una carta A y recorro todo el array
        for (int nCartaA = 0; nCartaA < cartastot.size(); nCartaA++) {
            Card CartaA = cartastot.get(nCartaA);

            //Si encuentro una carta B != A pero con el mismo numero, la cojo y vuelo a recorrer el array
            for (int nCartaB = 0; nCartaB < cartastot.size(); nCartaB++) {
                Card CartaB = cartastot.get(nCartaB);
                if (CartaB.getRank().equals(CartaA.getRank()) && !CartaB.getId().equals(CartaA.getId())) {
                    //Si encuentro una carta C != B y !=A pero con el mismo numero -> TRIO
                    for (int nCartaC = 0; nCartaC < cartastot.size(); nCartaC++) {
                        Card CartaC = cartastot.get(nCartaC);
                        if (CartaC.getRank().equals(CartaB.getRank()) && !CartaA.getId().equals(CartaC.getId())
                                && !CartaB.getId().equals(CartaC.getId())) {
                            //SI LLEGO AQUI HAY TRIO
                            for (int nCartaD = 0; nCartaD < cartastot.size(); nCartaD++) {
                                Card CartaD = cartastot.get(nCartaD);
                                if (CartaD.getRank().equals(CartaC.getRank()) && !CartaA.getId().equals(CartaD.getId())
                                        && !CartaB.getId().equals(CartaD.getId()) && !CartaC.getId().equals(CartaD.getId())) {

                                    //functions.imprimirdebug("POKER CON LAS CARTAS: " + " " + CartaA.toString() + " " + CartaB.toString() + " " + CartaC.toString() + " " + CartaD.toString(), 1);
                                    int max1 = Math.max(CartaA.getRankValue(), CartaB.getRankValue());
                                    int max2 = Math.max(CartaC.getRankValue(), CartaD.getRankValue());

                                    return Math.max(max1, max2);
                                }
                            }

                            //functions.imprimirdebug("TRIO CON LAS CARTAS: " + " " + CartaA.toString() + " " + CartaB.toString() + " " + CartaC.toString(), 1);

                            //AQUI YA HAY TRIO
                            CartaA.setUsed(true);
                            CartaB.setUsed(true);
                            CartaC.setUsed(true);

                            int max1 = Math.max(CartaA.getRankValue(), CartaB.getRankValue());
                            return (Math.max(max1, CartaC.getRankValue()) * -1);
                        }
                    }
                }
            }
        }

        return 0;
    }

    private int hayColor() {

        Card carta;
        int[] colors = new int[4];
        int cartaMasAlta = 0;
        for (int contador = 0; contador < cartastot.size(); contador++) {

            carta = cartastot.get(contador);
            cartaMasAlta = carta.getRankValue() > cartaMasAlta ? carta.getRankValue() : cartaMasAlta;
            switch (carta.getSuit()) {
                case "C":
                    colors[0]++;
                    break;
                case "D":
                    colors[1]++;
                    break;
                case "H":
                    colors[2]++;
                    break;
                case "S":
                    colors[3]++;
                    break;
            }
            if (colors[0] > 4 || colors[1] > 4 || colors[2] > 4 || colors[3] > 4) {
                return cartaMasAlta;
            }
        }
        return 0;
    }

    private int haiEscalera() {

        for (int nCarta = 0; nCarta < cartastot.size(); nCarta++) {
            Card cartaA = cartastot.get(nCarta);
            int numeroCartaA = cartaA.getRankValue();
            boolean uno, dos, tres, cuatro;
            uno=dos=tres=cuatro=true;

            int escalera = 0;
            int cartaMasAlta = 0;

            for (int i = 0; i < cartastot.size(); i++) {
                if (numeroCartaA>8){
                    break;
                }
                Card cartaAux = cartastot.get(i);
                int numeroCartaAux = cartaAux.getRankValue();
                if (uno && numeroCartaA + 1 == numeroCartaAux) {
                    escalera++;
                    uno = false;
                }if (dos &&numeroCartaA + 2 == numeroCartaAux) {
                    escalera++;
                    dos = false;
                }if (tres && numeroCartaA + 3 == numeroCartaAux) {
                    escalera++;
                    tres = false;
                }if (cuatro &&numeroCartaA + 4 == numeroCartaAux) {
                    escalera++;
                    cuatro = false;
                    cartaMasAlta = numeroCartaAux;
                }
                if (escalera == 4)
                    return cartaMasAlta;
            }

        }
        return 0;
    }


	public int calcularpuntuacion(){

    int puntosmesa = 0;

    int valorPoker, valorTrio, valorPareja1, valorPareja2, valorColor, valorEscalera;

    int highcardmesa = bestcard(false);
    puntosmesa = puntosmesa + highcardmesa;

    valorPoker = hayPokerOTrio();
    if (valorPoker > 0) {
        this.puntuacion = POKER * CASTTOBESTPLAY + valorPoker * CASTTOBESTCARDINPLAY;
        return ((int) this.puntuacion);
    } else if (valorPoker < 0)
        valorTrio = valorPoker * -1;
    else
        valorTrio = 0;

    valorPareja1 = hayPareja();
    if (valorTrio * valorPareja1 > 0) {
        puntuacion = FULL_HOUSE * CASTTOBESTPLAY + Math.max(valorTrio, valorPareja1) * CASTTOBESTCARDINPLAY;
        return ((int) this.puntuacion);
    }

    //cuesta aprox 100ms
    valorColor = hayColor();
    if (valorColor > 0) {
        puntuacion = FLUSH * CASTTOBESTPLAY + valorColor * CASTTOBESTCARDINPLAY;
        return ((int) this.puntuacion);
    }

    //cuesta aprox 100ms
    valorEscalera = 0;//haiEscalera();
    if (valorEscalera > 0) {
        puntuacion = STRAIGHT * CASTTOBESTPLAY + valorEscalera * CASTTOBESTCARDINPLAY;
        return ((int) this.puntuacion);
    }

    if (valorTrio > 0) {
        puntuacion = TRIO * CASTTOBESTPLAY + valorTrio * CASTTOBESTCARDINPLAY;
        return ((int) this.puntuacion);
    }

    valorPareja2 = hayPareja();
    if (valorPareja1 * valorPareja2 > 0) {
        puntuacion = DOUBLEPAIR * CASTTOBESTPLAY + Math.max(valorPareja1, valorPareja2) * CASTTOBESTCARDINPLAY;
        return ((int) this.puntuacion);
    }

    if (valorPareja1 > 0) {
        puntuacion = PAIR * CASTTOBESTPLAY + valorPareja1 * CASTTOBESTCARDINPLAY;
        return ((int) this.puntuacion);
    }
    return 0;


    }

}
