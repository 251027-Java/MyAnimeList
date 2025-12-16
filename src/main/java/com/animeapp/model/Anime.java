package com.animeapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "anime", schema = "myanimelist")
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer animeId;
    private String title;
    private Integer totalEpisodes;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Double avgRating;
    private String anime_img_url;

    public Integer getId() {
        return animeId;
    }

    public String getTitle() {
        return title;
    }

    public String getAnime_img_url() {
        return anime_img_url;
    }

    public void setAnime_img_url(String anime_img_url) {
        this.anime_img_url = anime_img_url;
    }

    public Integer getTotalEpisodes() {

        return totalEpisodes;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(Integer animeId) {
        this.animeId = animeId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalEpisodes(Integer totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
