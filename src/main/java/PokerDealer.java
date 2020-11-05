import game.GameAnswer;
import game.User;
import storage.IDataStorage;

import java.util.ArrayList;
import java.util.UUID;

/*
* Диллер покерной игры
* Создает игры и соединяет игроков
* Ведет игру
* */
public class PokerDealer {

    IDataStorage dataStorage;

    PokerDealer(IDataStorage dataStorage) { this.dataStorage = dataStorage; }

    /* Создать игру */
    public GameAnswer createGame(Integer userId) {
        String uuid= UUID.randomUUID().toString();
        this.dataStorage.createGame(uuid);
        long gameId = dataStorage.getGameId(uuid);
        dataStorage.addUserToGame(userId, gameId);
        GameAnswer answ = new GameAnswer(new ArrayList<String>(),
                "",
                "Вы создали игру с кодом: " + uuid);
        return answ;
    }

    /* Присоединиться к игре */
    public GameAnswer joinGame(Integer userId, String[] args) {
        // если меньше дувух, кидать ошибку
        String uuid = args[1];
        long gameId = dataStorage.getGameId(uuid);
        dataStorage.addUserToGame(userId, gameId);

        User user = dataStorage.getUser(userId);
        String fistName = user != null ? user.firstName: userId.toString();
        GameAnswer answ = new GameAnswer(
                GetGameMambersChatIds(gameId),
                "Пользователь "+ fistName +" присоединился к игре",
                 null);
        return answ;
    }

    /* Сделать ход */
    public void attendGame(long userId) {

    }

    /* Получаем чаты в которые нужно отправить общие оповедения */
    private ArrayList<String> GetGameMambersChatIds(Long gameId) {
        ArrayList<Integer> gamePlayerIds = dataStorage.getGamePlayerIds(gameId);
        ArrayList<String> codes = new ArrayList<>();
        for (Integer userId : gamePlayerIds) {
            User user = dataStorage.getUser(userId);
            if (user != null) {
                codes.add(user.chatId);
            }
        }
        return codes;
    }
}
