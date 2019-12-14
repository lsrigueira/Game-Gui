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


public class InGame extends AppCompatActivity {

    ArrayList<Card> cartasenbaraja;
    final ArrayList<Player> jugadores = new ArrayList<>();
    int nrondas = 0;
    int rondastotales=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        final int nronda = 0;
        //Creamos os xogadores
        Player jugador1 = new Player("player1",1000, ((ImageView) findViewById(R.id.player1card1)),((ImageView) findViewById(R.id.player1card2)) );
        jugadores.add(jugador1);
        Player jugador2 = new Player("player2",1000, ((ImageView) findViewById(R.id.player2card1)),((ImageView) findViewById(R.id.player2card2)) );
        jugadores.add(jugador2);
        Player jugador3 = new Player("player3",1000, ((ImageView) findViewById(R.id.player3card1)),((ImageView) findViewById(R.id.player3card2)) );
        jugadores.add(jugador3);
        Player persona = new Player("person",1000, ((ImageView) findViewById(R.id.personcard1)),((ImageView) findViewById(R.id.personcard2)) );
        jugadores.add(persona);

        int i=0;

        nuevamano();



        Button newroundbutton = (Button) findViewById(R.id.newround);
        newroundbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevamano();
            }
        });

        Button startbutton = (Button) findViewById(R.id.startbutton);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repartir();
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

    public void nuevamano(){
        this.rondastotales++;
        this.cartasenbaraja = functions.nueva_baraja();
        this.nrondas =0;
        if(this.rondastotales>1)this.nrondas++;
        findViewById(R.id.newround).setEnabled(false);
        findViewById(R.id.newround).setVisibility(View.INVISIBLE);

        for(int i=0;i<jugadores.size();i++){
            jugadores.get(i).clearCartaMesa();
            jugadores.get(i).start_playing();
            int index =  (int) (Math.random()*cartasenbaraja.size());
            Card card1 = cartasenbaraja.get(index);
            card1.setPosicion("Mano");
            cartasenbaraja.remove(index);
            index = (int) (Math.random()*cartasenbaraja.size());
            Card card2 = cartasenbaraja.get(index);
            cartasenbaraja.remove(index);
            card2.setPosicion("Mano");
            jugadores.get(i).setcards(card1,card2);
            jugadores.get(i).enseñar_reverso();
            if(jugadores.get(i).getname().equals("person")){
                ImageView aux = findViewById(R.id.personcard1);
                functions.enseñar_carta(aux,card1.getId());
                aux = findViewById(R.id.personcard2);
                functions.enseñar_carta(aux,card2.getId());
            }
        }

        functions.enseñar_carta((ImageView)findViewById(R.id.deck),"reverso");
        ((TextView) findViewById(R.id.cartasendeck)).setText(String.valueOf(cartasenbaraja.size()));
        findViewById(R.id.tablecard1).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard2).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard3).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard4).setVisibility(View.INVISIBLE);
        findViewById(R.id.tablecard5).setVisibility(View.INVISIBLE);
    }

    public void refreshpoints(){
        ((TextView) findViewById(R.id.player1Points)).setText(String.valueOf(jugadores.get(0).getMoney()));
        ((TextView) findViewById(R.id.player2Points)).setText(String.valueOf(jugadores.get(1).getMoney()));
        ((TextView) findViewById(R.id.player3Points)).setText(String.valueOf(jugadores.get(2).getMoney()));
        ((TextView) findViewById(R.id.personPoints)).setText(String.valueOf(jugadores.get(3).getMoney()));
    }

    public void backToMenu(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void repartir(){
        this.nrondas++;

        int index =  (int) (Math.random()*cartasenbaraja.size());
        Card card1 = cartasenbaraja.get(index);
        card1.setPosicion("Mesa");
        cartasenbaraja.remove(index);//Queimamos unha carta
        int contador = 0;
        switch (nrondas){
            case 1:
                refreshpoints();
                findViewById(R.id.player1Points).setVisibility(View.VISIBLE);
                findViewById(R.id.player2Points).setVisibility(View.VISIBLE);
                findViewById(R.id.player3Points).setVisibility(View.VISIBLE);
                findViewById(R.id.personPoints).setVisibility(View.VISIBLE);
                findViewById(R.id.startbutton).setVisibility(View.INVISIBLE);
                findViewById(R.id.startbutton).setEnabled(false);
                TextView textodeck = (TextView) findViewById(R.id.cartasendeck);
                textodeck.setText( Integer.toString(cartasenbaraja.size()) );
                textodeck.setVisibility(View.VISIBLE);
                findViewById(R.id.cartasendeck).setVisibility(View.VISIBLE);
                contador=0;
                for(Player x:jugadores){x.getdecision(this.nrondas);}

                break;
            case 2:
                int i=0;
                //Enseñamos a primeira carta da mesa(xerada arriba)
                ImageView aux = (ImageView) findViewById(R.id.tablecard1);
                functions.enseñar_carta(aux,card1.getId());
                for(Player x:jugadores){x.newCarta(card1);}
                //Queimamos unha carta
                index = (int) (Math.random()*cartasenbaraja.size());
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.burned);
                functions.enseñar_carta(aux,"reverso");
                //Collemos unha carta nova
                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard2);
                functions.enseñar_carta(aux,card1.getId());
                for(Player x:jugadores){x.newCarta(card1);}
                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux = (ImageView) findViewById(R.id.tablecard3);
                functions.enseñar_carta(aux,card1.getId());
                for(Player x:jugadores){x.newCarta(card1);x.getdecision(this.nrondas);}
                TextView auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("1");
                auxText.setVisibility(View.VISIBLE);

                break;
            case 3:
                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux =  (ImageView) findViewById(R.id.tablecard4);
                functions.enseñar_carta(aux,card1.getId());
                for(Player x:jugadores){x.newCarta(card1);x.getdecision(this.nrondas);}
                auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size()));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("2");
                i=0;
                break;
            case 4:
                index = (int) (Math.random()*cartasenbaraja.size());
                card1 = cartasenbaraja.get(index);
                card1.setPosicion("Mesa");
                cartasenbaraja.remove(index);
                aux =  (ImageView) findViewById(R.id.tablecard5);
                functions.enseñar_carta(aux,card1.getId());
                for(Player x:jugadores){
                    x.newCarta(card1);
                    if(x.getdecision(this.nrondas).equals("fold")){
                       x.stop_playing();
                       x.cartas_visibles(false);
                    }
                }
                auxText = (TextView) findViewById(R.id.cartasendeck);
                auxText.setText(Integer.toString(cartasenbaraja.size() ));
                auxText = (TextView) findViewById(R.id.cartasburned);
                auxText.setText("3");
                i=0;

                break;
            case 5:
                i=0;
                Integer [] puntuaciones= new Integer[jugadores.size()];
                for(contador=0;contador<jugadores.size();contador++){
                    puntuaciones[contador]=jugadores.get(contador).calcularpuntuacion();
                }
                int indexganador=Arrays.asList(puntuaciones).indexOf(functions.maximo(puntuaciones));
                functions.imprimirdebug("Ha ganado el jugador"+ (indexganador+1)+" con "+jugadores.get(indexganador).getPuntuacion()+" puntos",0);
                functions.cashflow(jugadores,indexganador);
                refreshpoints();
                for(i=0;i<jugadores.size();i++){if(jugadores.get(i).is_playing())jugadores.get(i).enseñar_cartas();}
                findViewById(R.id.newround).setEnabled(true);
                findViewById(R.id.newround).setVisibility(View.VISIBLE);
             default:
                 functions.imprimirdebug("NON SE PODEN XOGAR MAIS RONDAS,LEVAMOS"+nrondas,2);
        }
    }

}
