package com.animeapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_anime_watched")
public class UserAnimeWatched {
    @Id
    private Integer id;
    private Integer userId;
    private Integer animeId;
    private Boolean watched;

    public UserAnimeWatched(Integer id, Integer userId, Integer animeId, Boolean watched) {
        this.id = id;
        this.userId = userId;
        this.animeId = animeId;
        this.watched = watched;
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

    public Boolean getWatched() {
        return watched;
    }

    public void setWatched(Boolean watched) {
        this.watched = watched;
    }
}
