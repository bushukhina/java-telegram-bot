import dao.GameDAO;
import dao.UserDAO;
import entities.Game;
import entities.User;
import game.GameAnswer;
import game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
* Диллер покерной игры
* Отвечает на все запросы пользователей
* */
public class PokerDealer {

    UserDAO userDAO;
    GameDAO gameDAO;

    PokerDealer(UserDAO userDAO) {
        this.userDAO = userDAO;
        gameDAO = new GameDAO();
    }

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

    /* Сделать ход */
    public GameAnswer attendGame(long userId, String[] args) {

        return null;
    }

    /* Сформировать ответ с заданным текстом */
    public GameAnswer answerByDefault(String message) {
        return new GameAnswer(message, new ArrayList<String>(),"");
    }

    /* Создать игру */
    public GameAnswer createGame(Integer userId) {
        UUID uuid= UUID.randomUUID();
        int gameId = gameDAO.getAll().size() + 1;
        Game game = new Game(gameId, uuid, GameState.notStarted);
        User user = userDAO.getEntityById(userId);
        game.addUser(user);
        gameDAO.save(game);
        GameAnswer answ = new GameAnswer(
                "Вы создали игру с кодом: " + uuid,
                new ArrayList<String>(), "");
        return answ;
    }

    /* Присоединиться к игре */
    public GameAnswer joinGame(Integer userId, String[] args) {
        // если меньше дувух, кидать ошибку
        String uuid = args[1];

        int gameId = gameDAO.getIdByUUID(java.util.UUID.fromString(uuid));
        Game game = gameDAO.getEntityById(gameId);
        User user = userDAO.getEntityById(userId);
        if (!game.getUsers().contains(user)) {
            game.addUser(user); //Возможно, эту проверку нужно делать в другом месте
        }
        String fistName = user != null ? user.getFirstName(): userId.toString();
        GameAnswer answ = new GameAnswer(
                null,
                GetGameMembersChatIds(gameId),
                "Пользователь "+ fistName +" присоединился к игре");
        return answ;
    }

    /* Получаем чаты в которые нужно отправить общие оповедения */
    private ArrayList<String> GetGameMembersChatIds(int gameId) {
        Game game = gameDAO.getEntityById(gameId);
        List<User> users = game.getUsers();
        ArrayList<String> codes = new ArrayList<>();
        for (User user : users) {
            if (user != null) { //зачем проверяем?
                codes.add(user.getChatId());
            }
        }
        return codes;
    }
}
