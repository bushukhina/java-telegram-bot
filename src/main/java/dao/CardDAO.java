package dao;

import entities.Card;
import org.hibernate.Session;
import org.hibernate.Transaction;
import storage.IController;
import storage.SessionFactoryCreator;

import java.util.List;

public class CardDAO implements IController<Card, Integer> {
    @Override
    public List<Card> getAll() {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        List<Card> result = (List<Card>) session.createQuery("From Card").list();
        session.close();
        return result;
    }

    /* Получить карту по параметрам */
    public Card getCardByRankSuit(String rank, String suit) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        List<Card> result = (List<Card>) session.createQuery("From Card card where card.rank = :rank and card.suit = :suit")
                .setParameter("rank", rank)
                .setParameter("suit", suit)
                .list();
        session.close();
        return result.size() > 0 ? result.get(0) : null;
    }

    @Override
    public Card getEntityById(Integer id) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Card result = session.get(Card.class, id);
        session.close();
        return result;
    }

    @Override
    public void update(Card entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(entity);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Card entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(entity);
        tx1.commit();
        session.close();
    }

    @Override
    public void save(Card entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(entity);
        tx1.commit();
        session.close();
    }
}
