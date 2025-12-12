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
        anime: this.animeService.getAllAnime()
      }).subscribe({
        next: (data) => {
          const watchlistIds = new Set(data.watchlist.map((w: any) => w.animeId));
          const myAnime = data.anime.filter(a => watchlistIds.has(a.id));
          this.watchlist.set(myAnime);
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
}
