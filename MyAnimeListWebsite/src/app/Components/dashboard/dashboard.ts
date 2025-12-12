import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { UserService } from '../../Service/user.service';
import { AuthService } from '../../Service/auth.service';
import { AnimeService, Anime } from '../../Service/anime-service';
import { catchError, map, of, forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgFor],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  watchlist = signal<Anime[]>([]);
  watchedAnime = signal<Anime[]>([]);
  ratedAnime = signal<Map<number, number>>(new Map());
  username = signal<string>('');

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private animeService: AnimeService
  ) { }

  ngOnInit() {
    const userId = this.authService.getUserId();
    if (userId) {
      // get username from storage or service if available, but for now just show watchlist
      this.username.set(sessionStorage.getItem('username') || 'User');

      forkJoin({
        watchlist: this.userService.getWatchlist(userId),
        watched: this.userService.getWatchedAnime(userId),
        anime: this.animeService.getAllAnime()
      }).subscribe({
        next: (data) => {
          const watchlistIds = new Set(data.watchlist.map((w: any) => w.animeId));
          const watchedIds = new Set(data.watched.map((w: any) => w.animeId));
          const myAnime = data.anime.filter(a => watchlistIds.has(a.id));
          const myWatched = data.anime.filter(a => watchedIds.has(a.id));
          this.watchlist.set(myAnime);
          this.watchedAnime.set(myWatched);
        },
        error: (err) => console.error('Error loading dashboard', err)
      });
    }
  }

  removeFromWatchlist(animeId: number) {
    const userId = this.authService.getUserId();
    if (userId) {
      this.userService.removeFromWatchlist(userId, animeId).subscribe({
        next: () => {
          // update local list
          this.watchlist.update(list => list.filter(a => a.id !== animeId));
        },
        error: (err) => console.error('Error removing from watchlist', err)
      });
    }
  }

toggleWatched(animeId: number) {
    const userId = this.authService.getUserId();
    if (userId) {
      // Find the anime in watchlist
      const anime = this.watchlist().find(a => a.id === animeId);

      this.userService.setWatched(userId, animeId, true).subscribe({
        next: () => {
          // Add to watched anime list
          if (anime) {
            this.watchedAnime.update(list => [...list, anime]);
          }
          //alert('Marked as watched!');
        },
        error: (err) => console.error('Error marking as watched', err)
      });
    } else {
      alert('Please login first.');
    }
  }



  setRating(animeId: number, ratingInput: any) {
    const rating = +ratingInput.value;
    
    if (rating < 0 || rating > 10) {
      alert('Rating must be between 0 and 10');
      return;
    }
    
    const userId = this.authService.getUserId();
    if (userId) {
      this.userService.setRating(userId, animeId, rating).subscribe({
        next: () => {
          this.ratedAnime.update(map => {
            const newMap = new Map(map);
            newMap.set(animeId, rating);
            return newMap;
          });
          alert('Rating added!');
        },
        error: (err) => {
          console.error('Error setting rating', err);
          alert('Failed to add rating. Please try again.');
        }
      });
    } else {
      alert('Please login first.');
    }
  }

  hasRated(animeId: number): boolean {
    return this.ratedAnime().has(animeId);
  }

  getRating(animeId: number): number {
    return this.ratedAnime().get(animeId) || 0;
  }

}
