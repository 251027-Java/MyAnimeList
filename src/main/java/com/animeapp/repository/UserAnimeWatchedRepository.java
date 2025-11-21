package com.animeapp.repository;

import com.animeapp.model.UserAnimeWatched;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnimeWatchedRepository extends JpaRepository<UserAnimeWatched, Integer> {

    UserAnimeWatched findByUserIdAndAnimeId(Integer userId, Integer animeId);
}
