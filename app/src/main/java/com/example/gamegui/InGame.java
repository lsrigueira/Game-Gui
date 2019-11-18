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


public class InGame extends AppCompatActivity {

    final ArrayList<String> cartasenbaraja = new ArrayList<>();
    final ArrayList<Player> jugadores = new ArrayList<>();
    int nrondas = 0;


    List<String> cartaslist = Arrays.asList("corazones_10","corazones_5","corazones_7","corazones_8","corazones_9",
            "corazones_as","corazones_k","corazones_q","diamantes_10","diamantes_5","diamantes_7","diamantes_9",
            "diamantes_as","diamantes_k", "diamantes_q","picas_10","picas_5","picas_7","picas_8","picas_9","picas_as",
            "picas_k","picas_q","treboles_5","treboles_7","treboles_8","treboles_9","treboles_as","treboles_k",
            "treboles_q"
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        int nronda = 0;

        cartasenbaraja.addAll(cartaslist);

        Player jugador1 = new Player(0,0,true,"player1",1000);
        jugadores.add(jugador1);
        Player jugador2 = new Player(0,0,false,"player2",1000);
        jugadores.add(jugador2);
        Player jugador3 = new Player(0,0,false,"player3",1000);
        jugadores.add(jugador3);
        Player persona = new Player(0,0,false,"person",1000);
        jugadores.add(persona);

            int i=0;
            System.out.println(cartasenbaraja.size());
            //TODO este bucle é mais comodo cun in range
            for(i=0;i<jugadores.size();i++){
                int index =  (int) (Math.random()*cartasenbaraja.size());
                String card1 = cartasenbaraja.get(index);
                cartasenbaraja.remove(index);
                index = (int) (Math.random()*cartasenbaraja.size());
                String card2 = cartasenbaraja.get(index);
                cartasenbaraja.remove(index);
                jugadores.get(i).setcards(card1,card2);
                if(jugadores.get(i).getname().equals("person")){
                    ImageView aux = (ImageView) findViewById(R.id.personcard1);
                    functions.enseñar_carta(aux,card1);
                    aux = (ImageView) findViewById(R.id.personcard2);
                    functions.enseñar_carta(aux,card2);
                }
            }

            Button startbutton = (Button) findViewById(R.id.startbutton);
            startbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    empezar();
                }
            });

            Button backbutton = (Button) findViewById(R.id.BackButton);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backToMenu();
                }
            });

            Button betbutton = (Button) findViewById(R.id.betbutton);
            betbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    repartir();
                }
            });;

    }

    public void backToMenu(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void repartir(){
        this.nrondas++;

        int index =  (int) (Math.random()*cartasenbaraja.size());
        String card1 = cartasenbaraja.get(index);
        cartasenbaraja.remove(index);//Queimamos unha carta
        int contador = 0;
        for(contador=0;contador<jugadores.size();contador++){
            jugadores.get(contador).newCartaMesa(card1);
        }

        System.out.println(nrondas);
        switch (nrondas){
            case 0:
                break;
            case 1:
                int i=0;
                ImageView aux = (ImageView) findViewById(R.id.tablecard1);
                functions.enseñar_carta(aux,card1);

                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);

                aux = (ImageView) findViewById(R.id.burned);
                functions.enseñar_carta(aux,"reverso");
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard2);
                functions.enseñar_carta(aux,card1);
                for(i=0;i<jugadores.size();i++){
                    jugadores.get(i).newCartaMesa(card1);
                }
                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard3);
                functions.enseñar_carta(aux,card1);
                for(i=0;i<jugadores.size();i++){
                    jugadores.get(i).newCartaMesa(card1);
                }
                TextView auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()+2));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("1");
                auxText.setVisibility(View.VISIBLE);
                break;
            case 2:
                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                aux =  (ImageView) findViewById(R.id.tablecard4);
                functions.enseñar_carta(aux,card1);
                for(i=0;i<jugadores.size();i++){
                    jugadores.get(i).newCartaMesa(card1);
                }
                cartasenbaraja.remove(index);
                auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()+1 ));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("2");
                i=0;
                for(i=0;i<jugadores.size();i++){
                    jugadores.get(i).newCartaMesa(card1);
                }
                break;
            case 3:
                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                aux =  (ImageView) findViewById(R.id.tablecard5);
                functions.enseñar_carta(aux,card1);
                for(i=0;i<jugadores.size();i++){
                    jugadores.get(i).newCartaMesa(card1);
                }
                cartasenbaraja.remove(index);
                auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size() ));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("3");
                i=0;
                break;
            case 4:
                i=0;
                for(i=0;i<jugadores.size();i++){
                    functions.enseñar_carta((ImageView)findViewById(R.id.player1card1),jugadores.get(0).getcard1());
                    functions.enseñar_carta((ImageView)findViewById(R.id.player1card2),jugadores.get(0).getcard2());
                    functions.enseñar_carta((ImageView)findViewById(R.id.player2card1),jugadores.get(1).getcard1());
                    functions.enseñar_carta((ImageView)findViewById(R.id.player2card2),jugadores.get(1).getcard2());
                    functions.enseñar_carta((ImageView)findViewById(R.id.player3card1),jugadores.get(2).getcard1());
                    functions.enseñar_carta((ImageView)findViewById(R.id.player3card2),jugadores.get(2).getcard2());

                }
             default:
                 System.out.println(nrondas);
        }

    }

    protected void empezar(){
        try {
            int ncartas = jugadores.size()*2;
            ncartas--;
            findViewById(R.id.startbutton).setVisibility(View.INVISIBLE);
            findViewById(R.id.startbutton).setEnabled(false);
            findViewById(R.id.cartasendeck).setVisibility(View.VISIBLE);
            findViewById(R.id.deck).setVisibility(View.VISIBLE);
            findViewById(R.id.player1card1).setVisibility(View.VISIBLE);
            TextView aux = (TextView) findViewById(R.id.cartasendeck);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
            //Thread.sleep(500);
            findViewById(R.id.player1card2).setVisibility(View.VISIBLE);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
            //Thread.sleep(500);
            findViewById(R.id.player2card1).setVisibility(View.VISIBLE);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
            //Thread.sleep(500);
            findViewById(R.id.player2card2).setVisibility(View.VISIBLE);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
            //Thread.sleep(500);
            findViewById(R.id.player3card1).setVisibility(View.VISIBLE);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
            //Thread.sleep(500);
            findViewById(R.id.player3card2).setVisibility(View.VISIBLE);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
            //Thread.sleep(500);
            findViewById(R.id.personcard1).setVisibility(View.VISIBLE);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
            //Thread.sleep(500);
            findViewById(R.id.personcard2).setVisibility(View.VISIBLE);
            aux.setText(Integer.toString(cartasenbaraja.size() + ncartas));
            ncartas--;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
