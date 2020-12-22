package helpers;

import entities.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CardHelper {

    public List<Card> buildCardSet() {
        List<Card> cards = new ArrayList<>();
        for(int i=0; i<52; i++) {
            Card newCard = buildCard(i);
            cards.add(newCard);
        }
        return  cards;
    }

    public Card buildCard(int number){
        int rankI = number / 4;
        int suitI = number % 4;

        String suitS = suits.get(suitI);
        String rankS = ranks.get(rankI);
        return new Card(suitS, rankS);
    }

    private final HashMap<Integer,String> suits = new HashMap() {{
        put(0, Diamonds);
        put(1, Clubs);
        put(2, Hearts);
        put(3, Spades);
    }};

    private final HashMap<Integer,String> ranks = new HashMap() {{
        put(0, Two);
        put(1, Three);
        put(2, Four);
        put(3, Five);
        put(4, Six);
        put(5, Seven);
        put(6, Eight);
        put(7, Nine);
        put(8, Ten);
        put(9, Jack);
        put(10, Queen);
        put(11, King);
        put(12, Ace);
    }};

    // number == rank * 4 + suit;
    private static String Diamonds = "♦"; // 1
    private static String Clubs = "♣"; // 2
    private static String Hearts = "♥"; // 3
    private static String Spades = "♠"; // 4

    private static String Two = "2"; // 0
    private static String Three = "3"; // 1
    private static String Four = "4"; // 2
    private static String Five = "5"; // 3
    private static String Six = "6"; // 4
    private static String Seven = "7"; // 5
    private static String Eight = "8"; // 6
    private static String Nine = "9"; // 7
    private static String Ten = "10"; // 8
    private static String Jack = "J"; // 9
    private static String Queen = "Q"; // 10
    private static String King = "K"; // 11
    private static String Ace = "A"; // 12
}
