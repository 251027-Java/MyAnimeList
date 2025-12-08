import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Anime {
  id: number;
  title: string;
  totalEpisodes: number;
  status: string;
}

@Injectable({
  providedIn: 'root',
})

export class AnimeService {
  private apiUrl = 'http://localhost:8080/anime/all';

  constructor(private http: HttpClient) {}

  getAllAnime(): Observable<Anime[]> {
    return this.http.get<Anime[]>(this.apiUrl);
  }
}
