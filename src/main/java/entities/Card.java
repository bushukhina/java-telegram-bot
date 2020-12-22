package entities;

import javax.persistence.*;


@Entity
@Table (name = "cards")
public class Card {
    @Id
    private int id;
    private String suit;
    private String rank;

    @ManyToOne(optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne(optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name = "game_id")
    private Game game;

    protected Card() {}

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
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

    public User getUser() { //Если не будет работать, то поменять на int
        return user;
    }

    public void setUser(User user) { //Если не будет работать, то поменять на int
        this.user = user;
    }

    public Game getGame() { //Если не будет работать, то поменять на int
        return game;
    }

    public void setGame(Game game) { //Если не будет работать, то поменять на int
        this.game = game;
    }

    @Override
    public String toString() {
        return rank + suit;
    }
}
