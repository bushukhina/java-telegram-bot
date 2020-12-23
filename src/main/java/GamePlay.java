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

/*
* Игровое взаимодействие
* */
public class GamePlay {

    UserDAO userDAO;
    GameDAO gameDAO;
    CardDAO cardDAO;
    CardHelper cardHelper = new CardHelper();
    GameOrderStorage orderStorage = new GameOrderStorage();

    GamePlay(GameDAO gameDAO, UserDAO userDAO, CardDAO cardDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.cardDAO = cardDAO;
    }

    /* Сделать ход */
    public List<GameAnswer> attendGame(Integer userId, String[] args) {
        Game game = getGameByUserId(userId);
        if (game == null) {
            return getDefaultAnswer(userId,"Сначала создай свою игру или присоединитесь к игре друга");
        }
        GameState state = game.getState();
        String command = args[0];
        System.out.println(state);
        if (command.equals("/fold")) {
            return fold(userId, game);
        }
        if (command.equals("/run") && state == GameState.notStarted) {
            return startGame(userId,game);
        }
        if (command.equals("/blind") && (state == GameState.smallBlind || state == GameState.bigBlind)) {
            return blind(userId, game);
        }
        if (state == GameState.preTrade || state == GameState.tradeSecond
                || state == GameState.tradeThird || state == GameState.tradeFourth) {
            if (command.equals("/call")) {
                return call(userId, game);
            }
            if (args.length >= 2 && tryParseInt(args[1])) {

                int bet = Integer.parseInt(args[1]);
                System.out.println(bet);
                if (command.equals("/raise")) {
                    return raise(userId, game, bet);
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
    private List<GameAnswer> startGame(Integer userId, Game game) {
        if (game.getUsers().size() < 2) {
            return getDefaultAnswer(userId, "Необходимо не менее 2 игроков. Пригласи друзей присоедениться с кодом игры");
        }
        System.out.println(game.getUsers());
        if (!orderStorage.gameUsers.containsKey(game.getId())) {
            orderStorage.gameUsers.put(game.getId(), game.getUsers());
        }
        if (userId != getUserByIndex(game.getId(), 0).getId()) {
            return getDefaultAnswer(userId, "Дать старт может только создатель игры");
        }
        // notify about the beginning of the game
        ArrayList<GameAnswer> answers = new ArrayList<>();
        addCommonMessages(game.getId(), "Игра началась", answers);
        // set state to blind
        game.setState(GameState.smallBlind);
        gameDAO.update(game);
        // invite first player to set blind
        answers.add(new GameAnswer(userId.toString(),
                "Поставь малый блайнд(15). Используй команду /blind."));
        return answers;
    }

    private List<GameAnswer> blind(Integer userId, Game game) {
        List<GameAnswer> answers = new ArrayList<>();
        GameState state = game.getState();

        if (state == GameState.smallBlind) {
            if (userId != getUserByIndex(game.getId(), 0).getId()) {
                return getDefaultAnswer(userId, "Не твой ход. Подожди сообщения");
            }
            // set bet for first
            User firstUser = userDAO.getEntityById(userId);
            firstUser.setBet(15);
            userDAO.update(firstUser);
            // set state to big blind
            game.setState(GameState.bigBlind);
            gameDAO.update(game);
            //to everyone
            addCommonMessages(game.getId(),
                    firstUser.getFirstName()+" поставил(а) малый блайнд(15)",
                    answers);
            // invite second for big blind
            String secondUserChatId = getUserByIndex(game.getId(), 1).getChatId();
            answers.add(new GameAnswer(secondUserChatId,
                    "Поставь большой блайнд(30). Используй команду /blind"));
            return answers;
        }

        if (userId != getUserByIndex(game.getId(), 1).getId()) {
            return getDefaultAnswer(userId, "Не твой ход. Подожди сообщения");
        }
        // set bet for second
        User secondUser = userDAO.getEntityById(userId);
        secondUser.setBet(30);
        userDAO.update(secondUser);
        //to everyone
        addCommonMessages(game.getId(),
                secondUser.getFirstName()+" поставил(а) большой блайнд(30)",
                answers);

        // distribute Cards
        distributeCards(game, answers);

        // if next exists, set state to 1st street trade
//        game.setState(GameState.preTrade);
//        gameDAO.update(game);
        // trade stared for everyone
//        addCommonMessages(game.getId(), "Начался у круг торгов", answers);


        // invite next to trade (if next exists)
//        User secondUser = game.getUsers().get(1);
//        answers.add(new GameAnswer(secondUser.getChatId(),
//                "Сделайте ставку"));
//        answers.add(new GameAnswer(secondUser.getChatId(),
//                "У вас на счету: " + secondUser.getMoney()));

        // todo! проверка, что не конец круга
        // todo! если да, то перейти на следующий этап
        return answers;
    }

    private List<GameAnswer> call(Integer userId, Game game) {
        // set state

        // проверка, что не конец круга
        // если да, то перейти на следующий этап
        return null;
    }

    private List<GameAnswer> raise(Integer userId, Game game, int bet) {
        // проверка, что не конец круга
        // если да, то перейти на следующий этап
        return null;
    }

    /* Раздать карманные карты */
    private void distributeCards(Game game, List<GameAnswer> answers) {
        List<User> users = game.getUsers();
        List<Card> userCards = getGameUsersCards(users);

        for (User user: users) {
            Card existCard1 = cardHelper.getRandomNonExistingCard(userCards);
            userCards.add(existCard1);

            Card existCard2 = cardHelper.getRandomNonExistingCard(userCards);
            userCards.add(existCard2);

            user.addCard(existCard1);
            user.addCard(existCard2);
            userDAO.update(user);

            answers.add(
                    new GameAnswer(user.getChatId(),
                        "Твои карты: " + existCard1 + " " + existCard2));
        }
    }

    /* get stored order entity */
    private User getUserByIndex(int gameId, int index) {
        if (orderStorage.gameUsers.containsKey(gameId) && orderStorage.gameUsers.get(gameId).size() > index) {
            return orderStorage.gameUsers.get(gameId).get(index);
        }
        return null;
    }

    /* Карты на руках у всех игроков */
    private List<Card> getGameUsersCards(List<User> users) {
        List<Card> cards = new ArrayList<>();
        for (User user : users) {
            cards.addAll(user.getCards());
        }
        return cards;
    }

    /* Сформировать ответ с 1 сообщением */
    public List<GameAnswer> getDefaultAnswer(Integer userId, String message) {
        List<GameAnswer> answers = new ArrayList<>();
        answers.add(new GameAnswer(userId.toString(), message));
        return answers;
    }

    /* Добавить сообщение для всех */
    private void addCommonMessages(int gameId, String commonMessage, List<GameAnswer> answers) {
        List<String> chatIds = getGameMembersChatIds(gameId);
        for (String chatId: chatIds) {
            answers.add(new GameAnswer(chatId, commonMessage));
        }
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
