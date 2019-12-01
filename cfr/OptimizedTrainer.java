package psi;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

//Esta clase declara el hashmap, inicia os threads y guarda el resultado.
public class OptimizedTrainer {
	public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', CHECK = 'h', NUM_ACTIONS = 4; // Opciones posibles
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

	public static void main(String[] args) {
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
			FileOutputStream fos = new FileOutputStream("model.ser");
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
	public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', CHECK = 'h', NUM_ACTIONS = 4;
	public static final int PREFLOP = 5, POSTFLOP = 6, TURN = 7, RIVER = 8;
	public static final int ITERATIONS = 5000; // Iteraciones por thread
	public static final int LIMIT = 60; // Marca el limite de apuesta para todos los contando con que en cada raise se
										// sube 10 y hay 2 obligatorios al principio;
										// Lo malo de subir esto es que una partida puede hacerse largisima y la
										// velocidad de iteracion baja mucho
	public static final Random random = new Random();
	public static ConcurrentHashMap<String, Node> nodeMap;// = new ConcurrentHashMap<StringBuilder, Node>();

	public TrainerThread() {
		nodeMap = OptimizedTrainer.nodeMap; // Asigna el modelo para que todos trabajen en el mismo
	}

	public void run() {
		Random random = new Random(); // Esto es necesario para que no todos hagan lo mismo
		System.out.println("Thread started. Training...");
		train(ITERATIONS);

	}

	public void train(int iterations) {
		ArrayList<Card> deck; // Baraja
		Card[][] cards = new Card[5][5]; // Cartas de la mesa
		long timePass = 0; // Para calcular la eficiencia
		double util = 0; // Utilidad --------> 0
		StringBuilder rr = new StringBuilder("rr"); // Doble ciega obligatoria
		for (int i = 0; i < iterations; i++) {
			// nodeMap.clear();
			long time = System.nanoTime();
			deck = Card.nueva_baraja();
			cards = shufflecards(4, 2, deck);
			util += cfr(cards, rr, 1, 1, 1, 1, deck, false); // Ejecucion desde el nodo raiz
			timePass = (System.nanoTime() - time);
			if (i % 5 == 0)
				System.out.println("Iteracion:" + i * 6 + " ---- util:" + util / (i + 1) + " ----- nodeMap:"
						+ nodeMap.size() + "------" + (double) timePass / 1000000000 + " sgs.");
		}
		System.out.println("Average game value: " + util / iterations);
//		for (Node n : nodeMap.values())
//			System.out.println(n);
	}

	

	// Counterfal tual regret minimization(cartas de los jugadores y en la mesa,
	// historico de la partida, probabilidades de los jugadores, baraja, cahnce que
	// indica se nos encontramos en un nodo chance)
	private static double cfr(Card[][] cards, StringBuilder history, double p0, double p1, double p2, double p3,
			ArrayList<Card> deck, boolean chance) {
		int plays = history.length(); // Numero de jugadas
		int player = plays % 4; // Numero de jugador de 0 al 3

		///////////////////////////////////
		// Identificar nodos teminales y chance
		if (player == 0 && !chance) {// !chance ------------>Si el anterior fue chance este no lo es
			// Por ahora solo identifico como nodos terminales o chance si le toca al
			// jugador que empezo
			// Creo que no afecta al algoritmo pero si a la efieciencia
			boolean threeFolds = hasThreeFolds(history);
			boolean roundIsOver = isRoundOver(history, player);
			if (threeFolds) {// Si hubo tres folds antes que tu significa que acabo la partida.
				// System.out.println(history);
				Player[] plys = new Player[4];
				for (int i = 0; i < 4; i++) {
					plys[i] = new Player("x", 200);
					plys[i].setcards(cards[i][0], cards[i][1]);
					plys[i].cartastot.add(cards[4][0]);
					plys[i].cartastot.add(cards[4][1]);
					plys[i].cartastot.add(cards[4][2]);
					plys[i].cartastot.add(cards[4][3]);
					plys[i].cartastot.add(cards[4][4]);
				}
				boolean ganaCero = true;
				// Si la anterior jugada fue fold significa que gano otro
				if (history.charAt(history.length() - 4) == FOLD)
					ganaCero = false;
				return getPrize(history, ganaCero);
				// If there is only one player left its terminal
				// calcularPuntuacion(Cartas[][], history, player);
			} else if (roundIsOver) {
				// System.out.println("Round is over");
				int round = getFinishedRound(history, player, cards);
				int counter = 0;
				double util = 0;
				ArrayList<Integer> flopValue = new ArrayList<Integer>();// Aqui guardo las combinaciones ya vistas para
																		// que no se repitan
				switch (round) {
				case PREFLOP:// Chance node
					// We deal 3 cards to the board
					// Generate all possible combination of three cards dealt in the board from the
					// remaining deck
					// if (chance) break;
					// System.out.println(round);

					for (int i = 0; i < deck.size(); i++) {
						for (int j = i + 1; j < deck.size(); j++) {
							for (int k = j + 1; k < deck.size(); k++) {
								cards[4][0] = deck.get(i);
								cards[4][1] = deck.get(j);
								cards[4][2] = deck.get(k);
								Card[] toDiscard = { deck.get(i), deck.get(j), deck.get(k) };
								Integer flopValuei = getFlopValue(toDiscard);

								if (!flopValue.contains(flopValuei)) {
									flopValue.add(flopValuei);
									/* System.out.println("flop" + history + counter); */util += cfr(cards,
											new StringBuilder(history), p0, p1, p2, p3, discardCards(toDiscard, deck),
											true);
								} else {
									// System.out.println("Repetido" + flopValuei);
								}
								counter++;
							}
						}
					}
					return util / counter;
				case POSTFLOP:
					// if (chance) {/*System.out.println("dedededde");*/ break;}
					// System.out.println(round);
					// We deal one more card to the board, chance node
					for (int i = 0; i < deck.size(); i++) {
						cards[4][3] = deck.get(i);
						Card[] toDiscard = { deck.get(i) };
						Integer flopValuei = cardToPrime(toDiscard[0]);

						if (!flopValue.contains(flopValuei)) {
							flopValue.add(flopValuei);
							util += cfr(cards, new StringBuilder(history), p0, p1, p2, p3,
									discardCards(toDiscard, deck), true);
						}
						// System.out.println("turn" + history);
						counter++;
					}
					return util / counter;
				case TURN:
					// if (chance) break;
					// System.out.println(round);
					// We deal one more card to the board, chance node
					for (int i = 0; i < deck.size(); i++) {
						cards[4][4] = deck.get(i);
						Card[] toDiscard = { deck.get(i) };
						Integer flopValuei = cardToPrime(toDiscard[0]);

						if (!flopValue.contains(flopValuei)) {
							flopValue.add(flopValuei);
							// System.out.println("river" + history);
							util += cfr(cards, new StringBuilder(history), p0, p1, p2, p3,
									discardCards(toDiscard, deck), true);
						}
						counter++;
					}
					return util / counter;
				case RIVER:
					// if (chance) break;
					// System.out.println(history + "enedgame");
					// Game ends, terminal state
					Player[] plys = new Player[4];
					for (int i = 0; i < 4; i++) {
						plys[i] = new Player("x", 200);
						plys[i].setcards(cards[i][0], cards[i][1]);
						plys[i].cartastot.add(cards[4][0]);
						plys[i].cartastot.add(cards[4][1]);
						plys[i].cartastot.add(cards[4][2]);
						plys[i].cartastot.add(cards[4][3]);
						plys[i].cartastot.add(cards[4][4]);
					}
					boolean ganaCero = true;
					for (int i = 1; i < 4; i++) {
						if (plys[i].calcularpuntuacion() > plys[0].calcularpuntuacion())
							ganaCero = false;
					}
					return getPrize(history, ganaCero);

				// return 0;
				case 0:
					// Esto no puede pasar
					break;
				}
			}

		} // int folds = 0;
			// Si en la anterior ronda sac� fold a�ade f al historico y continua
		if (history.length() >= 4) {
			if (history.charAt(history.length() - 4) == FOLD)
				return cfr(cards, new StringBuilder(history).append(FOLD), p0, p1, p2, p3, deck, false);

//			for (int i = 0; i < 4; i++) {
//				if (history.charAt(history.length() - 1 - i) == FOLD)
//					folds++;
//			}
		}
		//////////////////////////Aqui se cambian los pametros de entrada para el jugador////////////////////////////////////////////
		//double handValue = getHandValue(cards[player]);
		//double boardValue = getBoardValue(cards[4]);
		// Integer playDuration = Math.floorDiv(history.length(), 4);
		// int TableMoney = getPrize(history, true);
		StringBuilder infoset = history.length() > 3 ? new StringBuilder(history.substring(history.length() - 4))
				: new StringBuilder(history);
		// StringBuilder infoset = new StringBuilder();
		// infoset.append(":");
		// infoset.append(folds);
		//infoset.append(":");
		//infoset.append(boardValue);
		//infoset.append(":");
		//infoset.append(handValue);
		Player plys;
			plys = new Player("x", 200);
			plys.setcards(cards[player][0], cards[player][1]);
			if(cards[4][0] != null){plys.cartastot.add(cards[4][0]);
			plys.cartastot.add(cards[4][1]);
			plys.cartastot.add(cards[4][2]);
			}
			if(cards[4][3] != null)plys.cartastot.add(cards[4][3]);
			if(cards[4][4] != null)plys.cartastot.add(cards[4][4]);
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
		case 2:
			strategy = node.getStrategy(p2);
			break;
		case 3:
			strategy = node.getStrategy(p3);
			break;
		default:
			// Esto es un error
			break;
		}

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
				if (isValidPlay(nextHistory, CHECK)) {
					nextHistory.append(CHECK);// System.out.println(nextHistory);
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
					util[a] = -cfr(cards, new StringBuilder(nextHistory), p0 * strategy[a], p1, p2, p3, deck, false);
					break;
				case 1:
					util[a] = -cfr(cards, new StringBuilder(nextHistory), p0, p1 * strategy[a], p2, p3, deck, false);
					break;
				case 2:
					util[a] = -cfr(cards, new StringBuilder(nextHistory), p0, p1, p2 * strategy[a], p3, deck, false);
					break;
				case 3:
					util[a] = -cfr(cards, new StringBuilder(nextHistory), p0, p1, p2, p3 * strategy[a], deck, false);
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
			// node.regretSum[a] += (player == 0 ? p1 : p0) * regret;
			boolean valid = false;
			switch (a) {
			case 0:
				if (isValidPlay(nextHistory, FOLD))
					valid = true;// System.out.println(player + "---" + "f");
				break;
			case 1:
				if (isValidPlay(nextHistory, RAISE))
					valid = true;// System.out.println(player + "---" + "r");
				break;
			case 2:
				if (isValidPlay(nextHistory, CALL))
					valid = true;// System.out.println(player + "---" + "c");
				break;
			case 3:
				if (isValidPlay(nextHistory, CHECK)) 
					valid = true;
				break;
			default:
				System.out.println("error");
				break;
			}
			switch (player) {
			case 0:
				if (valid)node.regretSum[a] += (p1 + p2 + p3) * regret;
				break;
			case 1:
				if (valid)node.regretSum[a] += (p0 + p2 + p3) * regret;
				break;
			case 2:
				if (valid)node.regretSum[a] += (p1 + p0 + p3) * regret;
				break;
			case 3:
				if (valid)node.regretSum[a] += (p1 + p2 + p0) * regret;
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
			// System.out.print(infoSet + "\n");
			// System.out.println(infoSet + "-------------no estaba");
			nodeMap.put(infoSet.toString(), node);
		} // else System.out.println(infoSet);
		return node;
	}
	
	// Devuelve true si tres jugadores han echo fold en la ronda anterior
	public static boolean hasThreeFolds(StringBuilder history) {
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			if (history.charAt(history.length() - 1 - i) == FOLD)
				counter++;
			if (counter >= 3)
				return true;
		}
		return false;
	}

	// Devuelve true si ha finalizado la ronda; Para ello calcular el el valor de la
	// apuesta, si es igual para todos la ronda ha finalizado
	public static boolean isRoundOver(StringBuilder history, int player) {
		int[] bets = new int[4];
		int potValue = 0;
		if (player != 0)
			return false;
		for (int i = 0; i < history.length(); i++) {
			bets[i % 4] = getBetValue(history.charAt(i), potValue);
			potValue += getPlayValue(history.charAt(i));

		}
		if ((bets[0] == potValue || bets[0] == 0) && (bets[1] == potValue || bets[1] == 0)
				&& (bets[2] == potValue || bets[2] == 0) && (bets[3] == potValue || bets[3] == 0))
			return true;
		else
			return false;
	}

	// Devuelve true si ya se alconzo el limite de apuesta para un jugador en
	// concreto
	public static boolean limitReached(StringBuilder history, int player, int limit) {
		int bet = 0;
		int potValue = 0;
		for (int i = 0; i < history.length(); i++) {
			if ((i - player) % 4 == 0)
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
//			if (!(history.length() < 4))
//				if (history.charAt(history.length() - 4) == RAISE)
//					return false;
			return true;
		case RAISE:
			if (limitReached(new StringBuilder(history).append(RAISE), (history.length()) % 4, LIMIT))// De esta manera
																										// evitamos el
																										// re-raise
																										// infinito
				return false;
//			for (int i = history.length() - 4; i >= 0; i -= 4) {
//				if (history.charAt(i) == FOLD)
//					return false;
//			}
			if (!(history.length() < 4)) {
				// if (history.charAt(history.length() - 4) == RAISE)
				// return false;
				if (history.charAt(history.length() - 3) != RAISE && history.charAt(history.length() - 2) != RAISE
						&& history.charAt(history.length() - 1) != RAISE)
					return false;
			}
			return true;
		case CALL:
//			for (int i = history.length() - 4; i >= 0; i -= 4) {
//				if (history.charAt(i) == FOLD)
//					return false;
//			}
			if (isRoundOver(history, 0))
				return false;
			return true;
		case CHECK:
//			for (int i = history.length() - 4; i >= 0; i -= 4) {
//				if (history.charAt(i) == FOLD)
//					return false;
//			}
			if (!isRoundOver(history, 0))
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
			Card[][] cards = new Card[5][5];
			for (int i = 0; i < cards.length; i++) {
				for (int j = 0; j < cards[0].length; j++)
					cards[i][j] = null;
			}
			for (int i = 0; i < players; i++) {
				for (int j = 0; j < n; j++) {
					int index = (int) (Math.random() * deck.size());
					cards[i][j] = deck.get(index);
					deck.remove(index);
				}

			}
			Collections.shuffle(deck);
			return cards;
		}

		// Elimina de la baraja las cartas de la variable toDiscard.
		public static ArrayList<Card> discardCards(Card[] toDiscard, ArrayList<Card> deck_) {
			ArrayList<Card> deck = new ArrayList<Card>(deck_);
			int counter = toDiscard.length;
			for (int i = 0; i < deck.size(); i++) {
				for (int j = 0; j < toDiscard.length; j++) {
					if (deck.get(i).equals(toDiscard[j])) {
						deck.remove(i);
						counter--;
						if (counter == 0)
							return deck;
					}
				}
			}

			return null;
		}
		

		// Devuelve el premio al jugador cero, si gana el bote, si pierde lo que pierde
		// en negativo. Posiblemente haya que cambiar esto
		public static int getPrize(StringBuilder history, boolean ganaCero) {
			int[] bets = new int[4];
			int potValue = 0;
			for (int i = 0; i < history.length(); i++) {
				if (getBetValue(history.charAt(i), potValue) != 0)
					bets[i % 4] = getBetValue(history.charAt(i), potValue);
				potValue += getPlayValue(history.charAt(i));

			}
			if (ganaCero)
				return (bets[0] + bets[1] + bets[2] + bets[3]);
			else
				return -bets[0];
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
			case CHECK:
				return potValue;
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
			case CHECK:
				return 0;
			default:
				// Otro error aqui
				return 0;
			}
		}

		// Devuelve un numero de ronda --> un valor entre 5 y 8 dependiendo del numero
		// de
		// cartas en la mesa
		public static int getFinishedRound(StringBuilder history, int player, Card[][] cards) {
			if (!isRoundOver(history, player))
				return 0;
			if (cards[4][0] == null)
				return PREFLOP;
			else if (cards[4][3] == null)
				return POSTFLOP;
			else if (cards[4][4] == null)
				return TURN;
			else
				return RIVER;
		}

		// Devuelve el valor de la multiplicacion de las cartas en la mano
		public static double getHandValue(Card[] playerCards) {
			int value = 1;
			for (int i = 0; i < 2; i++)
				value = value * cardToPrime(playerCards[i]);
			return value;
		}

		// Devuelve el valor de la multiplicacion de cartas en la mesa
		public static double getBoardValue(Card[] boardCards) {
			int value = 1;
			for (int i = 0; i < boardCards.length; i++)
				if (boardCards[i] != null)
					value = value * cardToPrime(boardCards[i]);
			return value;
		}
		
		// Devuelve el valor de la multiplicacion de las cartas del flop
		public static Integer getFlopValue(Card[] toDiscard) {
			Integer value = 1;
			for (int i = 0; i < toDiscard.length; i++)
				value *= cardToPrime(toDiscard[i]);
			return value;
		}

		// Convierte una carta en un primo basado unicamente en el rango
		public static int cardToPrime(Card card) {
			switch (card.getRankValue()) {
			case 2:
				return 2;
			case 3:
				return 3;
			case 4:
				return 5;
			case 5:
				return 7;
			case 6:
				return 11;
			case 7:
				return 13;
			case 8:
				return 17;
			case 9:
				return 19;
			case 10:
				return 23;
			case 11:
				return 29;
			case 12:
				return 31;
			case 13:
				return 37;
			case 14:
				return 41;
			default:
				return 0;
			}
		}

}

class Node {
	public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', CHECK = 'h', NUM_ACTIONS = 4;
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

class Card {

	private String id;
	private String suit;
	private String rank;
	private String posicion;

	public Card(String id) {

		this.id = id;

		if (id.length() == 2) {
			this.suit = String.valueOf(id.charAt(1));
			this.rank = String.valueOf(id.charAt(0));
		} else if (id.length() == 3) {
			this.suit = String.valueOf(id.charAt(2));
			this.rank = id.substring(0, 1);
		}

	}

	public static ArrayList<Card> nueva_baraja() {
		ArrayList<Card> cartasenbaraja = new ArrayList<>();
		String[] letras = { "S", "H", "C", "D" };// C e D
		String[] numeros = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };
		int contador1 = 0;
		for (contador1 = 0; contador1 < letras.length; contador1++) {
			int contador2 = 0;
			for (contador2 = 0; contador2 < numeros.length; contador2++) {
				cartasenbaraja.add(new Card(String.format("%s", numeros[contador2] + letras[contador1])));
			}
		}
		return cartasenbaraja;
	}

	public int cardToInt(Card card) {
		return 0;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return this.toString(); // Kept for backwards compatibility
	}

	public String getSuit() {

		return suit;
	}

	public String getRank() {

		return rank;
	}

	@Override
	public String toString() {

		String rank_name = "";
		String suit_name = "";
		switch (this.suit) {
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
		default:
			suit_name = "----";
		}

		switch (this.rank) {
		case "A":
			rank_name = "Ace";
			break;
		case "2":
			rank_name = "Two";
			break;
		case "3":
			rank_name = "Three";
			break;
		case "4":
			rank_name = "Four";
			break;
		case "5":
			rank_name = "Five";
			break;
		case "6":
			rank_name = "Six";
			break;
		case "7":
			rank_name = "Seven";
			break;
		case "8":
			rank_name = "Eight";
			break;
		case "9":
			rank_name = "Nine";
			break;
		case "T":
			rank_name = "Ten";
			break;
		case "J":
			rank_name = "Jack";
			break;
		case "Q":
			rank_name = "Queen";
			break;
		case "K":
			rank_name = "King";
			break;
		default:
			rank_name = "----";
		}
		if (rank_name == "" || suit_name == "")
			System.out.println("Todo mal macho");
		return rank_name + " of " + suit_name;

	}

	public boolean equals(Card other) {
		return this.id.equals(other.id);
	}

	public boolean equalSuit(Card other) {
		return this.suit.equals(other.suit);
	}

	public int compareRank(Card other) {
		return new Integer(this.getRankValue()).compareTo(new Integer(other.getRankValue()));
	}

	public int getRankValue() {
		try {
			return Integer.parseInt(this.rank);
		} catch (NumberFormatException nfe) {
			switch (this.rank) {
			case "J":
				return 11;
			case "T":
				return 10;
			case "Q":
				return 12;
			case "K":
				return 13;
			case "A":
				return 14;
			default:
				return 0;
			}
		}
	}
}

class Player {
	static final int pair = 1;
	static final int doublepair = 2;
	static final int trio = 3;
	static final int straight = 4;
	static final int flush = 5;
	static final int full_house = 6;
	static final int poker = 7;
	static final int straight_flush = 8;
	static final int royal_flush = 9;

	static final int castToBestPlay = 1000000;
	static final int castToBestCardInPlay = 10000;
	static final int castToWorstCardInPlay = 100;
	private Card card1;
	private Card card2;
	public ArrayList<Card> cartastot = new ArrayList<Card>();
	private String nome;
	private boolean horizontal;
	private int money;
	private int moneybet = 100;
	private long puntuacion;

	public Player(String nome, int money) {
		this.nome = nome;
		this.money = money;
		cartastot = new ArrayList<Card>();
	}

	public int getMoney() {
		return this.money;
	}

	public int getBet() {
		return this.moneybet;
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

	// Pasase asi para saber nun futuro a IA de onde prove�en as cartas, se tes
	// poker pero est� na mesa hai menos prob de ganar
	public int hasPair() {
		int puntos = 0;
		// Se tiveesemos duas parexas esto ser�a "doble parexa" e non estariamos aqui
		for (int contador = 0; contador < cartastot.size(); contador++) {
			Card tocompare = cartastot.get(contador);
			int i = 0;
			for (i = 0; i < cartastot.size(); i++) {
				Card comparable = cartastot.get(i);
				if (comparable != null && tocompare != null) {
					if (comparable.getId().equals(tocompare.getId()))
						continue;
					else if (comparable.getRank().equals(tocompare.getRank())) {
						puntos = pair * castToBestPlay + comparable.getRankValue() * castToBestCardInPlay;
						;
						// System.out.println(this.getname()+":ENCONTRADA PAREXA-->"+puntos);
						return puntos;
					}
				}
			}
		}
		return 0; // TODO
	}

	public int hasDoblePair() {
		int firstpairnumber = 0;
		int puntos = 0;
		// Se tivesemos duas parexas esto ser�a "doble parexa" e non estariamos aqui
		for (int contador = 0; contador < cartastot.size(); contador++) {
			Card tocompare = cartastot.get(contador);
			int i = 0;
			for (i = 0; i < cartastot.size(); i++) {
				Card comparable = cartastot.get(i);// Cambiamnos ese if para evitar falsas dobles parexas
				if (comparable != null && tocompare != null) {
					if (comparable.getId().equals(tocompare.getId()) || comparable.getRankValue() == firstpairnumber)
						continue;
					else if (comparable.getRank().equals(tocompare.getRank())) {
						if (firstpairnumber == 0) {
							firstpairnumber = comparable.getRankValue();
						} else {
							puntos = doublepair * castToBestPlay;
							switch (((Integer) comparable.getRankValue()).compareTo(firstpairnumber)) {
							case -1:// firstpair>comparable
								puntos += firstpairnumber * castToBestCardInPlay
										+ comparable.getRankValue() * castToWorstCardInPlay;
								break;
							case 0:// firstpai=comparab�e
								puntos += firstpairnumber * castToBestCardInPlay
										+ firstpairnumber * castToWorstCardInPlay;
								break;
							case 1:// comparable>firstpairnumber
								puntos += comparable.getRankValue() * castToBestCardInPlay
										+ firstpairnumber * castToWorstCardInPlay;
								break;
							default:
								System.out.println("ALGO RARO PASA NO DOUBLE PAIR COMPARANDO");
							}
							// System.out.println(this.getname()+":ENCONTRADA DOBLE PAREXA-->"+puntos);
							return puntos;
						}
					}
				}
			}
		}
		return 0; // TODO
	}

	public int hastrio() {
		boolean foundbefore;
		int puntos = 0;
		for (int contador = 0; contador < cartastot.size(); contador++) {
			foundbefore = false;
			Card tocompare = cartastot.get(contador);
			int i = 0;
			for (i = 0; i < cartastot.size(); i++) {
				Card comparable = cartastot.get(i);
				if (comparable != null && tocompare != null) {
					if (comparable.getId().equals(tocompare.getId()))
						continue;
					else if (comparable.getRank().equals(tocompare.getRank())) {
						if (foundbefore) {
							puntos += trio * castToBestPlay + comparable.getRankValue() * castToBestCardInPlay;
							// System.out.println(this.getname()+":ENCONTRADO TRIO-->"+puntos);
							return puntos;
						} else
							foundbefore = true;
					}
				}
			}
		}
		return 0;
	}

	public int haspoker() {
		int foundbefore;
		int puntos = 0;
		for (int contador = 0; contador < cartastot.size(); contador++) {
			foundbefore = 0;
			Card tocompare = cartastot.get(contador);
			int i = 0;
			for (i = 0; i < cartastot.size(); i++) {
				Card comparable = cartastot.get(i);
				if (comparable != null && tocompare != null) {
					if (comparable.getId().equals(tocompare.getId()))
						continue;
					else if (comparable.getRank().equals(tocompare.getRank())) {

						if (foundbefore == 2) {
							puntos += poker * castToBestPlay + comparable.getRankValue() * castToBestCardInPlay;
							// System.out.println(this.getname()+":ENCONTRADO POKER-->"+puntos);
							return puntos;
						} else
							foundbefore++;
					}
				}
			}
		}
		return 0;
	}

	public int hasflush() {
		int foundbefore = 0;
		int puntos = 0;
		for (int contador = 0; contador < cartastot.size(); contador++) {
			foundbefore = 0;
			Card tocompare = cartastot.get(contador);
			int i = 0;
			for (i = 0; i < cartastot.size(); i++) {

				Card comparable = cartastot.get(i);
				// System.out.println(comparable.getId() + "---------" + tocompare.getId());
				if (comparable != null && tocompare != null) {
					if (comparable.getId().equals(tocompare.getId()))
						continue;
					else if (comparable.getSuit().equals(tocompare.getSuit())) {
						if (foundbefore == 3) {
							puntos += flush * castToBestPlay;
							// System.out.println(this.getname()+":ENCONTRADO FLUSH-->"+puntos);
							return puntos;
						} else
							foundbefore++;
					}
				}
			}
		}
		return 0;
	}

	public void newCarta(Card carta) {
		this.cartastot.add(carta);
	}

	public void clearCartaMesa() {
		this.cartastot = new ArrayList<>();
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

	/*
	 * A puntuacion sera un int de formato xx-yy-yy-zz onde as letras solo indican o
	 * numero de dixistos e a orixe do calculo.Explicacion: XX: Indica se o xogar
	 * ten parexa,doble-parexa,trio... YY: Indica as cartas que usou para chegar
	 * ah�, non � o mesmo unha parexa de ases que de douses(hai 2 para distinguir as
	 * doble-parexas) ZZ: Indica a carta mais alta que non se empregou para o
	 * c�lculo de XX
	 */

	public int calcularpuntuacion() {
		//int puntos = hasflush();
		//if (puntos != 0)
		//	return puntos;
		int puntos = haspoker();
		if (puntos != 0)
			return puntos;
		puntos = hastrio();
		if (puntos != 0)
			return puntos;
		puntos = hasDoblePair();
		if (puntos != 0)
			return puntos;
		puntos = hasPair();
		if (puntos != 0)
			return puntos;
		return 0;
	}

}
