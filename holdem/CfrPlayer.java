package psi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class CfrPlayer {

	public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', CHECK = 'h', NUM_ACTIONS = 4;
	public static final int PREFLOP = 5, POSTFLOP = 6, TURN = 7, RIVER = 8;
	public static final int ITERATIONS = 1; // Iteraciones por thread
	public static final int LIMIT = 60; //
	static HashMap<String, Node> nodeMap = new HashMap<String, Node>();
	public static void main( String[] args ) throws IOException
	{
	    String filePath = "modelov1.ser";
	    

	    String line;
	    BufferedReader reader = new BufferedReader(new FileReader(filePath));
	    while ((line = reader.readLine()) != null)
	    {
	        String[] parts = line.split(Pattern.quote("["), 2);
	        if (parts.length >= 2)
	        {
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

	    for (String key : nodeMap.keySet())
	    {
	        //System.out.println(nodeMap.get(key));
	    }
	    ArrayList<Card> deck; 
		Card[][] cards = new Card[5][5];
		deck = Card.nueva_baraja();
		cards = TrainerThread.shufflecards(4, 2, deck);
		StringBuilder rr = new StringBuilder("rr"); 
		 double util = cfr(cards, rr, 1, 1, 1, 1, deck, false); 
		 System.out.println(util);
	}
	
	private static double cfr(Card[][] cards_, StringBuilder history, double p0, double p1, double p2, double p3,
			ArrayList<Card> deck_, boolean chance) {
		int plays = history.length(); // Numero de jugadas
		int player = plays % 4;
		ArrayList<Card> deck = new ArrayList<Card>(deck_);// Numero de jugador de 0 al 3
		//if(history.length() < 10 && isRoundOver(history, 0) > 1)System.out.println(history + ":::"+ cards[4][4] +":::" +  isRoundOver(history, 0));
		//System.out.println(history +"."+ cards_[4][4]);
		Card[][] cards = new Card[5][5];
		for (int i = 0; i < cards.length; i++) {
			for (int j = 0; j < cards[0].length; j++)
				cards[i][j] = cards_[i][j];
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
					plys[i] = new Player("x", 200, null , null);
					cards[i][0].setPosicion("Mano");
					cards[i][1].setPosicion("Mano");
					plys[i].setcards(cards[i][0], cards[i][1]);
					plys[i].newCarta(cards[4][0]);
					plys[i].newCarta(cards[4][1]);
					plys[i].newCarta(cards[4][2]);
					plys[i].newCarta(cards[4][3]);
					plys[i].newCarta(cards[4][4]);
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
						plys[i] = new Player("x", 200, null, null);
						cards[i][0].setPosicion("Mano");
						cards[i][1].setPosicion("Mano");
						plys[i].setcards(cards[i][0], cards[i][1]);
						plys[i].newCarta(cards[4][0]);
						plys[i].newCarta(cards[4][1]);
						plys[i].newCarta(cards[4][2]);
						plys[i].newCarta(cards[4][3]);
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
						plys[i] = new Player("x", 200, null, null);
						cards[i][0].setPosicion("Mano");
						cards[i][1].setPosicion("Mano");
						plys[i].setcards(cards[i][0], cards[i][1]);
						plys[i].newCarta(cards[4][0]);
						plys[i].newCarta(cards[4][1]);
						plys[i].newCarta(cards[4][2]);
						plys[i].newCarta(cards[4][3]);
						plys[i].newCarta(cards[4][4]);
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
		StringBuilder infoset = history.length() > 7 ? new StringBuilder(history.substring(history.length() - 8))
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

			strategy = node.getAverageStrategy();

		double[] util = new double[NUM_ACTIONS];
		double nodeUtil = 0;
		StringBuilder nextHistory;
		int a = -1;
		nextHistory = new StringBuilder(history);
		double index = Math.random();
		if(index < strategy[0]) {
			nextHistory.append(FOLD);
			a = 0;
			System.out.println("Jugador " + player + " jugó fold: " + history + "---------"+ cards[player][0] + ":" + cards[player][1] + ":" + Arrays.toString(strategy));
		}
		else if(index < strategy[0] + strategy[1] ) {
			nextHistory.append(RAISE);
			a = 1;
			System.out.println("Jugador " + player + " jugó raise: " + history +"---------"+ cards[player][0] + ":" + cards[player][1] + ":" + Arrays.toString(strategy));
		}
		else if(index < strategy[0] + strategy[1] + strategy[2]) {
			nextHistory.append(CALL);
			a = 2;
			System.out.println("Jugador " + player + " jugó call: " + history +"---------"+ cards[player][0] + ":" + cards[player][1] + ":" + Arrays.toString(strategy));
		}
		else{
			nextHistory.append(CHECK);
			a = 3;
			System.out.println("Jugador " + player + " jugó check: " + history +"---------"+ cards[player][0] + ":" + cards[player][1] + ":" + Arrays.toString(strategy));
		}
		
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
		System.out.println(util[a] + "----");
		nodeUtil += strategy[a] * util[a];
		
		// Para cada accion (si es posible) calcular utilidad
		
		// Apply regret to node
		// System.out.println("ey mal tio" + nodeUtil);
		return nodeUtil;
	}
	public static Node gisnoc(StringBuilder infoSet) {
		Node node = nodeMap.get(infoSet.toString());
		if (node == null) {
			//node = new Node();
			//node.infoSet = infoSet;
			//System.out.print(infoSet + ":" + nodeMap.size() + "\n");
			 System.out.println(infoSet + "-------------no estaba");
			//nodeMap.put(infoSet.toString(), node);
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
			return bets[player] * -1;
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
	
}
