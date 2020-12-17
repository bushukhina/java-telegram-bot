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

    protected Card() {}

    public Card(int id, String suit, String rank, User user) {
        this.id = id;
        this.suit = suit;
        this.rank = rank;
        this.user = user;
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
}
