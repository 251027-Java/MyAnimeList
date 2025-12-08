import { Component, signal, OnInit } from '@angular/core'; // import OnInit
import { RouterOutlet } from '@angular/router';
import { CommonModule, NgFor } from '@angular/common';
import { AnimeService, Anime } from './Service/anime-service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, NgFor],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})

export class App implements OnInit {
  animeList = signal<Anime[]>([]);
  title = signal('MyAnimeListWebsite');


    constructor(private animeService:AnimeService){};

    ngOnInit() {
      this.animeService.getAllAnime().subscribe({
        next: (data) => {
          this.animeList.set(data);
        console.log('Data received:', data);

        },
        error: (err:any) => console.error('Error fetching anime:', err),
         });
    }
 }

