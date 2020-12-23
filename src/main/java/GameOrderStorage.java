import entities.User;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameOrderStorage {
    public final ConcurrentHashMap<Integer, List<User>> gameUsers = new ConcurrentHashMap();

}
