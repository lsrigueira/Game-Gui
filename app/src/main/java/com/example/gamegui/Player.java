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

    static final String FOLD = "Fold";
    static final String CALL = "Call";
    static final String ALL_IN = "All in!";
    static final String BROKE = "Broke";

    static final short NO_BLIND = 0;
    static final short SMALL_BLIND = 1;
    static final short BIG_BLIND = 2;

    private Card card1;
    private Card card2;
    private ImageView imagencard1;
    private ImageView imagencard2;

    public TextView getTextPuntos() {
        return textPuntos;
    }

    private TextView textPuntos;
    private ArrayList<Card> cartastot;
    private ArrayList<Card> cartasmesa;
    private String nome;
    private boolean horizontal;
    private int money;
    private int moneybet = 100;
    private boolean playing = true;
    private String playState = Player.CALL;
    private long puntuacion;
    private long puntuacionmesa;
    private short blind = 0;

    public short getBlind() {
        return blind;
    }

    public void setBlind(short blind) {
        this.blind = blind;
    }

    public Player(String nome, int money, ImageView imagencarta1, ImageView imagencarta2, TextView textPuntos) {
        this.nome = nome;
        this.money = money;
        this.imagencard1 = imagencarta1;
        this.imagencard2 = imagencarta2;
        this.textPuntos = textPuntos;
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
        return "call";
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

    public void enseñar_reverso() {
        functions.enseñar_carta(this.imagencard1, "reverso");
        functions.enseñar_carta(this.imagencard2, "reverso");
    }

    public void enseñar_cartas() {
        functions.enseñar_carta(this.imagencard1, this.card1.getId());
        functions.enseñar_carta(this.imagencard2, this.card2.getId());
    }

    public String getState() {
        return playState;
    }

    public String setState(String playState) {
        this.playState = playState;
        return this.playState;
    }

    public void stop_playing() {
        this.playing = false;
    }

    public boolean is_playing() {
        return this.playing;
    }

    public void start_playing() {
        this.playing = true;
    }

    public void cartas_visibles(boolean visibilidad) {
        if (visibilidad) {
            this.imagencard1.setVisibility(View.VISIBLE);
            this.imagencard2.setVisibility(View.VISIBLE);
        } else {
            this.imagencard1.setVisibility(View.INVISIBLE);
            this.imagencard2.setVisibility(View.INVISIBLE);
        }
    }

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


    public void newCarta(Card carta) {
        this.cartastot.add(0, carta);
        this.puntuacion = 0;
        this.puntuacionmesa = 0;
        if (carta.getPosicion().equals("Mesa")) {
            this.cartasmesa.add(0, carta);
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


    public int bestcard(ArrayList<Card> cartas) {

        int bestcard = 0;
        for (int contador = 0; contador < cartas.size(); contador++) {
            if (!cartas.get(contador).getUsed()) {
                if (cartas.get(contador).getRankValue() > bestcard) {
                    bestcard = cartas.get(contador).getRankValue();
                }
            }
        }
        return bestcard;
    }

    private int hayPareja(ArrayList<Card> cartas) {

        for (int nCartaA = 0; nCartaA < cartas.size(); nCartaA++) {
            Card CartaA = cartas.get(nCartaA);

            for (int nCartaB = 0; nCartaB < cartas.size(); nCartaB++) {
                Card CartaB = cartas.get(nCartaB);
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

    private int hayPokerOTrio(ArrayList<Card> cartas) {

        //Cojo una carta A y recorro todo el array
        for (int nCartaA = 0; nCartaA < cartas.size(); nCartaA++) {
            Card CartaA = cartas.get(nCartaA);

            //Si encuentro una carta B != A pero con el mismo numero, la cojo y vuelo a recorrer el array
            for (int nCartaB = 0; nCartaB < cartas.size(); nCartaB++) {
                Card CartaB = cartas.get(nCartaB);
                if (CartaB.getRank().equals(CartaA.getRank()) && !CartaB.getId().equals(CartaA.getId())) {
                    //Si encuentro una carta C != B y !=A pero con el mismo numero -> TRIO
                    for (int nCartaC = 0; nCartaC < cartas.size(); nCartaC++) {
                        Card CartaC = cartas.get(nCartaC);
                        if (CartaC.getRank().equals(CartaB.getRank()) && !CartaA.getId().equals(CartaC.getId())
                                && !CartaB.getId().equals(CartaC.getId())) {
                            //SI LLEGO AQUI HAY TRIO
                            for (int nCartaD = 0; nCartaD < cartas.size(); nCartaD++) {
                                Card CartaD = cartas.get(nCartaD);
                                if (CartaD.getRank().equals(CartaC.getRank()) && !CartaA.getId().equals(CartaD.getId())
                                        && !CartaB.getId().equals(CartaD.getId()) && !CartaC.getId().equals(CartaD.getId())) {

                                    //AQUI HAY POKER
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

    private int hayColor(ArrayList<Card> cartas) {


        Card carta;
        int[] colors = new int[4];
        int cartaMasAlta = 0;
        for (int contador = 0; contador < cartas.size(); contador++) {

            carta = cartas.get(contador);
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

    private int haiEscalera(ArrayList<Card> cartas) {

        for (int nCarta = 0; nCarta < cartas.size(); nCarta++) {
            Card cartaA = cartas.get(nCarta);
            int numeroCartaA = cartaA.getRankValue();
            boolean uno, dos, tres, cuatro;
            uno = dos = tres = cuatro = true;

            int escalera = 0;
            int cartaMasAlta = 0;

            for (int i = 0; i < cartas.size(); i++) {
                if (numeroCartaA > 9) {
                    break;
                }
                Card cartaAux = cartas.get(i);
                int numeroCartaAux = cartaAux.getRankValue();
                if (uno && numeroCartaA + 1 == numeroCartaAux) {
                    escalera++;
                    uno = false;
                }
                if (dos && numeroCartaA + 2 == numeroCartaAux) {
                    escalera++;
                    dos = false;
                }
                if (tres && numeroCartaA + 3 == numeroCartaAux) {
                    escalera++;
                    tres = false;
                }
                if (cuatro && numeroCartaA + 4 == numeroCartaAux) {
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

    /*
    A puntuacion sera un int de formato xx-yy-yy-zz onde as letras solo indican o numero de dixistos e a orixe do calculo.Explicacion:
        XX: Indica se o xogar ten parexa,doble-parexa,TRIO...
        YY: Indica as cartas que usou para chegar ahí, non é o mesmo unha parexa de ases que de douses(hai 2 para distinguir as doble-parexas)
        ZZ: Indica a carta mais alta que non se empregou para o cálculo de XX
     */

    public int calcularPuntos(ArrayList<Card> cartas) {

        int puntos, valorPoker, valorTrio, valorPareja1, valorPareja2, valorColor, valorEscalera;

        int highcardmesa = bestcard(cartas);
        functions.imprimirdebug(this.getname() + " CARTA MAIS ALTA-->" + highcardmesa, 3);

        valorPoker = hayPokerOTrio(cartas);
        if (valorPoker > 0) {
            puntos = POKER * CASTTOBESTPLAY + valorPoker * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO POKER-->" + puntos, 1);
            return puntos;
        } else if (valorPoker < 0)
            valorTrio = valorPoker * -1;
        else
            valorTrio = 0;

        valorPareja1 = hayPareja(cartas);
        if (valorTrio * valorPareja1 > 0) {
            puntos = FULL_HOUSE * CASTTOBESTPLAY + Math.max(valorTrio, valorPareja1) * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO FULL-->" + puntos, 1);
            return puntos;
        }

        //cuesta aprox 100ms
        valorColor = hayColor(cartas);
        if (valorColor > 0) {
            puntos = FLUSH * CASTTOBESTPLAY + valorColor * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO FLUSH-->" + puntos, 1);
            return puntos;
        }

        //cuesta aprox 100ms
        valorEscalera = haiEscalera(cartas);
        if (valorEscalera > 0) {
            puntos = STRAIGHT * CASTTOBESTPLAY + valorEscalera * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO STRAIGHT-->" + puntos, 1);
            return puntos;
        }

        if (valorTrio > 0) {
            puntos = TRIO * CASTTOBESTPLAY + valorTrio * CASTTOBESTCARDINPLAY + highcardmesa + highcardmesa;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO TRIO-->" + puntos, 1);
            return puntos;
        }

        valorPareja2 = hayPareja(cartas);
        if (valorPareja1 * valorPareja2 > 0) {
            puntos = DOUBLEPAIR * CASTTOBESTPLAY + Math.max(valorPareja1, valorPareja2) * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":ENCONTRADA DOBLE PAREJA-->" + puntos, 1);
            return puntos;
        }

        if (valorPareja1 > 0) {
            puntos = PAIR * CASTTOBESTPLAY + valorPareja1 * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":ENCONTRADA PAREJA-->" + puntos, 1);
            return puntos;
        }

        puntos = highcardmesa;
        functions.imprimirdebug(this.getname() + ":ENCONTRADO SOLO CARTA ALTA-->" + puntos, 1);
        return puntos;
    }

    public int calcularpuntuacion() {

        ArrayList<Card> cartasTotales = new ArrayList<>();
        ArrayList<Card> cartasMesa = new ArrayList<>();
        cartasTotales.addAll(cartastot);
        cartasMesa.addAll(cartasmesa);

        int puntosjugador = calcularPuntos(cartasTotales);
        int puntuacionmesa = calcularPuntos(cartasMesa);

        functions.imprimirdebug("CARTAS DA MESA\n" + cartasmesa + "\nAS MIÑAS CARTAS\n" + cartastot, 3);
        functions.imprimirdebug("OS PUNTOS NA MAN-->" + puntosjugador, 1);
        functions.imprimirdebug("OS PUNTOS DA MESA-->" + puntuacionmesa, 1);
        functions.imprimirdebug("PUNTOS RESULTANTES-->" + (puntosjugador - puntuacionmesa), 1);

        return (puntosjugador - puntuacionmesa);
    }

}
