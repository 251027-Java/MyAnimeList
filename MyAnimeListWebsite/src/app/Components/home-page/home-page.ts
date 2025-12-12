import { Component, signal, OnInit } from '@angular/core'; // import OnInit
import { CommonModule, NgFor } from '@angular/common';
import { AnimeService, Anime } from '../../Service/anime-service';
import { UserService } from '../../Service/user.service';
import { AuthService } from '../../Service/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgFor],
  templateUrl: './home-page.html',
  styleUrls: ['./home-page.css']
})

export class HomePage implements OnInit {
  animeList = signal<Anime[]>([]);
  flippedCards = signal<Set<number>>(new Set());

  protected readonly title = signal('MyAnimeListWebsite');
  constructor(public animeService: AnimeService, private userService: UserService, private authService: AuthService) { };

  ngOnInit() {
    this.animeService.getAllAnime().subscribe({
      next: (data) => {
        const filteredData = data
          .filter(a => a.totalEpisodes > 0 && a.title !== '◯')
          .sort((a, b) => a.title.localeCompare(b.title));

        this.animeList.set(filteredData.slice(0, 216));
      },
      error: (err: any) => console.error('Error fetching anime:', err),
    });
  }

  toggleFlip(id: number) {
    this.flippedCards.update((set) => {
      const newSet = new Set(set);
      if (newSet.has(id)) {
        newSet.delete(id);
      } else {
        newSet.add(id);
      }
      return newSet;
    });
  }

  isFlipped(id: number): boolean {
    return this.flippedCards().has(id);
  }

  addToWatchlist(animeId: number) {
    const userId = this.authService.getUserId();
    if (userId) {
      this.userService.addToWatchlist(userId, animeId).subscribe({
        next: () => alert('Added to watchlist!'),
        error: (err) => console.error('Error adding to watchlist', err)
      });
    } else {
      alert('Please login first.');
    }
  }

  setRating(animeId: number, event: any) {
    const rating = +event.target.value;
    const userId = this.authService.getUserId();
    if (userId) {
      this.userService.setRating(userId, animeId, rating).subscribe({
        next: () => console.log('Rating updated'),
        error: (err) => console.error('Error setting rating', err)
      });
    }
  }

  toggleWatched(animeId: number) {
    const userId = this.authService.getUserId();
    if (userId) {
      // Toggle logic might need current state, but spec says "Watched? button". 
      // For simplicity, let's assume it sets to true. 
      // Or if I want to toggle, I'd need to fetch state first. 
      // Given constraints, I'll make it "Mark as Watched".
      this.userService.setWatched(userId, animeId, true).subscribe({
        next: () => alert('Marked as watched!'),
        error: (err) => console.error('Error marking as watched', err)
      });
    } else {
      alert('Please login first.');
    }
  }
}
