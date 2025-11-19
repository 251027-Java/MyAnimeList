package com.animeapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Ratings {
    @Id
    private int id;
    private int anime_id;
    private String userName;
    private int score;
    private String review;

    public Ratings(int id, int anime_id, String userName, int score, String review) {
        this.id = id;
        this.anime_id = anime_id;
        this.userName = userName;
        this.score = score;
        this.review = review;
    }

    public int getId() {
        return id;
    }

    public String getReview() {
        return review;
    }

    public int getScore() {
        return score;
    }

    public int getAnime_id() {
        return anime_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAnime_id(int anime_id) {
        this.anime_id = anime_id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUserName() {
        return userName;
    }
}
