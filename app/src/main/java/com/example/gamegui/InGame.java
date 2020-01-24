package com.example.gamegui;


import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.Intent;
import android.icu.text.Edits;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;


public class InGame extends AppCompatActivity {

    ArrayList<Card> cartasenbaraja;
    final ArrayList<Player> jugadores = new ArrayList<>();
    int current_round = 0;
    int rondastotales = 0;
    int currentPot = 0;
    int callValue = 100;
    static HashMap<String, Node> nodeMap = new HashMap<String, Node>();
    static long tiempoEjecucion = 0;
    static long vecesEjecucion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InputStream ins = getResources().openRawResource(R.raw.test);
        InputStreamReader inputStreamReader = new InputStreamReader(ins);
        String line;
        try {

            BufferedReader reader = new BufferedReader(inputStreamReader);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(Pattern.quote("["), 2);
                if (parts.length >= 2) {
                    String key = parts[0];
                    Node node = new Node();
                    node.infoSet = new StringBuilder(key);
                    String value = parts[1];
                    String[] parts2 = value.split(",");
                    node.strategySum[0] = Double.parseDouble(parts2[0]);
                    node.strategySum[1] = Double.parseDouble(parts2[1]);
                    node.strategySum[2] = Double.parseDouble(parts2[2]);
                    node.strategySum[3] = Double.parseDouble(parts2[3].split(Pattern.quote("]"))[0]);
                    nodeMap.put(key, node);
                } else {
                    System.out.println("ignoring line: " + line);
                }
            }

            for (String key : nodeMap.keySet()) {
                // System.out.println(nodeMap.get(key));
            }
        }catch(IOException e) {
            System.out.println("Wrong or inexistant file!!");
        }
        super.onCreate(savedInstanceState);
        this.setTitle("Poker Texas Holdem");
        setContentView(R.layout.activity_in_game);
        final int nronda = 0;
        //Creamos os xogadores
        Player jugador1 = new Player("player1", 10000,
                ((ImageView) findViewById(R.id.player1card1)),
                ((ImageView) findViewById(R.id.player1card2)),
                ((TextView) findViewById(R.id.player1Points))
        );
        jugador1.setBlind(Player.BIG_BLIND);
        jugadores.add(jugador1);

        final Player persona = new Player("person", 10000,
                ((ImageView) findViewById(R.id.personcard1)),
                ((ImageView) findViewById(R.id.personcard2)),
                ((TextView) findViewById(R.id.personPoints))
        );
        persona.setBlind(Player.SMALL_BLIND);
        jugadores.add(persona);

        int i = 0;

        nuevamano();


        Button newroundbutton = (Button) findViewById(R.id.newround);
        newroundbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevamano();
                repartir(null, 0);
            }
        });

        Button startbutton = (Button) findViewById(R.id.startbutton);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repartir(null, 0);
            }
        });

        Button backbutton = (Button) findViewById(R.id.BackButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button betbutton = (Button) findViewById(R.id.betbutton);
        betbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repartir("r", callValue);
            }
        });

        Button foldButton = (Button) findViewById(R.id.foldButton);
        foldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repartir("c", 0);
            }
        });
    }


    public void nuevamano() {
        this.rondastotales++;
        this.cartasenbaraja = functions.nueva_baraja();
        this.history = "rr";
        this.current_round = 0;
        findViewById(R.id.newround).setEnabled(false);
        findViewById(R.id.newround).setVisibility(View.INVISIBLE);

        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).clearCartaMesa();
            jugadores.get(i).start_playing();
            int index = (int) (Math.random() * cartasenbaraja.size());
            Card card1 = cartasenbaraja.get(index);
            card1.setPosicion("Mano");
            cartasenbaraja.remove(index);
            index = (int) (Math.random() * cartasenbaraja.size());
            Card card2 = cartasenbaraja.get(index);
            cartasenbaraja.remove(index);
            card2.setPosicion("Mano");
            jugadores.get(i).setcards(card1, card2);
            jugadores.get(i).enseñar_reverso();
            if (jugadores.get(i).getname().equals("person")) {
                ImageView aux = findViewById(R.id.personcard1);
                functions.enseñar_carta(aux, card1.getId());
                aux = findViewById(R.id.personcard2);
                functions.enseñar_carta(aux, card2.getId());
            }
        }

        functions.enseñar_carta((ImageView) findViewById(R.id.deck), "reverso");
        ((TextView) findViewById(R.id.cartasendeck)).setText(String.valueOf(cartasenbaraja.size()));
        findViewById(R.id.tablecard1).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard2).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard3).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard4).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard5).setVisibility(View.INVISIBLE);
    }

    public void setCurrentPot(int potValue) {
        this.currentPot = potValue;
        ((TextView) findViewById(R.id.currentPot)).setText(String.valueOf(this.currentPot));
    }

    public void addToCurrentPot(int amount) {
        this.currentPot += amount;
        ((TextView) findViewById(R.id.currentPot)).setText(String.valueOf(this.currentPot));
    }

    public void refreshpoints() {
        Iterator<Player> i = jugadores.iterator();
        while (i.hasNext()) {
            Player player = i.next();
            player.getTextPuntos().setText(Integer.toString(player.getMoney()));
        }
    }
    String history = "rr";
    public void repartir(String playerAction, int amount_value) {
        this.current_round++;
        Player maquina = jugadores.get(0);
        Player persona = jugadores.get(1);
        //System.out.println(this.history);
        int contador = 0;
        char machineAction = maquina.getdecision(new StringBuilder(this.history));
        if(current_round == 3 || current_round == 5 || current_round == 7){
            if(playerAction.charAt(0) == machineAction){
                this.current_round++;
            }
        }

        switch (current_round) {
            case 1:
                refreshpoints();
                findViewById(R.id.player1Points).setVisibility(View.VISIBLE);
                findViewById(R.id.player2Points).setVisibility(View.VISIBLE);
                findViewById(R.id.player3Points).setVisibility(View.VISIBLE);
                findViewById(R.id.personPoints).setVisibility(View.VISIBLE);
                findViewById(R.id.startbutton).setVisibility(View.INVISIBLE);
                findViewById(R.id.startbutton).setEnabled(false);
                TextView textodeck = (TextView) findViewById(R.id.cartasendeck);
                textodeck.setText(Integer.toString(cartasenbaraja.size()));
                textodeck.setVisibility(View.VISIBLE);
                findViewById(R.id.cartasendeck).setVisibility(View.VISIBLE);
                contador = 0;
                for (Player x : jugadores){
                    makePlay('c', x, 50 * x.getBlind(), 0);

                }
                refreshpoints();
                break;
            case 2: //PREFLOP
                int index = (int) (Math.random() * cartasenbaraja.size());
                Card card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);//Queimamos unha carta
                int i = 0;
                //Enseñamos a primeira carta da mesa(xerada arriba)
                ImageView aux = (ImageView) findViewById(R.id.tablecard1);
                functions.enseñar_carta(aux, card1.getId());
                for (Player x : jugadores) {
                    x.newCarta(card1);
                }
                //Queimamos unha carta
                index = (int) (Math.random() * cartasenbaraja.size());
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.burned);
                functions.enseñar_carta(aux, "reverso");
                //Collemos unha carta nova
                index = (int) (Math.random() * cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard2);
                functions.enseñar_carta(aux, card1.getId());
                for (Player x : jugadores) {
                    x.newCarta(card1);
                }
                index = (int) (Math.random() * cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard3);
                functions.enseñar_carta(aux, card1.getId());
                for (Player x : jugadores) {
                    x.newCarta(card1);
                }
                makePlay(playerAction.charAt(0), persona, amount_value, current_round);
                makePlay(machineAction, maquina, amount_value, current_round);
                refreshpoints();
                TextView auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("1");
                auxText.setVisibility(View.VISIBLE);
                break;

            case 3: //FLOP
                makePlay(playerAction.charAt(0), persona, amount_value, current_round);
                makePlay(machineAction, maquina, amount_value, current_round);
                refreshpoints();
                i = 0;
                break;
            case 4: //FLOP
                index = (int) (Math.random() * cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard4);
                functions.enseñar_carta(aux, card1.getId());
                for (Player x : jugadores) {
                    x.newCarta(card1);
                }
                makePlay(playerAction.charAt(0), persona, amount_value, current_round);
                makePlay(machineAction, maquina, amount_value, current_round);
                refreshpoints();
                auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("2");
                i = 0;
                break;
            case 5: //TURN
                makePlay(playerAction.charAt(0), persona, amount_value, current_round);
                makePlay(machineAction, maquina, amount_value, current_round);
                refreshpoints();
                auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("3");
                i = 0;
                break;
            case 6: //TURN
                index = (int) (Math.random() * cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard5);
                functions.enseñar_carta(aux, card1.getId());
                for (Player x : jugadores) {
                    x.newCarta(card1);
                }
                makePlay(playerAction.charAt(0), persona, amount_value, current_round);
                makePlay(machineAction, maquina, amount_value, current_round);
                refreshpoints();
                auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("4");
                i = 0;
                break;
            case 7: //RIVER
                makePlay(playerAction.charAt(0), persona, amount_value, current_round);
                makePlay(machineAction, maquina, amount_value, current_round);
                refreshpoints();

                break;
            case 8: //RIVER
                makePlay(playerAction.charAt(0), persona, amount_value, current_round);
                makePlay(machineAction, maquina, amount_value, current_round);
                refreshpoints();
                i = 0;
                long startTime = System.nanoTime();
                Integer[] puntuaciones = new Integer[jugadores.size()];
                for (contador = 0; contador < jugadores.size(); contador++) {
                    puntuaciones[contador] = (jugadores.get(contador).getState() != Player.FOLD) ?
                            jugadores.get(contador).calcularpuntuacion() : -1;
                }
                int indexganador = Arrays.asList(puntuaciones).indexOf(functions.maximo(puntuaciones));


                tiempoEjecucion += System.nanoTime() - startTime;
                vecesEjecucion++;
                functions.imprimirdebug("Tiempo medio de ejecucion = " + tiempoEjecucion / vecesEjecucion / 1000 + "ns", 1);

                functions.imprimirdebug("Ha ganado el jugador" + (indexganador + 1) + " con " + jugadores.get(indexganador).getPuntuacion() + " puntos", 0);
                jugadores.get(indexganador).win(currentPot);
                setCurrentPot(0);
                //this.callValue = 100;
                refreshpoints();
                if (jugadores.get(jugadores.size() - 1).getMoney() <= 0) {
                    Toast.makeText(this.getBaseContext(), "You lost!", Toast.LENGTH_SHORT);
                    finish();
                }
                Toast result = (jugadores.get(indexganador).getname() == "person") ?
                        Toast.makeText(this.getBaseContext(), "You win, congrats!", Toast.LENGTH_SHORT) :
                        Toast.makeText(this.getBaseContext(), "You lost!", Toast.LENGTH_SHORT);
                result.show();
                for (i = 0; i < jugadores.size(); i++) {
                    Player player = jugadores.get(i);
                    if (player.is_playing()) {
                        player.enseñar_cartas();
                    }
                    if (player.getMoney() <= 0) {
                        player.cartas_visibles(false);
                        jugadores.remove(player);
                    } else player.setState(Player.CALL);
                }
                findViewById(R.id.newround).setEnabled(true);
                findViewById(R.id.newround).setVisibility(View.VISIBLE);
                break;
            default:
                functions.imprimirdebug("NON SE PODEN XOGAR MAIS RONDAS,LEVAMOS" + current_round, 2);

        }
        return;
    }

    public void makePlay(char action, Player player, int amount, int nrondas) {

    this.history += action;
        switch (action) {
            case 'c':
                if (player.getState() == Player.ALL_IN || player.getState() == Player.FOLD) return;
                if (amount >= player.getMoney()) {
                    player.setState(Player.ALL_IN);
                }
                int loose_value = (amount >= player.getMoney()) ? player.getMoney() : amount;
                player.loose(loose_value);
                addToCurrentPot(amount);
                break;
            case 'f':
                player.setState(Player.FOLD);
                player.stop_playing();
        }
    }

    public static Node gisnoc(StringBuilder infoSet) {
        Node node = nodeMap.get(infoSet.toString());
        if (node == null) {
            node = new Node();
            node.infoSet = infoSet;
            // System.out.print(infoSet + ":--" + nodeMap.size() + "\n");
            System.out.println(infoSet + "-------------no estaba");
            nodeMap.put(infoSet.toString(), node);
        }else {
            System.out.println(infoSet + "------------estaba");
        }// else System.out.println(infoSet);
        return node;
    }
}

