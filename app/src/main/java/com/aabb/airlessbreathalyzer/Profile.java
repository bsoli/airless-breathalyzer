package com.aabb.airlessbreathalyzer;

/**
 * Created by oscar on 10/14/15.
 */
public class Profile {
    public String name;
    public int age;
    public int weight;
    public String race;
    public String sex;

    public Profile(String profile) {
        String[] data = profile.split("`");
        this.name = data[0];
        this.age = Integer.getInteger(data[1]);
        this.weight = Integer.getInteger(data[2]);
        this.race = data[3];
        this.sex = data[4];
    }
}
