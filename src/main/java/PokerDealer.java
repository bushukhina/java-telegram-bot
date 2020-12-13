import game.GameAnswer;
import game.User;
import storage.IDataStorage;

import java.io.Console;
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

    public GameAnswer processRequest(Integer userId, String[] args) {
        String command = args.length > 0 ? args[0] : "";
        System.out.println(command);
        switch (command) {
            case "/help":
                return answerByDefault("В будущем тут появится сообщение с информацией о командах :)");
            case "/start":
                return answerByDefault("Привет, давай сыграем в покер?");
            case "/create":
                return createGame(userId);
            case "/join":
                return joinGame(userId, args);
            case "/blind": // фиксированные 2 ставки в начале игры
            case "/call": //уравнять после райза
            case "/raise": // повысить ставки
            case "/check": //не вносить денег к ставке, можно если
                // ты первый или перед тобой тоже чеки
            case "/fold": // сброс карт
                return attendGame(userId, args);
            default:
                return answerByDefault("Некорректная команда");
        }
    }

    public GameAnswer answerByDefault(String message) {
        return new GameAnswer(message, new ArrayList<String>(),"");
    }

    /* Создать игру */
    public GameAnswer createGame(Integer userId) {
        String uuid= UUID.randomUUID().toString();
        this.dataStorage.createGame(uuid);
        long gameId = dataStorage.getGameId(uuid);
        dataStorage.addUserToGame(userId, gameId);
        GameAnswer answ = new GameAnswer(
                "Вы создали игру с кодом: " + uuid,
                new ArrayList<String>(), "");
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
                null,
                GetGameMambersChatIds(gameId),
                "Пользователь "+ fistName +" присоединился к игре");
        return answ;
    }

    /* Сделать ход */
    public GameAnswer attendGame(long userId, String[] args) {

        return null;
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
