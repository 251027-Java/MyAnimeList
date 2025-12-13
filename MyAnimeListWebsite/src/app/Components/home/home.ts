import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../Service/user.service';
import { AnimeService, Anime } from '../../Service/anime-service';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  mostWatchedAnimeList = signal<Array<{title: string, userCount: number}>>([]);
  topRatedAnimeList = signal<Array<{title: string, rating: number}>>([]);

  constructor(
    private userService: UserService,
    private animeService: AnimeService
  ) {}

  formatRating(value: number): string {
    const truncated = Math.floor(value * 100) / 100;
    let s = truncated.toFixed(2);
    // Remove a single trailing zero after decimal if present (e.g., 7.70 -> 7.7)
    s = s.replace(/(\.[\d]*[1-9])0$/, '$1');
    // Remove .00 entirely (e.g., 8.00 -> 8)
    s = s.replace(/\.00$/, '');
    return s;
  }

  ngOnInit() {
    this.animeService.getAllAnime().subscribe({
      next: (data) => {
        // Load most watched anime
        this.userService.getMostWatchedAnime().subscribe({
          next: (mostWatched) => {
            const mostWatchedList = mostWatched
              .map((item: any) => {
                const animeId = Object.keys(item)[0];
                const userCount = Object.values(item)[0] as number;
                const anime = data.find((a: Anime) => a.id === parseInt(animeId));
                return anime ? { title: anime.title, userCount } : null;
              })
              .filter((item: {title: string, userCount: number} | null) => item !== null && (item as {title: string, userCount: number}).userCount >= 2) as Array<{title: string, userCount: number}>;
            this.mostWatchedAnimeList.set(mostWatchedList);
          },
          error: (err: any) => console.error('Error fetching most watched:', err),
        });

        // Load top rated anime
        this.userService.getTopRatedAnime().subscribe({
          next: (topRated) => {
            const topRatedList = topRated
              .map((item: any) => {
                const animeId = Object.keys(item)[0];
                const rating = Object.values(item)[0] as number;
                // Filter out ratings below 6
                if (rating < 6) return null;
                const anime = data.find((a: Anime) => a.id === parseInt(animeId));
                return anime ? { title: anime.title, rating } : null;
              })
              .filter((item: {title: string, rating: number} | null) => item !== null) as Array<{title: string, rating: number}>;
            this.topRatedAnimeList.set(topRatedList);
          },
          error: (err: any) => console.error('Error fetching top rated:', err),
        });
      },
      error: (err: any) => console.error('Error fetching anime:', err),
    });
  }
}
