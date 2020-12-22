package entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table (name = "cards")
public class Card {
    @Id
    private int id;
    private String suit;
    private String rank;

    @ManyToMany
    @JoinTable(name = "user_cards",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @ManyToMany
    @JoinTable(name = "game_cards",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id"))
    private List<Game> games;

    protected Card() {}

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        users = new ArrayList<>();
        games = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public List<User> getUser() { //Если не будет работать, то поменять на int
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<Game> getGames() { //Если не будет работать, то поменять на int
        return games;
    }

    public void addGame(Game game) {
        games.add(game);
    }

    @Override
    public String toString() {
        return rank + suit;
    }
}
