package entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;


@Entity
@Table (name = "users")
public class User {
    @Id
    private Integer id;
    @Column (name = "firstname")
    private String firstName;
    @Column (name = "chatid")
    private String chatId;
    private int money;
    private int bet;
    @Column (name = "isactive")
    private boolean isActive;

    @ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable (name = "user_cards",
            joinColumns = @JoinColumn (name = "user_id"),
            inverseJoinColumns = @JoinColumn (name = "card_id"))
    private List<Card> cards;

    @ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable (name = "game_users",
        joinColumns = @JoinColumn (name = "user_id"),
        inverseJoinColumns = @JoinColumn (name = "game_id"))
    private List<Game> games;

    protected User() {}

    public User(Integer userId, String chatId, String firstName,
                int money, int bet, boolean isActive) { //добавить в конструктор ставку и деньги?
        this.id = userId;
        this.chatId = chatId;
        this.firstName = firstName;
        this.money = money;
        this.bet = bet;
        this.isActive = isActive;
        cards = new ArrayList<>();
        games = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getChatId() {
        return chatId;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean meaning) {
        isActive = meaning;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void deleteCards() {
        cards.clear();
    }

    public List<Game> getGames() {
        return games;
    }

    public void deleteGames() {
        games.clear();
    }

    //При необходимости добавить ещё методов

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", money=" + money +
                ", bet=" + bet +
                '}';
    }
}
