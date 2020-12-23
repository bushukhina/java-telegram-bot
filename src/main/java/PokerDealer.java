import dao.CardDAO;
import dao.GameDAO;
import dao.UserDAO;
import entities.Card;
import entities.Game;
import entities.User;
import game.GameAnswer;
import game.GameState;
import helpers.CardHelper;

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
    CardDAO cardDAO;
    GamePlay gamePlay;

    CardHelper cardHelper = new CardHelper();

    PokerDealer(UserDAO userDAO) {
        this.userDAO = userDAO;
        gameDAO = new GameDAO();
        cardDAO = new CardDAO();
        initCards();
        gamePlay = new GamePlay(gameDAO, userDAO, cardDAO);
    }

    public void initCards() {
        if (cardDAO.getAll().size() > 0) {
            return;
        }
        List<Card> cardSet= cardHelper.buildCardSet();
        for (Card card: cardSet) {
            cardDAO.save(card);
            System.out.println("Card " + card + " is created");
        }
    }

    public List<GameAnswer> processRequest(Integer userId, String[] args) {
        String command = args.length > 0 ? args[0] : "";
        System.out.println(command);
        switch (command) {
            case "/help":
                return gamePlay.getDefaultAnswer(userId,"В будущем тут появится сообщение с информацией о командах :)");
            case "/start":
                return gamePlay.getDefaultAnswer(userId,"Привет, давай сыграем в покер?");
            case "/create":
                return createGame(userId);
            case "/join":
                return joinGame(userId, args);
            case "/run": // начать игру
            case "/fold": // сброс карт
            case "/blind": // фиксированные 2 ставки в начале игры
            case "/call": //уравнять после райза
            case "/raise": // повысить ставки
            case "/check": //не вносить денег к ставке, можно если ты первый или перед тобой тоже чеки
                return gamePlay.attendGame(userId, args);
            default:
                return gamePlay.getDefaultAnswer(userId, "Некорректная команда");
        }
    }

    /* Создать игру */
    public List<GameAnswer> createGame(Integer userId) {
        UUID uuid= UUID.randomUUID();
        Game game = new Game(uuid, GameState.notStarted);
        User user = userDAO.getEntityById(userId);
        game.addUser(user);
        gameDAO.save(game);
        ArrayList<GameAnswer> answers = new ArrayList<>();
        answers.add(new GameAnswer(userId.toString(),"Ты создал игру с кодом: "));
        answers.add(new GameAnswer(userId.toString(), uuid.toString()));
        return answers;
    }

    /* Присоединиться к игре */
    public List<GameAnswer> joinGame(Integer userId, String[] args) {
        // если меньше дувух, кидать ошибку
        String uuid = args[1];

        int gameId = gameDAO.getIdByUUID(java.util.UUID.fromString(uuid));
        Game game = gameDAO.getEntityById(gameId);
        User user = userDAO.getEntityById(userId);
        if (game.getState() != GameState.notStarted) {
            return gamePlay.getDefaultAnswer(userId, "Невозможно присоединиться к игре, которая уже идет");
        }
        if (!game.getUsers().contains(user)) {
            game.addUser(user);
            gameDAO.update(game);
        }
        String fistName = user != null ? user.getFirstName(): userId.toString();
        List<String> chatIds = gamePlay.getGameMembersChatIds(gameId);
        String commonMessage = "Пользователь "+ fistName +" присоединился к игре";
        ArrayList<GameAnswer> answers = new ArrayList<>();
        for (String chatId: chatIds) {
            answers.add(new GameAnswer(chatId, commonMessage));
        }
        return answers;
    }

}
