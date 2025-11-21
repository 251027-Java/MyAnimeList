package com.animeapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animeapp.model.Anime;
import com.animeapp.repository.AnimeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnimeService {
    private final AnimeRepository animeRepository;

    @Autowired
    public AnimeService(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }

    public List<Anime> getAllAnime() {
        return animeRepository.findAll();
    }

    public Anime getAnimeByTitle(String title) {
        return animeRepository.findByTitle(title);
    }

//    public Anime updateRatings(String title, Integer rating){
//
//        return new Anime();
//    }
}
