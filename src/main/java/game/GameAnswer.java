package game;

import java.util.ArrayList;

public class GameAnswer {
    public ArrayList<String> respondTo;
    public String commonMessageText;
    public String originalUserMessageText;

    public GameAnswer(
        ArrayList<String> respondTo,
        String commonMessageText,
        String originalUserMessageText
    ) {
        this.respondTo = respondTo;
        this.commonMessageText = commonMessageText;
        this.originalUserMessageText = originalUserMessageText;
    }
}
