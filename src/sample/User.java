package sample;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private int points;

    public User(String name) {
        this.name = name;
        this.points = 0;
    }

    public void addPoint() {
        points++;
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
