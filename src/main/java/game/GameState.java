package game;

/* Состояние игры */
public enum GameState {
    /* Не началась */
    notStarted,
    /* Малый блайнд */
    smallBlind,
    /* Большой блайнд */
    bigBlind,
    /* Раздача карманных карт */
    distribution,
    /* Предварительная торговля (1 круг) */
    preTrade,
    /* Три карты на стол */
    flop,
    /* Второй круг торговли */
    tradeSecond,
    /* 4ая карта на стол */
    tern,
    /* Третий круг торговли */
    tradeThird,
    /* 5ая карта на стол */
    river,
    /* Четвертый круг торговли */
    tradeFourth,
    /* Вскрытие карт */
    showDown
}
