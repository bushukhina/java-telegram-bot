package storage;

import game.User;

import java.util.ArrayList;
import java.util.UUID;

/*
    Хранилище данных
 */
public interface IDataStorage {
    /* Создать игру */
    void createGame(String uuid);

    /* Получить Id игры по коду */
    long getGameId(String gameCode);

    /* Добавить игрока в игру */
    void addUserToGame(Integer userId, long gameId);

    /* Список id игроков игры */
    ArrayList<Integer> getGamePlayerIds(long gameId);

    void saveUser(User user);

    User getUser(Integer userId);
}
