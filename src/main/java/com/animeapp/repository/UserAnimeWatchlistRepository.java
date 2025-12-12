package com.animeapp.repository;

import com.animeapp.model.UserAnimeWatchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnimeWatchlistRepository extends JpaRepository<UserAnimeWatchlist, Integer> {
    List<UserAnimeWatchlist> findByUserId(Integer userId);

    Optional<UserAnimeWatchlist> findByUserIdAndAnimeId(Integer userId, Integer animeId);
}
