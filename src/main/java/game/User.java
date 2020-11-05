package game;

public class User {
    public Integer userId;
    public String firstName;
    public String chatId;

    public User(Integer userId, String chatId, String firstName) {
        this.userId = userId;
        this.chatId = chatId;
        this.firstName = firstName;
    }
}
