package com.example.gamegui;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Player {
    private static final int PAIR = 1;
    private static final int DOUBLEPAIR = 2;
    private static final int TRIO = 3;
    private static final int STRAIGHT = 4;
    private static final int FLUSH = 5;
    private static final int FULL_HOUSE = 6;
    private static final int POKER = 7;
    private static final int STRAIGHT_FLUSH = 8;
    private static final int ROYAL_FLUSH = 9;


    private static final int CASTTOBESTPLAY = 1000000;
    private static final int CASTTOBESTCARDINPLAY = 10000;
    private static final int CASTTOWORSTCARDINPLAY = 100;
    private static final int CASTTOBESTCARDOUTPLAY = 1;

    protected static final String FOLD = "Fold";
    protected static final String CALL = "Call";
    protected static final String ALL_IN = "All in!";
    protected static final String BROKE = "Broke";

    protected static final short NO_BLIND = 0;
    protected static final short SMALL_BLIND = 1;
    protected static final short BIG_BLIND = 2;

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

    public Player(String nome, int money, ImageView imagencarta1, ImageView imagencarta2, TextView textPuntos) {
        this.nome = nome;
        this.money = money;
        this.imagencard1 = imagencarta1;
        this.imagencard2 = imagencarta2;
        this.textPuntos = textPuntos;
        cartastot = new ArrayList<>();
        cartasmesa = new ArrayList<>();
    }

    public short getBlind() {
        return blind;
    }

    public void setBlind(short blind) {
        this.blind = blind;
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

    public char getdecision(StringBuilder history) {

        /*if(nronda==4&&calcularpuntuacion()==0){
            this.playing = false;
            return "fold";
        }*/
        final char FOLD1 = 'f', RAISE1 = 'r', CALL1 = 'c', NUM_ACTIONS = 3;
        StringBuilder infoset = history.length() > 6 ? new StringBuilder(history.substring(history.length() - 5))
                : new StringBuilder(history);
        infoset.append(":");
        infoset.append(calcularpuntuacion());
        Node node = InGame.gisnoc(infoset);
        double[] strategy = {};
        strategy = node.getAverageStrategy();
        double[] util = new double[NUM_ACTIONS];
        StringBuilder nextHistory = new StringBuilder(history);
        while (history.length() == nextHistory.length()) {
            double index = Math.random();

            if (index < strategy[0]) {
                if (isValidPlay(nextHistory, FOLD1)) {
                    nextHistory.append(FOLD1);
                    System.out.println("Jugador IA jugó fold: " + infoset);
                    return FOLD1;
                    //a = 0;
                    //System.out.println("Jugador " + player + " jugó fold: " + history + "---------" + cards[player][0]
                    //      + ":" + cards[player][1] + ":" + Arrays.toString(strategy));
                }
            } else if (index < strategy[0] + strategy[1]) {
                if (isValidPlay(nextHistory, RAISE1)) {
                    nextHistory.append(RAISE1);
                    System.out.println("Jugador IA jugó raise: " + infoset);
                    return RAISE1;
                    //a = 1;
                    //System.out.println("Jugador " + player + " jugó raise: " + history + "---------" + cards[player][0]
                    //      + ":" + cards[player][1] + ":" + Arrays.toString(strategy));
                }
            } else {
                if (isValidPlay(nextHistory, CALL1)) {
                    nextHistory.append(CALL1);
                    System.out.println("Jugador IA jugó call: " + infoset);
                    return CALL1;
                    //a = 2;
                    // System.out.println("Jugador " + player + " jugó call: " + history + "---------" + cards[player][0]
                    //       + ":" + cards[player][1] + ":" + Arrays.toString(strategy));
                }
            }
        }
        System.out.println("maaaal");
        //return "";
        return CALL1;
    }

    public String getname() {
        return this.nome;
    }

    public String getState() {
        return playState;
    }

    public String setState(String playState) {
        this.playState = playState;
        return this.playState;
    }

    public void setcards(Card c1, Card c2) {
        this.card1 = c1;
        this.card2 = c2;
        this.cartastot.add(c1);
        this.cartastot.add(c2);
        this.horizontal = horizontal;
    }

    public void win(int money) {
        this.money += money;
    }

    public void loose(int money) {
        this.money -= money;
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

    public void enseñar_reverso() {
        functions.enseñar_carta(this.imagencard1, "reverso");
        functions.enseñar_carta(this.imagencard2, "reverso");
    }

    public void enseñar_cartas() {
        functions.enseñar_carta(this.imagencard1, this.card1.getId());
        functions.enseñar_carta(this.imagencard2, this.card2.getId());
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

    public void cartas_visibles(boolean visibilidad) {
        if (visibilidad) {
            this.imagencard1.setVisibility(View.VISIBLE);
            this.imagencard2.setVisibility(View.VISIBLE);
        } else {
            this.imagencard1.setVisibility(View.INVISIBLE);
            this.imagencard2.setVisibility(View.INVISIBLE);
        }
    }

    public static boolean isValidPlay(StringBuilder history_, int play) {
        final char FOLD1 = 'f', RAISE1 = 'r', CALL1 = 'c', NUM_ACTIONS = 3;
        StringBuilder history = new StringBuilder(history_);
        switch (play) {
            case FOLD1:
                return true;
            case RAISE1:
                //if (limitReached(new StringBuilder(history).append(RAISE), (history.length()) % 2, LIMIT))// De esta manera
                //return false;																				// evitamos el
                // re-raise
                // infinito
                if (history.charAt(history.length() - 2) == RAISE1 || history.charAt(history.length() - 1) == RAISE1)
                    return false;
                return true;
            case CALL1:
                return true;
            default:
                // Otro error aqui
                System.out.println("erorrr");
                return false;
        }
    }

    // Funciones para el calculo de puntuaciones

    private int cartaAlta(ArrayList<Card> cartas) {

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

    private int hayEscalera(ArrayList<Card> cartas) {

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

    private int calcularPuntos(ArrayList<Card> cartas, String sitio) {

        resetearCartas(cartas);

        int puntos, valorPoker, valorTrio, valorPareja1, valorPareja2, valorColor, valorEscalera;

        int highcardmesa = cartaAlta(cartas);

        valorPoker = hayPokerOTrio(cartas);
        if (valorPoker > 0) {
            puntos = POKER * CASTTOBESTPLAY + valorPoker * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":" +  sitio +  ":ENCONTRADO POKER-->" + puntos, 1);
            return puntos;
        } else if (valorPoker < 0)
            valorTrio = valorPoker * -1;
        else
            valorTrio = 0;

        valorPareja1 = hayPareja(cartas);
        if (valorTrio * valorPareja1 > 0) {
            puntos = FULL_HOUSE * CASTTOBESTPLAY + Math.max(valorTrio, valorPareja1) * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":" +  sitio +  ":ENCONTRADO FULL-->" + puntos, 1);
            return puntos;
        }

        //cuesta aprox 100ms
        valorColor = hayColor(cartas);
        if (valorColor > 0) {
            puntos = FLUSH * CASTTOBESTPLAY + valorColor * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":" +  sitio +   ":ENCONTRADO FLUSH-->" + puntos, 1);
            return puntos;
        }

        //cuesta aprox 100ms
        valorEscalera = hayEscalera(cartas);
        if (valorEscalera > 0) {
            puntos = STRAIGHT * CASTTOBESTPLAY + valorEscalera * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":" +  sitio +   ":ENCONTRADO STRAIGHT-->" + puntos, 1);
            return puntos;
        }

        if (valorTrio > 0) {
            puntos = TRIO * CASTTOBESTPLAY + valorTrio * CASTTOBESTCARDINPLAY + highcardmesa + highcardmesa;
            functions.imprimirdebug(this.getname() + ":" +  sitio +   ":ENCONTRADO TRIO-->" + puntos, 1);
            return puntos;
        }

        valorPareja2 = hayPareja(cartas);
        if (valorPareja1 * valorPareja2 > 0) {
            puntos = DOUBLEPAIR * CASTTOBESTPLAY + Math.max(valorPareja1, valorPareja2) * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":" +  sitio +   ":ENCONTRADA DOBLE PAREJA-->" + puntos, 1);
            return puntos;
        }

        if (valorPareja1 > 0) {
            puntos = PAIR * CASTTOBESTPLAY + valorPareja1 * CASTTOBESTCARDINPLAY + highcardmesa;
            functions.imprimirdebug(this.getname() + ":" +  sitio +   ":ENCONTRADA PAREJA-->" + puntos, 1);
            return puntos;
        }

        puntos = highcardmesa;
        functions.imprimirdebug(this.getname() + ":" +  sitio +   ":ENCONTRADO SOLO CARTA ALTA-->" + puntos, 1);
        return puntos;
    }

    private void resetearCartas(ArrayList<Card> cartas) {
        for (int nCarta = 0; nCarta < cartas.size(); nCarta++) {
            Card CartaA = cartas.get(nCarta);
            CartaA.setUsed(false);
        }
    }

    public int calcularpuntuacion() {

        ArrayList<Card> cartasTotales = new ArrayList<>();
        ArrayList<Card> cartasMesa = new ArrayList<>();
        cartasTotales.addAll(cartastot);
        cartasMesa.addAll(cartasmesa);

        /*System.out.println(this.getname() + "Cartas TOTALES = " + cartasTotales.toString());
        System.out.println(this.getname() + "Cartas MESA = " + cartasMesa.toString());*/

        int puntosjugador = calcularPuntos(cartasTotales, "TOTAL");
        int puntuacionmesa = calcularPuntos(cartasMesa, "MESA");

        functions.imprimirdebug(this.getname() + ":PUNTOS MAN = " + puntosjugador + " PUNTOS MESA = "
                + puntuacionmesa+ " PUNTOS RESULTANTES = " + (puntosjugador - puntuacionmesa), 1);

        this.puntuacion =(puntosjugador - puntuacionmesa);
        return (puntosjugador - puntuacionmesa);
    }

}