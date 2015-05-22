package com.eva.backend2;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;

/**
 * Klasse der holder styr på et løbs variabler
 */
@Entity
public class Game {
    @Id
    private Long id;
    private String name;
    private List<Post> posts;
    private int difficultyLevel;
    private int postCounter;

    public Game() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPostCounter() {
        return postCounter;
    }

    public void setPostCounter(int postCounter) {
        this.postCounter = postCounter;
    }
}
