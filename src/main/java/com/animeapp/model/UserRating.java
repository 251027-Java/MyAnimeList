package com.animeapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rating", schema = "myanimelist")
public class UserRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer animeId;
    private float rating;

    public UserRating() {
    }

    public UserRating(Integer userId, Integer animeId, Integer rating) {
        this.userId = userId;
        this.animeId = animeId;
        this.rating = rating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAnimeId() {
        return animeId;
    }

    public void setAnimeId(Integer animeId) {
        this.animeId = animeId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
