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

    @org.springframework.data.jpa.repository.Query("SELECT r.animeId, AVG(r.rating) as avgRating FROM UserRating r GROUP BY r.animeId HAVING AVG(r.rating) >= 6 ORDER BY avgRating DESC, r.animeId DESC")
    List<Object[]> findTopRatedAnime();

    @org.springframework.data.jpa.repository.Query("SELECT r.animeId, AVG(r.rating) as avgRating, COUNT(DISTINCT r.userId) as userCount FROM UserRating r GROUP BY r.animeId HAVING COUNT(DISTINCT r.userId) >= 2 ORDER BY r.animeId")
    List<Object[]> findAnimeWithMultipleRatings();

    @org.springframework.data.jpa.repository.Query("SELECT r.animeId, AVG(r.rating) as avgRating FROM UserRating r WHERE r.animeId NOT IN :excludeIds GROUP BY r.animeId HAVING AVG(r.rating) < 6 ORDER BY avgRating ASC, r.animeId DESC")
    List<Object[]> findLeastRatedAnimeExcluding(java.util.List<Integer> excludeIds);
}
