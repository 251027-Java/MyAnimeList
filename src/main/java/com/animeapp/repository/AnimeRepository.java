package com.animeapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.animeapp.model.Anime;

import java.util.List;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Integer> {
    List<Anime> findByTitleContains(String title);
}
