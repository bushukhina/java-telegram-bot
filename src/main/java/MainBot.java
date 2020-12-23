import dao.UserDAO;
import entities.User;
import game.GameAnswer;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import storage.*;

import java.util.List;

/*
* Обертка для связи с телеграмом
* */
public class MainBot extends TelegramLongPollingBot {

    private final String BOT_TOKEN = System.getenv("TOKEN");
    private final String BOT_NAME = "poker_java_bot";

    private static PokerDealer pokerDealer;
    private static UserDAO userDAO;

    /* Запуск и регистрация */
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        userDAO = new UserDAO();
        DataBase dataBase = new DataBase();
        dataBase.connect();

        pokerDealer = new PokerDealer(userDAO);

        try {
            telegramBotsApi.registerBot(new MainBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    /* Отправка сообщений */
    public void sendMsg(String chatId, String text) {
        if (text == null) {return;}

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {

            sendMessage(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /* Обработка входящих сообщений */
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        int id = message.getFrom().getId();

        if(userDAO.getEntityById(id) == null) {
            userDAO.save(new User(
                    id,
                    chatId,
                    message.getFrom().getFirstName())
            );
        }


        if (message != null && message.hasText()) {

            String text = message.getText();
            String[] args = text.split(" ");

            List<GameAnswer> answers = pokerDealer.processRequest(message.getFrom().getId(), args);

            for (GameAnswer answer : answers) {
                sendMsg(answer.receiver, answer.messageText);
            }
        }
    }

    /* Имя бота для регистрации */
    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /* Токен для регистрации */
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
