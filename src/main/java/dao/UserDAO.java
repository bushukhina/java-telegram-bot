package dao;

import entities.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import storage.IController;
import storage.SessionFactoryCreator;

import java.util.List;

public class UserDAO implements IController<User, Integer> {

    @Override
    public List<User> getAll() {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        List<User> result = (List<User>) session.createQuery("From User").list();
        session.close();
        return result;
    }

    @Override
    public User getEntityById(Integer id) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        User result = session.get(User.class, id);
        session.close();
        return result;
    }

    @Override
    public void update(User entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(entity);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(User entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(entity);
        tx1.commit();
        session.close();
    }

    @Override
    public void save(User entity) {
        Session session = SessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(entity);
        tx1.commit();
        session.close();
    }
}
