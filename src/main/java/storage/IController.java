package storage;

import java.util.List;

public interface IController<E, K> {
    List<E> getAll();

    E getEntityById(K id);

    void update(E entity);

    void delete(E entity);

    void save(E entity);
}
