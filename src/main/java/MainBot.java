import game.GameAnswer;
import game.User;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import storage.GameDataStorage;
import storage.IDataStorage;

/*
* Обертка для связи с телеграмом
* */
public class MainBot extends TelegramLongPollingBot {

    private final String BOT_TOKEN = System.getenv("TOKEN");
    private final String BOT_NAME = "poker_java_bot";

    private static PokerDealer pokerDealer;
    private static IDataStorage dataStorage;

    /* Запуск и регистрация */
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        dataStorage = new GameDataStorage();
        pokerDealer = new PokerDealer(dataStorage);

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
        dataStorage.saveUser(new User(
                message.getFrom().getId(),
                chatId,
                message.getFrom().getFirstName()));

        if (message != null && message.hasText()) {

            String text = message.getText();
            String[] args = text.split(" ");

            GameAnswer answer = pokerDealer.processRequest(message.getFrom().getId(), args);

            sendMsg(chatId, answer.messageText);
            for (String memberChatId : answer.commonMessageReceivers) {
                sendMsg(memberChatId, answer.commonMessageText);
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
