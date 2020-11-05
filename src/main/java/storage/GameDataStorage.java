package storage;

import game.User;

import java.util.HashMap;
import java.util.ArrayList;

/* Хранилище данных с методами получения информации */
public class GameDataStorage implements IDataStorage {

    private long gameCount = 0;
    private final HashMap<String, Long> games = new HashMap<>();
    private final HashMap<Long, ArrayList<Integer>> gamePlayers = new HashMap<>();
    private final HashMap<Integer, User> users = new HashMap<>();


    @Override
    public void createGame(String uuid) {
        games.put(uuid, gameCount++);
    }

    @Override
    public long getGameId(String gameCode) {
        return games.get(gameCode);
    }

    @Override
    public void addUserToGame(Integer userId, long gameId) {
        if (!gamePlayers.containsKey(gameId)) {
            gamePlayers.put(gameId, new ArrayList<Integer>());
        }
        gamePlayers.get(gameId).add(userId);
    }

    @Override
    public ArrayList<Integer> getGamePlayerIds(long gameId) {
        return gamePlayers.get(gameId);
    }

    @Override
    public void saveUser(User user) {
        if (!users.containsKey(user.userId)) {
            users.put(user.userId, user);
        }
    }

    @Override
    public User getUser(Integer userId) {
        return users.get(userId);
    }
}
