package com.animeapp.repository;

import com.animeapp.model.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Integer> {
    List<UserRating> findByUserId(Integer userId);

    Optional<UserRating> findByUserIdAndAnimeId(Integer userId, Integer animeId);
}
