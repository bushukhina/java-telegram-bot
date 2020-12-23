package dao;

import entities.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;
import storage.IController;
import storage.SessionFactoryCreator;

import java.util.List;
import java.util.UUID;

public class GameDAO implements IController<Game, Integer> {
    @Override
    public List<Game> getAll() {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        List<Game> result = (List<Game>) session.createQuery("From Game").list();
        session.close();
        return result;
    }

    public Integer getIdByUUID(UUID uuid) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        List<Game> result = (List<Game>) session.createQuery("From Game game where game.code = :code")
                .setParameter("code", uuid)
                .list();
        session.close();
        return result.size() > 0 ? result.get(0).getId() : null;
    }

    @Override
    public Game getEntityById(Integer id) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Game result = session.get(Game.class, id);
        session.close();
        return result;
    }

    @Override
    public void update(Game entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(entity);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Game entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(entity);
        tx1.commit();
        session.close();
    }

    @Override
    public void save(Game entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(entity);
        tx1.commit();
        session.close();
    }
}
