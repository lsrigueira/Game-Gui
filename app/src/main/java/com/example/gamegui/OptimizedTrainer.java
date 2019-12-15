package psi;

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
//		b.start();
//		c.start();
//		d.start();
//		e.start();
//		f.start();
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
			FileOutputStream fos = new FileOutputStream("modelocp.ser");
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
	public static final int ITERATIONS = 1; // Iteraciones por thread
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
			if (i % 1 == 0)
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
	private static double cfr(Card[][] cards_, StringBuilder history, double p0, double p1, double p2, double p3,
			ArrayList<Card> deck_, boolean chance) {
		int plays = history.length(); // Numero de jugadas
		int player = plays % 4;
		ArrayList<Card> deck = new ArrayList<Card>(deck_);// Numero de jugador de 0 al 3
		//if(history.length() < 10 && isRoundOver(history, 0) > 1)System.out.println(history + ":::"+ cards[4][4] +":::" +  isRoundOver(history, 0));
		System.out.println(history +"."+ cards_[4][4]);
		Card[][] cards = new Card[5][5];
		for (int i = 0; i < cards.length; i++) {
			for (int j = 0; j < cards[0].length; j++) {
				cards[i][j] = cards_[i][j];
				}
		}
		//if (player == 0 && !chance) {/
		///////////////////////////////////
		// Identificar nodos teminales y chance
		if (history.length() >  4 && !chance) {// !chance ------------>Si el anterior fue chance este no lo es
			// Por ahora solo identifico como nodos terminales o chance si le toca al
			// jugador que empezo
			// Creo que no afecta al algoritmo pero si a la efieciencia
			boolean threeFolds = hasThreeFolds(history);
			boolean roundIsOver = isRoundOver(history, player) > 0;
			//System.out.println(isRoundOver(history, player));
			if (threeFolds) {// Si hubo tres folds antes que tu significa que acabo la partida.
				// System.out.println(history);
				//System.out.println(history + "......" + cards[4][4]+ "3 fold");
				Player[] plys = new Player[4];
				for (int i = 0; i < 4; i++) {
					plys[i] = new Player("x", 200, new ImageView(null) , new ImageView(null));
					cards[i][0].setPosicion("Mano");
					cards[i][1].setPosicion("Mano");
					plys[i].setcards(cards[i][0], cards[i][1]);
					if(cards[4][0] != null){
						cards[4][0].setPosicion("Mesa");
						cards[4][1].setPosicion("Mesa");
						cards[4][2].setPosicion("Mesa");
						plys[i].newCarta(cards[4][0]);
						plys[i].newCarta(cards[4][1]);
						plys[i].newCarta(cards[4][2]);
					}
					if(cards[4][3] != null) {
						cards[4][3].setPosicion("Mesa");
						plys[i].newCarta(cards[4][3]);
					}
					if(cards[4][4] != null) {
						cards[4][4].setPosicion("Mesa");
						plys[i].newCarta(cards[4][4]);
					}
				}
				boolean ganaCero = true;
				// Si la anterior jugada fue fold significa que gano otro
				if (history.charAt(history.length() - 4) == FOLD)
					ganaCero = false;
				return getPrize(history, ganaCero, player);
				// If there is only one player left its terminal
				// calcularPuntuacion(Cartas[][], history, player);
			} else if (roundIsOver && player == 0 ) {
				
				//System.out.println("Round is over " +  history);
				int round = getFinishedRound(history, player, cards);
				int counter = 0;
				double util = 0;
				ArrayList<Integer> flopValue = new ArrayList<Integer>();// Aqui guardo las combinaciones ya vistas para
				Card cards1[][] = cards.clone();
				int index = (int) (Math.random() * deck.size());
				// que no se repitan
				switch (round) {
				case PREFLOP:// Chance node
					// We deal 3 cards to the board
					// Generate all possible combination of three cards dealt in the board from the
					// remaining deck
					// if (chance) break;
					// System.out.println(round);
					//System.out.println("ACAbo la ronda" + history + round);	
					
						for (int j = 0; j < 3; j++) {
							index = (int) (Math.random() * deck.size());
							cards[4][j] = deck.get(index);
							cards[4][j].setPosicion("Mesa");
							deck.remove(index);
							
						}
						return cfr(cards1,
								new StringBuilder(history), p0, p1, p2, p3, deck,true);
//					for (int i = 0; i < deck.size(); i++) {
//						for (int j = i + 1; j < deck.size(); j++) {
//							for (int k = j + 1; k < deck.size(); k++) {
//								cards1[4][0] = deck.get(i);
//								cards1[4][1] = deck.get(j);
//								cards1[4][2] = deck.get(k);
//								Card[] toDiscard = { deck.get(i), deck.get(j), deck.get(k) };
//								Integer flopValuei = getFlopValue(toDiscard);
//
//								if (!flopValue.contains(flopValuei)) {
//									flopValue.add(flopValuei);
//									util += cfr(cards1,
//											new StringBuilder(history), p0, p1, p2, p3, discardCards(toDiscard, deck),
//											true);
//								} else {
//									// System.out.println("Repetido" + flopValuei);
//								}
//								counter++;
//							}
//						}
//					}
//					return util / counter;
				case POSTFLOP:
					//System.out.println("ACAbo la ronda" + history + round);
					// if (chance) {/*System.out.println("dedededde");*/ break;}
					//System.out.println(round);
					// We deal one more card to the board, chance node
					
					cards[4][3] = deck.get(index);
					cards[4][3].setPosicion("Mesa");
					deck.remove(index);
					return cfr(cards,
							new StringBuilder(history), p0, p1, p2, p3, deck,true);
//					for (int i = 0; i < deck.size(); i++) {
//						cards[4][3] = deck.get(i);
//						Card[] toDiscard = { deck.get(i) };
//						Integer flopValuei = cardToPrime(toDiscard[0]);
//
//						if (!flopValue.contains(flopValuei)) {
//							flopValue.add(flopValuei);
//							//System.out.println("1");
//							util += cfr(cards.clone(), new StringBuilder(history), p0, p1, p2, p3,
//									discardCards(toDiscard, deck), true);
//						}
//						// System.out.println("turn" + history);
//						counter++;
//					}
//					return util / counter;
				case TURN:
				//	System.out.println("ACAbo la ronda" + history + round);
					// if (chance) break;
					// System.out.println(round);
					// We deal one more card to the board, chance node
					
					cards[4][4] = deck.get(index);
					cards[4][4].setPosicion("Mesa");
					deck.remove(index);
					return cfr(cards,
							new StringBuilder(history), p0, p1, p2, p3, deck,true);
//					for (int i = 0; i < deck.size(); i++) {
//						cards[4][4] = deck.get(i);
//						Card[] toDiscard = { deck.get(i) };
//						Integer flopValuei = cardToPrime(toDiscard[0]);
//
//						if (!flopValue.contains(flopValuei)) {
//							flopValue.add(flopValuei);
//							//System.out.println("1");
//							// System.out.println("river" + history);
//							util += cfr(cards.clone(), new StringBuilder(history), p0, p1, p2, p3,
//									discardCards(toDiscard, deck), true);
//						}
//						counter++;
//					}
//					return util / counter;
				case RIVER:
					//System.out.println("ACAbo la ronda" + history + round);
					// if (chance) break;
					// System.out.println(history + "enedgame");
					// Game ends, terminal state
					//System.out.println(history + "......" + cards[4][4]+ "player 0");
					Player[] plys = new Player[4];
					for (int i = 0; i < 4; i++) {
						plys[i] = new Player("x", 200, new ImageView(null), new ImageView(null));
						cards[i][0].setPosicion("Mano");
						cards[i][1].setPosicion("Mano");
						plys[i].setcards(cards[i][0], cards[i][1]);
						if(cards[4][0] != null){
							cards[4][0].setPosicion("Mesa");
							cards[4][1].setPosicion("Mesa");
							cards[4][2].setPosicion("Mesa");
							plys[i].newCarta(cards[4][0]);
							plys[i].newCarta(cards[4][1]);
							plys[i].newCarta(cards[4][2]);
						}
						if(cards[4][3] != null) {
							cards[4][3].setPosicion("Mesa");
							plys[i].newCarta(cards[4][3]);
						}
						if(cards[4][4] != null) {
							cards[4][4].setPosicion("Mesa");
							plys[i].newCarta(cards[4][4]);
						}
						plys[i].newCarta(cards[4][4]);
					}
					boolean ganaCero = true;
					for (int i = 1; i < 4; i++) {
						if (plys[i].calcularpuntuacion() > plys[0].calcularpuntuacion())
							ganaCero = false;
					}
					return getPrize(history, ganaCero, 0);

				// return 0;
				case 0:
					System.out.println("Estapasando");// Esto no puede pasar
					break;
				}
			} else if (roundIsOver && isRoundOver(history,player)==5 ) {
				//System.out.println(history + " O ha pasado esto tambien");
				//if() {
					//System.out.println(history + "......" + cards[4][4]+ "player " + player + "round" + isRoundOver(history,player));
					Player[] plys = new Player[4];
					for (int i = 0; i < 4; i++) {
						plys[i] = new Player("x", 200, new ImageView(null), new ImageView(null));
						cards[i][0].setPosicion("Mano");
						cards[i][1].setPosicion("Mano");
						plys[i].setcards(cards[i][0], cards[i][1]);
						if(cards[4][0] != null){
							cards[4][0].setPosicion("Mesa");
							cards[4][1].setPosicion("Mesa");
							cards[4][2].setPosicion("Mesa");
							plys[i].newCarta(cards[4][0]);
							plys[i].newCarta(cards[4][1]);
							plys[i].newCarta(cards[4][2]);
						}
						if(cards[4][3] != null) {
							cards[4][3].setPosicion("Mesa");
							plys[i].newCarta(cards[4][3]);
						}
						if(cards[4][4] != null) {
							cards[4][4].setPosicion("Mesa");
							plys[i].newCarta(cards[4][4]);
						}
						
					}
					boolean ganaCero = true;
					for (int i = 0; i < 4; i++) {
						if (plys[i].calcularpuntuacion() > plys[player].calcularpuntuacion())
							ganaCero = false;
					}
					return getPrize(history, ganaCero, player);
				//}
//				System.out.println(history + " O ha pasado esto tambien");
//				if (history.charAt(history.length() - 4) == FOLD)
//				return cfr(cards.clone(), new StringBuilder(history).append(FOLD), p0, p1, p2, p3, deck, false);
//				else return cfr(cards.clone(), new StringBuilder(history).append(CHECK), p0, p1, p2, p3, deck, false);
			}else if(roundIsOver){
				//System.out.println(history + " O ha pasado esto tambien");
				if (history.charAt(history.length() - 4) == FOLD)
					return cfr(cards.clone(), new StringBuilder(history).append(FOLD), p0, p1, p2, p3, deck, false);
					else return cfr(cards.clone(), new StringBuilder(history).append(CHECK), p0, p1, p2, p3, deck, false);
			}

		} // int folds = 0;
			// Si en la anterior ronda sacó fold añade f al historico y continua
		if (history.length() >= 4) {
			if (history.charAt(history.length() - 4) == FOLD)
				return cfr(cards.clone(), new StringBuilder(history).append(FOLD), p0, p1, p2, p3, deck, false);

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
				:new StringBuilder(history);
		//System.out.println(history + ":" + cards[4][2]);
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
			if(cards[4][0] != null){plys.newCarta(cards[4][0]);
			plys.newCarta(cards[4][1]);
			plys.newCarta(cards[4][2]);
			}
			if(cards[4][3] != null)plys.newCarta(cards[4][3]);
			if(cards[4][4] != null)plys.newCarta(cards[4][4]);
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
					util[a] = -cfr(cards.clone(), new StringBuilder(nextHistory), p0 * strategy[a], p1, p2, p3, deck, false);
					break;
				case 1:
					util[a] = -cfr(cards.clone(), new StringBuilder(nextHistory), p0, p1 * strategy[a], p2, p3, deck, false);
					break;
				case 2:
					util[a] = -cfr(cards.clone(), new StringBuilder(nextHistory), p0, p1, p2 * strategy[a], p3, deck, false);
					break;
				case 3:
					util[a] = -cfr(cards.clone(), new StringBuilder(nextHistory), p0, p1, p2, p3 * strategy[a], deck, false);
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

	// Busca en el mapa el estado y si no lo encuentra añade un nodo nuevo con los
	// valores por defecto y el estado
	public static Node gisnoc(StringBuilder infoSet) {
		Node node = nodeMap.get(infoSet.toString());
		if (node == null) {
			node = new Node();
			node.infoSet = infoSet;
			//System.out.print(infoSet + ":" + nodeMap.size() + "\n");
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
	public static int isRoundOver(StringBuilder history, int player) {
		int[] bets = new int[4];
		boolean talk[] = new boolean[4];
		talk[0] = false;
		talk[1] = false;
		talk[2] = false;
		talk[3] = false;
		int potValue = 0;
		int round = 1;
//		if (player != 0)
//			return false;
		for (int i = 0; i < history.length(); i++) {
			bets[i%4] = getBetValue(history.charAt(i), potValue);
			if(i%4 == 0 || talk[0] == true)talk[i%4] = true;
			potValue += getPlayValue(history.charAt(i));
			
			if ((bets[0] == potValue || bets[0] == 0) && (bets[1] == potValue || bets[1] == 0)
					&& (bets[2] == potValue || bets[2] == 0) && (bets[3] == potValue || bets[3] == 0) && talk[0]&& talk[1]&& talk[2]&& talk[3] && history.charAt(i) != FOLD){ 
				round ++;
				talk[0] = false;
				talk[1] = false;
				talk[2] = false;
				talk[3] = false;
				//System.out.println(history.substring(0, i + 1));
				//if(history.length() - i < 4) break;
			}
		}
		
		if ((bets[0] == potValue || bets[0] == 0) && (bets[1] == potValue || bets[1] == 0)
				&& (bets[2] == potValue || bets[2] == 0) && (bets[3] == potValue || bets[3] == 0) && (talk[0] == talk[1] && talk[1] == talk[2] && talk[2] == talk[3] )) 
	{//System.out.println(talk[0] +":"+ talk[1] +": "+talk[2]+ ":" + talk[3] + ":" + history + ":" + round);
			return round;}
		else {//System.out.println((talk[0] && talk[1] && talk[2]+ ":"talk[3] + ":" + history + ":" + round);
			return -round;}
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
						&& history.charAt(history.length() - 1) != RAISE && history.charAt(history.length() - 3) != CHECK && history.charAt(history.length() - 2) != CHECK && history.charAt(history.length() - 1) != CHECK)
					return false;
			}
			return true;
		case CALL:
//			for (int i = history.length() - 4; i >= 0; i -= 4) {
//				if (history.charAt(i) == FOLD)
//					return false;
//			}
			for (int i = history.length() -1; i >= 0 && i  > history.length() - 4  ; i--)
				if(history.charAt(i) == RAISE) {/*System.out.println(history + "paaaaaasa");*/return true;
			}
			//System.out.println(history + "::" + isRoundOver(history, 0));
//			if (isRoundOver(history, 0) < 0) { 
//				return false;}
//			return true;
			//System.out.println("Esta pasando" +  history);
			return false;
		case CHECK:
//			for (int i = history.length() - 4; i >= 0; i -= 4) {
//				if (history.charAt(i) == FOLD)
//					return false;
//			}
//			if (isRoundOver(history.append(CHECK), 0) < 0)
//				return false;

				if(!isValidPlay(history,CALL)) return true;
				return false;
//			if (!(history.length() < 4)) {
//				// if (history.charAt(history.length() - 4) == RAISE)
//				// return false;
//				if (history.charAt(history.length() - 3) == RAISE || history.charAt(history.length() - 2) == RAISE
//						|| history.charAt(history.length() - 1) == RAISE)
//					return false;
//			}
			
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
		public static int getPrize(StringBuilder history, boolean ganaCero,int player) {
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
			//if (!isRoundOver(history, player))
				//return 0;
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
    private boolean used;

    public Card(String id) {
        this.id = id;
        if (id.length() == 2) {
            this.suit = String.valueOf(id.charAt(1));
            this.rank = String.valueOf(id.charAt(0));
        } else if (id.length() == 3) {
            this.suit = String.valueOf(id.charAt(2));
            this.rank = id.substring(0, 2);
        }
    }

    public void setUsed(boolean x){
        this.used = x;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public boolean getUsed(){
        return this.used;
    }

    public String getPosicion(){
        return this.posicion;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return this.toString(); //Kept for backwards compatibility
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
        switch(this.suit){
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
        }

        switch (this.rank){
            case"A":
                rank_name = "Ace";
                break;
            case"2":
                rank_name = "Two";
                break;
            case"3":
                rank_name = "Three";
                break;
            case"4":
                rank_name = "Four";
                break;
            case"5":
                rank_name = "Five";
                break;
            case"6":
                rank_name = "Six";
                break;
            case"7":
                rank_name = "Seven";
                break;
            case"8":
                rank_name = "Eight";
                break;
            case"9":
                rank_name = "Nine";
                break;
            case"10":
                rank_name = "Ten";
                break;
            case"J":
                rank_name = "Jack";
                break;
            case"Q":
                rank_name = "Queen";
                break;
            case"K":
                rank_name = "King";
                break;
        }

        return rank_name + " of " + suit_name;

    }
    
    public boolean equals(Card other) {
        return this.id.equals(other.id);
    }

    public boolean equalSuit(Card other) {
        return this.suit.equals(other.suit);
    }

    public int compareRank(Card other){
        return new Integer(this.getRankValue()).compareTo(new Integer(other.getRankValue())) ;
    }

    public int getRankValue() {
        try {
            return Integer.parseInt(this.rank);
        } catch (NumberFormatException nfe){
           switch (this.rank){
               case "J": return 11;
               case "Q": return 12;
               case "K": return 13;
               case "A": return 14;
               default: return 0;
           }
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
	
}

class Player {
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

//    public void enseñar_reverso() {
//        functions.enseñar_carta(this.imagencard1, "reverso");
//        functions.enseñar_carta(this.imagencard2, "reverso");
//    }
//
//    public void enseñar_cartas() {
//        functions.enseñar_carta(this.imagencard1, this.card1.getId());
//        functions.enseñar_carta(this.imagencard2, this.card2.getId());
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
            //functions.imprimirdebug(this.getname() + ":ENCONTRADA PAREJA-->" + puntos, 1);
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
            //functions.imprimirdebug(this.getname() + ":ENCONTRADA DOBLE PAREJA-->" + puntos, 1);
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
            //functions.imprimirdebug(this.getname() + ":ENCONTRADO TRIO-->" + puntos, 1);
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
                       // functions.imprimirdebug(this.getname() + ":ENCONTRADO POKER-->" + puntos, 1);
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
            //functions.imprimirdebug(this.getname() + ":ENCONTRADO FULL-->" + puntos, 1);
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
         //   functions.imprimirdebug(this.getname() + ":ENCONTRADO STRAIGHT-->" + puntos, 1);
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
           // functions.imprimirdebug(this.getname() + ":ENCONTRADO STRAIGHT_FLUSH-->" + puntos, 1);
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
            //functions.imprimirdebug(this.getname() + ":ENCONTRADO ROYAL_FLUSH-->" + puntos, 1);
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
           // functions.imprimirdebug(this.getname() + ":ENCONTRADO FLUSH-->" + puntos, 1);
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
        int puntosmesa = 0;
//        int puntosmesa = hasroyalflush(false);
//        if (puntosmesa == 0) puntosmesa = hasstraightflush(false);
//        if (puntosmesa == 0) puntosmesa = haspoker(false);
//        if (puntosmesa == 0) puntosmesa = hasfull(false);
//        if (puntosmesa == 0) puntosmesa = hasflush(false);
//        if (puntosmesa == 0) puntosmesa = hasstraight(false);
//        if (puntosmesa == 0) puntosmesa = hastrio(false);
//        if (puntosmesa == 0) puntosmesa = hasDoblePair(false);
//        if (puntosmesa == 0) puntosmesa = hasPair(false);

        puntosmesa = puntosmesa + highcardmesa;

//        functions.imprimirdebug("CARTAS DA MESA\n" + cartasmesa + "\nAS MIÑAS CARTAS\n" + cartastot, 3);
//        functions.imprimirdebug("OS PUNTOS DA MESA-->" + puntosmesa, 1);
//        functions.imprimirdebug(this.getname() + " CARTA MAIS ALTA-->" + highcardmesa, 3);
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