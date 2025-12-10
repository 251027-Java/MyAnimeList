import { Component, signal, OnInit } from '@angular/core'; // import OnInit
import { CommonModule, NgFor } from '@angular/common';
import { AnimeService, Anime } from '../../Service/anime-service';

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
  constructor(public animeService: AnimeService) { };

  ngOnInit() {
    this.animeService.getAllAnime().subscribe({
      next: (data) => {
        const filteredData = data
          .filter(a => a.totalEpisodes > 0 && a.title !== '◯')
          .sort((a, b) => a.title.localeCompare(b.title));

        this.animeList.set(filteredData.slice(0, 216));

        console.log('Original data length:', data.length);
        console.log('Filtered data length:', filteredData.length);
        console.log('Sliced data length:', this.animeList().length);

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
}
