package cards;

import java.util.HashMap;

public class CardConsts {

    public final HashMap<Integer,String> suits = new HashMap() {{
        put(1, Diamonds);
        put(2, Clubs);
        put(3, Hearts);
        put(4, Spades);
    }};

    public final HashMap<Integer,String> ranks = new HashMap() {{
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
    public static String Diamonds = "♦"; // 1
    public static String Clubs = "♣"; // 2
    public static String Hearts = "♥"; // 3
    public static String Spades = "♠"; // 4

    public static String Two = "2"; // 0
    public static String Three = "3"; // 1
    public static String Four = "4"; // 2
    public static String Five = "5"; // 3
    public static String Six = "6"; // 4
    public static String Seven = "7"; // 5
    public static String Eight = "8"; // 6
    public static String Nine = "9"; // 7
    public static String Ten = "10"; // 8
    public static String Jack = "J"; // 9
    public static String Queen = "Q"; // 10
    public static String King = "K"; // 11
    public static String Ace = "A"; // 12
}
