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
  protected readonly title = signal('MyAnimeListWebsite');
  constructor(public animeService: AnimeService) { };

  ngOnInit() {
    this.animeService.getAllAnime().subscribe({
      next: (data) => {
        this.animeList.set(data);
        console.log('Data received:', data);

      },
      error: (err: any) => console.error('Error fetching anime:', err),
    });
  }
}
