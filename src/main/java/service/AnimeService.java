package service;

import model.Anime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.AnimeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnimeService {
    private final AnimeRepository animeRepository;

    @Autowired
    public AnimeService(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }

    public List<Anime> getAllAnime() {
        return animeRepository.findAll();
    }

    public Optional<Anime> getAnimeById(int id) {
        return animeRepository.findById(id);
    }

    public List<Anime> getAnimeByTitle(String title) {
        return animeRepository.findByTitle(title);
    }

    public List<Anime> getAnimeByGenre(String genre) {
        return animeRepository.findByGenre(genre);
    }

    public Anime saveAnime(Anime anime) {
        return animeRepository.save(anime);
    }

    public void deleteAnime(int id) {
        animeRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return animeRepository.existsById(id);
    }
}
