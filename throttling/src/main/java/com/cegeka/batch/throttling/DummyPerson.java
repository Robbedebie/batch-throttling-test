package com.cegeka.batch.throttling;

import java.util.Random;
import java.util.UUID;

import static java.lang.Math.random;

public class DummyPerson {

    private UUID id;
    private double favoriteNumber;
    private String name;
    private String favoriteColor;



    public DummyPerson() {
        this.id = UUID.randomUUID();
        this.favoriteNumber = Math.floor(random() *(100));
        this.name = getRandomName();
        this.favoriteColor = getRandomColor();
    }

    private String getRandomColor() {
        String[] colors = {"red", "green", "blue", "appelblauwzeegroen", "banaankleur"};
        Random random = new Random();
        int randomIndex = random.nextInt(colors.length) + 1;

        return colors[randomIndex];
    }

    private String getRandomName() {
        String[] names = {"Alice", "Bob", "Charlie", "David", "Eve", "Frank"};
        Random random = new Random();
        int randomIndex = random.nextInt(names.length) + 1;

        return names[randomIndex];
    }

    @Override
    public String toString() {
        return "DummyPerson{" +
            "id=" + id +
            ", favoriteNumber=" + favoriteNumber +
            ", name='" + name + '\'' +
            ", favoriteColor='" + favoriteColor + '\'' +
            '}';
    }
}
