import dao.CardDAO;
import dao.GameDAO;
import dao.UserDAO;
import entities.Game;
import entities.User;
import game.GameAnswer;
import game.GameState;

import java.util.ArrayList;
import java.util.List;

/*
* Игровое взаимодействие
* */
public class GamePlay {

    UserDAO userDAO;
    GameDAO gameDAO;
    CardDAO cardDAO;

    GamePlay(GameDAO gameDAO, UserDAO userDAO, CardDAO cardDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.cardDAO = cardDAO;
    }

    /* Сделать ход */
    public List<GameAnswer> attendGame(Integer userId, String[] args) {
        Game game = getGameByUserId(userId);
        if (game == null) {
            return getDefaultAnswer(userId,"Сначала создай свою или присоединитесь к игре друга");
        }
        GameState state = game.getState();
        String command = args[0];
        System.out.println(state);
        if (command.equals("/fold")) {
            return fold(userId, game);
        }
        if (command.equals("/run") && state == GameState.notStarted) {
            return startGame(game);
        }
        if (command.equals("/blind") && (state == GameState.smallBlind || state == GameState.bigBlind)) {
            return blind(userId, game);
        }
        if (state == GameState.preTrade || state == GameState.tradeSecond
                || state == GameState.tradeThird || state == GameState.tradeFourth) {
            if (args.length >= 2 && tryParseInt(args[1])) {

                int bet = Integer.parseInt(args[1]);
                if (command.equals("/raise")) {
                    return raise(userId, game, bet);
                }
                if (command.equals("/call")) {
                    return call(userId, game, bet);
                }
            }
        }

        return getDefaultAnswer(userId,"Не умею так играть :( Проверь правила игры");
    }

    private List<GameAnswer> fold(Integer userId, Game game) {
        User user = userDAO.getEntityById(userId);
        String fistName = user.getFirstName();
        user.setIsActive(false);
        userDAO.update(user);
        List<String> chatIds = getGameMembersChatIds(game.getId());
        String commonMessage = "Пользователь "+ fistName +" покинул игру";
        ArrayList<GameAnswer> answers = new ArrayList<>();
        for (String chatId: chatIds) {
            answers.add(new GameAnswer(chatId, commonMessage));
        }

        // todo проверка, что не конец круга или не один человек в игре;
        // todo если да, то перейти на следующий этап или раскрытие карт
        return answers;
    }

    /* Начать игру */
    private List<GameAnswer> startGame(Game game) {
        // notify about the beginning of the game
        List<String> chatIds = getGameMembersChatIds(game.getId());
        String commonMessage = "Игра началась.";
        ArrayList<GameAnswer> answers = new ArrayList<>();
        for (String chatId: chatIds) {
            answers.add(new GameAnswer(chatId, commonMessage));
        }
        // set state to blind
        game.setState(GameState.smallBlind);
        gameDAO.update(game);
        // invite first player to set blind
        User user = game.getUsers().get(0);
        String chatId = user.getChatId();
        answers.add(new GameAnswer(chatId,
                "Поставьте малый блайнд. Используйте команду /blind"));
        answers.add(new GameAnswer(chatId,
                "Размер малого блайнда 15. У вас на счету: " + user.getMoney()));
        return answers;
    }

    private List<GameAnswer> blind(Integer userId, Game game) {
        // set state to big blind or (to market and give pocket money)

        // проверка, что не конец круга
        // если да, то перейти на следующий этап
        return null;
    }

    private List<GameAnswer> call(Integer userId, Game game, int bet) {
        // set state to blind

        // проверка, что не конец круга
        // если да, то перейти на следующий этап
        return null;
    }

    private List<GameAnswer> raise(Integer userId, Game game, int bet) {
        // проверка, что не конец круга
        // если да, то перейти на следующий этап
        return null;
    }

    private List<GameAnswer> check(Integer userId, Game game) {
        //проверка, что можно сделать чек

        // проверка, что не конец круга
        // если да, то перейти на следующий этап
        return null;
    }

    /* Сформировать ответ с 1 сообщением */
    public List<GameAnswer> getDefaultAnswer(Integer userId, String message) {
        List<GameAnswer> answers = new ArrayList<>();
        answers.add(new GameAnswer(userId.toString(), message));
        return answers;
    }

    /* Получаем чаты всех в игре */
    public ArrayList<String> getGameMembersChatIds(int gameId) {
        Game game = gameDAO.getEntityById(gameId);
        List<User> users = game.getUsers();
        ArrayList<String> codes = new ArrayList<>();
        for (User user : users) {
            codes.add(user.getChatId());
        }
        return codes;
    }

    /* Получить игру пользователя */
    private Game getGameByUserId(Integer userId) {
        User user = userDAO.getEntityById(userId);
        List<Game> games = user.getGames();
        if (games.size() < 1) {
            return null;
        }
        return games.get(0);
    }

    /* Можно ли получить число из строки */
    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
