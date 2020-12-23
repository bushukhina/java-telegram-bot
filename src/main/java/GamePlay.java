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
        System.out.println(state);
        if (args[0] == "/run" && state == GameState.notStarted) {
            return startGame(userId);
        }

        return getDefaultAnswer(userId,"Не умею так играть :( Проверь правила игры");
    }

    /* Сформировать ответ с заданным текстом */
    public List<GameAnswer> getDefaultAnswer(Integer userId, String message) {
        List<GameAnswer> answers = new ArrayList<GameAnswer>();
        answers.add(new GameAnswer(userId.toString(), message));
        return answers;
    }

    /* Получаем чаты в которые нужно отправить общие оповедения */
    public ArrayList<String> GetGameMembersChatIds(int gameId) {
        Game game = gameDAO.getEntityById(gameId);
        List<User> users = game.getUsers();
        System.out.println(users);
        ArrayList<String> codes = new ArrayList<>();
        for (User user : users) {
            codes.add(user.getChatId());
        }
        return codes;
    }

    private Game getGameByUserId(Integer userId) {
        User user = userDAO.getEntityById(userId);
        List<Game> games = user.getGames();
        if (games.size() < 1) {
            return null;
        }
        return games.get(0);
    }

    private List<GameAnswer> startGame(Integer userId) {
        // set state to blind
        return null;
    }

    private List<GameAnswer> blind(Integer userId) {
        // set state to big blind or (to market and give pocket money)
        return null;
    }

    private List<GameAnswer> call(Integer userId) {
        // set state to blind
        return null;
    }

    private List<GameAnswer> raise(Integer userId) {
        // set state to blind
        return null;
    }

    private List<GameAnswer> fold(Integer userId) {
        // set state to blind
        return null;
    }

    private List<GameAnswer> check(Integer userId) {
        // set state to blind
        return null;
    }
}
