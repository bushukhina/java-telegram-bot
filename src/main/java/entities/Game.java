package entities;

import game.GameState;
import org.hibernate.annotations.Type;
import storage.EnumTypePostgreSql;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@org.hibernate.annotations.TypeDef(name = "enum_postgressql", typeClass = EnumTypePostgreSql.class)
@Entity
@Table (name = "game")
public class Game {
    @Id
    private int id;
    @Type(type="pg-uuid")
    private UUID code;
    @Enumerated(EnumType.STRING)
    @Type(type = "enum_postgressql")
    private GameState state;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable (name = "game_users",
        joinColumns = @JoinColumn (name = "game_id"),
        inverseJoinColumns = @JoinColumn (name = "user_id"))
    private List<User> users;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable (name = "game_cards",
            joinColumns = @JoinColumn (name = "game_id"),
            inverseJoinColumns = @JoinColumn (name = "card_id"))
    private List<Card> cards;

    protected Game() {}

    public Game(int id, UUID code, GameState state) {
        this.id = id;
        this.code = code;
        this.state = state;
        users = new ArrayList<>();
        cards = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public UUID getCode() {
        return code;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public void deleteUsers() {
        users.clear();
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

    //По мере необходимости можно ещё добавить методов
}
