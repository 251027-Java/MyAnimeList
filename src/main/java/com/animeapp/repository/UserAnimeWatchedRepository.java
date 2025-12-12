package com.animeapp.repository;

import com.animeapp.model.UserAnimeWatched;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnimeWatchedRepository extends JpaRepository<UserAnimeWatched, Integer> {

    Optional<UserAnimeWatched> findByUserIdAndAnimeId(Integer userId, Integer animeId);
    
    java.util.List<UserAnimeWatched> findByUserId(Integer userId);
    
    @org.springframework.data.jpa.repository.Query("SELECT w.animeId, COUNT(DISTINCT w.userId) as watchCount FROM UserAnimeWatched w WHERE w.watched = TRUE GROUP BY w.animeId ORDER BY watchCount DESC")
    java.util.List<Object[]> findMostWatchedAnime();
}
