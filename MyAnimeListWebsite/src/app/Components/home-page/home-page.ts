import { Component, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { AnimeService, Anime } from '../../Service/anime-service';
import { UserService } from '../../Service/user.service';
import { AuthService } from '../../Service/auth.service';
import { SearchService } from '../../Service/search.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgFor],
  templateUrl: './home-page.html',
  styleUrls: ['./home-page.css']
})

export class HomePage implements OnInit, OnDestroy {
  animeList = signal<Anime[]>([]);
  filteredAnimeList = signal<Anime[]>([]);
  flippedCards = signal<Set<number>>(new Set());
  addedToWatchlist = signal<Set<number>>(new Set());
  private searchSubscription?: Subscription;

  protected readonly title = signal('MyAnimeListWebsite');
  constructor(
    public animeService: AnimeService,
    private userService: UserService,
    private authService: AuthService,
    private searchService: SearchService
  ) {}

  ngOnInit() {
    this.animeService.getAllAnime().subscribe({
      next: (data) => {
        const filteredData = data
          .filter(a => a.totalEpisodes > 0 && a.title !== '◯')
          .sort((a, b) => a.title.localeCompare(b.title));

        const allAnime = filteredData.slice(0, 216);
        this.animeList.set(allAnime);
        this.filteredAnimeList.set(allAnime);
      },
      error: (err: any) => console.error('Error fetching anime:', err),
    });

    // Subscribe to search query changes for real-time filtering
    this.searchSubscription = this.searchService.searchQuery$.subscribe(query => {
      this.filterAnimeList(query);
    });
  }

  private filterAnimeList(searchQuery: string) {
    const query = searchQuery.toLowerCase().trim();
    if (query === '') {
      this.filteredAnimeList.set(this.animeList());
    } else {
      const filtered = this.animeList().filter(anime =>
        anime.title.toLowerCase().includes(query) ||
        anime.id.toString().includes(query)
      );
      this.filteredAnimeList.set(filtered);
    }
  }

  ngOnDestroy() {
    this.searchSubscription?.unsubscribe();
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
        next: () => {
          this.addedToWatchlist.update(set => {
            const newSet = new Set(set);
            newSet.add(animeId);
            return newSet;
          });
          alert('Added to watchlist!');
        },
        error: (err) => console.error('Error adding to watchlist', err)
      });
    } else {
      alert('Please login first.');
    }
  }

  isInWatchlist(animeId: number): boolean {
    return this.addedToWatchlist().has(animeId);
  }

}
