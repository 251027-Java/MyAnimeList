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
    private UserRepository userRepository;
    private UserAnimeWatchlistRepository userAnimeWatchlistRepository;
    private UserRatingRepository userRatingRepository;
    private UserAnimeWatchedRepository userAnimeWatchedRepository;

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

        User loggedInUser = userRepository.findByUsernameAndPassword(username, password);

        return loggedInUser;
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
                .collect(java.util.stream.Collectors.toList());

        // Calculate limit: if total < 10, show half; otherwise show up to 5
        int limit = twoOrMoreUsers.size() < 10 ? (twoOrMoreUsers.size() + 1) / 2 : 5;
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
        // Calculate limit: if total < 10, show half; otherwise show up to 5
        int limit = allResults.size() < 10 ? (allResults.size() + 1) / 2 : 5;
        return allResults.stream()
                .limit(limit)
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
                .collect(java.util.stream.Collectors.toList());

        List<Object[]> twoOrMoreUsers = allResults.stream()
                .filter(row -> (Long) row[1] >= 2)
                .collect(java.util.stream.Collectors.toList());

        // Calculate limits for most/least watched
        int mostWatchedLimit = twoOrMoreUsers.size() < 10 ? (twoOrMoreUsers.size() + 1) / 2 : 5;

        // Get most watched IDs (2+ users only)
        List<Integer> mostWatchedIds = twoOrMoreUsers.stream()
                .limit(mostWatchedLimit)
                .map(row -> (Integer) row[0])
                .collect(java.util.stream.Collectors.toList());

        // Calculate least watched limit
        int leastWatchedLimit = twoOrMoreUsers.size() < 10 ? (twoOrMoreUsers.size() + 1) / 2 : 5;

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
            leastResults.addAll(oneUserAnime.stream().limit(slotsNeeded).collect(java.util.stream.Collectors.toList()));
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
        List<Object[]> allResults = userRatingRepository.findTopRatedAnime();
        if (allResults.isEmpty()) {
            return List.of();
        }

        // Calculate how many to show in least rated
        int totalLimit = allResults.size() < 10 ? (allResults.size() + 1) / 2 : 5;

        // Get top rated IDs to exclude
        List<Integer> topRatedIds = allResults.stream()
                .limit(totalLimit)
                .map(row -> (Integer) row[0])
                .collect(java.util.stream.Collectors.toList());

        // Get least rated, excluding top rated
        List<Object[]> leastResults = userRatingRepository.findLeastRatedAnimeExcluding(topRatedIds);
        return leastResults.stream()
                .limit(totalLimit)
                .map(row -> {
                    Map<Integer, Double> map = new java.util.HashMap<>();
                    map.put((Integer) row[0], ((Number) row[1]).doubleValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

}
