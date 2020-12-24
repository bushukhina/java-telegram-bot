package helpers;

import entities.Card;

import java.util.*;
import java.util.stream.Collectors;

public final class CardHelper {

    public List<Card> buildCardSet() {
        List<Card> cards = new ArrayList<>();
        for(int i=0; i<52; i++) {
            Card newCard = buildCard(i);
            cards.add(newCard);
        }
        return  cards;
    }

    public Card getRandomNonExistingCard(List<Card> existingCards) {
        List<Integer> cardNumbers = getCardValues(existingCards);
        Card newCard = null;

        while (newCard == null) {
            int number = getRandomNumber(0, 51);
            if (!cardNumbers.contains(number)) {
                newCard = buildCard(number);
            }
        }
        return newCard;
    }

    /* Получить значения для списка карт */
    private List<Integer> getCardValues(List<Card> cards) {
        return cards.stream().map(card -> {
            int rank = ranksBack.get(card.getRank());
            int suit = suitsBack.get(card.getSuit());
            return  rank* 4 + suit;
        }).collect(Collectors.toList());
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
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

    private final HashMap<String, Integer> suitsBack = new HashMap() {{
        put(Diamonds, 0);
        put(Clubs, 1);
        put(Hearts, 2);
        put(Spades, 3);
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

    private final HashMap<String, Integer> ranksBack = new HashMap() {{
        put(Two, 0);
        put(Three, 1);
        put(Four, 2);
        put(Five, 3);
        put(Six, 4);
        put(Seven, 5);
        put(Eight, 6);
        put(Nine, 7);
        put(Ten, 8);
        put(Jack, 9);
        put(Queen, 10);
        put(King, 11);
        put(Ace, 12);
    }};

    // number == rank * 4 + suit;
    private static final String Diamonds = "♦"; // 1
    private static final String Clubs = "♣"; // 2
    private static final String Hearts = "♥"; // 3
    private static final String Spades = "♠"; // 4

    private static final String Two = "2"; // 0
    private static final String Three = "3"; // 1
    private static final String Four = "4"; // 2
    private static final String Five = "5"; // 3
    private static final String Six = "6"; // 4
    private static final String Seven = "7"; // 5
    private static final String Eight = "8"; // 6
    private static final String Nine = "9"; // 7
    private static final String Ten = "10"; // 8
    private static final String Jack = "J"; // 9
    private static final String Queen = "Q"; // 10
    private static final String King = "K"; // 11
    private static final String Ace = "A"; // 12

    private int calculateWinner(List<Card> cards) {
        int result = 0;
        Map<Integer,List<Card>> mapCards = cardToMap(cards);

        boolean flagForDouble = false; // Флаг для определения флеша
        boolean flagForTriple = false; // Флаг для определения флеша
        boolean flash = getFlash(cards);

        for(int card : mapCards.keySet())
        {
            boolean street = mapCards.get(card).size() == 1
                    && mapCards.get(card+1).size() == 1
                    && mapCards.get(card+2).size() == 1
                    && mapCards.get(card+3).size() == 1
                    && mapCards.get(card+4).size() == 1;

            if (street) //Стрит - карты по порядку
            {
                result = 40 * (card+22) * 5;
            }
            if (flash) //Флеш - одиннаковой масти
            {
                result = 80 * (card+20) * 5;
            }
            if (mapCards.get(card).size() == 4) //Каре - 4 карты
            {
                result += 310 * (card+20) * 4;
            }
            if (getStreetFlash(mapCards, card)) //СтритФлеш - Одиннаковой масти и по порядку
            {
                result = 450 * (card+20) * 5;
            }
            if (getStreetFlash(mapCards, card) && card == 8) // ФлешРояль, тот же стритфлеш только начинается с 8ки.
            {
                result = 1000 * (card+20) * 5;
            }
            if (mapCards.get(card).size() == 2 && !flagForTriple) //Пара если до этого у нас была тройка, то у нас фулхаус.
            {
                flagForDouble = true;
                result += 10 * (card+20) * 2;
            }
            if (mapCards.get(card).size() == 3 && !flagForDouble) //Трипла если до этого у нас была двойка, то у нас фулхаус.
            {
                flagForTriple = true;
                result += 30 * (card+20) * 3;
            }
            if (flagForDouble && flagForTriple) //ФулХаус - двойка и тройка одновременно
            {
                result = 120 * (card+20) * 5;
            }
            result = card; // Если нету никаких комбинаций, то берется самая старшая
        }
        return result;
    }

    /**
     * Метод, который преобразует карты в мапу типа [Номер карты:Сами карты] для удобного подсчета победителя
     * @param cards
     * @return Мапа
     */
    private Map<Integer, List<Card>> cardToMap(List<Card> cards){
        Map<Integer, List<Card>> result = new HashMap<>();

        for(Card card : cards)
        {
            if (result.get(Integer.parseInt(card.getRank())) == null)
            {
                result.put(Integer.parseInt(card.getRank()), Collections.singletonList(card));
            }
            else
            {
                result.get(Integer.parseInt(card.getRank())).add(card);
                result.put(Integer.parseInt(card.getRank()), (result.get(Integer.parseInt(card.getRank()))));
            }
        }
        return result;
    }

    /**
     * Определяем есть ли у игрока флеш.(5 одиннаковых мастей)
     * @param cards
     * @return является или нет
     */
    private boolean getFlash(List<Card> cards){
        Map<String,Integer> result = new HashMap<>();
        for(Card card : cards)
        {
            if (result.get(card.getSuit()) == null)
            {
                result.put(card.getSuit(), 1);
            }
            else
            {
                result.put(card.getSuit(), result.get(card.getSuit()) + 1);
            }
        }
        for (int sumSuit : result.values())
        {
            if (sumSuit >= 5)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Определяем есть ли у игрока флеш.(5 одиннаковых мастей и по порядку)
     * @param mapCard
     * @param index
     * @return является или нет
     */
    private boolean getStreetFlash(Map<Integer,List<Card>> mapCard, int index){
        boolean result = true;
        for (int i=0;i<4;i++)
        {
            if (!mapCard.get(index + i).get(1).getSuit().equals(mapCard.get(index).get(1).getSuit()))
            {
                result = false;
                break;
            }
        }
        for (int i=0;i<4;i++)
        {
            if (!mapCard.get(index + i).get(1).getSuit().equals(mapCard.get(index).get(1).getSuit()))
            {
                result = false;
                break;
            }
        }
        return result;
    }
}
