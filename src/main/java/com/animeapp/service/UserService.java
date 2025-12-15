package com.animeapp.service;

import com.animeapp.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animeapp.model.User;
import com.animeapp.model.UserAnimeWatchlist;
import com.animeapp.model.UserRating;
import com.animeapp.model.UserAnimeWatched;
import com.animeapp.repository.UserRepository;
import com.animeapp.repository.UserAnimeWatchlistRepository;
import com.animeapp.repository.UserRatingRepository;
import com.animeapp.repository.UserAnimeWatchedRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserAnimeWatchlistRepository userAnimeWatchlistRepository;
    private final UserRatingRepository userRatingRepository;
    private final UserAnimeWatchedRepository userAnimeWatchedRepository;

    @Autowired
    public UserService(UserRepository userRepository,
            UserAnimeWatchlistRepository userAnimeWatchlistRepository,
            UserRatingRepository userRatingRepository,
            UserAnimeWatchedRepository userAnimeWatchedRepository) {
        this.userRepository = userRepository;
        this.userAnimeWatchlistRepository = userAnimeWatchlistRepository;
        this.userRatingRepository = userRatingRepository;
        this.userAnimeWatchedRepository = userAnimeWatchedRepository;
    }

    public User registerUser(String username, String password) throws UserException {
        if (username.isBlank() || password.isBlank() || password.length() < 8) {
            throw new UserException("Incorrect username or password.");
        }
        return userRepository.save(new User(username, password));
    }

    public boolean doesUsernameExist(String username) {
        return userRepository.existsUserByUsername(username);
    }

    public User loginUser(User user) throws UserException {
        String username = user.getUsername();
        String password = user.getPassword();

        return userRepository.findByUsernameAndPassword(username, password);
    }

    // Watchlist
    public void addToWatchlist(Integer userId, Integer animeId) {
        if (userAnimeWatchlistRepository.findByUserIdAndAnimeId(userId, animeId).isEmpty()) {
            UserAnimeWatchlist entry = new UserAnimeWatchlist(userId, animeId);
            entry.setAddedDate(java.time.LocalDateTime.now());
            userAnimeWatchlistRepository.save(entry);
        }
    }

    public void removeFromWatchlist(Integer userId, Integer animeId) {
        userAnimeWatchlistRepository.findByUserIdAndAnimeId(userId, animeId)
                .ifPresent(userAnimeWatchlistRepository::delete);
    }

    public List<UserAnimeWatchlist> getWatchlist(Integer userId) {
        return userAnimeWatchlistRepository.findByUserId(userId);
    }

    // Rating
    public void setRating(Integer userId, Integer animeId, Float rating) {
        Optional<UserRating> existingRating = userRatingRepository.findByUserIdAndAnimeId(userId, animeId);
        if (existingRating.isPresent()) {
            UserRating r = existingRating.get();
            r.setRating(rating);
            userRatingRepository.save(r);
        } else {
            userRatingRepository.save(new UserRating(userId, animeId, rating));
        }
    }

    public Optional<UserRating> getRating(Integer userId, Integer animeId) {
        return userRatingRepository.findByUserIdAndAnimeId(userId, animeId);
    }

    // Watched
    public void setWatched(Integer userId, Integer animeId, Boolean watched) {
        Optional<UserAnimeWatched> existing = userAnimeWatchedRepository.findByUserIdAndAnimeId(userId, animeId);
        if (existing.isPresent()) {
            UserAnimeWatched w = existing.get();
            w.setWatched(watched);
            userAnimeWatchedRepository.save(w);
        } else {
            userAnimeWatchedRepository.save(new UserAnimeWatched(userId, animeId, watched));
        }
    }

    public boolean isWatched(Integer userId, Integer animeId) {
        return userAnimeWatchedRepository.findByUserIdAndAnimeId(userId, animeId)
                .map(UserAnimeWatched::getWatched)
                .orElse(false);
    }

    public List<UserAnimeWatched> getWatchedAnime(Integer userId) {
        return userAnimeWatchedRepository.findByUserId(userId);
    }

    public List<Map<Integer, Long>> getMostWatchedAnimeWithCount() {
        List<Object[]> allResults = userAnimeWatchedRepository.findMostWatchedAnime();
        // Filter only anime with 2+ users (exclude 1-user anime)
        List<Object[]> twoOrMoreUsers = allResults.stream()
                .filter(row -> (Long) row[1] >= 2)
                .toList();

        // Show all if < 10 total; cap at 10 if >= 10
        int limit = Math.min(10, twoOrMoreUsers.size());
        return twoOrMoreUsers.stream()
                .limit(limit)
                .map(row -> {
                    Map<Integer, Long> map = new java.util.HashMap<>();
                    map.put((Integer) row[0], (Long) row[1]);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Map<Integer, Double>> getTopRatedAnime() {
        List<Object[]> allResults = userRatingRepository.findTopRatedAnime();
        // Show all if < 10 total; cap at 10 if >= 10
        int limit = Math.min(allResults.size(), 10);

        return allResults.stream()
                .limit(limit)
                .sorted((a, b) -> {
                    // Primary: by rating (descending - already sorted)
                    // Secondary: by ID descending (to get newest ones)
                    // But return in reverse order for display (oldest first in ties)
                    double ratingA = ((Number) a[1]).doubleValue();
                    double ratingB = ((Number) b[1]).doubleValue();
                    if (Math.abs(ratingA - ratingB) > 0.001) {
                        return Double.compare(ratingB, ratingA); // descending
                    }
                    // Within same rating: keep newest (higher ID) last for display
                    return Integer.compare((Integer) a[0], (Integer) b[0]);
                })
                .map(row -> {
                    Map<Integer, Double> map = new java.util.HashMap<>();
                    map.put((Integer) row[0], ((Number) row[1]).doubleValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Map<Integer, Long>> getLeastWatchedAnime() {
        List<Object[]> allResults = userAnimeWatchedRepository.findMostWatchedAnime();
        if (allResults.isEmpty()) {
            return List.of();
        }

        // Separate 1-user and 2+ user anime
        List<Object[]> oneUserAnime = allResults.stream()
                .filter(row -> (Long) row[1] == 1)
                .toList();

        List<Object[]> twoOrMoreUsers = allResults.stream()
                .filter(row -> (Long) row[1] >= 2)
                .toList();

        // Calculate limits for most/least watched
        int mostWatchedLimit = twoOrMoreUsers.size() < 10 ? (twoOrMoreUsers.size() + 1) / 2 : 5;

        // Get most watched IDs (2+ users only)
        List<Integer> mostWatchedIds = twoOrMoreUsers.stream()
                .limit(mostWatchedLimit)
                .map(row -> (Integer) row[0])
                .collect(java.util.stream.Collectors.toList());

        // Calculate least watched limit
        int leastWatchedLimit = Math.min(10, twoOrMoreUsers.size());

        // Get bottom entries from 2+ users (excluding most watched)
        List<Object[]> leastResults = new java.util.ArrayList<>();
        if (!mostWatchedIds.isEmpty()) {
            List<Object[]> bottomTwoOrMore = userAnimeWatchedRepository.findLeastWatchedAnimeExcluding(mostWatchedIds);
            leastResults.addAll(bottomTwoOrMore);
        } else {
            leastResults.addAll(twoOrMoreUsers);
        }

        // Fill remaining slots with 1-user anime if needed
        if (leastResults.size() < leastWatchedLimit) {
            int slotsNeeded = leastWatchedLimit - leastResults.size();
            leastResults.addAll(oneUserAnime.stream().limit(slotsNeeded).toList());
        }

        return leastResults.stream()
                .limit(leastWatchedLimit)
                .map(row -> {
                    Map<Integer, Long> map = new java.util.HashMap<>();
                    map.put((Integer) row[0], (Long) row[1]);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Map<Integer, Double>> getLeastRatedAnime() {
        List<Object[]> allTopRated = userRatingRepository.findTopRatedAnime();
        if (allTopRated.isEmpty()) {
            return List.of();
        }

        // Calculate how many to show in top/least rated
        int topRatedLimit = Math.min(10, allTopRated.size());

        // Get top rated IDs to exclude
        List<Integer> topRatedIds = allTopRated.stream()
                .limit(topRatedLimit)
                .map(row -> (Integer) row[0])
                .collect(java.util.stream.Collectors.toList());

        // Get least rated, excluding top rated
        List<Object[]> leastResults = userRatingRepository.findLeastRatedAnimeExcluding(topRatedIds);

        // Show all least rated if < 10; otherwise cap at topRatedLimit
        int leastRatedLimit = Math.min(leastResults.size(), 10);

        return leastResults.stream()
                .limit(leastRatedLimit)
                .sorted((a, b) -> {
                    // Primary: by rating (ascending - already sorted)
                    // Secondary: by ID descending (to get newest ones)
                    // But return in reverse order for display (oldest first in ties)
                    double ratingA = ((Number) a[1]).doubleValue();
                    double ratingB = ((Number) b[1]).doubleValue();
                    if (Math.abs(ratingA - ratingB) > 0.001) {
                        return Double.compare(ratingA, ratingB); // ascending
                    }
                    // Within same rating: keep newest (higher ID) last for display
                    return Integer.compare((Integer) a[0], (Integer) b[0]);
                })
                .map(row -> {
                    Map<Integer, Double> map = new java.util.HashMap<>();
                    map.put((Integer) row[0], ((Number) row[1]).doubleValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Map<String, Object>> getAnimeWithMultipleRatings() {
        List<Object[]> results = userRatingRepository.findAnimeWithMultipleRatings();
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("animeId", (Integer) row[0]);
                    map.put("avgRating", ((Number) row[1]).doubleValue());
                    map.put("userCount", ((Number) row[2]).longValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

}
