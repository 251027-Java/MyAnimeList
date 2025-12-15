import { Component, signal, computed, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { AnimeService, Anime } from '../../Service/anime-service';
import { UserService } from '../../Service/user.service';
import { AuthService } from '../../Service/auth.service';
import { SearchService } from '../../Service/search.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-anime-list',
  standalone: true,
  imports: [CommonModule, NgFor],
  templateUrl: './anime-list.html',
  styleUrls: ['./anime-list.css']
})

export class AnimeList implements OnInit, OnDestroy {
  animeList = signal<Anime[]>([]);
  filteredAnimeList = signal<Anime[]>([]);
  flippedCards = signal<Set<number>>(new Set());
  addedToWatchlist = signal<Set<number>>(new Set());
  userWatchlist = signal<Set<number>>(new Set());
  userWatchedAnime = signal<Set<number>>(new Set());
  readonly searchResultLimit = 100;
  private readonly pageSize = 100;
  currentPage = signal(1);
  searchActive = signal(false);
  displayedAnimeList = computed(() => {
    const list = this.filteredAnimeList();
    if (this.searchActive()) {
      return list.slice(0, this.searchResultLimit);
    }
    const startIndex = (this.currentPage() - 1) * this.pageSize;
    return list.slice(startIndex, startIndex + this.pageSize);
  });
  totalPages = computed(() => {
    if (this.searchActive()) {
      return 1;
    }
    const totalItems = this.filteredAnimeList().length;
    return totalItems > 0 ? Math.ceil(totalItems / this.pageSize) : 1;
  });
  pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, idx) => idx + 1));
  private searchSubscription?: Subscription;

  protected readonly title = signal('MyAnimeListWebsite');
  constructor(
    public animeService: AnimeService,
    private userService: UserService,
    private authService: AuthService,
    private searchService: SearchService
  ) { }

  ngOnInit() {
    // Load user's watchlist and watched anime
    const userId = this.authService.getUserId();
    if (userId) {
      this.userService.getWatchlist(userId).subscribe({
        next: (watchlist) => {
          const watchlistIds = new Set(watchlist.map((item: any) => item.animeId));
          this.userWatchlist.set(watchlistIds);
          this.addedToWatchlist.set(watchlistIds);
        },
        error: (err: any) => console.error('Error fetching watchlist:', err),
      });

      this.userService.getWatchedAnime(userId).subscribe({
        next: (watchedAnime) => {
          const watchedIds = new Set(watchedAnime.map((item: any) => item.animeId));
          this.userWatchedAnime.set(watchedIds);
        },
        error: (err: any) => console.error('Error fetching watched anime:', err),
      });
    }

    this.animeService.getAllAnime().subscribe({
      next: (data) => {
        const filteredData = data
          .filter(a => a.totalEpisodes > 0)
          .sort((a, b) => a.title.localeCompare(b.title));

        this.animeList.set(filteredData);
        this.filteredAnimeList.set(filteredData);
        this.currentPage.set(1);
        this.searchActive.set(false);
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
      this.searchActive.set(false);
    } else {
      const filtered = this.animeList().filter(anime =>
        anime.title.toLowerCase().includes(query) ||
        anime.id.toString().includes(query)
      );
      this.filteredAnimeList.set(filtered);
      this.searchActive.set(true);
    }
    this.currentPage.set(1);
  }

  ngOnDestroy() {
    this.searchSubscription?.unsubscribe();
  }

  goToPreviousPage() {
    this.goToPage(this.currentPage() - 1);
  }

  goToNextPage() {
    this.goToPage(this.currentPage() + 1);
  }

  goToPage(page: number) {
    if (this.searchActive()) {
      return;
    }
    const total = this.totalPages();
    if (page >= 1 && page <= total) {
      this.currentPage.set(page);
    }
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

    // Check if anime is already in watchlist
    if (this.userWatchlist().has(animeId)) {
      alert('This anime is already in your watchlist!');
      return;
    }

    if (userId) {
      this.userService.addToWatchlist(userId, animeId).subscribe({
        next: () => {
          this.userWatchlist.update(set => {
            const newSet = new Set(set);
            newSet.add(animeId);
            return newSet;
          });
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

  isWatched(animeId: number): boolean {
    return this.userWatchedAnime().has(animeId);
  }

}
