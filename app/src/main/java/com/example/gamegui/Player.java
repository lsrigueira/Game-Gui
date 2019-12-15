package com.example.gamegui;


import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

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

    public void enseñar_reverso() {
        functions.enseñar_carta(this.imagencard1, "reverso");
        functions.enseñar_carta(this.imagencard2, "reverso");
    }

    public void enseñar_cartas() {
        functions.enseñar_carta(this.imagencard1, this.card1.getId());
        functions.enseñar_carta(this.imagencard2, this.card2.getId());
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

    /*
     *  FUNCIONES PARA CALCULAR MANOS
     */
    private int hasPair(boolean completo) {
        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        setUnsued(cartas);

        int valorPareja = hayPareja(cartas);

        if (valorPareja > 0) {
            int puntos = PAIR * CASTTOBESTPLAY + valorPareja * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADA PAREJA-->" + puntos, 1);
            return puntos;
        }
        return 0;
    }

    private int hasDoblePair(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        setUnsued(cartas);

        int valorPareja1 = hayPareja(cartas);
        int valorPareja2 = hayPareja(cartas);

        if (valorPareja1 * valorPareja2 > 0) {
            int puntos = DOUBLEPAIR * CASTTOBESTPLAY + Math.max(valorPareja1, valorPareja2) * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADA DOBLE PAREJA-->" + puntos, 1);
            return puntos;
        }

        return 0;
    }

    private int hastrio(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        setUnsued(cartas);

        int hayTrio = hayTrio(cartas);

        if (hayTrio > 0) {
            int puntos = TRIO * CASTTOBESTPLAY + hayTrio * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO TRIO-->" + puntos, 1);
            return puntos;
        }
        return 0;
    }

    private int haspoker(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        int foundbefore;
        int puntos = 0;
        for (int contador = 0; contador < cartas.size(); contador++) {
            foundbefore = 0;
            Card tocompare = cartas.get(contador);
            for (int i = 0; i < cartas.size(); i++) {
                Card comparable = cartas.get(i);
                if (comparable.getRank().equals(tocompare.getRank()) && !comparable.getId().equals(tocompare.getId())) {

                    if (foundbefore == 2) {
                        puntos += POKER * CASTTOBESTPLAY + comparable.getRankValue() * CASTTOBESTCARDINPLAY;
                        functions.imprimirdebug(this.getname() + ":ENCONTRADO POKER-->" + puntos, 1);
                        return puntos;
                    } else foundbefore++;
                }
            }
        }
        return 0;
    }

    private int hasfull(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        setUnsued(cartas);

        int valorTrio = hayTrio(cartas);
        int valorPareja = hayPareja(cartas);

        if (valorTrio * valorPareja > 0) {
            int puntos = FULL_HOUSE * CASTTOBESTPLAY + Math.max(valorTrio, valorPareja) * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO FULL-->" + puntos, 1);
            return puntos;
        }
        return 0;
    }

    private int hasstraight(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        int valorEscalera = hayEscalera(cartas);

        if (valorEscalera > 0) {
            int puntos = STRAIGHT * CASTTOBESTPLAY + valorEscalera * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO STRAIGHT-->" + puntos, 1);
            return puntos;
        }
        return 0;

    }

    private int hasstraightflush(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        int valorEscalera = hayEscalera(cartas);

        if (valorEscalera > 1000) {
            int puntos = STRAIGHT_FLUSH * CASTTOBESTPLAY + valorEscalera/1000 * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO STRAIGHT_FLUSH-->" + puntos, 1);
            return puntos;
        }
        return 0;
    }

    private int hasroyalflush(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        int valorEscalera = hayEscalera(cartas);

        if (valorEscalera == 14000) {
            int puntos = ROYAL_FLUSH * CASTTOBESTPLAY + valorEscalera/1000 * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO ROYAL_FLUSH-->" + puntos, 1);
            return puntos;
        }
        return 0;
    }

    private int hasflush(boolean completo) {

        ArrayList<Card> cartas = new ArrayList<>();
        if (completo) {
            cartas.addAll(cartastot);
        } else {
            cartas.addAll(cartasmesa);
        }

        int valorColor = 0;

        for (int nCartaA = 0; nCartaA < cartas.size(); nCartaA++) {
            Card CartaA = cartas.get(nCartaA);

            for (int nCartaB = 0; nCartaB < cartas.size(); nCartaB++) {
                Card CartaB = cartas.get(nCartaB);
                if (CartaA.getSuit().equals(CartaB.getSuit()) && !CartaA.getId().equals(CartaB.getId())) {
                    for (int nCartaC = 0; nCartaC < cartas.size(); nCartaC++) {
                        Card CartaC = cartas.get(nCartaC);
                        if (CartaB.getSuit().equals(CartaC.getSuit()) && !CartaB.getId().equals(CartaC.getId())
                                && !CartaC.getId().equals(CartaA.getId())) {
                            for (int nCartaD = 0; nCartaD < cartas.size(); nCartaD++) {
                                Card CartaD = cartas.get(nCartaD);
                                if (CartaC.getSuit().equals(CartaD.getSuit()) && !CartaD.getId().equals(CartaA.getId())
                                        && !CartaD.getId().equals(CartaB.getId()) && !CartaD.getId().equals(CartaC.getId())) {
                                    for (int nCartaE = 0; nCartaE < cartas.size(); nCartaE++) {
                                        Card CartaE = cartas.get(nCartaE);
                                        if (CartaD.getSuit().equals(CartaE.getSuit()) && !CartaE.getId().equals(CartaA.getId())
                                                && !CartaE.getId().equals(CartaB.getId()) && !CartaE.getId().equals(CartaC.getId())
                                                && !CartaE.getId().equals(CartaD.getId())) {

                                            /*
                                            functions.imprimirdebug("COLOR CON LAS CARTAS: " + " " + CartaA.toString() + " " + CartaB.toString()
                                                    + " " + CartaC.toString() + " " + CartaD.toString() + " " + CartaE.toString(), 1);
                                            */

                                            int max1 = Math.max(CartaA.getRankValue(), CartaB.getRankValue());
                                            int max2 = Math.max(CartaC.getRankValue(), CartaD.getRankValue());
                                            int max12 = Math.max(max1, max2);
                                            valorColor = Math.max(max12, CartaE.getRankValue());
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        if (valorColor > 0) {
            int puntos = FLUSH * CASTTOBESTPLAY + valorColor * CASTTOBESTCARDINPLAY;
            functions.imprimirdebug(this.getname() + ":ENCONTRADO FLUSH-->" + puntos, 1);
            return puntos;
        }
        return 0;
    }

    /*
     * FUNCIONES AUXILIARES PARA CALCULAR LAS MANOS
     */
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

    // Si existe un trio no usada devuelve el numero de la carta mas alta de este
    private int hayTrio(ArrayList<Card> cartas) {

        //Cojo una carta A y recorro todo el array
        for (int nCartaA = 0; nCartaA < cartas.size(); nCartaA++) {
            Card CartaA = cartas.get(nCartaA);

            //Si encuentro una carta B != A pero con el mismo numero, la cojo y vuelo a recorrer el array
            for (int nCartaB = 0; nCartaB < cartas.size(); nCartaB++) {
                Card CartaB = cartas.get(nCartaB);
                if (CartaA.getRank().equals(CartaB.getRank()) && !CartaA.getId().equals(CartaB.getId())
                        && !CartaA.getUsed() && !CartaB.getUsed()) {

                    //Si encuentro una carta C != B y !=A pero con el mismo numero -> TRIO
                    for (int nCartaC = 0; nCartaC < cartas.size(); nCartaC++) {
                        Card CartaC = cartas.get(nCartaC);
                        if (CartaB.getRank().equals(CartaC.getRank()) && !CartaB.getId().equals(CartaC.getId())
                                && !CartaA.getId().equals(CartaC.getId()) && !CartaB.getUsed() && !CartaC.getUsed()) {

                            //functions.imprimirdebug("TRIO CON LAS CARTAS: " + " " + CartaA.toString() + " " + CartaB.toString() + " " + CartaC.toString(), 1);

                            CartaA.setUsed(true);
                            CartaB.setUsed(true);
                            CartaC.setUsed(true);

                            int max1 = Math.max(CartaA.getRankValue(), CartaB.getRankValue());
                            return Math.max(max1, CartaC.getRankValue());
                        }
                    }
                }
            }
        }
        return 0;
    }

    /*
     * Si existe una escalera devuelve el numero de la carta mas alta de esta
     * Si ademas es de color, multiplica el valor de esa carta por mil, por motivos
     * de identificacion de la mano
     */
    private int hayEscalera(ArrayList<Card> cartas) {
        if (cartas.size() < 5)
            return 0;

        for (int nCartaA = 0; nCartaA < cartas.size(); nCartaA++) {
            Card CartaA = cartas.get(nCartaA);

            for (int nCartaB = 0; nCartaB < cartas.size(); nCartaB++) {
                Card CartaB = cartas.get(nCartaB);
                if (CartaA.getRankValue() + 1 == CartaB.getRankValue() || (CartaA.getRankValue() == 14 && CartaB.getRankValue() == 2)) {
                    for (int nCartaC = 0; nCartaC < cartas.size(); nCartaC++) {
                        Card CartaC = cartas.get(nCartaC);
                        if (CartaB.getRankValue() + 1 == CartaC.getRankValue()) {
                            for (int nCartaD = 0; nCartaD < cartas.size(); nCartaD++) {
                                Card CartaD = cartas.get(nCartaD);
                                if (CartaC.getRankValue() + 1 == CartaD.getRankValue()) {
                                    for (int nCartaE = 0; nCartaE < cartas.size(); nCartaE++) {
                                        Card CartaE = cartas.get(nCartaE);
                                        if (CartaD.getRankValue() + 1 == CartaE.getRankValue()) {

                                            /*
                                            functions.imprimirdebug("ESCALERA CON LAS CARTAS: " + " " + CartaA.toString() + " " + CartaB.toString()
                                                    + " " + CartaC.toString() + " " + CartaD.toString() + " " + CartaE.toString(), 1);
                                             */

                                            int esColor = 1;

                                            if (CartaA.getSuit().equals(CartaB) && CartaA.getSuit().equals(CartaC) && CartaA.getSuit().equals(CartaD) &&
                                                    CartaA.getSuit().equals(CartaE))
                                                esColor = 1000;

                                            return CartaE.getRankValue() * esColor;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }



    private void setUnsued(ArrayList<Card> cartas) {

        for (Card carta : cartas) {
            carta.setUsed(false);
        }

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

    /*
    A puntuacion sera un int de formato xx-yy-yy-zz onde as letras solo indican o numero de dixistos e a orixe do calculo.Explicacion:
        XX: Indica se o xogar ten parexa,doble-parexa,TRIO...
        YY: Indica as cartas que usou para chegar ahí, non é o mesmo unha parexa de ases que de douses(hai 2 para distinguir as doble-parexas)
        ZZ: Indica a carta mais alta que non se empregou para o cálculo de XX
     */

    public int calcularpuntuacion() {

        int highcardmesa = bestcard(false);

        int puntosmesa = hasroyalflush(false);
        if (puntosmesa == 0) puntosmesa = hasstraightflush(false);
        if (puntosmesa == 0) puntosmesa = haspoker(false);
        if (puntosmesa == 0) puntosmesa = hasfull(false);
        if (puntosmesa == 0) puntosmesa = hasflush(false);
        if (puntosmesa == 0) puntosmesa = hasstraight(false);
        if (puntosmesa == 0) puntosmesa = hastrio(false);
        if (puntosmesa == 0) puntosmesa = hasDoblePair(false);
        if (puntosmesa == 0) puntosmesa = hasPair(false);

        puntosmesa = puntosmesa + highcardmesa;

        functions.imprimirdebug("CARTAS DA MESA\n" + cartasmesa + "\nAS MIÑAS CARTAS\n" + cartastot, 3);
        functions.imprimirdebug("OS PUNTOS DA MESA-->" + puntosmesa, 1);
        functions.imprimirdebug(this.getname() + " CARTA MAIS ALTA-->" + highcardmesa, 3);
        //TRUE A PARTIR DE AQUI
        int puntos = hasroyalflush(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        puntos = hasstraightflush(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        puntos = haspoker(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        puntos = hasfull(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        puntos = hasflush(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }

        puntos = hasstraight(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        puntos = hastrio(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        puntos = hasDoblePair(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        puntos = hasPair(true);
        if (puntos != 0) {
            this.puntuacion = (puntos - puntosmesa + bestcard(true));
            return ((int) this.puntuacion);
        }
        return 0;

    }

}