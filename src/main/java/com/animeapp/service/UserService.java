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
    public void setRating(Integer userId, Integer animeId, Integer rating) {
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
        List<Object[]> results = userAnimeWatchedRepository.findMostWatchedAnime();
        return results.stream()
                .map(row -> {
                    Map<Integer, Long> map = new java.util.HashMap<>();
                    map.put((Integer) row[0], (Long) row[1]);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Map<Integer, Double>> getTopRatedAnime() {
        List<Object[]> results = userRatingRepository.findTopRatedAnime();
        return results.stream()
                .map(row -> {
                    Map<Integer, Double> map = new java.util.HashMap<>();
                    map.put((Integer) row[0], ((Number) row[1]).doubleValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

}
