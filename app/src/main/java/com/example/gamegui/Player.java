package com.example.gamegui;


import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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
    static final int CASTTOBESTCARDOUTPLAY =1;
    private Card card1;
    private Card card2;
    private ImageView imagencard1;
    private ImageView imagencard2;
    private ArrayList<Card> cartastot;
    private ArrayList<Card> cartasmesa;
    private String nome;
    private boolean horizontal;
    private int money;
    private int moneybet=100;
    private boolean playing = false;
    private long puntuacion;
    private long puntuacionmesa;

    public Player(String nome, int money, ImageView imagencarta1,ImageView imagencarta2){
        this.nome=nome;
        this.money=money;
        this.imagencard1=imagencarta1;
        this.imagencard2=imagencarta2;
        cartastot= new ArrayList<>();
        cartasmesa= new ArrayList<>();
    }

    public long getPuntuacion(){return this.puntuacion;}

    public int getMoney(){
        return this.money;
    }

    public int getBet(){
        return this.moneybet;
    }

    public String getdecision(int nronda){

        /*if(nronda==4&&calcularpuntuacion()==0){
            this.playing = false;
            return "fold";
        }*/
        return "bet";
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

    public ArrayList<Card> getcartastot(){return this.cartastot;}

    public ArrayList<Card> getcartasmesa(){return this.cartasmesa;}

    public void enseñar_reverso(){
        functions.enseñar_carta(this.imagencard1,"reverso");
        functions.enseñar_carta(this.imagencard2,"reverso");
    }

    public void enseñar_cartas(){
        functions.enseñar_carta(this.imagencard1,this.card1.getId());
        functions.enseñar_carta(this.imagencard2,this.card2.getId());
    }

    public void stop_playing(){
        this.playing=false;
    }

    public boolean is_playing(){
        return this.playing;
    }

    public void start_playing(){
        this.playing=true;
    }

    public void cartas_visibles(boolean visibilidad){
        if(visibilidad){
            this.imagencard1.setVisibility(View.VISIBLE);
            this.imagencard2.setVisibility(View.VISIBLE);
        }else{
            this.imagencard1.setVisibility(View.INVISIBLE);
            this.imagencard2.setVisibility(View.INVISIBLE);
        }
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

    public int bestcard(boolean completo){
        ArrayList<Card> cartas = new ArrayList<>();
        int bestcard = 0;
        if(completo){
            cartas.addAll(cartastot);
        }else{
            cartas.addAll(cartasmesa);
        }
        for(int contador=0;contador<cartas.size();contador++){
            if(!cartas.get(contador).getUsed()){
                if(cartas.get(contador).getRankValue() > bestcard){bestcard=cartas.get(contador).getRankValue();}
            }
        }
        return bestcard;
    }

    public void hasPair() {

        int puntos = 0;
        //Se tiveesemos duas parexas esto sería "doble parexa" e non estariamos aqui
        HashMap<String,String> mapa = new HashMap<>();
        Card carta;
        for(int contador=0;contador<cartastot.size();contador++){

            carta = cartastot.get(contador);
            mapa.put(carta.getRank(),"ola");
            if(mapa.size()<=contador){
                puntos= PAIR * CASTTOBESTPLAY +carta.getRankValue()* CASTTOBESTCARDINPLAY;
                if(contador < cartasmesa.size()){
                    this.puntuacionmesa = puntos;
                }

                this.puntuacion = puntos;
                functions.imprimirdebug(this.getname()+":ENCONTRADA PAREXA-->"+puntos,1);
                return;
            }


            /*Card tocompare =cartastot.get(contador);
            int i=0;
            for(i=0;i<cartastot.size();i++){
                Card comparable = cartastot.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getRank().equals(tocompare.getRank())){
                    puntos= PAIR * CASTTOBESTPLAY +comparable.getRankValue()* CASTTOBESTCARDINPLAY;;
                    functions.imprimirdebug(this.getname()+":ENCONTRADA PAREXA-->"+puntos,1);
                    this.puntuacion=puntos;
                    if(i <= cartasmesa.size()){this.puntuacionmesa=puntos;}
                }
            }*/
        }
    }

    public void hasDoblePair() {


        int puntos = 0;
        //Se tiveesemos duas parexas esto sería "doble parexa" e non estariamos aqui
        HashMap<String,String> mapa = new HashMap<>();
        Card carta;
        int foundbefore = 0;
        for(int contador=0;contador<cartastot.size();contador++){

            carta = cartastot.get(contador);
            mapa.put(carta.getRank(),"ola");
            if(mapa.size()<=contador){

                if(foundbefore!=0){
                    puntos = DOUBLEPAIR * CASTTOBESTPLAY;
                    switch (((Integer) carta.getRankValue()).compareTo(foundbefore) ){
                        case -1://foundbefore>carta
                            puntos+=foundbefore* CASTTOBESTCARDINPLAY +carta.getRankValue()* CASTTOWORSTCARDINPLAY;
                            break;
                        case 0://foundbefore=carta
                            puntos+=foundbefore* CASTTOBESTCARDINPLAY +foundbefore* CASTTOWORSTCARDINPLAY;
                            break;
                        case 1://foundbefore>carta
                            puntos +=carta.getRankValue()* CASTTOBESTCARDINPLAY +foundbefore* CASTTOWORSTCARDINPLAY;
                            break;
                        default:
                            functions.imprimirdebug("ALGO RARO PASA NO DOUBLE PAIR COMPARANDO",0);
                    }
                    if(contador < cartasmesa.size()){
                        this.puntuacionmesa = puntos;
                    }

                    this.puntuacion = puntos;
                    functions.imprimirdebug(this.getname()+":ENCONTRADA DOBLE PAREXA-->"+puntos,1);
                    return;
                }
                foundbefore = carta.getRankValue();
                mapa.put(String.valueOf(contador+100),"basura");


            }






        /*int firstpairnumber = 0;
        int puntos = 0;
        //Se tivesemos duas parexas esto sería "doble parexa" e non estariamos aqui
        for(int contador=0;contador<cartas.size();contador++){
            Card tocompare =cartas.get(contador);
            int i=0;
            for(i=0;i<cartas.size();i++){
                Card comparable = cartas.get(i);//Cambiamnos ese if para evitar falsas dobles parexas
                if(comparable.getId().equals(tocompare.getId()) || comparable.getRankValue()== firstpairnumber )  continue;
                else if(comparable.getRank().equals(tocompare.getRank())){
                    if(firstpairnumber == 0){
                        firstpairnumber=comparable.getRankValue();
                    }else {
                        puntos = DOUBLEPAIR * CASTTOBESTPLAY;
                        switch (((Integer) comparable.getRankValue()).compareTo(firstpairnumber) ){
                            case -1://firstpair>comparable
                                puntos+=firstpairnumber* CASTTOBESTCARDINPLAY +comparable.getRankValue()* CASTTOWORSTCARDINPLAY;
                                break;
                            case 0://firstpai=comparabñe
                                puntos+=firstpairnumber* CASTTOBESTCARDINPLAY +firstpairnumber* CASTTOWORSTCARDINPLAY;
                                break;
                            case 1://comparable>firstpairnumber
                                puntos +=comparable.getRankValue()* CASTTOBESTCARDINPLAY +firstpairnumber* CASTTOWORSTCARDINPLAY;
                                break;
                            default:
                                functions.imprimirdebug("ALGO RARO PASA NO DOUBLE PAIR COMPARANDO",0);
                        }
                        functions.imprimirdebug(this.getname()+":ENCONTRADA DOBLE PAREXA-->"+puntos,1);
                        return puntos;
                    }
                }
            }*/
        }
        return ;
    }

    public void hastrio(){

        HashMap<String,String> mapa = new HashMap<>();
        Card carta;
        ArrayList<String> cartasrepetidas = new ArrayList<>();
        int puntos=0;
        for(int contador=0;contador<cartastot.size();contador++){

            carta = cartastot.get(contador);
            mapa.put(carta.getRank(),"ola");
            if(mapa.size()<=contador ) {//carta repetida
                if(cartasrepetidas.contains(carta.getRank())){
                    puntos+= TRIO * CASTTOBESTPLAY +carta.getRankValue()* CASTTOBESTCARDINPLAY;
                    if(contador<cartasmesa.size()){
                        this.puntuacionmesa = puntos;
                    }

                    functions.imprimirdebug(this.getname()+":ENCONTRADO TRIO-->"+puntos,1);
                    this.puntuacion = puntos;
                    return;

                }
                cartasrepetidas.add(carta.getRank());
                mapa.put(String.valueOf(contador+100),"basura");
            }






            /*foundbefore = false;
            Card tocompare =cartas.get(contador);
            int i=0;
            for(i=0;i<cartas.size();i++){
                Card comparable = cartas.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getRank().equals(tocompare.getRank())){
                    if(foundbefore){
                        puntos+= TRIO * CASTTOBESTPLAY +comparable.getRankValue()* CASTTOBESTCARDINPLAY;
                        functions.imprimirdebug(this.getname()+":ENCONTRADO TRIO-->"+puntos,1);
                        return puntos;}
                    else foundbefore=true;
                }
            }*/
        }
        return ;
    }

    public void haspoker(){


        HashMap<String,String> mapa = new HashMap<>();
        Card carta;
        ArrayList<String> cartasrepetidas = new ArrayList<>();
        ArrayList<String> cartasrepetidas2 = new ArrayList<>();
        int puntos=0;
        for(int contador=0;contador<cartastot.size();contador++){

            carta = cartastot.get(contador);
            mapa.put(carta.getRank(),"ola");
            if(mapa.size()<=contador ) {//carta repetida
                if(cartasrepetidas.contains(carta.getRank())){

                    if(cartasrepetidas2.contains(carta.getRank())){
                        puntos+= POKER * CASTTOBESTPLAY +carta.getRankValue()* CASTTOBESTCARDINPLAY;
                        if(contador<cartasmesa.size()){
                            this.puntuacionmesa = puntos;
                        }
                        this.puntuacion = puntos;
                        functions.imprimirdebug(this.getname()+":ENCONTRADO POKER-->"+puntos,1);
                        return ;

                    }else{
                        cartasrepetidas2.add(carta.getRank());
                    }
                }
                cartasrepetidas.add(carta.getRank());
                mapa.put(String.valueOf(contador+100),"basura");
            }

        /*
        ArrayList<Card> cartas = new ArrayList<>();
        if(completo){
            cartas.addAll(cartastot);
        }else{
            cartas.addAll(cartasmesa);
        }

        int foundbefore;
        int puntos =0;
        for(int contador=0;contador<cartas.size();contador++){
            foundbefore = 0;
            Card tocompare =cartas.get(contador);
            int i=0;
            for(i=0;i<cartas.size();i++){
                Card comparable = cartas.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getRank().equals(tocompare.getRank())){

                    if(foundbefore == 2){
                        puntos+= POKER * CASTTOBESTPLAY +comparable.getRankValue()* CASTTOBESTCARDINPLAY;
                        functions.imprimirdebug(this.getname()+":ENCONTRADO POKER-->"+puntos,1);
                        return puntos;
                    }else foundbefore++;
                }
            }*/
        }
        return ;
    }

    //TODO Aqui cata mais alta da a da mesa non a do suit

    public void hasflush() {
        Card carta;
        int[] colors = new int[4];
        int cartaMasAlta = 0;
        for(int contador=0;contador<cartastot.size();contador++){

            carta = cartastot.get(contador);
            cartaMasAlta = carta.getRankValue()>cartaMasAlta? carta.getRankValue():cartaMasAlta;
            switch(carta.getSuit()) {
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
            int puntos = 0;
            if(colors[0]>4 ||  colors[1]>4 || colors[2]>4 || colors[3]>4) {
                puntos = FLUSH * CASTTOBESTPLAY +cartaMasAlta* CASTTOBESTCARDINPLAY;
                this.puntuacion = puntos;
            }
            if(contador < cartasmesa.size()){
                this.puntuacionmesa = puntos;
            }
        }
    }
        /*ArrayList<Card> cartas = new ArrayList<>();
        if(completo){
            cartas.addAll(cartastot);
        }else{
            cartas.addAll(cartasmesa);
        }

        int foundbefore=0;
        int puntos =0;
        for(int contador=0;contador<cartas.size();contador++){
            foundbefore =0;
            Card tocompare =cartas.get(contador);
            int i=0;
            for(i=0;i<cartas.size();i++){
                Card comparable = cartas.get(i);
                if(comparable.getId().equals(tocompare.getId()))continue;
                else if(comparable.getSuit().equals(tocompare.getSuit())){
                    if(foundbefore == 3){
                        puntos+= FLUSH * CASTTOBESTPLAY;
                        return puntos;}
                    else foundbefore++;
                }
            }
        }
        return 0;
    }*/

    public void newCarta(Card carta){
        this.cartastot.add(0,carta);
        this.puntuacion = 0;
        this.puntuacionmesa = 0;
        if(carta.getPosicion().equals("Mesa")){
            this.cartasmesa.add(0,carta);
        }
        for(int i=0;i<cartastot.size();i++){cartastot.get(i).setUsed(false);}
        for(int i=0;i<cartasmesa.size();i++){cartasmesa.get(i).setUsed(false);}
    }

    public void clearCartaMesa(){
        this.playing=true;
        this.cartastot = new ArrayList<>();
        this.cartasmesa = new ArrayList<>();
    }

    /*
    A puntuacion sera un int de formato xx-yy-yy-zz onde as letras solo indican o numero de dixistos e a orixe do calculo.Explicacion:
        XX: Indica se o xogar ten parexa,doble-parexa,TRIO...
        YY: Indica as cartas que usou para chegar ahí, non é o mesmo unha parexa de ases que de douses(hai 2 para distinguir as doble-parexas)
        ZZ: Indica a carta mais alta que non se empregou para o cálculo de XX
     */

    public int calcularpuntuacion(){

        int puntosmesa=0;

        int highcardmesa = bestcard(false);
        puntosmesa = puntosmesa + highcardmesa;
        functions.imprimirdebug("CARTAS DA MESA\n"+cartasmesa +"\nAS MIÑAS CARTAS\n"+cartastot,3);
        functions.imprimirdebug("OS PUNTOS DA MESA-->"+puntosmesa,1);
        functions.imprimirdebug(this.getname()+" CARTA MAIS ALTA-->"+highcardmesa,3);
        int puntos=0;
        hasflush();
        puntos = ((int) this.puntuacion) - ((int)this.puntuacionmesa);
        if(puntos!=0) {this.puntuacion = (puntos-puntosmesa+bestcard(true)); return ((int) this.puntuacion);}
        haspoker();
        puntos = ((int) this.puntuacion) - ((int)this.puntuacionmesa);
        if(puntos!=0){this.puntuacion = (puntos-puntosmesa+bestcard(true)); return ((int) this.puntuacion);}
        hastrio();
        puntos = ((int) this.puntuacion) - ((int)this.puntuacionmesa);
        if(puntos!=0){this.puntuacion = (puntos-puntosmesa+bestcard(true)); return ((int) this.puntuacion);}
        hasDoblePair();
        puntos = ((int) this.puntuacion) - ((int)this.puntuacionmesa);
        if(puntos !=0){this.puntuacion = (puntos-puntosmesa+bestcard(true)); return ((int) this.puntuacion);}
        hasPair();
        puntos = ((int) this.puntuacion) - ((int)this.puntuacionmesa);
        if(puntos!=0){this.puntuacion = (puntos-puntosmesa+bestcard(true)); return ((int) this.puntuacion);}
        return 0;


    }

}
