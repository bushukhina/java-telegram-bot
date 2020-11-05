package game;

import storage.IDataStorage;

/* Классическая игра в покер */
public class ClassicPokerGame {

    IDataStorage dataStorage;

    ClassicPokerGame(IDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

}
