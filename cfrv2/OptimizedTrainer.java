
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.io.PrintWriter;
//import java.util.BitSet;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.lang.*;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.Writer;
//import java.util.TreeMap;
//import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.text.html.ImageView;

//Esta clase declara el hashmap, inicia os threads y guarda el resultado.
public class OptimizedTrainer {
	public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', DOUBLE = 'h', NUM_ACTIONS = 4; // Opciones posibles
	public static final int PREFLOP = 5, POSTFLOP = 6, TURN = 7, RIVER = 8; // Rondas
	public static final Random random = new Random();
	public static ConcurrentHashMap<String, Node> nodeMap = new ConcurrentHashMap<String, Node>(); // Modelo donde se
																									// guardan las
																									// jugadas
	private static final TrainerThread a = new TrainerThread();// Los threads, con 6 va bien
	private static final TrainerThread b = new TrainerThread();
	private static final TrainerThread c = new TrainerThread();
	private static final TrainerThread d = new TrainerThread();
	private static final TrainerThread e = new TrainerThread();
	private static final TrainerThread f = new TrainerThread();

	public static void main(String[] args) throws Exception {
//		String filePath = "modeloNewCalcularPuntuacion.ser";
//	    //HashMap<String, Node> nodeMap = new HashMap<String, Node>();
//
//	    String line;
//	    BufferedReader reader = new BufferedReader(new FileReader(filePath));
//	    while ((line = reader.readLine()) != null)
//	    {
//	        String[] parts = line.split(Pattern.quote("["), 2);
//	        if (parts.length >= 2)
//	        {
//	            String key = parts[0];
//	            Node node = new Node();
//	            node.infoSet = new StringBuilder(key);
//	            String value = parts[1];
//	            String[] parts2 = value.split(",");
//	            node.strategySum[0] = Double.parseDouble(parts2[0]);
//	            node.strategySum[1] = Double.parseDouble(parts2[1]);
//	            node.strategySum[2] = Double.parseDouble(parts2[2]);
//	            node.strategySum[3] = Double.parseDouble(parts2[3].split(Pattern.quote("]"))[0]);
//	            nodeMap.put(key, node);
//	        } else {
//	            System.out.println("ignoring line: " + line);
//	        }
//	    }
//	    reader.close();
		a.start();
		b.start();
		c.start();
		d.start();
		e.start();
		f.start();
		try {
			a.join();
			b.join();
			c.join();
			d.join();
			e.join();
			f.join();
		} catch (InterruptedException e2) {
			System.out.println("Error al iniciar los threads");
		}
		try {
			//Guardadando datos del hashmap
			FileOutputStream fos = new FileOutputStream("modelocp3.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (Node n : nodeMap.values())
				oos.writeObject(n.toString());
			oos.close();
			fos.close();
		} catch (Exception e1) {
			System.out.println("Error al guardar");
		}
		System.out.println("Done and saved");
	}
}

class TrainerThread extends Thread {
	public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', DOUBLE = 'h', NUM_ACTIONS = 4;
	public static final int PREFLOP = 5, POSTFLOP = 6, TURN = 7, RIVER = 8;
	public static final int ITERATIONS = 500000; // Iteraciones por thread
	public static final int LIMIT = 80; // Marca el limite de apuesta para todos los contando con que en cada raise se
										// sube 10 y hay 2 obligatorios al principio;
										// Lo malo de subir esto es que una partida puede hacerse largisima y la
										// velocidad de iteracion baja mucho
	public static final Random random = new Random(-System.nanoTime());
	public static ConcurrentHashMap<String, Node> nodeMap;// = new ConcurrentHashMap<StringBuilder, Node>();

	public TrainerThread() {
		nodeMap = OptimizedTrainer.nodeMap; // Asigna el modelo para que todos trabajen en el mismo
	}

	public void run() {
		// Esto es necesario para que no todos hagan lo mismo
		System.out.println("Thread started. Training...");
		train(ITERATIONS);

	}

	public void train(int iterations) {
		ArrayList<Card> deck; // Baraja
		Card[][] cards = new Card[3][5]; // Cartas de la mesa
		long timePass = 0; // Para calcular la eficiencia
		double util = 0; // Utilidad --------> 0
		StringBuilder rr = new StringBuilder("rr"); // Doble ciega obligatoria
		for (int i = 0; i < iterations; i++) {
			// nodeMap.clear();
			long time = System.nanoTime();
			deck = Card.nueva_baraja();
			cards = shufflecards(2, 2, deck);
			util += cfr(cards, rr, 1, 1, deck, false); // Ejecucion desde el nodo raiz
			timePass = (System.nanoTime() - time);
			if (i%200== 0)
				System.out.println("Iteracion:" + i + " ---- util:" + util / (i + 1) + " ----- nodeMap:"
						+ nodeMap.size() + "------" + (double) timePass / 1000000000 + " sgs.");
		}
		System.out.println("Average game value: " + util / iterations);
//		for (Node n : nodeMap.values())
//			System.out.println(n);
	}
	public static int isChanceNode(StringBuilder history) {
		int[] bets = new int[2];
		boolean talk[] = new boolean[2];
		talk[0] = false;
		talk[1] = false;
		int potValue = 0;
		int round = 0;
//		if (player != 0)
//			return false;
		for (int i = 0; i < history.length(); i++) {
			bets[i%2] = getBetValue(history.charAt(i), potValue);
			talk[i%2] = true;
			potValue += getPlayValue(history.charAt(i));

			if ((bets[0] == potValue || bets[0] == 0) && (bets[1] == potValue || bets[1] == 0)
					 && talk[0]&& talk[1]){
				round ++;
				talk[0] = false;
				talk[1] = false;
				//System.out.println(history.substring(0, i + 1));
				//if(history.length() - i < 4) break;
			}
		}

		if ((bets[0] == potValue || bets[0] == 0) && (bets[1] == potValue || bets[1] == 0)
				 && (!talk[0] && !talk[1]))
	{//System.out.println(talk[0] +":"+ talk[1] + ":" + history + ":" + round);
			return round + 4;}
		else {//System.out.println((talk[0] && talk[1] && talk[2]+ ":"talk[3] + ":" + history + ":" + round);
			return -1;}
	}
	public static boolean isTerminalNode(StringBuilder history) {
		if(history.charAt(history.length()-1) == FOLD) {
			return true;
		}
		else
		return false;
	}


	// Counterfal tual regret minimization(cartas de los jugadores y en la mesa,
	// historico de la partida, probabilidades de los jugadores, baraja, cahnce que
	// indica se nos encontramos en un nodo chance)
	private static double cfr(Card[][] cards_, StringBuilder history, double p0, double p1,
			ArrayList<Card> deck_, boolean chance) {
		int plays = history.length(); // Numero de jugadas
		int player = plays % 2;
		ArrayList<Card> deck = new ArrayList<Card>(deck_);// Numero de jugador de 0 al 3
		//if(history.length() < 10 && isRoundOver(history, 0) > 1)System.out.println(history + ":::"+ cards[2][4] +":::" +  isRoundOver(history, 0));
		//System.out.println(history +"."+ cards_[4][4]);
		Card[][] cards = new Card[3][5];
		for (int i = 0; i < cards.length; i++) {
			for (int j = 0; j < cards[0].length; j++) {
				cards[i][j] = cards_[i][j];
				}
		}
		//if (player == 0 && !chance) {/
		///////////////////////////////////
		// Identificar nodos teminales y chance
		if(isTerminalNode(history) && !chance) {
			return getPrize(history, true, player);
		}
		int icn = isChanceNode(history);
		if (icn > 0 && !chance) {
			int index = random.nextInt(deck.size());
			Card cards1[][] = cards.clone();
		switch(icn) {
			case PREFLOP:
				for (int j = 0; j < 3; j++) {
					index = random.nextInt(deck.size());
					cards[2][j] = deck.get(index);
					cards[2][j].setPosicion("Mesa");
					deck.remove(index);

				}
				return cfr(cards1,new StringBuilder(history), p0, p1, deck,true);
			case POSTFLOP:
				cards[2][3] = deck.get(index);
				cards[2][3].setPosicion("Mesa");
				deck.remove(index);
				return cfr(cards, new StringBuilder(history), p0, p1, deck,true);
			case TURN:
				cards[2][4] = deck.get(index);
				cards[2][4].setPosicion("Mesa");
				deck.remove(index);
				return cfr(cards, new StringBuilder(history), p0, p1, deck,true);
			case RIVER:
				Player[] plys = new Player[2];
				for (int i = 0; i < 2; i++) {
					plys[i] = new Player("x", 200, new ImageView(null), new ImageView(null));
					cards[i][0].setPosicion("Mano");
					cards[i][1].setPosicion("Mano");
					plys[i].setcards(cards[i][0], cards[i][1]);
					if(cards[2][0] != null){
						cards[2][0].setPosicion("Mesa");
						cards[2][1].setPosicion("Mesa");
						cards[2][2].setPosicion("Mesa");
						plys[i].newCarta(cards[2][0]);
						plys[i].newCarta(cards[2][1]);
						plys[i].newCarta(cards[2][2]);
					}
					if(cards[2][3] != null) {
						cards[2][3].setPosicion("Mesa");
						plys[i].newCarta(cards[2][3]);
					}
					if(cards[2][4] != null) {
						cards[2][4].setPosicion("Mesa");
						plys[i].newCarta(cards[2][4]);
					}

				}
				boolean wins;

					if (plys[player].calcularpuntuacion() > plys[1 - player].calcularpuntuacion())
						wins = true;
					else wins = false;
				//System.out.println("Player " + player + " wins " +  getPrize(history, wins, player));
				return getPrize(history, wins, player);

			}

		}

		//////////////////////////Aqui se cambian los pametros de entrada para el jugador////////////////////////////////////////////
		//double handValue = getHandValue(cards[player]);
		//double boardValue = getBoardValue(cards[2]);
		// Integer playDuration = Math.floorDiv(history.length(), 4);
		// int TableMoney = getPrize(history, true);
		StringBuilder infoset = /*history.length() > 3 ? new StringBuilder(history.substring(history.length() - 4))
				:*/new StringBuilder(history);
		//System.out.println(history + ":" + cards[2][2]);
		//if(history.length()< 10)System.out.println(history);
		// StringBuilder infoset = new StringBuilder();
		// infoset.append(":");
		// infoset.append(folds);
		//infoset.append(":");
		//infoset.append(boardValue);
		infoset.append(":");
		//infoset.append(handValue);
		Player plys;
			plys = new Player("x", 200, null, null);
			cards[player][0].setPosicion("Mano");
			cards[player][1].setPosicion("Mano");
			plys.setcards(cards[player][0], cards[player][1]);
			if(cards[2][0] != null){plys.newCarta(cards[2][0]);
			plys.newCarta(cards[2][1]);
			plys.newCarta(cards[2][2]);
			}
			if(cards[2][3] != null)plys.newCarta(cards[2][3]);
			if(cards[2][4] != null)plys.newCarta(cards[2][4]);
		infoset.append(plys.calcularpuntuacion());
		// history.length() <= 15 System.out.println(history);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Node node = gisnoc(infoset);
		// if (nodeMap.size()%1000 == 0) System.out.println(nodeMap.size() +
		// "----------" + boardValue + "--------"+ handValue + "-----" + history);

		// Conseguir la estrategia del nodo, en caso de nodo nuevo la estrategia por
		// defecto
		double[] strategy = {};
		switch (player) {
		case 0:
			strategy = node.getStrategy(p0);
			break;
		case 1:
			strategy = node.getStrategy(p1);
			break;
		default:
			// Esto es un error
			break;
		}
		//System.out.println(Arrays.toString(strategy));
		double[] util = new double[NUM_ACTIONS];
		double nodeUtil = 0;
		StringBuilder nextHistory;
		// Para cada accion (si es posible) calcular utilidad
		for (int a = 0; a < NUM_ACTIONS; a++) {
			nextHistory = new StringBuilder(history);
			switch (a) {
			case 0:
				if (isValidPlay(nextHistory, FOLD)) { // System.out.println(player + "---" + "f");
					nextHistory.append(FOLD);
				}
				break;
			case 1:
				if (isValidPlay(nextHistory, RAISE)) { // System.out.println(player + "---" + "r");
					nextHistory.append(RAISE);
				}
				break;
			case 2:
				if (isValidPlay(nextHistory, CALL)) { // System.out.println(player + "---" + "c");
					nextHistory.append(CALL);// System.out.println(nextHistory + ":"+ history);
				}
				break;
			case 3:
				if (isValidPlay(nextHistory, DOUBLE)) {
					nextHistory.append(DOUBLE);// System.out.println(nextHistory);
				}
				break;
			default:
				System.out.println("error");
				break;
			}
			if (history.length() != nextHistory.length()) {
				// System.out.println(player + "next" + history + "------" + nextHistory);
				switch (player) {
				case 0:
					util[a] = -cfr(cards.clone(), new StringBuilder(nextHistory), p0 * strategy[a], p1, deck, false);
					break;
				case 1:
					util[a] = -cfr(cards.clone(), new StringBuilder(nextHistory), p0, p1 * strategy[a], deck, false);
					break;
				default:
					// Esto es un error
					// System.out.println("error");
					break;
				}
				// Suma ponderada de las utilidades de cada accion por la estrategia
				// correspondiente
				nodeUtil += strategy[a] * util[a];
			}
		}
		// Apply regret to node
		for (int a = 0; a < NUM_ACTIONS; a++) {
			double regret = util[a] - nodeUtil;
			nextHistory = new StringBuilder(history);

			switch (player) {
			case 0:
				/*if (valid)*/node.regretSum[a] += p1 * regret;
				break;
			case 1:
				/*if (valid)*/node.regretSum[a] += p0 * regret;
				break;
			default:
				// Esto es un error
				break;
			}
		}
		// System.out.println("ey mal tio" + nodeUtil);
		return nodeUtil;
	}

	// Busca en el mapa el estado y si no lo encuentra a�ade un nodo nuevo con los
	// valores por defecto y el estado
	public static Node gisnoc(StringBuilder infoSet) {
		Node node = nodeMap.get(infoSet.toString());
		if (node == null) {
			node = new Node();
			node.infoSet = infoSet;
			//System.out.print(infoSet + ":--" + nodeMap.size() + "\n");
			// System.out.println(infoSet + "-------------no estaba");
			nodeMap.put(infoSet.toString(), node);
		} // else System.out.println(infoSet);
		return node;
	}





	// Devuelve true si ya se alconzo el limite de apuesta para un jugador en
	// concreto
	public static boolean limitReached(StringBuilder history, int player, int limit) {
		int bet = 0;
		int potValue = 0;
		for (int i = 0; i < history.length(); i++) {
			if ((i - player) % 2 == 0)
				bet = getBetValue(history.charAt(i), potValue);
			potValue += getPlayValue(history.charAt(i));
			if (bet >= limit)
				return true;
		}

		return false;
	}

	public static boolean isValidPlay(StringBuilder history_, int play) {
		StringBuilder history = new StringBuilder(history_);
		switch (play) {
		case FOLD:

			return true;
		case RAISE:
			if (limitReached(new StringBuilder(history).append(RAISE), (history.length()) % 2, LIMIT))// De esta manera
						return false;																				// evitamos el
																										// re-raise
																									// infinito
				if(history.charAt(history.length()-1) != RAISE && history.charAt(history.length()-2) == RAISE)
			return false;
					return true;
		case CALL:
			return true;
		case DOUBLE:
			if (limitReached(new StringBuilder(history).append(DOUBLE), (history.length()) % 2, LIMIT))// De esta manera
				return false;
			return true;
		default:
			// Otro error aqui
			System.out.println("erorrr");
			return false;
		}
	}

	// Devuelve un array 5x5 con las cartas de los jugadores. La primera posicion
		// indica el jugador(board en caso de ser posicion 4). Tambien baraja las
		// cartas.
		// Esta copiado de alguna clase de android studio
		public static Card[][] shufflecards(int players, int n, ArrayList<Card> deck) {
			Card[][] cards = new Card[3][5];
			for (int i = 0; i < cards.length; i++) {
				for (int j = 0; j < cards[0].length; j++)
					cards[i][j] = null;
			}
			for (int i = 0; i < players; i++) {
				for (int j = 0; j < n; j++) {
					int index = random.nextInt(deck.size());
					cards[i][j] = deck.get(index);
					deck.remove(index);
				}

			}
			Collections.shuffle(deck);
			return cards;
		}



		// Devuelve el premio al jugador cero, si gana el bote, si pierde lo que pierde
		// en negativo. Posiblemente haya que cambiar esto
		public static int getPrize(StringBuilder history, boolean wins,int player) {
			int[] bets = new int[2];
			int potValue = 0;
			for (int i = 0; i < history.length(); i++) {
				if (getBetValue(history.charAt(i), potValue) != 0)
					bets[i % 2] = getBetValue(history.charAt(i), potValue);
				potValue += getPlayValue(history.charAt(i));

			}
			if (wins)
				return (bets[1-player]);
			else
				return -bets[player];
		}

		// Devuelve cuanto vale alapuesta del jugador dado el bote actual en la mesa.
		public static int getBetValue(char play, int potValue) {
			switch (play) {
			case FOLD:
				return 0;
			case RAISE:
				return potValue + 10;
			case CALL:
				return potValue;
			case DOUBLE:
				return potValue + 20;
			default:
				// Por aqui no
				return 0;
			}
		}

	//Devuelve caunto aumenta el bote en la mesa para una jugada dada
		public static int getPlayValue(char play) {
			switch (play) {
			case FOLD:
				return 0;
			case RAISE:
				return 10;
			case CALL:
				return 0;
			case DOUBLE:
				return 20;
			default:
				// Otro error aqui
				return 0;
			}
		}




}

class Node {
	public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', DOUBLE = 'h', NUM_ACTIONS = 4;
	StringBuilder infoSet;
	double[] regretSum = new double[NUM_ACTIONS], strategy = new double[NUM_ACTIONS],
			strategySum = new double[NUM_ACTIONS];

	public double[] getStrategy(double realizationWeight) {
		double normalizingSum = 0;
		for (int a = 0; a < NUM_ACTIONS; a++) {
			strategy[a] = regretSum[a] > 0 ? regretSum[a] : 0;
			normalizingSum += strategy[a];
		}
		for (int a = 0; a < NUM_ACTIONS; a++) {
			if (normalizingSum > 0)
				strategy[a] /= normalizingSum;
			else
				strategy[a] = 1.0 / NUM_ACTIONS;
			strategySum[a] += realizationWeight * strategy[a];
		}
		return strategy;
	}

	public double[] getAverageStrategy() {
		double[] avgStrategy = new double[NUM_ACTIONS];
		double normalizingSum = 0;
		for (int a = 0; a < NUM_ACTIONS; a++)
			normalizingSum += strategySum[a];
		for (int a = 0; a < NUM_ACTIONS; a++)
			if (normalizingSum > 0)
				avgStrategy[a] = strategySum[a] / normalizingSum;
			else
				avgStrategy[a] = 1.0 / NUM_ACTIONS;
		return avgStrategy;
	}

	public String toString() {
		return String.format("\n" + infoSet + Arrays.toString(getAverageStrategy()));
	}
}
