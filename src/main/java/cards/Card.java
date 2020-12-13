package cards;

public class Card {

    public String Suit;
    public String Rank;
    public static CardConsts cardConsts = new CardConsts();

    public Card(int suit, int rank) {
        if (suit < 1 || suit > 4 || rank < 0 || rank > 12)
            throw new IllegalArgumentException("Некорректный ранг " + rank + "или масть " + suit);

        Suit = cardConsts.suits.get(suit);
        Rank = cardConsts.ranks.get(rank);
    }

    public Card(int number){
        if (number < 1 || number > 52)
            throw new IllegalArgumentException("Значение вне границ значиений для карт " + number);

        int rank = number / 4;
        int suit = number % 4;

        Suit = cardConsts.suits.get(suit);
        Rank = cardConsts.ranks.get(rank);
    }

    @Override
    public String toString() {
        return Rank + Suit;
    }
}
