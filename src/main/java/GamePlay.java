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
            } else {
                return getDefaultAnswer(userId,"После /raise должно идти число, ставка до которой хочешь повысить");
            }
        }
        return getDefaultAnswer(userId,"Не умею так играть :( Проверь правила игры");
    }

    /* Начать игру */
    private List<GameAnswer> startGame(Integer userId, Game game) {
        if (game.getUsers().size() < 2) {
            return getDefaultAnswer(userId, "Необходимо не менее 2 игроков. Пригласи друзей присоедениться с кодом игры");
        }
        if (!orderStorage.gameUsers.containsKey(game.getId())) {
            orderStorage.gameUsers.put(game.getId(), game.getUsers());
            orderStorage.gamePointer.put(game.getId(), 0);
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
            int smallBlind = 15;
            firstUser.setBet(smallBlind);
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
            // следующий ход второго
            orderStorage.gamePointer.put(game.getId(), 1);
            return answers;
        }

        if (userId != getUserByIndex(game.getId(), 1).getId()) {
            return getDefaultAnswer(userId, "Не твой ход. Подожди сообщения");
        }
        // set bet for second
        User secondUser = userDAO.getEntityById(userId);
        int bigBlind = 30;
        secondUser.setBet(bigBlind);
        userDAO.update(secondUser);
        //to everyone
        addCommonMessages(game.getId(),
                secondUser.getFirstName()+" поставил(а) большой блайнд(30)",
                answers);

        // distribute Cards
        distributeCards(game, answers);

        // set state to 1st street trade
        game.setState(GameState.preTrade);
        gameDAO.update(game);
        addCommonMessages(game.getId(), "Начался у круг торгов", answers);

        // invite next to trade (if next exists)
        getNextUserId(game.getId(), 30);

        String chatId = getUserByIndex(game.getId(), orderStorage.gamePointer.get(game.getId()))
                .getChatId();
        answers.add(new GameAnswer(chatId,
                "Сделайте ставку /call или /raise <ставка>"));
        return answers;
    }

    private List<GameAnswer> call(Integer userId, Game game) {
        // right user
        int currIndex = orderStorage.gamePointer.get(game.getId());
        User currUser = orderStorage.gameUsers.get(game.getId()).get(currIndex);
        if (currUser.getId() != userId) {
            return getDefaultAnswer(userId, "Не твой ход. Подожди сообщения");
        }
        List<GameAnswer> answers = new ArrayList<>();
        // update bet
        int maxBet = getMaxBet(game);
        User user = userDAO.getEntityById(userId);
        user.setBet(maxBet);
        userDAO.update(user);
        getMaxBet(game);
        addCommonMessages(game.getId(),
                user.getFirstName() + " уровнял(а) ставку до " + maxBet ,
                answers);

        Integer nextUserId = getNextUserId(game.getId(), maxBet);

        if (nextUserId == null) { // конец круга
            orderStorage.gamePointer.put(game.getId(), 0);
            // раздать карту на стол
            String newCardMessage = distributeCardOnTable(game);
            addCommonMessages(game.getId(), newCardMessage, answers);
            // set state
            setNextState(game);
            if (game.getState() != GameState.showDown) {
                addCommonMessages(game.getId(), "Новый круг торгов", answers);
                String chatId = getUserByIndex(game.getId(), 0).getChatId();
                answers.add(new GameAnswer(chatId, "Сделайте ставку /call или /raise <ставка>"));
            } else  {
                //todo показать победителя и перевести деньги

                // clear game
                 clearData(game);
            }
        } else {
            String chatId = getUserByIndex(game.getId(), orderStorage.gamePointer.get(game.getId()))
                    .getChatId();
            answers.add(new GameAnswer(chatId, "Сделайте ставку /call или /raise <ставка>"));
        }
        return answers;
    }

    private List<GameAnswer> raise(Integer userId, Game game, int bet) {
        // right user
        int currIndex = orderStorage.gamePointer.get(game.getId());
        User currUser = orderStorage.gameUsers.get(game.getId()).get(currIndex);
        if (currUser.getId() != userId) {
            return getDefaultAnswer(userId, "Не твой ход. Подожди сообщения");
        }
        // update bet
        List<GameAnswer> answers = new ArrayList<>();

        int maxBet = getMaxBet(game);
        if (maxBet >= bet) {
            answers.add(new GameAnswer(userId.toString(), "Ставка должна быть больше ставок в игре"));
        }
        maxBet = bet;
        // update bet
        User user = userDAO.getEntityById(userId);
        user.setBet(bet);
        userDAO.update(user);
        getMaxBet(game);
        addCommonMessages(game.getId(),
                user.getFirstName() + " повысил(а) ставку до " + bet,
                answers);
        // ask next to bet
        getNextUserId(game.getId(), maxBet);
        String chatId = getUserByIndex(
                game.getId(),
                orderStorage.gamePointer.get(game.getId())).getChatId();
        answers.add(new GameAnswer(chatId, "Сделайте ставку /call или /raise <ставка>"));
        return answers;
    }

    private void clearData(Game game) {
        for(User user: game.getUsers()) {
            user.deleteCards();
            user.deleteGames();
            user.setBet(0);
            userDAO.update(user);
        }
        game.deleteCards();
        gameDAO.update(game);
    }

    private void setNextState(Game game) {
        GameState state = game.getState();
        GameState nextState = GameState.preTrade;
        if (state == GameState.preTrade) {
            nextState = GameState.tradeSecond;
        }
        if (state == GameState.tradeSecond) {
            nextState = GameState.tradeThird;
        }
        if (state == GameState.tradeThird) {
            nextState = GameState.tradeFourth;
        }
        if (state == GameState.tradeFourth) {
            nextState = GameState.showDown;
        }
        game.setState(nextState);
        gameDAO.update(game);
    }

    /* get next user or null */
    private Integer getNextUserId(int gameId, int maxBet) {
        int currIndex = orderStorage.gamePointer.get(gameId);
        List<User> orderedUsers = orderStorage.gameUsers.get(gameId);
        int sizeUsers = orderedUsers.size();

        int nextUserIndex = (currIndex + 1) % sizeUsers;
        int nextUserId = orderedUsers.get(nextUserIndex).getId();
        orderStorage.gamePointer.put(gameId, nextUserIndex);

        if (userDAO.getEntityById(nextUserId).getBet() == maxBet) {
            return null;
        }
        return nextUserId;
    }

    private Integer getMaxBet(Game game) {
        List<User> users = game.getUsers();
        Integer maxBet = 0;
        for (User user: users) {
            int bet = user.getBet();
//            System.out.print(user.getFirstName() +"'s bet "+bet+ ". ");
            maxBet = bet > maxBet ? bet : maxBet;
        }
//        System.out.println("Max bet " + maxBet);
        return maxBet;
    }

    private String distributeCardOnTable(Game game) {
        List<Card> usedCards = getGameUsersCards(game.getUsers());
        usedCards.addAll(game.getCards());
        Card newCard = cardHelper.getRandomNonExistingCard(usedCards);
        game.addCard(newCard);
        gameDAO.update(game);
        return "Новая карта на столе " + newCard;
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
