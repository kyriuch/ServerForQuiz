package sample;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomek on 07.05.2017.
 */
public class UserManager {
    private Map<String, User> map = new HashMap<>();

    public Map<String, User> getMap() {
        return map;
    }

    public User getUser(String name) {
        return map.get(name);
    }

    public void addUser(String name) {
        map.put(name, new User(name));
    }

    public void removeUser(String name) {
        map.remove(name);
    }

    public void addPointToUser(String name) {
        map.get(name).addPoint();
    }

    public int getUserPoints(String name) {
        return map.get(name).getPoints();
    }
}
