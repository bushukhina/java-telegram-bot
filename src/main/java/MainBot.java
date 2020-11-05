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

public class MainBot extends TelegramLongPollingBot {

    private final String BOT_TOKEN = System.getenv("TOKEN");
    private final String BOT_NAME = "poker_java_bot";

    private static PokerDealer pokerDealer;
    private static IDataStorage dataStorage;

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

    public void sendMsg(String chatId, String text) {
        if (text == null) {return;}
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        sendMessage.setChatId(chatId);

        // Будет пересылать сообщение при ответе
        // sendMessage.setReplyToMessageId(message.getMessageId());

        sendMessage.setText(text);
        try {

            sendMessage(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

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
            String command = args.length > 0 ? args[0] : text;
            GameAnswer answ;

            switch (command) {
                case "/help":
                    sendMsg(chatId, "В будущем тут появится сообщение с информацией о командах :)");
                    return;
                case "/start":
                    sendMsg(chatId, "Привет, давай сыграем в покер?");
                    return;
                case "/create":
                    answ = pokerDealer.createGame(message.getFrom().getId());
                    break;
                case "/join":
                    answ = pokerDealer.joinGame(message.getFrom().getId(), args);
                    break;
                default:
                    sendMsg(chatId, "text");
                    return;
            }
            sendMsg(chatId, answ.originalUserMessageText);
            for (String memberChatId : answ.respondTo) {
                sendMsg(memberChatId, answ.commonMessageText);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
