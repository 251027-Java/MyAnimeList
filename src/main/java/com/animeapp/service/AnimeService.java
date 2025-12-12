package com.animeapp.service;

import com.animeapp.model.UserAnimeWatched;
import com.animeapp.model.requests.UserAnimeWatchedRequest;
import com.animeapp.repository.UserAnimeWatchedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animeapp.model.Anime;
import com.animeapp.repository.AnimeRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AnimeService {
    private final AnimeRepository animeRepository;

    private final UserAnimeWatchedRepository userAnimeWatchedRepository;

    @Autowired
    public AnimeService(AnimeRepository animeRepository, UserAnimeWatchedRepository userAnimeWatchedRepository) {
        this.animeRepository = animeRepository;
        this.userAnimeWatchedRepository = userAnimeWatchedRepository;
    }

    public List<Anime> getAllAnime() {
        return animeRepository.findAll();
    }

    public Anime getAnimeByTitle(String title) {
        return animeRepository.findByTitle(title);
    }

    public UserAnimeWatched updateAnimeWatchStatus(UserAnimeWatchedRequest request) {
        UserAnimeWatched userAnimeWatched = new UserAnimeWatched(request.getUserId(), request.getAnimeId(),
                request.getWatched());
        Optional<UserAnimeWatched> existsOpt = userAnimeWatchedRepository
                .findByUserIdAndAnimeId(userAnimeWatched.getUserId(), userAnimeWatched.getAnimeId());

        // Save a record in the table if a user has not set an anime as watched or not
        // watched
        if (existsOpt.isEmpty()) {
            return userAnimeWatchedRepository.save(userAnimeWatched);
        }
        // if the user has previously set an anime as watched or not and wishes to
        // change it, confirm that the watched
        // statuses are different and update the same record.
        else {
            UserAnimeWatched exists = existsOpt.get();
            if (exists.getWatched() != request.getWatched()) {
                exists.setWatched(request.getWatched());
                return userAnimeWatchedRepository.save(exists);
            }
        }
        return null;
    }
}
