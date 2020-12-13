package game;

import java.util.ArrayList;

public class GameAnswer {
    public ArrayList<String> commonMessageReceivers;
    public String commonMessageText;
    public String messageText;

    public GameAnswer(
        String messageText,
        ArrayList<String> commonMessageReceivers,
        String commonMessageText

    ) {
        this.messageText = messageText;
        this.commonMessageReceivers = commonMessageReceivers;
        this.commonMessageText = commonMessageText;
    }
}
